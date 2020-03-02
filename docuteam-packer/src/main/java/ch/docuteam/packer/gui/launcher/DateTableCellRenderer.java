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
package ch.docuteam.packer.gui.launcher;

import javax.swing.table.DefaultTableCellRenderer;

import static ch.docuteam.packer.gui.PackerConstants.DATE_FORMAT;
import ch.docuteam.tools.string.DateFormatter;

public class DateTableCellRenderer extends DefaultTableCellRenderer {

	public DateTableCellRenderer() {
		super();
		this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
	}

	@Override
	public void setValue(Object object) {
		if (object == null){
			return;
		}
		super.setValue(DateFormatter.getDateTimeString((Long) object, DATE_FORMAT));
	}

}
