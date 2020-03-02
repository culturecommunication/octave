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

import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class ConvertFilesStartDialog extends JDialog {

	protected boolean goButtonWasClicked = false;

	protected JLabel messageLabel;
	protected JCheckBox keepOriginalCheckBox;
	protected JButton goButton;

	protected ConvertFilesStartDialog(JFrame owner, int filesCount, long totalSize) {
		super(owner, I18N.translate("TitleQuestionConvertFiles"), true);
		
		this.setIconImage(getImage(PACKER_PNG));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getRootPane().registerKeyboardAction(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConvertFilesStartDialog.this.close();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);


		this.keepOriginalCheckBox = new JCheckBox(I18N.translate("LabelKeepOriginal"), ((SIPView)owner).getLauncherView().isMigrateFileKeepOriginal());
		this.keepOriginalCheckBox.setToolTipText(I18N.translate("ToolTipKeepOriginal"));

		this.messageLabel = new JLabel(I18N.translate("QuestionConvertFiles", filesCount, FileUtil.getHumanReadableFileSize(totalSize)));
		
		this.goButton = new JButton(getImageIcon("Go.png"));
		this.goButton.setToolTipText(I18N.translate("ToolTipAppendMigratedFile"));
		this.goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goButtonClicked();
			}
		});


		GridBagPanel gridBagPanel = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(0, 5, 0, 5));

		gridBagPanel.add(this.messageLabel, 0, 0, 0, 2, GridBagConstraints.WEST);
		gridBagPanel.add(this.keepOriginalCheckBox, 2, 0, GridBagConstraints.WEST);
		gridBagPanel.add(this.goButton, 2, 3, GridBagConstraints.EAST);


		this.add(gridBagPanel);

		this.setPreferredSize(new Dimension(300, 100));
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	protected void goButtonClicked() {
		this.goButtonWasClicked = true;
		this.close();
	}

	protected void close() {
		this.setVisible(false);
		this.dispose();
	}
}
