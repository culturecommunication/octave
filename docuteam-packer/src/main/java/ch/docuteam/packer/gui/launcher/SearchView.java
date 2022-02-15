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

import static ch.docuteam.packer.gui.PackerConstants.CLEAR_PNG;
import static ch.docuteam.packer.gui.PackerConstants.OPEN_PNG;
import static ch.docuteam.packer.gui.PackerConstants.OPERATOR;
import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SEARCH_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

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
import ch.docuteam.tools.file.exception.FileUtilExceptionListException;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.gui.SmallPeskyMessageWindow;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.string.Pattern;
import ch.docuteam.tools.translations.I18N;

/**
 * @author denis
 */
public class SearchView extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String ScreenTitle = I18N.translate("TitleSearchInWorkspace");

    private static Dimension StartScreenSize = new Dimension(600, 400);

    private static SearchView SearchView = null;

    private final LauncherView launcherView;

    private final JTable searchResultTable;

    private final JLabel rowCountLabel;

    private final JTextField fileNameTextField;

    private final JTextField metadataTextField;

    private final JTextField fileContentTextField;

    private final JRadioButton searchModeANDRadioButton;

    private final JRadioButton searchModeORRadioButton;

    private final ButtonGroup searchModeButtonGroup;

    private final Action clearAction;

    private final Action searchAction;

    private final Action pickAction;

    private SearchView(final LauncherView launcherView) {
        super(ScreenTitle);
        setIconImage(getImage(PACKER_PNG));

        this.launcherView = launcherView;

        searchResultTable = new JTable(new SearchTableModel());
        searchResultTable.setAutoCreateRowSorter(true);
        searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(final ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting()) {
                            SearchView.this.tableSelectionChanged();
                        }
                    }
                });
        searchResultTable.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mousePressed(final MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            SearchView.this.pickButtonClicked();
                        }
                    }
                });
        searchResultTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        searchResultTable.getColumnModel().getColumn(1).setPreferredWidth(300);

        rowCountLabel = new JLabel();

        fileNameTextField = new JTextField();
        fileNameTextField.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        SearchView.this.searchButtonClicked();
                    }
                });
        fileNameTextField.setToolTipText(I18N.translate("ToolTipSearchInWorkspaceFileNameTextField"));

        metadataTextField = new JTextField();
        metadataTextField.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        SearchView.this.searchButtonClicked();
                    }
                });
        metadataTextField.setToolTipText(I18N.translate("ToolTipSearchInWorkspaceMetadataTextField"));

        // This text field is not used yet:
        fileContentTextField = new JTextField();
        fileContentTextField.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        SearchView.this.searchButtonClicked();
                    }
                });
        fileContentTextField.setToolTipText(I18N.translate("ToolTipSearchInWorkspaceFileContentTextField"));
        fileContentTextField.setEnabled(false);

        searchModeANDRadioButton = new JRadioButton(I18N.translate("LabelSearchModeAND"));
        searchModeANDRadioButton.setSelected(true);
        searchModeANDRadioButton.setToolTipText(I18N.translate("ToolTipSearchModeAND"));

        searchModeORRadioButton = new JRadioButton(I18N.translate("LabelSearchModeOR"));
        searchModeORRadioButton.setToolTipText(I18N.translate("ToolTipSearchModeOR"));

        searchModeButtonGroup = new ButtonGroup();
        searchModeButtonGroup.add(searchModeANDRadioButton);
        searchModeButtonGroup.add(searchModeORRadioButton);

        searchAction = new AbstractAction(I18N.translate("ActionSearchInWorkspace"), getImageIcon(SEARCH_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                SearchView.this.searchButtonClicked();
            }
        };
        searchAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchInWorkspace"));

        clearAction = new AbstractAction(I18N.translate("ActionSearchInWorkspaceClearTextField"), getImageIcon(
                CLEAR_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                SearchView.this.clearButtonClicked();
            }
        };
        clearAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchInWorkspaceClearTextField"));

        pickAction = new AbstractAction(I18N.translate("ActionSearchInWorkspacePick"), getImageIcon(OPEN_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                SearchView.this.pickButtonClicked();
            }
        };
        pickAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchInWorkspacePick"));

        final JButton searchButton = new JButton(searchAction);
        searchButton.setHideActionText(true);

        final JButton clearButton = new JButton(clearAction);
        clearButton.setHideActionText(true);

        final JButton pickButton = new JButton(pickAction);
        pickButton.setHideActionText(true);

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(searchModeANDRadioButton);
        buttonPanel.add(searchModeORRadioButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);

        final GridBagPanel mainPanel = new GridBagPanel(new Insets(0, 5, 0, 5));
        mainPanel.add(new JLabel(I18N.translate("LabelSearchMetadata")), 3, 1, GridBagConstraints.EAST);
        mainPanel.add(metadataTextField, 3, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        mainPanel.add(new JLabel(I18N.translate("LabelSearchSIPName")), 3, 3, GridBagConstraints.EAST);
        mainPanel.add(fileNameTextField, 3, 4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
        // mainPanel.add(new JLabel(I18N.translate("LabelSearchContent")), 3, 5, GridBagConstraints.EAST);
        // mainPanel.add(this.fileContentTextField, 3, 6, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1,
        // 0);

        mainPanel.add(buttonPanel, 7, 7, 1, 4, GridBagConstraints.WEST);
        mainPanel.add(new JScrollPane(searchResultTable), 8, 8, 1, 4, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, 1, 1);
        mainPanel.add(rowCountLabel, 9, 9, 1, 3, GridBagConstraints.WEST);
        mainPanel.add(pickButton, 9, 4, GridBagConstraints.EAST);

        this.add(mainPanel, BorderLayout.CENTER);

        setPreferredSize(StartScreenSize);
        pack();
        this.setLocation(launcherView.getLocation().x + 50, launcherView.getLocation().y + 50);

        enableOrDisableActions();
    }

    public static void open(final LauncherView launcherView) {
        if (SearchView == null) {
            SearchView = new SearchView(launcherView);
        }

        SearchView.metadataTextField.requestFocusInWindow();
        SearchView.setVisible(true);
    }

    public static void closeAndDispose() {
        if (SearchView != null) {
            SearchView.setVisible(false);
            SearchView.dispose();
        }
    }

    private void tableSelectionChanged() {
        enableOrDisableActions();
    }

    private void searchButtonClicked() {
        new SwingWorker<Integer, Object>() {

            @Override
            public Integer doInBackground() {
                SearchView.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                final SmallPeskyMessageWindow waitWindow = SmallPeskyMessageWindow.openBlocking(SearchView.this,
                        "...");

                try {
                    String fileNamePattern = fileNameTextField.getText();
                    final String searchString = metadataTextField.getText();

                    if (searchString.isEmpty()) {
                        return 0;
                    }
                    if (fileNamePattern.isEmpty()) {
                        fileNamePattern = "*";
                    }

                    final Pattern filePattern = new Pattern(fileNamePattern);
                    final String[] matchingSIPNames = new File(launcherView.getSipDirectory().toString()).list(
                            new FilenameFilter() {

                                @Override
                                public boolean accept(final File workspaceFolder, final String fileName) {
                                    return !fileName.startsWith(".") && !fileName.endsWith(
                                            Document.LOCK_FILE_SUFFIX) && filePattern.match(fileName);
                                }
                            });

                    Document doc = null;
                    final List<SearchResultElement> searchResult = new ArrayList<SearchResultElement>(
                            matchingSIPNames.length);
                    for (final String matchingSIPName : matchingSIPNames) {
                        Logger.debug("Searching in packet: " + matchingSIPName);
                        waitWindow.setText(matchingSIPName);

                        // Skip folder that doesn't contain a mets.xml file:
                        final String sipName = launcherView.getSipDirectory() + File.separator + matchingSIPName;
                        if (new File(sipName).isDirectory() && !new File(sipName + File.separator +
                                Document.DEFAULT_METS_FILE_NAME).exists()) {
                            continue;
                        }

                        try {
                            doc = Document.openReadOnly(sipName, OPERATOR);

                            final List<NodeAbstract> nodes =
                                    searchModeANDRadioButton.isSelected()
                                            ? doc.searchForAllQuoted(searchString)
                                            : doc.searchForAnyQuoted(searchString);
                            if (nodes.isEmpty()) {
                                continue;
                            }

                            for (final NodeAbstract node : nodes) {
                                searchResult.add(new SearchResultElement(matchingSIPName, node));
                            }
                        } catch (final ZIPDoesNotContainMETSFileException ex) {
                            // Ignore this bad SIP and continue the search
                        } catch (final Exception ex) {
                            Logger.error(ex.getMessage(), ex);
                            continue;
                        } finally {
                            if (doc != null) {
                                try {
                                    doc.cleanupWorkingCopy();
                                } catch (final FileUtilExceptionListException ex) {
                                }
                            }
                        }
                    }

                    ((SearchTableModel) searchResultTable.getModel()).setList(searchResult);
                } finally {
                    waitWindow.close();
                    SearchView.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                    // Don't know why but under Windows the SearchView loses the focus after the search:
                    SearchView.this.toFront();
                }

                final int searchResultCount = ((SearchTableModel) searchResultTable.getModel()).getRowCount();
                if (searchResultCount == 0) {
                    rowCountLabel.setForeground(Color.RED);
                    rowCountLabel.setText(I18N.translate("MessageSearchNothingFound"));
                } else {
                    rowCountLabel.setForeground(Color.BLACK);
                    rowCountLabel.setText("" + ((SearchTableModel) searchResultTable.getModel()).getRowCount());
                }

                SearchView.this.enableOrDisableActions();

                return 0;
            }
        }.execute();
    }

    private void clearButtonClicked() {
        fileNameTextField.setText("");
        metadataTextField.setText("");
        fileContentTextField.setText("");

        ((SearchTableModel) searchResultTable.getModel()).clearList();
        rowCountLabel.setText("");

        enableOrDisableActions();
    }

    private void pickButtonClicked() {
        final int selectionIndex = searchResultTable.getSelectedRow();
        if (selectionIndex == -1) {
            return;
        }
        final int translatedIndexAfterSorting = searchResultTable.convertRowIndexToModel(selectionIndex);
        final SearchResultElement pickedElement = ((SearchTableModel) searchResultTable.getModel()).get(
                translatedIndexAfterSorting);

        launcherView.openSIPInWorkspace(pickedElement.sipName, Mode.ReadWrite, pickedElement.node.getAdmId());
    }

    private void enableOrDisableActions() {
        pickAction.setEnabled(searchResultTable.getSelectedRow() != -1);
    }

    public class SearchTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        List<SearchResultElement> searchResult = new ArrayList<SearchResultElement>();

        public void setList(final List<SearchResultElement> searchResult) {
            this.searchResult = searchResult;
            fireTableDataChanged();
        }

        public void clearList() {
            searchResult.clear();
            fireTableDataChanged();
        }

        public SearchResultElement get(final int rowIndex) {
            return searchResult.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return searchResult.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getColumnName(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return I18N.translate("HeaderSearchInWorkspaceResultSIP");
                case 1:
                    return I18N.translate("HeaderSearchInWorkspaceResultFile");
            }

            return null;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final SearchResultElement resultElement = searchResult.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return resultElement.sipName;
                case 1:
                    return ((NodeAbstract) resultElement.node.getTreePath().getLastPathComponent()).getPathString();
            }

            return null;
        }
    }

    private class SearchResultElement {

        private final String sipName;

        private final NodeAbstract node;
        // private TreePath nodeTreePath;

        public SearchResultElement(final String sipName, final NodeAbstract node) {
            super();
            this.sipName = sipName;
            this.node = node;
            // this.nodeTreePath = node.getTreePath();
        }
    }

}
