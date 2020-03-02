/*
 * Copyright (C) since 2011  Docuteam GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.docuteam.packer.gui.sipView;

import static ch.docuteam.packer.gui.PackerConstants.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class TextAreaTableCellEditor extends AbstractCellEditor
		implements TableCellEditor, TableCellRenderer, KeyListener {

	private static final Font Font = (Font) UIManager.get("Table.font");
	private static final Color ForegroundColor = (Color) UIManager.get("Table.foreground");
	private static final Color BackgroundColor = (Color) UIManager.get("Table.background");
	private static final Color SelectionForegroundColor = (Color) UIManager.get("Table.selectionForeground");
	private static final Color SelectionBackgroundColor = (Color) UIManager.get("Table.selectionBackground");

	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JTable table;

	public TextAreaTableCellEditor() {
		this.textArea = new JTextArea();
		this.textArea.setLineWrap(true);
		this.textArea.setWrapStyleWord(true);
		this.textArea.setFont(Font);
		this.textArea.addKeyListener(this);

		this.scrollPane = new JScrollPane(this.textArea);
		this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		this.textArea.setText(value == null ? "" : value.toString());

		if (isSelected) {
			this.textArea.setForeground(SelectionForegroundColor);
			this.textArea.setBackground(SelectionBackgroundColor);
		} else {
			this.textArea.setForeground(ForegroundColor);
			this.textArea.setBackground(BackgroundColor);
		}

		// Hide the scrollbars in rendering mode, so return this.textArea
		// instead of this.scrollPane:
		return this.textArea;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		// Remember the table I am in. I need this for setting the selection
		// when loosing focus.
		if (this.table == null)
			this.table = table;

		this.textArea.setText(value == null ? "" : value.toString());

		// Show the scrollbars in editing mode, so return this.scrolPane instead
		// of this.textArea:
		return this.scrollPane;
	}

	@Override
	public Object getCellEditorValue() {
		return this.textArea.getText();
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent)
			return ((MouseEvent) anEvent).getClickCount() >= CLICK_COUNT_TO_START;

		return true;
	}

	// ----- Key Listener methods to trap the TAB key:

	@Override
	public void keyPressed(KeyEvent e) {
		// Trap the TAB and Alt-Enter keys:
		if ((e.getKeyCode() == KeyEvent.VK_TAB) || (e.getKeyCode() == KeyEvent.VK_ENTER && e.isAltDown())) {
			int editingRow = this.table.getEditingRow();

			e.consume();
			this.fireEditingStopped();

			// On TAB key, move focus to next row or, if I am editing the last
			// row, to the 1st row:
			if (e.getKeyCode() == KeyEvent.VK_TAB) {
				int nextEditingRow = editingRow + 1;
				if (nextEditingRow >= this.table.getRowCount())
					nextEditingRow = 0;
				this.table.getSelectionModel().setSelectionInterval(nextEditingRow, nextEditingRow);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
