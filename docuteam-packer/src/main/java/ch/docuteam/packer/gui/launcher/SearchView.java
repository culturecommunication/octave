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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import ch.docuteam.darc.exceptions.ZIPDoesNotContainMETSFileException;
import ch.docuteam.darc.mets.Document;
import ch.docuteam.darc.mets.Document.Mode;
import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.gui.SmallPeskyMessageWindow;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.string.Pattern;
import ch.docuteam.tools.translations.I18N;

/**
 * @author denis
 *
 */
public class SearchView extends JFrame
{
	private static final String ScreenTitle = I18N.translate("TitleSearchInWorkspace");
	private static Dimension StartScreenSize = new Dimension(600, 400);

	private static SearchView SearchView = null;

	private LauncherView launcherView;

	private JTable searchResultTable;
	private JLabel rowCountLabel;

	private JTextField fileNameTextField;
	private JTextField metadataTextField;
	private JTextField fileContentTextField;

	private JRadioButton searchModeANDRadioButton;
	private JRadioButton searchModeORRadioButton;
	private ButtonGroup searchModeButtonGroup;

	private Action clearAction;
	private Action searchAction;
	private Action pickAction;

	private SearchView(LauncherView launcherView) {
		super(ScreenTitle);
		this.setIconImage(getImage(PACKER_PNG));

		this.launcherView = launcherView;

		this.searchResultTable = new JTable(new SearchTableModel());
		this.searchResultTable.setAutoCreateRowSorter(true);
		this.searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.searchResultTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() { @Override public void valueChanged(ListSelectionEvent e){ if (!e.getValueIsAdjusting()) SearchView.this.tableSelectionChanged(); }});
		this.searchResultTable.addMouseListener(
				new MouseAdapter() { @Override public void mousePressed(MouseEvent e){ if (e.getClickCount() == 2) SearchView.this.pickButtonClicked(); }});
		this.searchResultTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		this.searchResultTable.getColumnModel().getColumn(1).setPreferredWidth(300);

		this.rowCountLabel = new JLabel();

		this.fileNameTextField = new JTextField();
		this.fileNameTextField.addActionListener(
				new ActionListener(){ @Override public void actionPerformed(ActionEvent e){ SearchView.this.searchButtonClicked(); }});
		this.fileNameTextField.setToolTipText(I18N.translate("ToolTipSearchInWorkspaceFileNameTextField"));

		this.metadataTextField = new JTextField();
		this.metadataTextField.addActionListener(
				new ActionListener(){ @Override public void actionPerformed(ActionEvent e){ SearchView.this.searchButtonClicked(); }});
		this.metadataTextField.setToolTipText(I18N.translate("ToolTipSearchInWorkspaceMetadataTextField"));

		//	This text field is not used yet:
		this.fileContentTextField = new JTextField();
		this.fileContentTextField.addActionListener(
				new ActionListener(){ @Override public void actionPerformed(ActionEvent e){ SearchView.this.searchButtonClicked(); }});
		this.fileContentTextField.setToolTipText(I18N.translate("ToolTipSearchInWorkspaceFileContentTextField"));
		this.fileContentTextField.setEnabled(false);

		this.searchModeANDRadioButton = new JRadioButton(I18N.translate("LabelSearchModeAND"));
		this.searchModeANDRadioButton.setSelected(true);
		this.searchModeANDRadioButton.setToolTipText(I18N.translate("ToolTipSearchModeAND"));

		this.searchModeORRadioButton = new JRadioButton(I18N.translate("LabelSearchModeOR"));
		this.searchModeORRadioButton.setToolTipText(I18N.translate("ToolTipSearchModeOR"));

		this.searchModeButtonGroup = new ButtonGroup();
		this.searchModeButtonGroup.add(this.searchModeANDRadioButton);
		this.searchModeButtonGroup.add(this.searchModeORRadioButton);

		this.searchAction = new AbstractAction(I18N.translate("ActionSearchInWorkspace"), getImageIcon(SEARCH_PNG))
		{	@Override public void actionPerformed(ActionEvent e){ SearchView.this.searchButtonClicked(); }};
		this.searchAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchInWorkspace"));

		this.clearAction = new AbstractAction(I18N.translate("ActionSearchInWorkspaceClearTextField"), getImageIcon(CLEAR_PNG))
		{	@Override public void actionPerformed(ActionEvent e){ SearchView.this.clearButtonClicked(); }};
		this.clearAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchInWorkspaceClearTextField"));

