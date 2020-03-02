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
package ch.docuteam.packer.gui.sipView.tableModel;

import javax.swing.table.AbstractTableModel;

import ch.docuteam.darc.premis.Event;
import ch.docuteam.tools.translations.I18N;

public class EventDetailTableModel extends AbstractTableModel {

	private Event event;

	public void setEvent(Event event) {
		this.event = event;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return 5;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			switch (rowIndex) {
			case 0:
				return I18N.translate("HeaderTimestamp");
			case 1:
				return I18N.translate("HeaderType");
			case 2:
				return I18N.translate("HeaderEventDetail");
			case 3:
				return I18N.translate("HeaderEventOutcome");
			case 4:
				return I18N.translate("HeaderEventOutcomeDetail");
			}
		} else if (columnIndex == 1) {
			if (event == null)
				return null;

			switch (rowIndex) {
			case 0:
				return event.getDateTime();
			case 1:
				return event.getType();
			case 2:
				return event.getDetail();
			case 3:
				return event.getOutcome();
			case 4:
				return event.getOutcomeDetail();
			}
		}

		return null;
	}

}
