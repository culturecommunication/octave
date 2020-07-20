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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ch.docuteam.darc.util.mets.DuplicateFinder;
import ch.docuteam.darc.util.mets.DuplicateNodeFile;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

/**
 * Shows all duplicates with the same checksum in a table.
 *
 * @author l.dumitrescu
 */
@SuppressWarnings("serial")
public class DuplicatesTable extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JTable table;

    private DuplicatesTableModel duplicatesTableModel;

    public DuplicatesTable(final List<DuplicateNodeFile> duplicateList, final boolean withoutHeader) {
        super();

        final Comparator<DuplicateNodeFile> comparator = new Comparator<DuplicateNodeFile>() {

            @Override
            public int compare(final DuplicateNodeFile f1, final DuplicateNodeFile f2) {
                return (f1.getPath() + f1.getLabel()).compareToIgnoreCase(f2.getPath() + f2.getLabel());
            }
        };
        Collections.sort(duplicateList, comparator);
        duplicatesTableModel = new DuplicatesTableModel(duplicateList);
        table = new JTable(duplicatesTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(120);
        table.getColumnModel().getColumn(2).setMinWidth(300);
        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxCellRenderer());
        if (withoutHeader) {
            table.setTableHeader(null);
        }

        final JCheckBox checkBox = new JCheckBox();
        final TableCellEditor cellEditor = new CheckBoxCellEditor(checkBox);
        table.getColumnModel().getColumn(0).setCellEditor(cellEditor);

        final GridBagPanel gridBag = new GridBagPanel(new BevelBorder(1), new Insets(1, 1, 1, 1));
        final JScrollPane component = new JScrollPane(table);
        component.setPreferredSize(new Dimension(850, 100));

        gridBag.add(component, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 1, 0.1);
        add(gridBag);
    }

    /**
     * Returns a list with the selected items, if all selected returns empty list.
     * 
     * @return the list with the selected DuplicateNodeFile
     */
    List<DuplicateNodeFile> getSelected() {
        final List<DuplicateNodeFile> duplicateNodeFileList = duplicatesTableModel.getDuplicateNodeFileList();
        final List<DuplicateNodeFile> returnList = new ArrayList<DuplicateNodeFile>();

        for (final DuplicateNodeFile duplicate : duplicateNodeFileList) {
            if (duplicate.isSelectedForDeletion()) {
                returnList.add(duplicate);
            }
        }
        if (allSelected(duplicateNodeFileList, returnList)) {
            // do not allow deleting all duplicates
            return Collections.emptyList();
        }
        return returnList;
    }

    void clearSelection() {
        duplicatesTableModel.clearSelection();
        table.repaint();
    }

    /**
     * @param allItems
     * @param selectedItems
     * @return true if all items selected.
     */
    private boolean allSelected(final List<DuplicateNodeFile> allItems, final List<DuplicateNodeFile> selectedItems) {
        return selectedItems.containsAll(allItems);
    }

    class DuplicatesTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        List<DuplicateNodeFile> duplicateList;

        DuplicatesTableModel(final List<DuplicateNodeFile> allDuplicatesList_) {
            duplicateList = allDuplicatesList_;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public int getRowCount() {
            return duplicateList.size();
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            if (column == 1) {
                return duplicateList.get(row).getLabel();
            } else if (column == 2) {
                return duplicateList.get(row).getPath();
            }
            return duplicateList.get(row).isSelectedForDeletion();
        }

        @Override
        public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
            if (columnIndex == 0) {
                final DuplicateNodeFile duplicateNodeFile = getSelectedDuplicateNodeFile();
                final boolean allAreSelected = DuplicateFinder.areAllOtherItemsSelected(duplicatesTableModel
                        .getDuplicateNodeFileList(), duplicateNodeFile);
                if (!allAreSelected) {
                    duplicateNodeFile.setSelectedForDeletion((Boolean) aValue);
                }
            }
        }

        public void clearSelection() {
            for (final DuplicateNodeFile duplicate : duplicateList) {
                duplicate.setSelectedForDeletion(false);
            }
        }

        @Override
        public String getColumnName(final int column) {
            switch (column) {
                case 0:
                    return I18N.translate("DuplicatesTableSelect");
                case 1:
                    return I18N.translate("DuplicatesTableFileName");
                case 2:
                    return I18N.translate("DuplicatesTablePath");
            }
            return "";
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            return column == 0;
        }

        DuplicateNodeFile getSelectedDuplicateNodeFile() {
            final int selectedRow = table.getSelectedRow();
            return duplicateList.get(selectedRow);
        }

        List<DuplicateNodeFile> getDuplicateNodeFileList() {
            return duplicateList;
        }
    }

    class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object selectedForDeletion,
                final boolean isSelected, final boolean hasFocus,
                final int row, final int column) {
            setSelected((Boolean) selectedForDeletion);
            return this;
        }

    }

    class CheckBoxCellEditor extends DefaultCellEditor implements ActionListener {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public CheckBoxCellEditor(final JCheckBox checkBox) {
            super(checkBox);
            super.setClickCountToStart(1);
            checkBox.addActionListener(this);
        }

        @Override
        public JCheckBox getComponent() {
            return (JCheckBox) super.getComponent();
        }

        @Override
        public void actionPerformed(final ActionEvent arg0) {
            final DuplicateNodeFile selectedItem = ((DuplicatesTableModel) table.getModel())
                    .getSelectedDuplicateNodeFile();
            final boolean allAreSelected = DuplicateFinder.areAllOtherItemsSelected(duplicatesTableModel
                    .getDuplicateNodeFileList(), selectedItem);
            if (!allAreSelected) {
                selectedItem.setSelectedForDeletion(true);
            } else {
                // Inform user and deselect last selection
                getComponent().setSelected(false);
                JOptionPane.showMessageDialog(table, I18N.translate("MessageCannotRemoveAllDuplicates"), I18N
                        .translate("MessageTitleCannotRemoveAllDuplicates"), JOptionPane.WARNING_MESSAGE);
            }
        }

    }
}
