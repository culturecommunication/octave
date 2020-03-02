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
package ch.docuteam.packer.gui.launcher;

import static ch.docuteam.packer.gui.PackerConstants.*;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import ch.docuteam.tools.gui.GUIUtil;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class CreateNewSIPFromTemplateDialog extends JDialog {

	private LauncherView launcherView;
	protected JButton selectDestinationZIPOrFolderButton;
	protected JButton selectDestinationIsWorkspaceButton;
	protected JButton goButton;

	protected JTextField destinationFolderTextField;
	protected JTextField destinationNameTextField;
	protected JComboBox templateComboBox;
	protected JCheckBox beZIPCheckBox;

	protected JLabel messageLabel;

	protected boolean goButtonWasClicked = false;

	protected CreateNewSIPFromTemplateDialog(LauncherView owner) {
		super(owner, I18N.translate("TitleCreateNewSIPFromTemplate"), true);
		this.launcherView = owner;

		this.setIconImage(getImage(PACKER_PNG));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getRootPane().registerKeyboardAction(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPFromTemplateDialog.this.close();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.templateComboBox = new JComboBox(this.getTemplateNames());
		this.templateComboBox.setToolTipText(I18N.translate("ToolTipSelectTemplate"));
		this.templateComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPFromTemplateDialog.this.templateSelected();
			}
		});

		this.selectDestinationZIPOrFolderButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
		this.selectDestinationZIPOrFolderButton.setToolTipText(I18N.translate("ToolTipSelectDestinationFolder"));
		this.selectDestinationZIPOrFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPFromTemplateDialog.this.selectDestinationFolderButtonClicked();
			}
		});

		this.selectDestinationIsWorkspaceButton = new JButton(getImageIcon("Workspace.png"));
		this.selectDestinationIsWorkspaceButton
				.setToolTipText(I18N.translate("ToolTipSelectDestinationIsWorkspaceFolder"));
		this.selectDestinationIsWorkspaceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPFromTemplateDialog.this.selectDestinationIsWorkspaceButtonClicked();
			}
		});

		this.destinationFolderTextField = new JTextField(new File(owner.getLastUsedOpenOrSaveDirectory()).getAbsolutePath());
		this.destinationFolderTextField.setToolTipText(I18N.translate("ToolTipDestinationFolder"));

		this.destinationNameTextField = new JTextField((String) this.templateComboBox.getSelectedItem());
		this.destinationNameTextField.setToolTipText(I18N.translate("ToolTipDestinationName"));
		this.destinationNameTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				CreateNewSIPFromTemplateDialog.this.enableOrDisableButtonsAndFields();
			}
		});

		this.beZIPCheckBox = new JCheckBox(ZIP, owner.isNewSIPZippedByDefault());
		this.beZIPCheckBox.setToolTipText(I18N.translate("ToolTipBeZIP"));

		this.messageLabel = new JLabel();

		this.goButton = new JButton(getImageIcon(SAVE_PNG));
		this.goButton.setToolTipText(I18N.translate("ToolTipCreateNew"));
		this.goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPFromTemplateDialog.this.goButtonClicked();
			}
		});

		GridBagPanel gridBag = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(0, 5, 0, 0));
		gridBag.add(new JLabel(I18N.translate("LabelTemplate")), 1, 0, GridBagConstraints.EAST);
		gridBag.add(this.templateComboBox, 1, 3, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBag.add(new JLabel(I18N.translate("LabelNewSIPDestination")), 2, 0, GridBagConstraints.EAST);
		gridBag.add(this.selectDestinationIsWorkspaceButton, 2, 1);
		gridBag.add(this.selectDestinationZIPOrFolderButton, 2, 2);
		gridBag.add(this.destinationFolderTextField, 2, 3, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 2,
				0);
		gridBag.add(this.destinationNameTextField, 2, 4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBag.add(this.beZIPCheckBox, 2, 5);
		gridBag.add(this.messageLabel, 3, 3, 0, 4, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBag.add(this.goButton, 3, 5, GridBagConstraints.EAST);

		this.add(gridBag);

		this.setPreferredSize(new Dimension(800, 150));
		this.pack();
		this.setLocationRelativeTo(owner);

		this.templateComboBox.requestFocusInWindow();
		this.enableOrDisableButtonsAndFields();

		this.setVisible(true);
	}

	protected void templateSelected() {
		this.destinationNameTextField.setText((String) this.templateComboBox.getSelectedItem());

		CreateNewSIPFromTemplateDialog.this.enableOrDisableButtonsAndFields();
	}

	protected void selectDestinationFolderButtonClicked() {
		JFileChooser fileChooser = new JFileChooser(this.destinationFolderTextField.getText());
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle(I18N.translate("TitleSelectDestinationFolder"));
		fileChooser.setMultiSelectionEnabled(false);
		int result = fileChooser.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;

		this.destinationFolderTextField.setText(fileChooser.getSelectedFile().getPath());
	}

	protected void selectDestinationIsWorkspaceButtonClicked() {
		this.destinationFolderTextField.setText(launcherView.getSipDirectory());
	}

	protected void goButtonClicked() {
		String templateName = (String) this.templateComboBox.getSelectedItem();
		String destinationFolder = this.destinationFolderTextField.getText();
		String destinationName = this.destinationNameTextField.getText();

		// Don't accept empty template name:
		if (templateName == null) {
			GUIUtil.shake(this);
			this.messageLabel.setText(I18N.translate("MessageTemplateNameIsEmpty"));
			return;
		}

		// Don't accept empty file name:
		if (destinationName.isEmpty()) {
			GUIUtil.shake(this);
			this.messageLabel.setText(I18N.translate("MessageDestinationNameIsEmpty"));
			return;
		}

		if (this.beZIPCheckBox.isSelected()) {
			if (!destinationName.toLowerCase().endsWith(ZIP_EXT))
				destinationName += ZIP_EXT;
		} else {
			if (destinationName.toLowerCase().endsWith(ZIP_EXT))
				destinationName = destinationName.substring(0, destinationName.length() - 4);
		}

		File destinationFile = new File(destinationFolder + "/" + destinationName);

		// Don't accept if a SIP with this name already exists:
		if (destinationFile.exists()) {
			GUIUtil.shake(this);
			this.messageLabel.setText(I18N.translate("MessageSIPExistsAlready"));
			return;
		}

		// Remember the SIP directory:
		launcherView.setLastUsedOpenOrSaveDirectory(destinationFolder);

		this.goButtonWasClicked = true;
		this.close();
	}

	protected void close() {
		this.setVisible(false);
		this.dispose();
	}

	private String[] getTemplateNames() {
		File templateDirectory = new File(launcherView.getTemplateDirectory());
		if (!templateDirectory.exists() || templateDirectory.isFile())
			return new String[] {};

		File[] templates = templateDirectory.listFiles((FilenameFilter) ch.docuteam.tools.file.FileFilter.VisibleAll);
		Vector<String> templateNames = new Vector<String>();
		for (File file : templates)
			templateNames.add(file.getName());

		return templateNames.toArray(new String[] {});
	}

	protected void enableOrDisableButtonsAndFields() {
		this.goButton.setEnabled(
				!this.destinationNameTextField.getText().isEmpty() && this.templateComboBox.getSelectedItem() != null);
	}

}