		this.pickAction = new AbstractAction(I18N.translate("ActionSearchInWorkspacePick"), getImageIcon(OPEN_PNG))
		{	@Override public void actionPerformed(ActionEvent e){ SearchView.this.pickButtonClicked(); }};
		this.pickAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchInWorkspacePick"));


		JButton searchButton = new JButton(this.searchAction);
		searchButton.setHideActionText(true);

		JButton clearButton = new JButton(this.clearAction);
		clearButton.setHideActionText(true);

		JButton pickButton = new JButton(this.pickAction);
		pickButton.setHideActionText(true);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(this.searchModeANDRadioButton);
		buttonPanel.add(this.searchModeORRadioButton);
		buttonPanel.add(searchButton);
		buttonPanel.add(clearButton);

		GridBagPanel mainPanel = new GridBagPanel(new Insets(0, 5, 0, 5));
		mainPanel.add(new JLabel(I18N.translate("LabelSearchMetadata")),3,    1,	GridBagConstraints.EAST);
		mainPanel.add(this.metadataTextField,							3,    2,	GridBagConstraints.WEST,		GridBagConstraints.HORIZONTAL, 1, 0);
		mainPanel.add(new JLabel(I18N.translate("LabelSearchSIPName")),	3,    3,	GridBagConstraints.EAST);
		mainPanel.add(this.fileNameTextField,							3,    4,	GridBagConstraints.WEST,		GridBagConstraints.HORIZONTAL, 1, 0);
//		mainPanel.add(new JLabel(I18N.translate("LabelSearchContent")),	3,    5,	GridBagConstraints.EAST);
//		mainPanel.add(this.fileContentTextField,						3,    6,	GridBagConstraints.WEST,		GridBagConstraints.HORIZONTAL, 1, 0);

		mainPanel.add(buttonPanel,										7, 7, 1, 4,	GridBagConstraints.WEST);
		mainPanel.add(new JScrollPane(this.searchResultTable),			8, 8, 1, 4,	GridBagConstraints.CENTER,		GridBagConstraints.BOTH, 1, 1);
		mainPanel.add(this.rowCountLabel,								9, 9, 1, 3,	GridBagConstraints.WEST);
		mainPanel.add(pickButton,										9,    4,	GridBagConstraints.EAST);

		this.add(mainPanel, BorderLayout.CENTER);

		this.setPreferredSize(StartScreenSize);
		this.pack();
		this.setLocation(launcherView.getLocation().x + 50, launcherView.getLocation().y + 50);

