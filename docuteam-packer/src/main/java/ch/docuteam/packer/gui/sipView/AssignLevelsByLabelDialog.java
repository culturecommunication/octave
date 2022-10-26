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

package ch.docuteam.packer.gui.sipView;

import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import ch.docuteam.darc.mdconfig.LevelOfDescription;
import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.mets.structmap.NodeFolder;
import ch.docuteam.tools.exception.ExceptionCollector;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class AssignLevelsByLabelDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected boolean goButtonWasClicked = false;

    protected final JComboBox comboBoxSeries;

    protected final JComboBox comboBoxDossier;

    protected final JComboBox comboBoxItem;

    protected final JButton goButton;

    protected AssignLevelsByLabelDialog(final JFrame owner, final NodeAbstract node) {
        super(owner, I18N.translate("TitleAssignLevelsByLabel"), true);

        setIconImage(getImage(PACKER_PNG));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                AssignLevelsByLabelDialog.this.close();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        Vector<LevelOfDescription> levels = new Vector<LevelOfDescription>(10);
        levels.add(null);
        levels.addAll(node.getDocument().getLevels().getAll());
        comboBoxSeries = new JComboBox(levels);
        comboBoxSeries.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                AssignLevelsByLabelDialog.this.enableOrDisableGoButton();
            }
        });

        levels = new Vector<LevelOfDescription>(10);
        levels.add(null);
        levels.addAll(node.getDocument().getLevels().getAll());
        comboBoxDossier = new JComboBox(levels);
        comboBoxDossier.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                AssignLevelsByLabelDialog.this.enableOrDisableGoButton();
            }
        });

        levels = new Vector<LevelOfDescription>(10);
        levels.add(null);
        levels.addAll(node.getDocument().getLevels().getAll());
        comboBoxItem = new JComboBox(levels);
        comboBoxItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                AssignLevelsByLabelDialog.this.enableOrDisableGoButton();
            }
        });

        goButton = new JButton(getImageIcon("Go.png"));
        goButton.setToolTipText(I18N.translate("ToolTipAssignLevelsByLabel"));
        goButton.setEnabled(false);
        goButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                AssignLevelsByLabelDialog.this.goButtonClicked(node);
            }
        });

        final GridBagPanel gridBagPanel = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(2, 5, 0, 5));
        gridBagPanel.add(new JLabel(I18N.translate("LabelFunctionalLevel")), 0, 0, GridBagConstraints.CENTER);
        gridBagPanel.add(new JLabel(I18N.translate("LabelAssignedLevel")), 0, 1, GridBagConstraints.CENTER);
        gridBagPanel.add(new JLabel(I18N.translate("LabelSeries")), 2, 0, GridBagConstraints.CENTER);
        gridBagPanel.add(comboBoxSeries, 2, 1, GridBagConstraints.WEST);
        gridBagPanel.add(new JLabel(I18N.translate("LabelDossier")), 3, 0, GridBagConstraints.CENTER);
        gridBagPanel.add(comboBoxDossier, 3, 1, GridBagConstraints.WEST);
        gridBagPanel.add(new JLabel(I18N.translate("LabelItem")), 4, 0, GridBagConstraints.CENTER);
        gridBagPanel.add(comboBoxItem, 4, 1, GridBagConstraints.WEST);
        gridBagPanel.add(goButton, 5, 1, GridBagConstraints.EAST);
        add(gridBagPanel);

        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    protected void goButtonClicked(final NodeAbstract topNode) {
        if (!areAllComboBoxesSet()) {
            return;
        }

        ExceptionCollector.clear();

        for (final NodeAbstract node : topNode.getWithDescendants()) {
            try {
                // set the node's level according to it's type (file or folder)
                // or label:
                if (node.isFile()) {
                    node.setLevel((LevelOfDescription) comboBoxItem.getSelectedItem());
                } else if (((NodeFolder) node).doesLabelHaveNumericPrefix()) {
                    node.setLevel((LevelOfDescription) comboBoxSeries.getSelectedItem());
                } else {
                    node.setLevel((LevelOfDescription) comboBoxDossier.getSelectedItem());
                }
            } catch (final Exception ex) {
                ch.docuteam.tools.exception.Exception.remember(ex);
            }
        }

        goButtonWasClicked = true;
        close();

        if (!ExceptionCollector.isEmpty()) {
            ExceptionCollector.systemOut();
        }
    }

    protected void close() {
        setVisible(false);
        dispose();
    }

    protected void enableOrDisableGoButton() {
        goButton.setEnabled(areAllComboBoxesSet());
    }

    protected boolean areAllComboBoxesSet() {
        return comboBoxSeries.getSelectedItem() != null && comboBoxDossier.getSelectedItem() != null && comboBoxItem
                .getSelectedItem() != null;
    }

}
