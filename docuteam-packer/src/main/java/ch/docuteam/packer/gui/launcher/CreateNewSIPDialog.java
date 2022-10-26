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

import static ch.docuteam.packer.gui.ComponentNames.SIP_CREATE_NEW_SIP_ROOT_NAME_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CREATE_OK_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_DESTINATION_NAME_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_FROM_SOURCE_FILE_OR_FOLDER_RADIO_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_SOURCE_FILE_OR_FOLDER_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_SOURCE_FOLDER_FILE_CHOOSER;
import static ch.docuteam.packer.gui.PackerConstants.OPEN_FOLDER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SAVE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.ZIP;
import static ch.docuteam.packer.gui.PackerConstants.ZIP_EXT;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import ch.docuteam.darc.sa.SubmissionAgreement;
import ch.docuteam.darc.sa.SubmissionAgreement.Overview;
import ch.docuteam.tools.gui.GUIUtil;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class CreateNewSIPDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final LauncherView launcherView;

    protected JButton selectSourceFileOrFolderButton;

    protected JButton selectDestinationZIPOrFolderButton;

    protected JButton selectDestinationIsWorkspaceButton;

    protected JButton goButton;

    protected JRadioButton selectSIPEmptyRadioButton;

    protected JRadioButton selectSIPFromSourceFileOrFolderRadioButton;

    protected JTextField rootFolderNameTextField;

    protected JTextField sourceFileOrFolderTextField;

    // if added to the docuteamPacker.actionsNotVisible, it doesn't show deleteSourcesCheckBox
    private static final String ACTION_NAME_DELETE_SOURCES = "deleteSourcesCreateNewCheckbox";

    private JCheckBox deleteSourcesCheckBox;

    protected JTextField destinationFolderTextField;

    protected JTextField destinationNameTextField;

    protected JCheckBox beZIPCheckBox;

    protected JComboBox saComboBox;

    protected JLabel messageLabel;

    protected boolean goButtonWasClicked = false;

    protected CreateNewSIPDialog(final LauncherView launcherView) {
        super(launcherView, I18N.translate("TitleCreateNewSIP"), true);
        this.launcherView = launcherView;

        setIconImage(getImage(PACKER_PNG));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPDialog.this.close();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        selectSIPEmptyRadioButton = new JRadioButton();
        selectSIPEmptyRadioButton.setSelected(true);
        selectSIPEmptyRadioButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPDialog.this.selectSIPRootRadioButtonClicked();
            }
        });
        selectSIPFromSourceFileOrFolderRadioButton = new JRadioButton();
        selectSIPFromSourceFileOrFolderRadioButton.setName(SIP_SELECT_FROM_SOURCE_FILE_OR_FOLDER_RADIO_BUTTON);
        selectSIPFromSourceFileOrFolderRadioButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPDialog.this.selectSIPRootRadioButtonClicked();
            }
        });

        final ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(selectSIPEmptyRadioButton);
        radioButtonGroup.add(selectSIPFromSourceFileOrFolderRadioButton);

        rootFolderNameTextField = new JTextField("");
        rootFolderNameTextField.setName(SIP_CREATE_NEW_SIP_ROOT_NAME_TEXT_FIELD);
        rootFolderNameTextField.setToolTipText(I18N.translate("ToolTipRootFolderName"));
        rootFolderNameTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPDialog.this.rootFolderNameTextFieldChanged();
            }
        });
        rootFolderNameTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(final FocusEvent e) {
                CreateNewSIPDialog.this.rootFolderNameTextFieldChanged();
            }
        });

        selectSourceFileOrFolderButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
        selectSourceFileOrFolderButton.setName(SIP_SELECT_SOURCE_FILE_OR_FOLDER_BUTTON);
        selectSourceFileOrFolderButton.setToolTipText(I18N.translate("ToolTipSelectSource"));
        selectSourceFileOrFolderButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPDialog.this.selectSourceFolderButtonClicked();
            }
        });

        sourceFileOrFolderTextField = new JTextField(new File(launcherView.getDataDirectory()).getAbsolutePath());
        sourceFileOrFolderTextField.setToolTipText(I18N.translate("ToolTipSourceFileOrFolder"));
        sourceFileOrFolderTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPDialog.this.sourceFolderTextFieldChanged();
            }
        });
        sourceFileOrFolderTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(final FocusEvent e) {
                CreateNewSIPDialog.this.sourceFolderTextFieldChanged();
            }
        });

        selectDestinationZIPOrFolderButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
        selectDestinationZIPOrFolderButton.setToolTipText(I18N.translate("ToolTipSelectDestinationFolder"));
        selectDestinationZIPOrFolderButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPDialog.this.selectDestinationFolderButtonClicked();
            }
        });

        selectDestinationIsWorkspaceButton = new JButton(getImageIcon("Workspace.png"));
        selectDestinationIsWorkspaceButton.setName(SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON);
        selectDestinationIsWorkspaceButton
                .setToolTipText(I18N.translate("ToolTipSelectDestinationIsWorkspaceFolder"));
        selectDestinationIsWorkspaceButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPDialog.this.selectDestinationIsWorkspaceButtonClicked();
            }
        });

        boolean deleteSourcesByDefaultSelected = !launcherView.isActionNotVisible(ACTION_NAME_DELETE_SOURCES)
                && launcherView.isDeleteSourcesByDefault();
        deleteSourcesCheckBox = new JCheckBox(I18N.translate("LabelDeleteSources"), deleteSourcesByDefaultSelected);
        deleteSourcesCheckBox.setToolTipText(I18N.translate("ToolTipDeleteSources"));

        destinationFolderTextField = new JTextField(
                new File(launcherView.getLastUsedOpenOrSaveDirectory()).getAbsolutePath());
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
        destinationNameTextField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(final KeyEvent e) {
                CreateNewSIPDialog.this.enableOrDisableButtonsAndFields();
            }
        });
        destinationNameTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(final FocusEvent e) {
                destinationNameTextField
                        .setText(destinationNameTextField.getText().trim());
            }
        });

        beZIPCheckBox = new JCheckBox(ZIP, launcherView.isNewSIPZippedByDefault());
        beZIPCheckBox.setToolTipText(I18N.translate("ToolTipBeZIP"));

        saComboBox = new JComboBox(readSAOverviewFile().toArray());
        saComboBox.setToolTipText(I18N.translate("ToolTipSelectSA"));

        messageLabel = new JLabel();

        goButton = new JButton(getImageIcon(SAVE_PNG));
        goButton.setName(SIP_CREATE_OK_BUTTON);
        goButton.setToolTipText(I18N.translate("ToolTipCreateNew"));
        goButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPDialog.this.goButtonClicked();
            }
        });

        final GridBagPanel gridBag = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(0, 5, 0, 0));
        gridBag.add(selectSIPEmptyRadioButton, 0, 0, GridBagConstraints.EAST);
        gridBag.add(new JLabel(I18N.translate("LabelNewSIPRootName")), 0, 1, GridBagConstraints.EAST);
        gridBag.add(rootFolderNameTextField, 0, 4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(selectSIPFromSourceFileOrFolderRadioButton, 1, 0, GridBagConstraints.EAST);
        gridBag.add(new JLabel(I18N.translate("LabelNewSIPSource")), 1, 1, GridBagConstraints.EAST);
        gridBag.add(selectSourceFileOrFolderButton, 1, 3);
        gridBag.add(sourceFileOrFolderTextField, 1, 1, 4, 6, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, 1, 0);
        if (!launcherView.isActionNotVisible(ACTION_NAME_DELETE_SOURCES)) {
            gridBag.add(deleteSourcesCheckBox, 2, 2, 4, 6, GridBagConstraints.WEST);
        }
        gridBag.add(new JLabel(" "), 3, 2);
        gridBag.add(new JLabel(I18N.translate("LabelNewSIPDestination")), 4, 1, GridBagConstraints.EAST);
        gridBag.add(selectDestinationIsWorkspaceButton, 4, 2);
        gridBag.add(selectDestinationZIPOrFolderButton, 4, 3);
        gridBag.add(destinationFolderTextField, 4, 4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 2,
                0);
        gridBag.add(destinationNameTextField, 4, 5, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(beZIPCheckBox, 4, 6);
        gridBag.add(new JLabel(I18N.translate("LabelNewSIPSA")), 5, 1, GridBagConstraints.EAST);
        gridBag.add(saComboBox, 5, 5, 4, 6, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(messageLabel, 6, 6, 0, 5, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(goButton, 6, 6, GridBagConstraints.EAST);

        this.add(gridBag);

        setPreferredSize(new Dimension(800, 240));
        pack();
        setLocationRelativeTo(launcherView);

        rootFolderNameTextField.requestFocusInWindow();
        selectSIPRootRadioButtonClicked();

        setVisible(true);
    }

    protected void selectSIPRootRadioButtonClicked() {
        if (selectSIPEmptyRadioButton.isSelected()) {
            destinationNameTextField.setText(rootFolderNameTextField.getText());
        } else {
            destinationNameTextField.setText(new File(sourceFileOrFolderTextField.getText()).getName());
        }

        enableOrDisableButtonsAndFields();
    }

    protected void selectSourceFolderButtonClicked() {
        final JFileChooser fileChooser = new JFileChooser(sourceFileOrFolderTextField.getText());
        fileChooser.setName(SIP_SELECT_SOURCE_FOLDER_FILE_CHOOSER);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setDialogTitle(I18N.translate("TitleSelectSourceFileOrFolder"));
        fileChooser.setMultiSelectionEnabled(false);
        final int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        sourceFileOrFolderTextField.setText(fileChooser.getSelectedFile().getPath());
        sourceFolderTextFieldChanged();
    }

    protected void rootFolderNameTextFieldChanged() {
        rootFolderNameTextField.setText(rootFolderNameTextField.getText().trim());
        destinationNameTextField.setText(rootFolderNameTextField.getText());
        enableOrDisableButtonsAndFields();
    }

    protected void sourceFolderTextFieldChanged() {
        sourceFileOrFolderTextField.setText(sourceFileOrFolderTextField.getText().trim());
        destinationNameTextField.setText(new File(sourceFileOrFolderTextField.getText()).getName());
        enableOrDisableButtonsAndFields();
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

    protected void goButtonClicked() {
        final String rootFolderName = rootFolderNameTextField.getText();
        final String sourceFileOrFolder = sourceFileOrFolderTextField.getText();
        final String destinationFolder = destinationFolderTextField.getText();
        String destinationName = destinationNameTextField.getText();

        // Don't accept empty SIP name:
        if (destinationName.isEmpty()) {
            GUIUtil.shake(this);
            messageLabel.setText(I18N.translate("MessageDestinationNameIsEmpty"));
            return;
        }

        if (selectSIPFromSourceFileOrFolderRadioButton.isSelected()) {
            // Don't accept empty source file or folder:
            if (sourceFileOrFolder.isEmpty()) {
                GUIUtil.shake(this);
                messageLabel.setText(I18N.translate("MessageSourceFileOrFolderIsEmpty"));
                return;
            }

            // If the destination folder lies within the source folder, show
            // message and reject:
            if (destinationFolder.contains(sourceFileOrFolder)) {
                GUIUtil.shake(this);
                messageLabel.setText(I18N.translate("MessageDestinationIsWithinSource"));
                return;
            }
        } else {
            // Don't accept empty root folder name:
            if (rootFolderName.isEmpty()) {
                GUIUtil.shake(this);
                messageLabel.setText(I18N.translate("MessageRootFolderNameIsEmpty"));
                return;
            }
        }

        if (beZIPCheckBox.isSelected()) {
            if (!destinationName.toLowerCase().endsWith(ZIP_EXT)) {
                destinationName += ZIP_EXT;
            }
        } else {
            if (destinationName.toLowerCase().endsWith(ZIP_EXT)) {
                destinationName = destinationName.substring(0, destinationName.length() - 4);
            }
        }

        final File destinationFile = new File(destinationFolder + "/" + destinationName);

        // Don't accept if a SIP with this name already exists:
        if (destinationFile.exists()) {
            GUIUtil.shake(this);
            messageLabel.setText(I18N.translate("MessageSIPExistsAlready"));
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

    protected List<Overview> readSAOverviewFile() {
        return SubmissionAgreement.getAllFinalOverviews();
    }

    protected void enableOrDisableButtonsAndFields() {
        if (selectSIPEmptyRadioButton.isSelected()) {
            rootFolderNameTextField.setEnabled(true);
            selectSourceFileOrFolderButton.setEnabled(false);
            sourceFileOrFolderTextField.setEnabled(false);

            goButton.setEnabled(!(destinationNameTextField.getText().isEmpty() || rootFolderNameTextField.getText()
                    .isEmpty()));
        } else {
            rootFolderNameTextField.setEnabled(false);
            selectSourceFileOrFolderButton.setEnabled(true);
            sourceFileOrFolderTextField.setEnabled(true);

            goButton.setEnabled(!(destinationNameTextField.getText().isEmpty() || sourceFileOrFolderTextField
                    .getText().isEmpty()));
        }
    }

    boolean isDeleteSourcesCheckBoxSelected() {
        return deleteSourcesCheckBox.isSelected();
    }

}
