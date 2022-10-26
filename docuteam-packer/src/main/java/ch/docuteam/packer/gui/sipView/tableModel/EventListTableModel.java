/*
 * Copyright (C) since 2011 by docuteam AG
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

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.premis.Event;
import ch.docuteam.tools.translations.I18N;

public class EventListTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private NodeAbstract fileStructureNode;

    public void setFileStructureNode(final NodeAbstract fileStructureNode) {
        this.fileStructureNode = fileStructureNode;
        fireTableDataChanged();
    }

    public NodeAbstract getFileStructureNode() {
        return fileStructureNode;
    }

    @Override
    public int getRowCount() {
        if (fileStructureNode == null) {
            return 0;
        }

        return fileStructureNode.getMyEvents().size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(final int column) {
        switch (column) {
            case 0:
                return I18N.translate("HeaderTimestamp");
            case 1:
                return I18N.translate("HeaderType");
            case 2:
                return I18N.translate("HeaderEventOutcome");
        }

        return null;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (fileStructureNode == null) {
            return 0;
        }

        final Event event = fileStructureNode.getMyEvent(rowIndex);

        switch (columnIndex) {
            case 0:
                return event.getDateTime();
            case 1:
                return event.getType();
            case 2:
                return event.getOutcome();
        }

        return null;
    }

}
