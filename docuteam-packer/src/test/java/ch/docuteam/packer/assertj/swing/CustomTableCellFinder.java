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
package ch.docuteam.packer.assertj.swing;

import javax.swing.JTable;

import org.assertj.swing.cell.JTableCellReader;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.data.TableCellFinder;

import ch.docuteam.tools.out.Logger;

public class CustomTableCellFinder implements TableCellFinder {
	
	private String searchedValue;
	private int colIndex;

	public CustomTableCellFinder(String searchedValue_, int colIndex_) {
		searchedValue = searchedValue_;
		colIndex = colIndex_;
	}
	
	@Override
	public TableCell findCell(JTable table, JTableCellReader cellReader) {			
		int rows = table.getModel().getRowCount();
		for(int rowIndex=0; rowIndex<rows; rowIndex++) {
			String value = table.getModel().getValueAt(rowIndex, colIndex).toString();
			if(searchedValue.equals(value)) {
			    //Logger.debug("findCell succesful");
				return TableCell.row(rowIndex).column(colIndex);
			}
		}
		return null;
	}
}
