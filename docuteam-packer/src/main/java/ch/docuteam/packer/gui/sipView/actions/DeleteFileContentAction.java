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
package ch.docuteam.packer.gui.sipView.actions;

import static ch.docuteam.packer.gui.PackerConstants.DELETE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JOptionPane;

import ch.docuteam.darc.exceptions.FileOperationNotAllowedException;
import ch.docuteam.darc.exceptions.NodeFileDeletionNotAllowedException;
import ch.docuteam.darc.mets.Document.Mode;
import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.mets.structmap.NodeAbstract.SubmitStatus;
import ch.docuteam.darc.mets.structmap.NodeFile;
import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.tools.file.exception.FileUtilExceptionListException;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

/**
 * @author iliya
 */
public class DeleteFileContentAction extends AbstractSIPViewAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DeleteFileContentAction(final SIPView sipView) {
        super(I18N.translate("DeleteFileContent"), getImageIcon(DELETE_PNG), sipView);
        putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipDeleteFileContent"));
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {

        final int selectedRowCount = sipView.getTreeTable().getSelectedRowCount();
        if (selectedRowCount == 0) {
            return;
        } else {
            final int[] selectedRows = sipView.getTreeTable().getSelectedRows();
            if (JOptionPane.showConfirmDialog(sipView, I18N.translate("QuestionDeleteFileContent"),
                    I18N.translate("TitleDeleteFileContent"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // TODO Exceptions should be handled properly
                try {
                    for (final int selectedRow : selectedRows) {
                        ((NodeFile) sipView.getTreeTable().getPathForRow(selectedRow).getLastPathComponent())
                                .deleteFileContent();
                        // sipView.getTreeTable().getPathForRow(selectedRow)
                        sipView.getTreeTableModel().refreshTreeStructure(sipView.getTreeTable().getPathForRow(
                                selectedRow));
                        sipView.getTreeTableModel().refreshNode(sipView.getTreeTable().getPathForRow(selectedRow));
                    }
                } catch (IOException | FileOperationNotAllowedException | FileUtilExceptionListException e) {
                    Logger.error(e.getMessage(), e);
                } catch (final NodeFileDeletionNotAllowedException e) {
                    Logger.error(e.getMessage(), e);
                }
            } else {
                return;
            }
        }
    }

    @Override
    public void enableOrDisable() {
        final int selectedRowCount = sipView.getTreeTable().getSelectedRowCount();
        boolean isEnabled = sipView.getDocument().getMode().equals(Mode.ReadWrite) && selectedRowCount > 0;
        final int[] selectedRows = sipView.getTreeTable().getSelectedRows();

        for (final int selected : selectedRows) {
            final NodeAbstract node = (NodeAbstract) sipView.getTreeTable().getPathForRow(selected)
                    .getLastPathComponent();
            // enabled is a consequence of several of the conditions being true
            // for all the selected nodes
            final boolean enabledForCurrentNode = node.isFile() && node.canWrite() && node.getSubmitStatus().equals(
                    SubmitStatus.Submitted);
            isEnabled &= enabledForCurrentNode;
        }

        setEnabled(isEnabled);
    }

}
