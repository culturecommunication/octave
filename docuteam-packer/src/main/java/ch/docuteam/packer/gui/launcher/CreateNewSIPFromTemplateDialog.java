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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

import javax.swing.AbstractAction;
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

import ch.docuteam.tools.gui.GUIUtil;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class CreateNewSIPFromTemplateDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final LauncherView launcherView;

    protected JButton selectDestinationZIPOrFolderButton;

    protected JButton selectDestinationIsWorkspaceButton;

    protected JButton goButton;

    protected JTextField destinationFolderTextField;

    protected JTextField destinationNameTextField;

    protected JComboBox templateComboBox;

    protected JCheckBox beZIPCheckBox;

    protected JLabel messageLabel;

    protected boolean goButtonWasClicked = false;

    protected CreateNewSIPFromTemplateDialog(final LauncherView owner) {
        super(owner, I18N.translate("TitleCreateNewSIPFromTemplate"), true);
        launcherView = owner;

        setIconImage(getImage(PACKER_PNG));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPFromTemplateDialog.this.close();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        templateComboBox = new JComboBox(getTemplateNames());
        templateComboBox.setToolTipText(I18N.translate("ToolTipSelectTemplate"));
        templateComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPFromTemplateDialog.this.templateSelected();
            }
        });

        selectDestinationZIPOrFolderButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
        selectDestinationZIPOrFolderButton.setToolTipText(I18N.translate("ToolTipSelectDestinationFolder"));
        selectDestinationZIPOrFolderButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPFromTemplateDialog.this.selectDestinationFolderButtonClicked();
            }
        });

        selectDestinationIsWorkspaceButton = new JButton(getImageIcon("Workspace.png"));
        selectDestinationIsWorkspaceButton
                .setToolTipText(I18N.translate("ToolTipSelectDestinationIsWorkspaceFolder"));
        selectDestinationIsWorkspaceButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPFromTemplateDialog.this.selectDestinationIsWorkspaceButtonClicked();
            }
        });

        destinationFolderTextField = new JTextField(new File(owner.getLastUsedOpenOrSaveDirectory())
                .getAbsolutePath());
        destinationFolderTextField.setToolTipText(I18N.translate("ToolTipDestinationFolder"));

        destinationNameTextField = new JTextField((String) templateComboBox.getSelectedItem());
        destinationNameTextField.setToolTipText(I18N.translate("ToolTipDestinationName"));
        destinationNameTextField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(final KeyEvent e) {
                CreateNewSIPFromTemplateDialog.this.enableOrDisableButtonsAndFields();
            }
        });

        beZIPCheckBox = new JCheckBox(ZIP, owner.isNewSIPZippedByDefault());
        beZIPCheckBox.setToolTipText(I18N.translate("ToolTipBeZIP"));

        messageLabel = new JLabel();

        goButton = new JButton(getImageIcon(SAVE_PNG));
        goButton.setToolTipText(I18N.translate("ToolTipCreateNew"));
        goButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CreateNewSIPFromTemplateDialog.this.goButtonClicked();
            }
        });

        final GridBagPanel gridBag = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(0, 5, 0, 0));
        gridBag.add(new JLabel(I18N.translate("LabelTemplate")), 1, 0, GridBagConstraints.EAST);
        gridBag.add(templateComboBox, 1, 3, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(new JLabel(I18N.translate("LabelNewSIPDestination")), 2, 0, GridBagConstraints.EAST);
        gridBag.add(selectDestinationIsWorkspaceButton, 2, 1);
        gridBag.add(selectDestinationZIPOrFolderButton, 2, 2);
        gridBag.add(destinationFolderTextField, 2, 3, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 2,
                0);
        gridBag.add(destinationNameTextField, 2, 4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(beZIPCheckBox, 2, 5);
        gridBag.add(messageLabel, 3, 3, 0, 4, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(goButton, 3, 5, GridBagConstraints.EAST);

        this.add(gridBag);

        setPreferredSize(new Dimension(800, 150));
        pack();
        setLocationRelativeTo(owner);

        templateComboBox.requestFocusInWindow();
        enableOrDisableButtonsAndFields();

        setVisible(true);
    }

    protected void templateSelected() {
        destinationNameTextField.setText((String) templateComboBox.getSelectedItem());

        CreateNewSIPFromTemplateDialog.this.enableOrDisableButtonsAndFields();
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
        final String templateName = (String) templateComboBox.getSelectedItem();
        final String destinationFolder = destinationFolderTextField.getText();
        String destinationName = destinationNameTextField.getText();

        // Don't accept empty template name:
        if (templateName == null) {
            GUIUtil.shake(this);
            messageLabel.setText(I18N.translate("MessageTemplateNameIsEmpty"));
            return;
        }

        // Don't accept empty file name:
        if (destinationName.isEmpty()) {
            GUIUtil.shake(this);
            messageLabel.setText(I18N.translate("MessageDestinationNameIsEmpty"));
            return;
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

        // Remember the SIP directory:
        launcherView.setLastUsedOpenOrSaveDirectory(destinationFolder);

        goButtonWasClicked = true;
        close();
    }

    protected void close() {
        setVisible(false);
        dispose();
    }

    private String[] getTemplateNames() {
        final File templateDirectory = new File(launcherView.getTemplateDirectory());
        if (!templateDirectory.exists() || templateDirectory.isFile()) {
            return new String[] {};
        }

        final File[] templates = templateDirectory.listFiles(
                (FilenameFilter) ch.docuteam.tools.file.FileFilter.VisibleAll);
        final Vector<String> templateNames = new Vector<String>();
        for (final File file : templates) {
            templateNames.add(file.getName());
        }

        return templateNames.toArray(new String[] {});
    }

    protected void enableOrDisableButtonsAndFields() {
        goButton.setEnabled(
                !destinationNameTextField.getText().isEmpty() && templateComboBox.getSelectedItem() != null);
    }

}
