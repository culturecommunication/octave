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
import static ch.docuteam.packer.gui.ComponentNames.*;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import ch.docuteam.darc.sa.SubmissionAgreement;
import ch.docuteam.darc.sa.SubmissionAgreement.Overview;
import ch.docuteam.tools.gui.GUIUtil;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class CreateNewSIPDialog extends JDialog {

	private LauncherView launcherView;
	protected JButton selectSourceFileOrFolderButton;
	protected JButton selectDestinationZIPOrFolderButton;
	protected JButton selectDestinationIsWorkspaceButton;
	protected JButton goButton;

	protected JRadioButton selectSIPEmptyRadioButton;
	protected JRadioButton selectSIPFromSourceFileOrFolderRadioButton;

	protected JTextField rootFolderNameTextField;
	protected JTextField sourceFileOrFolderTextField;
	protected JCheckBox deleteSourcesCheckBox;
	protected JTextField destinationFolderTextField;
	protected JTextField destinationNameTextField;
	protected JCheckBox beZIPCheckBox;
	protected JComboBox saComboBox;

	protected JLabel messageLabel;

	protected boolean goButtonWasClicked = false;

	protected CreateNewSIPDialog(LauncherView launcherView) {
		super(launcherView, I18N.translate("TitleCreateNewSIP"), true);
		this.launcherView = launcherView;

		this.setIconImage(getImage(PACKER_PNG));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getRootPane().registerKeyboardAction(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPDialog.this.close();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.selectSIPEmptyRadioButton = new JRadioButton();
		this.selectSIPEmptyRadioButton.setSelected(true);
		this.selectSIPEmptyRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPDialog.this.selectSIPRootRadioButtonClicked();
			}
		});
		this.selectSIPFromSourceFileOrFolderRadioButton = new JRadioButton();
		this.selectSIPFromSourceFileOrFolderRadioButton.setName(SIP_SELECT_FROM_SOURCE_FILE_OR_FOLDER_RADIO_BUTTON);
		this.selectSIPFromSourceFileOrFolderRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPDialog.this.selectSIPRootRadioButtonClicked();
			}
		});

		ButtonGroup radioButtonGroup = new ButtonGroup();
		radioButtonGroup.add(this.selectSIPEmptyRadioButton);
		radioButtonGroup.add(this.selectSIPFromSourceFileOrFolderRadioButton);

		this.rootFolderNameTextField = new JTextField("");
		this.rootFolderNameTextField.setName(SIP_CREATE_NEW_SIP_ROOT_NAME_TEXT_FIELD);
		this.rootFolderNameTextField.setToolTipText(I18N.translate("ToolTipRootFolderName"));
		this.rootFolderNameTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPDialog.this.rootFolderNameTextFieldChanged();
			}
		});
		this.rootFolderNameTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				CreateNewSIPDialog.this.rootFolderNameTextFieldChanged();
			}
		});

		this.selectSourceFileOrFolderButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
		this.selectSourceFileOrFolderButton.setName(SIP_SELECT_SOURCE_FILE_OR_FOLDER_BUTTON);
		this.selectSourceFileOrFolderButton.setToolTipText(I18N.translate("ToolTipSelectSource"));
		this.selectSourceFileOrFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPDialog.this.selectSourceFolderButtonClicked();
			}
		});

		this.sourceFileOrFolderTextField = new JTextField(new File(launcherView.getDataDirectory()).getAbsolutePath());
		this.sourceFileOrFolderTextField.setToolTipText(I18N.translate("ToolTipSourceFileOrFolder"));
		this.sourceFileOrFolderTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPDialog.this.sourceFolderTextFieldChanged();
			}
		});
		this.sourceFileOrFolderTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				CreateNewSIPDialog.this.sourceFolderTextFieldChanged();
			}
		});

		this.selectDestinationZIPOrFolderButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
		this.selectDestinationZIPOrFolderButton.setToolTipText(I18N.translate("ToolTipSelectDestinationFolder"));
		this.selectDestinationZIPOrFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPDialog.this.selectDestinationFolderButtonClicked();
			}
		});

		this.selectDestinationIsWorkspaceButton = new JButton(getImageIcon("Workspace.png"));
		this.selectDestinationIsWorkspaceButton.setName(SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON);
		this.selectDestinationIsWorkspaceButton
				.setToolTipText(I18N.translate("ToolTipSelectDestinationIsWorkspaceFolder"));
		this.selectDestinationIsWorkspaceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPDialog.this.selectDestinationIsWorkspaceButtonClicked();
			}
		});

		this.deleteSourcesCheckBox = new JCheckBox(I18N.translate("LabelDeleteSources"), launcherView.isDeleteSourcesByDefault());
		this.deleteSourcesCheckBox.setToolTipText(I18N.translate("ToolTipDeleteSources"));

		this.destinationFolderTextField = new JTextField(
				new File(launcherView.getLastUsedOpenOrSaveDirectory()).getAbsolutePath());
		this.destinationFolderTextField.setToolTipText(I18N.translate("ToolTipDestinationFolder"));
		this.destinationFolderTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				CreateNewSIPDialog.this.destinationFolderTextField
						.setText(CreateNewSIPDialog.this.destinationFolderTextField.getText().trim());
			}
		});

		this.destinationNameTextField = new JTextField(new File(launcherView.getDataDirectory()).getName());
		this.destinationNameTextField.setName(SIP_DESTINATION_NAME_TEXT_FIELD);
		this.destinationNameTextField.setToolTipText(I18N.translate("ToolTipDestinationName"));
		this.destinationNameTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				CreateNewSIPDialog.this.enableOrDisableButtonsAndFields();
			}
		});
		this.destinationNameTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				CreateNewSIPDialog.this.destinationNameTextField
						.setText(CreateNewSIPDialog.this.destinationNameTextField.getText().trim());
			}
		});

		this.beZIPCheckBox = new JCheckBox(ZIP, launcherView.isNewSIPZippedByDefault());
		this.beZIPCheckBox.setToolTipText(I18N.translate("ToolTipBeZIP"));

		this.saComboBox = new JComboBox(this.readSAOverviewFile().toArray());
		this.saComboBox.setToolTipText(I18N.translate("ToolTipSelectSA"));

		this.messageLabel = new JLabel();

		this.goButton = new JButton(getImageIcon(SAVE_PNG));
		this.goButton.setName(SIP_CREATE_OK_BUTTON);
		this.goButton.setToolTipText(I18N.translate("ToolTipCreateNew"));
		this.goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateNewSIPDialog.this.goButtonClicked();
			}
		});

		GridBagPanel gridBag = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(0, 5, 0, 0));
		gridBag.add(this.selectSIPEmptyRadioButton, 0, 0, GridBagConstraints.EAST);
		gridBag.add(new JLabel(I18N.translate("LabelNewSIPRootName")), 0, 1, GridBagConstraints.EAST);
		gridBag.add(this.rootFolderNameTextField, 0, 4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBag.add(this.selectSIPFromSourceFileOrFolderRadioButton, 1, 0, GridBagConstraints.EAST);
		gridBag.add(new JLabel(I18N.translate("LabelNewSIPSource")), 1, 1, GridBagConstraints.EAST);
		gridBag.add(this.selectSourceFileOrFolderButton, 1, 3);
		gridBag.add(this.sourceFileOrFolderTextField, 1, 1, 4, 6, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, 1, 0);
		gridBag.add(this.deleteSourcesCheckBox, 2, 2, 4, 6, GridBagConstraints.WEST);
		gridBag.add(new JLabel(" "), 3, 2);
		gridBag.add(new JLabel(I18N.translate("LabelNewSIPDestination")), 4, 1, GridBagConstraints.EAST);
		gridBag.add(this.selectDestinationIsWorkspaceButton, 4, 2);
		gridBag.add(this.selectDestinationZIPOrFolderButton, 4, 3);
		gridBag.add(this.destinationFolderTextField, 4, 4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 2,
				0);
		gridBag.add(this.destinationNameTextField, 4, 5, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBag.add(this.beZIPCheckBox, 4, 6);
		gridBag.add(new JLabel(I18N.translate("LabelNewSIPSA")), 5, 1, GridBagConstraints.EAST);
		gridBag.add(this.saComboBox, 5, 5, 4, 6, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBag.add(this.messageLabel, 6, 6, 0, 5, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBag.add(this.goButton, 6, 6, GridBagConstraints.EAST);

		this.add(gridBag);

		this.setPreferredSize(new Dimension(800, 240));
		this.pack();
		this.setLocationRelativeTo(launcherView);

		this.rootFolderNameTextField.requestFocusInWindow();
		this.selectSIPRootRadioButtonClicked();

		this.setVisible(true);
	}

	protected void selectSIPRootRadioButtonClicked() {
		if (this.selectSIPEmptyRadioButton.isSelected())
			this.destinationNameTextField.setText(this.rootFolderNameTextField.getText());
		else
			this.destinationNameTextField.setText(new File(this.sourceFileOrFolderTextField.getText()).getName());

		this.enableOrDisableButtonsAndFields();
	}

	protected void selectSourceFolderButtonClicked() {
		JFileChooser fileChooser = new JFileChooser(this.sourceFileOrFolderTextField.getText());
		fileChooser.setName(SIP_SELECT_SOURCE_FOLDER_FILE_CHOOSER);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setDialogTitle(I18N.translate("TitleSelectSourceFileOrFolder"));
		fileChooser.setMultiSelectionEnabled(false);
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;

		this.sourceFileOrFolderTextField.setText(fileChooser.getSelectedFile().getPath());
		this.sourceFolderTextFieldChanged();
	}

	protected void rootFolderNameTextFieldChanged() {
		this.rootFolderNameTextField.setText(this.rootFolderNameTextField.getText().trim());
		this.destinationNameTextField.setText(this.rootFolderNameTextField.getText());
		this.enableOrDisableButtonsAndFields();
	}

	protected void sourceFolderTextFieldChanged() {
		this.sourceFileOrFolderTextField.setText(this.sourceFileOrFolderTextField.getText().trim());
		this.destinationNameTextField.setText(new File(this.sourceFileOrFolderTextField.getText()).getName());
		this.enableOrDisableButtonsAndFields();
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
		String rootFolderName = this.rootFolderNameTextField.getText();
		String sourceFileOrFolder = this.sourceFileOrFolderTextField.getText();
		String destinationFolder = this.destinationFolderTextField.getText();
		String destinationName = this.destinationNameTextField.getText();

		// Don't accept empty SIP name:
		if (destinationName.isEmpty()) {
			GUIUtil.shake(this);
			this.messageLabel.setText(I18N.translate("MessageDestinationNameIsEmpty"));
			return;
		}

		if (this.selectSIPFromSourceFileOrFolderRadioButton.isSelected()) {
			// Don't accept empty source file or folder:
			if (sourceFileOrFolder.isEmpty()) {
				GUIUtil.shake(this);
				this.messageLabel.setText(I18N.translate("MessageSourceFileOrFolderIsEmpty"));
				return;
			}

			// If the destination folder lies within the source folder, show
			// message and reject:
			if (destinationFolder.contains(sourceFileOrFolder)) {
				GUIUtil.shake(this);
				this.messageLabel.setText(I18N.translate("MessageDestinationIsWithinSource"));
				return;
			}
		} else {
			// Don't accept empty root folder name:
			if (rootFolderName.isEmpty()) {
				GUIUtil.shake(this);
				this.messageLabel.setText(I18N.translate("MessageRootFolderNameIsEmpty"));
				return;
			}
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

		// Remember the Data and SIP directories:
		launcherView.setDataDirectory(sourceFileOrFolder);
		launcherView.setLastUsedOpenOrSaveDirectory(destinationFolder);

		this.goButtonWasClicked = true;
		this.close();
	}

	protected void close() {
		this.setVisible(false);
		this.dispose();
	}

	protected List<Overview> readSAOverviewFile() {
		return SubmissionAgreement.getAllFinalOverviews();
	}

	protected void enableOrDisableButtonsAndFields() {
		if (this.selectSIPEmptyRadioButton.isSelected()) {
			this.rootFolderNameTextField.setEnabled(true);
			this.selectSourceFileOrFolderButton.setEnabled(false);
			this.sourceFileOrFolderTextField.setEnabled(false);

			this.goButton.setEnabled(!(this.destinationNameTextField.getText().isEmpty()
					|| this.rootFolderNameTextField.getText().isEmpty()));
		} else {
			this.rootFolderNameTextField.setEnabled(false);
			this.selectSourceFileOrFolderButton.setEnabled(true);
			this.sourceFileOrFolderTextField.setEnabled(true);

			this.goButton.setEnabled(!(this.destinationNameTextField.getText().isEmpty()
					|| this.sourceFileOrFolderTextField.getText().isEmpty()));
		}
	}

}
