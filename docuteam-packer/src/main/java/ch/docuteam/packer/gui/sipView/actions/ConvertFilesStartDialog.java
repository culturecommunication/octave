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
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class ConvertFilesStartDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected boolean goButtonWasClicked = false;

    protected JLabel messageLabel;

    protected JCheckBox keepOriginalCheckBox;

    protected JButton goButton;

    protected ConvertFilesStartDialog(final JFrame owner, final int filesCount, final long totalSize) {
        super(owner, I18N.translate("TitleQuestionConvertFiles"), true);

        setIconImage(getImage(PACKER_PNG));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                ConvertFilesStartDialog.this.close();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        keepOriginalCheckBox = new JCheckBox(I18N.translate("LabelKeepOriginal"), ((SIPView) owner).getLauncherView()
                .isMigrateFileKeepOriginal());
        keepOriginalCheckBox.setToolTipText(I18N.translate("ToolTipKeepOriginal"));

        messageLabel = new JLabel(I18N.translate("QuestionConvertFiles", filesCount, FileUtil
                .getHumanReadableFileSize(totalSize)));

        goButton = new JButton(getImageIcon("Go.png"));
        goButton.setToolTipText(I18N.translate("ToolTipAppendMigratedFile"));
        goButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                goButtonClicked();
            }
        });

        final GridBagPanel gridBagPanel = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(0, 5, 0, 5));

        gridBagPanel.add(messageLabel, 0, 0, 0, 2, GridBagConstraints.WEST);
        gridBagPanel.add(keepOriginalCheckBox, 2, 0, GridBagConstraints.WEST);
        gridBagPanel.add(goButton, 2, 3, GridBagConstraints.EAST);

        this.add(gridBagPanel);

        setPreferredSize(new Dimension(300, 100));
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    protected void goButtonClicked() {
        goButtonWasClicked = true;
        close();
    }

    protected void close() {
        setVisible(false);
        dispose();
    }
}
