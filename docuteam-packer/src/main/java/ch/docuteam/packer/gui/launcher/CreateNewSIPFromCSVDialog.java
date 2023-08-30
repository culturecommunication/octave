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
package ch.docuteam.packer.gui.launcher;

import static ch.docuteam.packer.gui.ComponentNames.SIP_CREATE_OK_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_DESTINATION_NAME_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_CSV_MAPPING_FILE_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_CSV_MAPPING_FILE_CHOOSER;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_CSV_MAPPING_FILE_COMBO_BOX;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_SOURCE_FILE_OR_FOLDER_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_SOURCE_FOLDER_FILE_CHOOSER;
import static ch.docuteam.packer.gui.PackerConstants.CHECKSUM;
import static ch.docuteam.packer.gui.PackerConstants.OPEN_FOLDER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SAVE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SKIP_LEVEL_VALIDATION;
import static ch.docuteam.packer.gui.PackerConstants.ZIP;
import static ch.docuteam.packer.gui.PackerConstants.ZIP_EXT;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import ch.docuteam.darc.sa.SubmissionAgreement;
import ch.docuteam.tools.file.FileFilter;
import ch.docuteam.tools.gui.GUIUtil;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class CreateNewSIPFromCSVDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final LauncherView launcherView;

    protected JButton selectSourceFileButton;

    protected JTextField sourceFileTextField;

    protected JButton selectDestinationZIPOrFolderButton;

    protected JButton selectDestinationIsWorkspaceButton;

    protected JTextField destinationFolderTextField;

    protected JTextField destinationNameTextField;

    protected JCheckBox beZIPCheckBox;

    protected JComboBox<String> saComboBox;

    protected JCheckBox checksumCheckbox;

    protected JCheckBox skipLevelValidationCheckbox;

    protected JButton selectCsvMappingButton;

    protected JComboBox<String> csvMappingComboBox;

    protected JLabel messageLabel;

    protected JButton goButton;

    protected boolean goButtonWasClicked = false;

    protected CreateNewSIPFromCSVDialog(final LauncherView owner, final String[] csvMappingOptions) {
        super(owner, I18N.translate("TitleCreateNewSIPFromCSV"), true);
        launcherView = owner;

        setIconImage(getImage(PACKER_PNG));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPFromCSVDialog.this.close();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        selectSourceFileButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
        selectSourceFileButton.setName(SIP_SELECT_SOURCE_FILE_OR_FOLDER_BUTTON);
        selectSourceFileButton.setToolTipText(I18N.translate("ToolTipSelectSourceFile"));
        selectSourceFileButton.addActionListener(e -> CreateNewSIPFromCSVDialog.this.selectSourceFolderButtonClicked());

        sourceFileTextField = new JTextField(new File(launcherView.getDataDirectory()).getAbsolutePath());
        sourceFileTextField.setToolTipText(I18N.translate("ToolTipSourceFile"));
        sourceFileTextField.addActionListener(e -> CreateNewSIPFromCSVDialog.this.sourceFileTextFieldChanged());
        sourceFileTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(final FocusEvent e) {
                CreateNewSIPFromCSVDialog.this.sourceFileTextFieldChanged();
            }
        });

        selectDestinationZIPOrFolderButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
        selectDestinationZIPOrFolderButton.setToolTipText(I18N.translate("ToolTipSelectDestinationFolder"));
        selectDestinationZIPOrFolderButton.addActionListener(e -> CreateNewSIPFromCSVDialog.this.selectDestinationFolderButtonClicked());

        selectDestinationIsWorkspaceButton = new JButton(getImageIcon("Workspace.png"));
        selectDestinationIsWorkspaceButton.setName(SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON);
        selectDestinationIsWorkspaceButton
            .setToolTipText(I18N.translate("ToolTipSelectDestinationIsWorkspaceFolder"));
        selectDestinationIsWorkspaceButton.addActionListener(e -> CreateNewSIPFromCSVDialog.this.selectDestinationIsWorkspaceButtonClicked());

        destinationFolderTextField = new JTextField(new File(launcherView.getLastUsedOpenOrSaveDirectory()).getAbsolutePath());
        destinationFolderTextField.setToolTipText(I18N.translate("ToolTipDestinationFolder"));
        destinationFolderTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(final FocusEvent e) {
                destinationFolderTextField
                    .setText(destinationFolderTextField.getText().trim());
            }
        });

        destinationNameTextField = new JTextField(new File(launcherView.getDataDirectory()).getName());
        destinationNameTextField.setName(SIP_DESTINATION_NAME_TEXT_FIELD);
        destinationNameTextField.setToolTipText(I18N.translate("ToolTipDestinationName"));
        destinationNameTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(final FocusEvent e) {
                destinationNameTextField
                    .setText(destinationNameTextField.getText().trim());
            }
        });

        beZIPCheckBox = new JCheckBox(ZIP, launcherView.isNewSIPZippedByDefault());
        beZIPCheckBox.setToolTipText(I18N.translate("ToolTipBeZIP"));

        saComboBox = new JComboBox(SubmissionAgreement.getAllFinalOverviews().toArray());
        saComboBox.setToolTipText(I18N.translate("ToolTipSelectSA"));

        checksumCheckbox = new JCheckBox(CHECKSUM, false);
        checksumCheckbox.setToolTipText(I18N.translate("ToolTipChecksum"));

        skipLevelValidationCheckbox = new JCheckBox(SKIP_LEVEL_VALIDATION, false);
        skipLevelValidationCheckbox.setToolTipText(I18N.translate("ToolTipSkipLevelValidation"));

        selectCsvMappingButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
        selectCsvMappingButton.setName(SIP_SELECT_CSV_MAPPING_FILE_BUTTON);
        selectCsvMappingButton.setToolTipText(I18N.translate("ToolTipSelectCSVMappingFile"));
        selectCsvMappingButton.addActionListener(e -> CreateNewSIPFromCSVDialog.this.selectCsvMappingButtonClicked());

        csvMappingComboBox = new JComboBox<>(csvMappingOptions);
        csvMappingComboBox.setName(SIP_SELECT_CSV_MAPPING_FILE_COMBO_BOX);
        csvMappingComboBox.setToolTipText(I18N.translate("ToolTipSelectCSVMappingFile"));

        messageLabel = new JLabel();

        goButton = new JButton(getImageIcon(SAVE_PNG));
        goButton.setName(SIP_CREATE_OK_BUTTON);
        goButton.setToolTipText(I18N.translate("ToolTipCreateNew"));
        goButton.addActionListener(e -> CreateNewSIPFromCSVDialog.this.goButtonClicked());

        final GridBagPanel gridBag = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(0, 5, 0, 0));
        gridBag.add(new JLabel(I18N.translate("LabelNewSIPSourceFile")), 1, 1, GridBagConstraints.EAST);
        gridBag.add(selectSourceFileButton, 1, 3);
        gridBag.add(sourceFileTextField, 1, 1, 4, 6, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(new JLabel(I18N.translate("LabelNewSIPCSVMapping")), 2, 1, GridBagConstraints.EAST);
        gridBag.add(selectCsvMappingButton, 2, 3);
        gridBag.add(csvMappingComboBox, 2, 2, 4, 6, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);

        gridBag.add(new JLabel(" "), 3, 3);
        gridBag.add(new JLabel(I18N.translate("LabelNewSIPDestination")), 4, 1, GridBagConstraints.EAST);
        gridBag.add(selectDestinationIsWorkspaceButton, 4, 2);
        gridBag.add(selectDestinationZIPOrFolderButton, 4, 3);
        gridBag.add(destinationFolderTextField, 4, 4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 2,
            0);
        gridBag.add(destinationNameTextField, 4, 5, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(beZIPCheckBox, 4, 6);
        gridBag.add(new JLabel(I18N.translate("LabelNewSIPSA")), 5, 1, GridBagConstraints.EAST);
        gridBag.add(saComboBox, 5, 5, 4, 6, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(checksumCheckbox, 6, 4);
        gridBag.add(skipLevelValidationCheckbox, 6, 5);

        gridBag.add(messageLabel, 7, 7, 0, 5, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(goButton, 7, 6, GridBagConstraints.EAST);
        this.add(gridBag);

        setPreferredSize(new Dimension(800, 220));
        pack();
        setLocationRelativeTo(owner);

        setVisible(true);
    }

    protected void selectSourceFolderButtonClicked() {
        final JFileChooser fileChooser = new JFileChooser(sourceFileTextField.getText());
        fileChooser.setName(SIP_SELECT_SOURCE_FOLDER_FILE_CHOOSER);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(FileFilter.CSV_OR_DIRECTORY);
        fileChooser.setDialogTitle(I18N.translate("TitleSelectSourceFile"));
        fileChooser.setMultiSelectionEnabled(false);
        final int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        sourceFileTextField.setText(fileChooser.getSelectedFile().getPath());
        sourceFileTextFieldChanged();
    }

    protected void sourceFileTextFieldChanged() {
        final var sourceFileText = sourceFileTextField.getText().trim();
        sourceFileTextField.setText(sourceFileText);
        final var sourceFileName = new File(sourceFileText).getName();
        destinationNameTextField.setText(sourceFileName.substring(0, sourceFileName.lastIndexOf('.')));
    }

    protected void selectDestinationFolderButtonClicked() {
        final JFileChooser fileChooser = new JFileChooser(destinationFolderTextField.getText());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(I18N.translate("TitleSelectDestinationFolder"));
        fileChooser.setMultiSelectionEnabled(false);
        final int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        destinationFolderTextField.setText(fileChooser.getSelectedFile().getPath());
    }

    protected void selectDestinationIsWorkspaceButtonClicked() {
        destinationFolderTextField.setText(launcherView.getSipDirectory());
    }

    protected void selectCsvMappingButtonClicked() {
        final JFileChooser fileChooser = new JFileChooser(new File(launcherView.getDataDirectory()).getAbsolutePath());
        fileChooser.setName(SIP_SELECT_CSV_MAPPING_FILE_CHOOSER);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".xml");
            }

            @Override
            public String getDescription() {
                return "XML files";
            }
        });
        fileChooser.setDialogTitle(I18N.translate("TitleSelectSourceFile"));
        fileChooser.setMultiSelectionEnabled(false);
        final int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        final String csvPath = fileChooser.getSelectedFile().getPath();

        // If the option is not present in the combobox, add it
        if (((DefaultComboBoxModel<String>) csvMappingComboBox.getModel()).getIndexOf(csvPath) == -1) {
            csvMappingComboBox.addItem(csvPath);
        }
        csvMappingComboBox.setSelectedItem(csvPath);
    }

    protected void goButtonClicked() {
        final var sourceFileOrFolder = sourceFileTextField.getText();
        final var destinationFolder = destinationFolderTextField.getText();
        var destinationName = destinationNameTextField.getText();
        final String csvMapping = (String) csvMappingComboBox.getSelectedItem();

        // Don't accept empty SIP name:
        if (destinationName.isEmpty()) {
            GUIUtil.shake(this);
            messageLabel.setText(I18N.translate("MessageDestinationNameIsEmpty"));
            return;
        }

        // Don't accept empty source file or folder:
        if (sourceFileOrFolder.isEmpty()) {
            GUIUtil.shake(this);
            messageLabel.setText(I18N.translate("MessageSourceFileOrFolderIsEmpty"));
            return;
        } else if (!new File(sourceFileTextField.getText()).isFile()) {
            messageLabel.setText(I18N.translate("MessageSourceFileDoesNotExist"));
            return;
        }

        if (beZIPCheckBox.isSelected()) {
            if (!destinationName.toLowerCase().endsWith(ZIP_EXT)) {
                destinationName += ZIP_EXT;
            }
        } else if (destinationName.toLowerCase().endsWith(ZIP_EXT)) {
            destinationName = destinationName.substring(0, destinationName.length() - ZIP_EXT.length());
        }

        final var destinationFile = Path.of(destinationFolder, destinationName).toFile();

        // Don't accept if a SIP with this name already exists:
        if (destinationFile.exists()) {
            GUIUtil.shake(this);
            messageLabel.setText(I18N.translate("MessageSIPExistsAlready"));
            return;
        }

        // Don't accept empty or not file csv mapping:
        if (csvMapping.isEmpty()) {
            GUIUtil.shake(this);
            messageLabel.setText(I18N.translate("MessageCSVMappingIsEmpty"));
            return;
        } else if (!new File(csvMapping).isFile()) {
            messageLabel.setText(I18N.translate("MessageCSVMappingDoesNotExist"));
            return;
        }

        // Remember the Data and SIP directories:
        launcherView.setDataDirectory(sourceFileOrFolder);
        launcherView.setLastUsedOpenOrSaveDirectory(destinationFolder);

        goButtonWasClicked = true;
        close();
    }

    protected void close() {
        setVisible(false);
        dispose();
    }
}
