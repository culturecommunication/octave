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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ch.docuteam.darc.mets.structmap.NodeFile;
import ch.docuteam.tools.translations.I18N;

public class BadFilesTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    List<NodeFile> badFiles = new ArrayList<NodeFile>();

    public void clearList() {
        badFiles.clear();
        fireTableDataChanged();
    }

    public void setList(final List<NodeFile> badFiles) {
        this.badFiles = badFiles;
        fireTableDataChanged();
    }

    public List<NodeFile> getList() {
        return badFiles;
    }

    public NodeFile get(final int i) {
        return badFiles.get(i);
    }

    @Override
    public int getRowCount() {
        return badFiles.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(final int columnIndex) {
        switch (columnIndex) {
            case 0:
                return I18N.translate("HeaderNodePath");
            case 1:
                return I18N.translate("HeaderNodeIsAllowedBySA");
        }

        return null;
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Boolean.class;
        }

        return null;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final NodeFile nodeFile = badFiles.get(rowIndex);
        if (nodeFile == null) {
            return null;
        }

        switch (columnIndex) {
            case 0:
                return nodeFile.getPathString();
            case 1:
                return !nodeFile.isAllowedBySA();
        }

        return null;
    }

}
