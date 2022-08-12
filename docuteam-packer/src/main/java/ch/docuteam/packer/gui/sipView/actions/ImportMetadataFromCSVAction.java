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

import static ch.docuteam.packer.gui.PackerConstants.IMPORT_CSV_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import ch.docuteam.darc.exceptions.DocumentIsReadOnlyException;
import ch.docuteam.darc.exceptions.FileOrFolderIsInUseException;
import ch.docuteam.darc.exceptions.OriginalSIPIsMissingException;
import ch.docuteam.mapping.csv.CsvToMetsImporter;
import ch.docuteam.packer.gui.launcher.LauncherView;
import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.tools.file.ConfigurationFileLoader;
import ch.docuteam.tools.file.FileFilter;
import ch.docuteam.tools.file.exception.FileUtilExceptionListException;
import ch.docuteam.tools.translations.I18N;

public class ImportMetadataFromCSVAction extends AbstractSIPViewAction {

    public static final String CHOOSER_NAME = "CHOOSE_CSV";

    protected String lastUsedFileChooserFolder;

    private final Path configLocation;
    private final boolean isContextAction;

    public ImportMetadataFromCSVAction(final SIPView sipView, final boolean isContextAction) {
        super(I18N.translate("ButtonImportCSVFile"), getImageIcon(IMPORT_CSV_PNG), sipView);
        this.isContextAction = isContextAction;
        putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipImportCSV"));
        putValue(LauncherView.ACTION_HIDE_KEY, "importMetadataFromCSVAction");
        setEnabled(false);
        configLocation = new ConfigurationFileLoader(sipView.getLauncherView().getConfigDirectory()).load(
            "csv-mapping.xml").toPath();
        lastUsedFileChooserFolder = sipView.getLauncherView().getDataDirectory();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Path pathToCsv = getPathToCsv();
        if (pathToCsv == null) {
            return;
        }

        final var importResult = CsvToMetsImporter.importCsv(sipView.getDocument(), pathToCsv, configLocation, getSaveHandler());
        if (importResult.isSuccess()) {
            JOptionPane.showMessageDialog(sipView, importResult.getSuccessMessagesWithPotentialWarnings(), I18N.translate("TitleImportCsvSuccess"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(sipView, I18N.translate(importResult.getError().getMessageKey(), importResult.getAdditionalErrorObjects()), I18N.translate("TitleImportCsvFailure"), JOptionPane.ERROR_MESSAGE);
        }

        sipView.updateTree();
    }

    private BooleanSupplier getSaveHandler() {
        return () -> {
            final var selectedOption = JOptionPane.showConfirmDialog(sipView, I18N.translate("ImportCsvManualSaveMessage"), I18N.translate("ImportCsvManualSaveTitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (selectedOption == JOptionPane.YES_OPTION) {
                try {
                    sipView.getDocument().save(false, true);
                    return true;
                } catch (IOException | FileUtilExceptionListException | DocumentIsReadOnlyException | FileOrFolderIsInUseException | OriginalSIPIsMissingException ex) {
                    return false;
                }
            }
            return false;
        };
    }

    private Path getPathToCsv() {
        if (isContextAction) {
            return getContextFile();
        }
        return getFileChooserFile();
    }

    private Path getFileChooserFile() {
        final var fileChooser = new JFileChooser(lastUsedFileChooserFolder);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(FileFilter.CSV_OR_DIRECTORY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setName(CHOOSER_NAME);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle(I18N.translate("TitleImportCSVChooser"));
        final var result = fileChooser.showOpenDialog(sipView);

        if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) {
            return null;
        }
        lastUsedFileChooserFolder = fileChooser.getSelectedFile().getParent();
        return fileChooser.getSelectedFile().toPath();
    }

    private Path getContextFile() {
        return sipView.getSelectedNode().getFile().toPath();
    }

    @Override
    public void enableOrDisable() {
        final var enabled = sipView.getDocument().canWrite() && Files.isReadable(configLocation) && (!isContextAction || isCsvNode());
        setEnabled(enabled);
    }

    private boolean isCsvNode() {
        return sipView.getTreeTable().getSelectedRowCount() == 1 && sipView.getSelectedNode().isFile() && sipView.getSelectedNode().fileExists() && sipView.getSelectedNode().getLabel().endsWith(".csv");
    }

}
