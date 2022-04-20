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

import static ch.docuteam.packer.gui.PackerConstants.CONVERT_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import ch.docuteam.converter.FileConverter;
import ch.docuteam.converter.exceptions.BadPronomIdException;
import ch.docuteam.converter.exceptions.FileConversionException;
import ch.docuteam.darc.exceptions.FileAlreadyExistsException;
import ch.docuteam.darc.exceptions.FileOperationNotAllowedException;
import ch.docuteam.darc.exceptions.FolderNameIsEmptyException;
import ch.docuteam.darc.mets.Document.Mode;
import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.mets.structmap.NodeFile;
import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.packer.gui.util.CancellableOperation;
import ch.docuteam.tools.exception.ExceptionCollector;
import ch.docuteam.tools.exception.ExceptionCollectorException;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.file.exception.DROIDCouldNotInitializeException;
import ch.docuteam.tools.file.exception.DROIDMultipleIdentificationsFoundException;
import ch.docuteam.tools.file.exception.DROIDNoIdentificationFoundException;
import ch.docuteam.tools.file.exception.FileIsNotADirectoryException;
import ch.docuteam.tools.file.exception.FileUtilExceptionListException;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

import org.dom4j.DocumentException;

@SuppressWarnings("serial")
public class ConvertFilesAction extends AbstractSIPViewAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final ActionMonitoringDialog convertMonitoringDialog;

    private int convertedFilesCount = 0;

    private List<NodeFile> nodeList = new ArrayList<>();

    public ConvertFilesAction(final SIPView sipView) {
        super(I18N.translate("ButtonConvertFiles"), getImageIcon(CONVERT_PNG), sipView);
        putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipConvertFiles"));
        convertMonitoringDialog = new ActionMonitoringDialog(null, I18N.translate("ButtonConvertFiles"));
    }

    /**
     * Retrieves the list of nodes out of the selected ones for which convert action could be performed
     *
     * @return
     */
    private List<NodeFile> getNodesForAction() {
        final List<NodeFile> nodesForAction = new ArrayList<>();
        final int selectedRows[] = sipView.getTreeTable().getSelectedRows();
        for (final int selectedRow : selectedRows) {
            final NodeAbstract node = (NodeAbstract) sipView.getTreeTable().getPathForRow(selectedRow)
                .getLastPathComponent();
            if (isNodeReadWrite(node)) {
                nodesForAction.add((NodeFile) node);
            }
        }
        return nodesForAction;
    }

    private boolean isNodeReadWrite(final NodeAbstract node) {
        return sipView.getDocument().getMode().equals(Mode.ReadWrite) && node.isFile() && node.canRead() && node
                .canWrite() && node.getSubmitStatus().isEditingAllowed() && node.getMigrationDerivedNode() == null;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {

        nodeList = getNodesForAction();
        long totalSize = 0;
        convertedFilesCount = 0;
        for (final NodeFile node : nodeList) {
            totalSize += node.getSize();
        }

        final ConvertFilesStartDialog d = new ConvertFilesStartDialog(sipView, nodeList.size(), totalSize);
        if (!d.goButtonWasClicked) {
            return;
        }

        final ConvertTracker convertTracker = new ConvertTracker(convertMonitoringDialog, d.keepOriginalCheckBox
            .isSelected());
        convertMonitoringDialog.setOperation(convertTracker);
        convertTracker.execute();
    }

    @Override
    public void enableOrDisable() {
        nodeList = getNodesForAction();
        setEnabled(nodeList.size() > 0);
    }

    class ConvertTracker extends SwingWorker<Void, String> implements CancellableOperation {

        private final ActionMonitoringDialog convertTrackerDialog;

        private boolean isKeepOriginal = true;

        private final AtomicBoolean cancelRequested = new AtomicBoolean(false);

        private final String MessageSuccess = String.format(" : %s\n", I18N.translate("MessageSuccess"));

        private final String MessageNoAction = String.format(" : %s\n", I18N.translate("MessageNoAction"));

        private final String MessageFailure = String.format(" : %s\n", I18N.translate("MessageFailure"));

        private final String MessageCancelled = String.format("\n%s\n", I18N.translate("MessageCanceled"));

        public ConvertTracker(final ActionMonitoringDialog convertTrackerDialog, final boolean isKeepOriginal) {
            this.convertTrackerDialog = convertTrackerDialog;
            this.isKeepOriginal = isKeepOriginal;
        }

        /**
         * The method is mostly duplicated from ch.docuteam.actions.ingest.SIPFileMigrator.migrateFile(NodeFile file,
         * Boolean retainOriginalFile).<br>
         * Logs/error codes related to exceptions have been dropped in order to avoid [ actions <- packer ]
         * dependency.
         *
         * @param file
         * @param retainOriginalFile
         * @return
         */
        private String migrateFile(final NodeFile file, final Boolean retainOriginalFile) {
            String result = MessageFailure;
            File convertedFile = null;

            try {
                convertedFile = FileConverter.convertFile(file.getAbsolutePathString());

                if (convertedFile != null) {
                    try {
                        Logger.info("Migrated file: '" + file.getPathString() + "' into '" + convertedFile.getName() +
                            "'");
                        // Migration was OK, now update the METS-file:
                        final String migrationToolName = FileConverter.getRecentlyUsedFileConverterName();

                        ExceptionCollector.clear();

                        if (retainOriginalFile) {
                            file.migrateToFileKeepOriginal(convertedFile.getPath(), migrationToolName);
                        } else {
                            file.migrateToFile(convertedFile.getPath(), migrationToolName);
                        }
                        result = MessageSuccess;
                    } catch (final FileOperationNotAllowedException | FolderNameIsEmptyException e) {
                    } finally {
                        FileUtil.delete(convertedFile);
                    }

                    if (!ExceptionCollector.isEmpty()) {
                        throw new ExceptionCollectorException();
                    }
                } else {
                    result = MessageNoAction;
                }
            } catch (IllegalArgumentException | SecurityException | IndexOutOfBoundsException | DocumentException |
                FileAlreadyExistsException | IOException | IllegalAccessException | InvocationTargetException |
                NoSuchMethodException | ClassNotFoundException | InterruptedException |
                FileIsNotADirectoryException |
                BadPronomIdException | ExceptionCollectorException | FileUtilExceptionListException |
                DROIDCouldNotInitializeException |
                DROIDNoIdentificationFoundException | DROIDMultipleIdentificationsFoundException |
                FileConversionException e) {
                Logger.error(String.format("Exception during migration for '%s', keepOriginal = %b",
                    file.getAbsolutePathString(), retainOriginalFile), e);
                result = MessageFailure;
            }
            return result;
        }

        @Override
        protected void process(final List<String> chunks) {
            for (final String chunk : chunks) {
                convertTrackerDialog.appendMessage(chunk);
            }
        }

        @Override
        protected Void doInBackground() {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    convertTrackerDialog.init();
                    convertTrackerDialog.setVisible(true);
                }
            });

            for (final NodeFile node : nodeList) {
                if (cancelRequested.get()) {
                    publish(MessageCancelled);
                    convertTrackerDialog.setOver();
                    break;
                }

                convertedFilesCount++;
                publish(convertedFilesCount + ": " + node.getPathString() + " ... ");

                final String result = migrateFile(node, isKeepOriginal);

                publish(result);
            }

            // Refresh the tree
            sipView.getTreeTableModel().refreshTreeStructure(sipView.getTreeTable().getPathForRow(0));
            sipView.enableOrDisableActions();

            convertTrackerDialog.setOver();
            return null;
        }

        @Override
        public void cancelOperation() {
            cancelRequested.set(true);
        }
    }
}
