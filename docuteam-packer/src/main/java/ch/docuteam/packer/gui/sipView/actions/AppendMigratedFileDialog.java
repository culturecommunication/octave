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

import static ch.docuteam.packer.gui.PackerConstants.OPEN_FOLDER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

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
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class AppendMigratedFileDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected boolean goButtonWasClicked = false;

    protected String defaultFileChooserFolder;

    protected JButton chooseDerivedFileButton;

    protected JButton goButton;

    protected JTextField derivedFileTextField;

    protected JCheckBox keepOriginalCheckBox;

    protected JLabel messageLabel;

    protected AppendMigratedFileDialog(final JFrame owner, final NodeAbstract node) {
        super(owner, I18N.translate("TitleAppendMigratedFile"), true);

        defaultFileChooserFolder = ((SIPView) owner).getLauncherView().getDataDirectory();

        setIconImage(getImage(PACKER_PNG));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                AppendMigratedFileDialog.this.close();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        chooseDerivedFileButton = new JButton(getImageIcon(OPEN_FOLDER_PNG));
        chooseDerivedFileButton.setToolTipText(I18N.translate("ToolTipSelectOutcome"));
        chooseDerivedFileButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                selectSourceFolderButtonClicked();
            }
        });

        derivedFileTextField = new JTextField();
        derivedFileTextField.setToolTipText(I18N.translate("ToolTipOutcome"));
        derivedFileTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                derivedFileTextFieldChanged();
            }
        });
        derivedFileTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(final FocusEvent e) {
                derivedFileTextFieldChanged();
            }
        });

        keepOriginalCheckBox = new JCheckBox(I18N.translate("LabelKeepOriginal"), ((SIPView) owner).getLauncherView()
                .isMigrateFileKeepOriginal());
        keepOriginalCheckBox.setToolTipText(I18N.translate("ToolTipKeepOriginal"));

        messageLabel = new JLabel();

        goButton = new JButton(getImageIcon("Go.png"));
        goButton.setToolTipText(I18N.translate("ToolTipAppendMigratedFile"));
        goButton.setEnabled(false);
        goButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                goButtonClicked(node);
            }
        });

        final GridBagPanel gridBagPanel = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(2, 5, 0, 5));
        gridBagPanel.add(new JLabel(I18N.translate("LabelDerivedFile")), 0, 0, GridBagConstraints.EAST);
        gridBagPanel.add(derivedFileTextField, 0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBagPanel.add(chooseDerivedFileButton, 0, 2, GridBagConstraints.WEST);
        gridBagPanel.add(keepOriginalCheckBox, 1, 1, GridBagConstraints.WEST);
        gridBagPanel.add(messageLabel, 2, 2, 0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBagPanel.add(goButton, 2, 2, GridBagConstraints.EAST);

        this.add(gridBagPanel);

        setPreferredSize(new Dimension(500, 140));
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    protected void selectSourceFolderButtonClicked() {
        final JFileChooser fileChooser = new JFileChooser(defaultFileChooserFolder);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setDialogTitle(I18N.translate("TitleSelectMigratedFile"));
        fileChooser.setMultiSelectionEnabled(false);
        final int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        derivedFileTextField.setText(fileChooser.getSelectedFile().getPath());
        defaultFileChooserFolder = fileChooser.getSelectedFile().getParent();
        derivedFileTextFieldChanged();
    }

    protected void derivedFileTextFieldChanged() {
        derivedFileTextField.setText(derivedFileTextField.getText().trim());
        enableOrDisableGoButton();
    }

    protected void enableOrDisableGoButton() {
        goButton.setEnabled(!derivedFileTextField.getText().isEmpty());
    }

    protected void goButtonClicked(final NodeAbstract node) {
        goButtonWasClicked = true;
        close();
    }

    protected void close() {
        setVisible(false);
        dispose();
    }
}
