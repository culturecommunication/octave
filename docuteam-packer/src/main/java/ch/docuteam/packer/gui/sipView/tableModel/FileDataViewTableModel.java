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

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.mets.structmap.NodeFile;
import ch.docuteam.tools.translations.I18N;

public class FileDataViewTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private NodeAbstract fileStructureNode;

    public void setFileStructureNode(final NodeAbstract fileStructureNode) {
        this.fileStructureNode = fileStructureNode;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return 13;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (columnIndex == 0) {
            switch (rowIndex) {
                case 0:
                    return I18N.translate("HeaderName");
                case 1:
                    return I18N.translate("HeaderPropertiesPath");
                case 2:
                    return I18N.translate("HeaderType");
                case 3:
                    return I18N.translate("HeaderPropertiesMIMEType");
                case 4:
                    return I18N.translate("HeaderPropertiesFormat");
                case 5:
                    return I18N.translate("HeaderPropertiesFormatKey");
                case 6:
                    return I18N.translate("HeaderPropertiesSizeKB");
                case 7:
                    return I18N.translate("HeaderPropertiesSize%");
                case 8:
                    return I18N.translate("HeaderPropertiesChildren");
                case 9:
                    return I18N.translate("HeaderPropertiesDescendants");
                case 10:
                    return I18N.translate("HeaderPropertiesEvents");
                case 11:
                    return I18N.translate("HeaderPropertiesSubmitStatus");
                case 12:
                    return I18N.translate("HeaderPropertiesPreservationRole");
            }
        } else if (columnIndex == 1) {
            if (fileStructureNode == null) {
                return null;
            }

            switch (rowIndex) {
                case 0:
                    return fileStructureNode.getLabel();
                case 1:
                    return fileStructureNode.getPathString();
                case 2:
                    return fileStructureNode.getType();
                case 3:
                    return fileStructureNode.getMimeType();
                case 4:
                    return fileStructureNode.getFormatName();
                case 5:
                    return fileStructureNode.getFormatKey();
                case 6:
                    return ((Long) (fileStructureNode.getSize() / 1024)).toString();
                case 7:
                    return fileStructureNode.getRelativeSize().toString();
                case 8:
                    return fileStructureNode.isFile() ? "-" : fileStructureNode.getChildCount();
                case 9:
                    return fileStructureNode.isFile() ? "-" : fileStructureNode.getDescendantCount();
                case 10:
                    return ((Integer) fileStructureNode.getMyEvents().size()).toString();
                case 11:
                    return I18N.translate(fileStructureNode.getSubmitStatus().toString());
                case 12:
                    if (fileStructureNode.isFolder()) {
                        return null;
                    }
                    NodeFile targetNode = ((NodeFile) fileStructureNode).getMigrationDerivedNode();
                    if (targetNode != null) {
                        return I18N.translate("PreservationRoleMaster");
                    }
                    targetNode = ((NodeFile) fileStructureNode).getMigrationSourceNode();
                    if (targetNode != null) {
                        return I18N.translate("PreservationRoleDerivate");
                    }
                    return null;
            }
        }

        return null;
    }

}
