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

import static ch.docuteam.packer.gui.PackerConstants.*;
import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class AppendMigratedFileDialog extends JDialog {

	protected boolean goButtonWasClicked = false;

	protected String defaultFileChooserFolder;

	protected JButton chooseDerivedFileButton;
	protected JButton goButton;

	protected JTextField derivedFileTextField;

	protected JCheckBox keepOriginalCheckBox;

	protected JLabel messageLabel;

	protected AppendMigratedFileDialog(JFrame owner, final NodeAbstract node) {
		super(owner, I18N.translate("TitleAppendMigratedFile"), true);

		defaultFileChooserFolder = ((SIPView)owner).getLauncherView().getDataDirectory();

		this.setIconImage(getImage(PACKER_PNG));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getRootPane().registerKeyboardAction(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppendMigratedFileDialog.this.close();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);


		this.chooseDerivedFileButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
		this.chooseDerivedFileButton.setToolTipText(I18N.translate("ToolTipSelectOutcome"));
		this.chooseDerivedFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSourceFolderButtonClicked();
			}
		});


		this.derivedFileTextField = new JTextField();
		this.derivedFileTextField.setToolTipText(I18N.translate("ToolTipOutcome"));
		this.derivedFileTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				derivedFileTextFieldChanged();
			}
		});
		this.derivedFileTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				derivedFileTextFieldChanged();
			}
		});

		this.keepOriginalCheckBox = new JCheckBox(I18N.translate("LabelKeepOriginal"), ((SIPView)owner).getLauncherView().isMigrateFileKeepOriginal());
		this.keepOriginalCheckBox.setToolTipText(I18N.translate("ToolTipKeepOriginal"));

		this.messageLabel = new JLabel();

		this.goButton = new JButton(getImageIcon("Go.png"));
		this.goButton.setToolTipText(I18N.translate("ToolTipAppendMigratedFile"));
		this.goButton.setEnabled(false);
		this.goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goButtonClicked(node);
			}
		});


		GridBagPanel gridBagPanel = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(2, 5, 0, 5));
		gridBagPanel.add(new JLabel(I18N.translate("LabelDerivedFile")), 0, 0, GridBagConstraints.EAST);
		gridBagPanel.add(this.derivedFileTextField, 0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBagPanel.add(this.chooseDerivedFileButton, 0, 2, GridBagConstraints.WEST);
		gridBagPanel.add(this.keepOriginalCheckBox, 1, 1, GridBagConstraints.WEST);
		gridBagPanel.add(this.messageLabel, 2, 2, 0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBagPanel.add(this.goButton, 2, 2, GridBagConstraints.EAST);

		this.add(gridBagPanel);

		this.setPreferredSize(new Dimension(500, 140));
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	protected void selectSourceFolderButtonClicked() {
		JFileChooser fileChooser = new JFileChooser(defaultFileChooserFolder);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle(I18N.translate("TitleSelectMigratedFile"));
		fileChooser.setMultiSelectionEnabled(false);
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;

		this.derivedFileTextField.setText(fileChooser.getSelectedFile().getPath());
		defaultFileChooserFolder = fileChooser.getSelectedFile().getParent();
		this.derivedFileTextFieldChanged();
	}

	protected void derivedFileTextFieldChanged() {
		this.derivedFileTextField.setText(this.derivedFileTextField.getText().trim());
		this.enableOrDisableGoButton();
	}

	protected void enableOrDisableGoButton() {
		this.goButton.setEnabled(!this.derivedFileTextField.getText().isEmpty());
	}

	protected void goButtonClicked(NodeAbstract node) {
		this.goButtonWasClicked = true;
		this.close();
	}

	protected void close() {
		this.setVisible(false);
		this.dispose();
	}
}
