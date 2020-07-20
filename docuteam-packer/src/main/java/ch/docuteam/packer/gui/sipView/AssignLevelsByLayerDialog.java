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

package ch.docuteam.packer.gui.sipView;

import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
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
import ch.docuteam.tools.exception.ExceptionCollector;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class AssignLevelsByLayerDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected boolean goButtonWasClicked = false;

    protected final List<JComboBox> comboBoxes;

    protected final JButton goButton;

    protected AssignLevelsByLayerDialog(final JFrame owner, final NodeAbstract node) {
        super(owner, I18N.translate("TitleAssignLevelsByLayer"), true);

        setIconImage(getImage(PACKER_PNG));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                AssignLevelsByLayerDialog.this.close();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        goButton = new JButton(getImageIcon("Go.png"));
        goButton.setToolTipText(I18N.translate("ToolTipAssignLevelsByLayer"));
        goButton.setEnabled(false);
        goButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                AssignLevelsByLayerDialog.this.goButtonClicked(node);
            }
        });

        // The treeDepth is defined as 0 for a leaf and max(children.treeDepth)
        // + 1 otherwise.
        // Here I create one row for each level, so the number of rows is the
        // treeDepth + 1.
        final int treeDepth = node.getTreeDepth() + 1;
        comboBoxes = new Vector<JComboBox>(treeDepth);

        final GridBagPanel gridBagPanel = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(2, 5, 0, 5));
        gridBagPanel.add(new JLabel(I18N.translate("LabelTreeLevel")), 0, 0, GridBagConstraints.CENTER);
        gridBagPanel.add(new JLabel(I18N.translate("LabelAssignedLevel")), 0, 1, GridBagConstraints.CENTER);

        int i;
        for (i = 1; i <= treeDepth; i++) {
            final Vector<LevelOfDescription> levels = new Vector<LevelOfDescription>(10);
            levels.add(null);
            levels.addAll(node.getDocument().getLevels().getAll());
            final JComboBox comboBox = new JComboBox(levels);
            comboBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    AssignLevelsByLayerDialog.this.enableOrDisableGoButton();
                }
            });
            comboBoxes.add(comboBox);

            gridBagPanel.add(new JLabel("" + i), i + 1, 0, GridBagConstraints.CENTER);
            gridBagPanel.add(comboBox, i + 1, 1, GridBagConstraints.WEST);
        }

        gridBagPanel.add(goButton, i + 1, 1, GridBagConstraints.EAST);
        this.add(gridBagPanel);

        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    protected void goButtonClicked(final NodeAbstract topNode) {
        ExceptionCollector.clear();

        final int topNodeDepth = topNode.getDepth();

        for (final NodeAbstract node : topNode.getWithDescendants()) {
            final int relativeNodeDepth = node.getDepth() - topNodeDepth;
            final LevelOfDescription selectedLevel = (LevelOfDescription) comboBoxes.get(relativeNodeDepth)
                    .getSelectedItem();
            if (selectedLevel == null) {
                continue;
            }

            try {
                node.setLevel(selectedLevel);
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
        goButton.setEnabled(!areAllComboBoxesEmpty());
    }

    protected boolean areAllComboBoxesEmpty() {
        for (final JComboBox comboBox : comboBoxes) {
            if (comboBox.getSelectedItem() != null) {
                return false;
            }
        }

        return true;
    }

}
