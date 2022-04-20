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

package ch.docuteam.packer.gui.sipView.actions;

import static ch.docuteam.packer.gui.PackerConstants.MIGRATE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import ch.docuteam.darc.exceptions.FileAlreadyExistsException;
import ch.docuteam.darc.exceptions.FileOperationNotAllowedException;
import ch.docuteam.darc.exceptions.FolderNameIsEmptyException;
import ch.docuteam.darc.mets.Document.Mode;
import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.mets.structmap.NodeFile;
import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.packer.gui.util.Util;
import ch.docuteam.tools.exception.ExceptionCollector;
import ch.docuteam.tools.file.exception.FileUtilExceptionListException;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

public class AppendMigratedFileAction extends AbstractSIPViewAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AppendMigratedFileAction(final SIPView sipView) {
        super(I18N.translate("ButtonAppendMigratedFile"), getImageIcon(MIGRATE_PNG), sipView);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final int selectedRow = sipView.getTreeTable().getSelectedRow();
        final NodeAbstract selectedNode = (NodeAbstract) sipView.getTreeTable().getPathForRow(selectedRow)
                .getLastPathComponent();
        final String admId = selectedNode.getAdmId();

        final AppendMigratedFileDialog dialog = new AppendMigratedFileDialog(sipView, selectedNode);
        if (!dialog.goButtonWasClicked) {
            return;
        }

        final File newFile = new File(dialog.derivedFileTextField.getText());
        if (!newFile.exists()) {
            return;
        }

        ExceptionCollector.clear();
        try {
            if (dialog.keepOriginalCheckBox.isSelected()) {
                ((NodeFile) selectedNode).migrateToFileKeepOriginal(newFile.getAbsolutePath(), "docuteam packer");
            } else {
                ((NodeFile) selectedNode).migrateToFile(newFile.getAbsolutePath(), "docuteam packer");
            }
        } catch (FileOperationNotAllowedException | FileUtilExceptionListException | IOException |
                FileAlreadyExistsException | FolderNameIsEmptyException e1) {
            Logger.error(e1.getMessage(), e1);
            JOptionPane.showMessageDialog(sipView, e1.toString(), I18N.translate("TitleCantInsertFileOrFolder"),
                    JOptionPane.ERROR_MESSAGE);
        }

        // feedback from migration
        Util.showAllFromExceptionCollector(null, sipView);

        // Refresh the selected node:
        sipView.getTreeTableModel().refreshTreeStructure(sipView.getTreeTable().getPathForRow(selectedRow));
        sipView.selectNode(admId);
        sipView.enableOrDisableActions();
    }

    @Override
    public void enableOrDisable() {
        boolean isEnabled = false;

        if (sipView.getTreeTable().getSelectedRowCount() == 1) {
            final int selectedRow = sipView.getTreeTable().getSelectedRow();
            final NodeAbstract node = (NodeAbstract) sipView.getTreeTable().getPathForRow(selectedRow)
                    .getLastPathComponent();

            // enabled is a consequence of several of the conditions being true
            // for all the selected nodes
            isEnabled = sipView.getDocument().getMode().equals(Mode.ReadWrite) && node.isFile() && node.canRead() &&
                    node.canWrite() && node.getMigrationDerivedNode() == null && node.getSubmitStatus()
                            .isEditingAllowed();
        }

        setEnabled(isEnabled);
    }

}
