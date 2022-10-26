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

import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SAVE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.ZIP;
import static ch.docuteam.packer.gui.PackerConstants.ZIP_EXT;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.Component;
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
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.gui.MultiLineLabel;
import ch.docuteam.tools.translations.I18N;

public class CopySIPDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected JTextField copyNameTextField;

    protected JCheckBox beZIPCheckBox;

    protected JButton goButton;

    protected boolean goButtonWasClicked = false;

    protected static String open(final LauncherView owner, final String message, final String title,
            final String textFieldContent) {
        final CopySIPDialog dialog = new CopySIPDialog(owner, message, title, textFieldContent);

        if (!dialog.goButtonWasClicked) {
            return null;
        }

        String name = dialog.copyNameTextField.getText();
        if (dialog.beZIPCheckBox.isSelected()) {
            if (!name.toLowerCase().endsWith(ZIP_EXT)) {
                name += ZIP_EXT;
            }
        } else {
            if (name.toLowerCase().endsWith(ZIP_EXT)) {
                name = FileUtil.asFilePathWithoutExtension(name);
            }
        }

        return name;
    }

    private CopySIPDialog(final LauncherView owner, final String message, final String title,
            final String textFieldContent) {
        super(owner, title, true);

        setIconImage(getImage(PACKER_PNG));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                CopySIPDialog.this.close();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        copyNameTextField = new JTextField(textFieldContent);
        copyNameTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CopySIPDialog.this.goButtonWasClicked();
            }
        });

        beZIPCheckBox = new JCheckBox(ZIP, owner.isNewSIPZippedByDefault());
        beZIPCheckBox.setToolTipText(I18N.translate("ToolTipBeZIP"));

        goButton = new JButton(getImageIcon(SAVE_PNG));
        goButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                CopySIPDialog.this.goButtonWasClicked();
            }
        });

        new JLabel(getImageIcon(PACKER_PNG));
        final GridBagPanel gridBag = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(0, 5, 0, 5));
        gridBag.add(new JLabel(getImageIcon(PACKER_PNG)), 0, 2, 0, 0, GridBagConstraints.CENTER);
        gridBag.add(new MultiLineLabel(message, Component.LEFT_ALIGNMENT), 1, 1, GridBagConstraints.WEST);
        gridBag.add(copyNameTextField, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        gridBag.add(beZIPCheckBox, 2, 2);
        gridBag.add(goButton, 3, 2, GridBagConstraints.EAST);

        this.add(gridBag);

        setPreferredSize(new Dimension(450, 150));
        pack();
        setLocationRelativeTo(owner);

        copyNameTextField.requestFocusInWindow();

        setVisible(true);
    }

    private void goButtonWasClicked() {
        goButtonWasClicked = true;
        close();
    }

    private void close() {
        setVisible(false);
        dispose();
    }

}
