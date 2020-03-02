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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import ch.docuteam.darc.mets.structmap.NodeFile;
import ch.docuteam.darc.sa.SubmissionAgreement;
import ch.docuteam.darc.sa.SubmissionAgreement.Overview;
import ch.docuteam.packer.gui.sipView.tableModel.BadFilesTableModel;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

public class TestOrAssignSADialog extends JDialog {

	protected JComboBox saComboBox;
	protected JButton setSAButton;
	protected JButton deleteAllButton;
	protected JButton markAllButton;
	protected JLabel badFilesCountLabel;

	protected JTable badFilesTable;

	protected Overview selectedSAOverview;
	protected boolean setSAButtonClicked = false;

	protected SIPView sipView;

	protected TestOrAssignSADialog(SIPView sipView) {
		super(sipView, I18N.translate("TitleTestOrAssignSADialog"), true);
		this.sipView = sipView;
		setSAButtonClicked = false;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(getImage(PACKER_PNG));

		saComboBox = new JComboBox(getAllFinalSAOverviews().toArray());
		saComboBox.setSelectedIndex(0);
		saComboBox.setToolTipText(I18N.translate("ToolTipSelectSA"));
		saComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saSelected();
			}
		});

		setSAButton = new JButton(getImageIcon("Go.png"));
		setSAButton.setToolTipText(I18N.translate("ToolTipSetSA"));
		setSAButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSAButtonClicked();
			}
		});

		deleteAllButton = new JButton(getImageIcon("Delete.png"));
		deleteAllButton.setToolTipText(I18N.translate("ToolTipDeleteAllBadFiles"));
		deleteAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteAllButtonClicked();
			}
		});

		markAllButton = new JButton(getImageIcon("Mark.png"));
		markAllButton.setToolTipText(I18N.translate("ToolTipMarkAllBadFiles"));
		markAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				markAllButtonClicked();
			}
		});

		badFilesTable = new JTable(new BadFilesTableModel());
		badFilesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		badFilesTable.getColumnModel().getColumn(1).setMaxWidth(30);

		badFilesCountLabel = new JLabel();

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(setSAButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(markAllButton);
		buttonBox.add(deleteAllButton);

		GridBagPanel gridBag = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(5, 5, 5, 5));
		gridBag.add(new JLabel(I18N.translate("LabelSelectSA")), 1, 1, 1, 2, GridBagConstraints.WEST);
		gridBag.add(saComboBox, 2, 2, 1, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBag.add(new JLabel(I18N.translate("LabelBadFilesTable")), 3, 3, 1, 2, GridBagConstraints.WEST);
		gridBag.add(new JScrollPane(badFilesTable), 4, 4, 1, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 1,
				1);
		gridBag.add(badFilesCountLabel, 5, 1, GridBagConstraints.WEST);
		gridBag.add(buttonBox, 5, 2, GridBagConstraints.EAST);

		add(gridBag);

		// The ESC key closes this View:
		gridBag.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				"Close");
		gridBag.getActionMap().put("Close", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		setPreferredSize(new Dimension(600, 400));
		pack();

		setLocationRelativeTo(sipView);

		// Select the 1st SAOverview in the comboBox, which is the one used in
		// my document (or null):
		saComboBox.setSelectedIndex(0);
		setVisible(true);
	}

	protected void saSelected() {
		selectedSAOverview = (Overview) saComboBox.getSelectedItem();
		if (selectedSAOverview == null) {
			((BadFilesTableModel) badFilesTable.getModel()).getList().clear();
			badFilesCountLabel.setText("0");
			enableOrDisableButtonsAndFields();
			return;
		}

		SubmissionAgreement selectedSA = null;
		try {
			selectedSA = selectedSAOverview.getSubmissionAgreement();
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
		}

		if (selectedSA == null) {
			((BadFilesTableModel) badFilesTable.getModel()).getList().clear();
			badFilesCountLabel.setText("0");
			enableOrDisableButtonsAndFields();
			return;
		}

		List<NodeFile> badFiles = sipView.getDocument().filesNotAllowedBySubmissionAgreement(selectedSA,
				selectedSAOverview.dssId);
		((BadFilesTableModel) badFilesTable.getModel()).setList(badFiles);
		badFilesCountLabel.setText("" + badFiles.size());

		enableOrDisableButtonsAndFields();
	}

	protected void setSAButtonClicked() {
		if (selectedSAOverview == null)
			return;

		List<NodeFile> badFiles = null;
		try {
			badFiles = sipView.getDocument().setSubmissionAgreement(selectedSAOverview.saId, selectedSAOverview.dssId);
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
		}

		if (badFiles == null) {
			((BadFilesTableModel) badFilesTable.getModel()).getList().clear();
			badFilesCountLabel.setText("0");
			enableOrDisableButtonsAndFields();
			return;
		}

		((BadFilesTableModel) badFilesTable.getModel()).setList(badFiles);
		badFilesCountLabel.setText("" + badFiles.size());
		setSAButtonClicked = true;

		enableOrDisableButtonsAndFields();
	}

	protected void deleteAllButtonClicked() {
		for (NodeFile nodeFile : ((BadFilesTableModel) badFilesTable.getModel()).getList())
			try {
				nodeFile.delete();
			} catch (Exception ex) {
				Logger.error(ex.getMessage(), ex);
			}

		((BadFilesTableModel) badFilesTable.getModel()).clearList();
		badFilesCountLabel.setText("0");

		enableOrDisableButtonsAndFields();
	}

	protected void markAllButtonClicked() {
		for (NodeFile nodeFile : ((BadFilesTableModel) badFilesTable.getModel()).getList())
			nodeFile.setIsNotAllowedBySA();

		((BadFilesTableModel) badFilesTable.getModel()).fireTableDataChanged();
	}

	protected void close() {
		setVisible(false);
		dispose();
	}

	protected List<Overview> getAllFinalSAOverviews() {
		List<Overview> saOverviewList = SubmissionAgreement.getAllFinalOverviews();
		saOverviewList.add(0, null);

		// Search the SAOverview that matches the SA used in my document. If
		// found, move it to the begin of the list:
		for (Overview overview : saOverviewList)
			if (overview != null && overview.saId.equals(sipView.getDocument().getSAId())
					&& overview.dssId.equals(sipView.getDocument().getDSSId())) {
				saOverviewList.remove(overview);
				saOverviewList.add(0, overview);
				break;
			}

		return saOverviewList;
	}

	protected void enableOrDisableButtonsAndFields() {
		if (selectedSAOverview == null) {
			setSAButton.setEnabled(false);
			markAllButton.setEnabled(false);
			deleteAllButton.setEnabled(false);
			return;
		}

		setSAButton.setEnabled(true);

		if (setSAButtonClicked) {
			markAllButton.setEnabled(true);
			deleteAllButton.setEnabled(true);
		} else {
			markAllButton.setEnabled(false);
			deleteAllButton.setEnabled(false);
		}
	}

 
}
