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

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

public class RelativeSizeBarTableCellRenderer extends DefaultTableCellRenderer {

	// 100 vertical bars:
	private static final String BARS = "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||";
	// Magic Number: approximate width of the "|" character in pixels!
	private static final int BAR_CHAR_WIDTH = 5;

	private TableColumn column;

	public RelativeSizeBarTableCellRenderer(TableColumn column) {
		this.column = column;
	}

	@Override
	public void setValue(Object object) {
		if (object == null)
			return;

		int columnWidth = column.getWidth();
		float relativeFileSize = new Integer(object.toString()).floatValue() / 100;

		int nrOfBars100 = columnWidth / BAR_CHAR_WIDTH;
		int nrOfBars = (int) (relativeFileSize * nrOfBars100);
		String bars = BARS.substring(0, Math.min(BARS.length(), nrOfBars));

		setToolTipText("" + relativeFileSize);
		super.setValue(bars);
	}

}