		this.enableOrDisableActions();
	}


	public static void open(LauncherView launcherView)
	{
		if (SearchView == null)		SearchView = new SearchView(launcherView);

		SearchView.metadataTextField.requestFocusInWindow();
		SearchView.setVisible(true);
	}


	public static void closeAndDispose()
	{
		if (SearchView != null)
		{
			SearchView.setVisible(false);
			SearchView.dispose();
		}
	}

	private void tableSelectionChanged()
	{
		this.enableOrDisableActions();
	}


	private void searchButtonClicked()
	{
		new SwingWorker<Integer, Object>()
		{
			@Override public Integer doInBackground()
			{
				SearchView.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				SmallPeskyMessageWindow waitWindow = SmallPeskyMessageWindow.openBlocking(SearchView.this, "...");

				try
				{
					String fileNamePattern = SearchView.this.fileNameTextField.getText();
					String searchString = SearchView.this.metadataTextField.getText();

					if (searchString.isEmpty())			return 0;
					if (fileNamePattern.isEmpty())		fileNamePattern = "*";

					final Pattern filePattern = new Pattern(fileNamePattern);
					String[] matchingSIPNames = new File(launcherView.getSipDirectory().toString()).list(new FilenameFilter() {
						@Override
						public boolean accept(File workspaceFolder, String fileName) {
							return !fileName.startsWith(".") && !fileName.endsWith(Document.LOCK_FILE_SUFFIX)
									&& filePattern.match(fileName);
						}
					});

					Document doc = null;
					List<SearchResultElement> searchResult = new ArrayList<SearchResultElement>(matchingSIPNames.length);
					for (String matchingSIPName: matchingSIPNames)
					{
						Logger.debug("Searching in packet: " + matchingSIPName);
						waitWindow.setText(matchingSIPName);

						//	Skip folder that doesn't contain a mets.xml file:
						String sipName = launcherView.getSipDirectory() + File.separator + matchingSIPName;
						if (	 new File(sipName).isDirectory()
							&&	!new File(sipName + File.separator + Document.DEFAULT_METS_FILE_NAME).exists())		continue;

						try
						{
							doc = Document.openReadOnly(sipName, OPERATOR);

							List<NodeAbstract> nodes =
									SearchView.this.searchModeANDRadioButton.isSelected()
										? doc.searchForAllQuoted(searchString)
										: doc.searchForAnyQuoted(searchString);
							if (nodes.isEmpty())		continue;

							for (NodeAbstract node: nodes)		searchResult.add(new SearchResultElement(matchingSIPName, node));
						}
						catch (ZIPDoesNotContainMETSFileException ex)
						{
							//	Ignore this bad SIP and continue the search
						}
						catch (Exception ex)
						{
							Logger.error(ex.getMessage(), ex);
							continue;
						}
						finally
						{
							if (doc != null)
								try { doc.cleanupWorkingCopy(); }
								catch (Exception ex) {}
						}
					}

					((SearchTableModel)SearchView.this.searchResultTable.getModel()).setList(searchResult);
				}
				finally
				{
					waitWindow.close();
					SearchView.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

					//	Don't know why but under Windows the SearchView loses the focus after the search:
					SearchView.this.toFront();
				}

				int searchResultCount = ((SearchTableModel)SearchView.this.searchResultTable.getModel()).getRowCount();
				if (searchResultCount == 0)
				{
					SearchView.this.rowCountLabel.setForeground(Color.RED);
					SearchView.this.rowCountLabel.setText(I18N.translate("MessageSearchNothingFound"));
				}
				else
				{
					SearchView.this.rowCountLabel.setForeground(Color.BLACK);
					SearchView.this.rowCountLabel.setText("" + ((SearchTableModel)SearchView.this.searchResultTable.getModel()).getRowCount());
				}

				SearchView.this.enableOrDisableActions();

				return 0;
			}
		}.execute();
	}


	private void clearButtonClicked()
	{
		this.fileNameTextField.setText("");
		this.metadataTextField.setText("");
		this.fileContentTextField.setText("");

		((SearchTableModel)this.searchResultTable.getModel()).clearList();
		this.rowCountLabel.setText("");

		this.enableOrDisableActions();
	}


	private void pickButtonClicked()
	{
		int selectionIndex = this.searchResultTable.getSelectedRow();
		if (selectionIndex == -1) {
			return;
		}
		int translatedIndexAfterSorting = this.searchResultTable.convertRowIndexToModel(selectionIndex);
		SearchResultElement pickedElement = ((SearchTableModel)this.searchResultTable.getModel()).get(translatedIndexAfterSorting);

		this.launcherView.openSIPInWorkspace(pickedElement.sipName, Mode.ReadWrite, pickedElement.node.getAdmId());
	}


	private void enableOrDisableActions()
	{
		this.pickAction.setEnabled(this.searchResultTable.getSelectedRow() != -1);
	}

	public class SearchTableModel extends AbstractTableModel
	{
		List<SearchResultElement>		searchResult = new ArrayList<SearchResultElement>();


		public void setList(List<SearchResultElement> searchResult)
		{
			this.searchResult = searchResult;
			this.fireTableDataChanged();
		}


		public void clearList()
		{
			this.searchResult.clear();
			this.fireTableDataChanged();
		}


		public SearchResultElement get(int rowIndex)
		{
			return this.searchResult.get(rowIndex);
		}


		@Override
		public int getRowCount()
		{
			return this.searchResult.size();
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:		return I18N.translate("HeaderSearchInWorkspaceResultSIP");
				case 1:		return I18N.translate("HeaderSearchInWorkspaceResultFile");
			}

			return null;
		}


		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			SearchResultElement resultElement = this.searchResult.get(rowIndex);

			switch (columnIndex)
			{
				case 0:		return resultElement.sipName;
				case 1:		return ((NodeAbstract)resultElement.node.getTreePath().getLastPathComponent()).getPathString();
			}

			return null;
		}
	}



	private class SearchResultElement
	{
		private String			sipName;
		private NodeAbstract	node;
//		private TreePath		nodeTreePath;


		public SearchResultElement(String sipName, NodeAbstract node)
		{
			super();
			this.sipName = sipName;
			this.node = node;
//			this.nodeTreePath = node.getTreePath();
		}
	}

}
