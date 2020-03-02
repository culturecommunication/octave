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

import static ch.docuteam.packer.gui.PackerConstants.*;

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
import javax.swing.border.EmptyBorder;

import ch.docuteam.darc.mdconfig.LevelOfDescription;
import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.tools.exception.ExceptionCollector;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

public class AssignLevelsByLayerDialog extends JDialog {

	protected boolean goButtonWasClicked = false;

	protected final List<JComboBox> comboBoxes;
	protected final JButton goButton;

	protected AssignLevelsByLayerDialog(JFrame owner, final NodeAbstract node) {
		super(owner, I18N.translate("TitleAssignLevelsByLayer"), true);

		this.setIconImage(getImage(PACKER_PNG));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getRootPane().registerKeyboardAction(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AssignLevelsByLayerDialog.this.close();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.goButton = new JButton(getImageIcon("Go.png"));
		this.goButton.setToolTipText(I18N.translate("ToolTipAssignLevelsByLayer"));
		this.goButton.setEnabled(false);
		this.goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AssignLevelsByLayerDialog.this.goButtonClicked(node);
			}
		});

		// The treeDepth is defined as 0 for a leaf and max(children.treeDepth)
		// + 1 otherwise.
		// Here I create one row for each level, so the number of rows is the
		// treeDepth + 1.
		int treeDepth = node.getTreeDepth() + 1;
		this.comboBoxes = new Vector<JComboBox>(treeDepth);

		GridBagPanel gridBagPanel = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(2, 5, 0, 5));
		gridBagPanel.add(new JLabel(I18N.translate("LabelTreeLevel")), 0, 0, GridBagConstraints.CENTER);
		gridBagPanel.add(new JLabel(I18N.translate("LabelAssignedLevel")), 0, 1, GridBagConstraints.CENTER);

		int i;
		for (i = 1; i <= treeDepth; i++) {
			Vector<LevelOfDescription> levels = new Vector<LevelOfDescription>(10);
			levels.add(null);
			levels.addAll(node.getDocument().getLevels().getAll());
			JComboBox comboBox = new JComboBox(levels);
			comboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AssignLevelsByLayerDialog.this.enableOrDisableGoButton();
				}
			});
			this.comboBoxes.add(comboBox);

			gridBagPanel.add(new JLabel("" + i), i + 1, 0, GridBagConstraints.CENTER);
			gridBagPanel.add(comboBox, i + 1, 1, GridBagConstraints.WEST);
		}

		gridBagPanel.add(this.goButton, i + 1, 1, GridBagConstraints.EAST);
		this.add(gridBagPanel);

		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	protected void goButtonClicked(NodeAbstract topNode) {
		ExceptionCollector.clear();

		int topNodeDepth = topNode.getDepth();

		for (NodeAbstract node : topNode.getWithDescendants()) {
			int relativeNodeDepth = node.getDepth() - topNodeDepth;
			LevelOfDescription selectedLevel = (LevelOfDescription) this.comboBoxes.get(relativeNodeDepth)
					.getSelectedItem();
			if (selectedLevel == null)
				continue;

			try {
				node.setLevel(selectedLevel);
			} catch (Exception ex) {
				ch.docuteam.tools.exception.Exception.remember(ex);
			}
		}

		this.goButtonWasClicked = true;
		this.close();

		if (!ExceptionCollector.isEmpty())
			ExceptionCollector.systemOut();
	}

	protected void close() {
		this.setVisible(false);
		this.dispose();
	}

	protected void enableOrDisableGoButton() {
		this.goButton.setEnabled(!this.areAllComboBoxesEmpty());
	}

	protected boolean areAllComboBoxesEmpty() {
		for (JComboBox comboBox : this.comboBoxes)
			if (comboBox.getSelectedItem() != null)
				return false;

		return true;
	}

}
