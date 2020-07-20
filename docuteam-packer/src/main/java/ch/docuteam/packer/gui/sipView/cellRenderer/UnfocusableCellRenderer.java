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

package ch.docuteam.packer.gui.sipView.cellRenderer;

import static ch.docuteam.packer.gui.PackerConstants.EDITABLE_COLUMN_INDEX;
import static ch.docuteam.packer.gui.PackerConstants.URL_PATTERN;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class UnfocusableCellRenderer extends DefaultTableCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus,
            final int row, final int column) {
        final Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Display URLs in blue
        if (column == EDITABLE_COLUMN_INDEX && value != null && value.toString().matches(URL_PATTERN)) {
            comp.setForeground(Color.blue);
        } else {
            comp.setForeground((Color) UIManager.get("Table.foreground"));
        }

        if (hasFocus) {
            table.changeSelection(row, 2, false, false);
        }
        return comp;
    }
}