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

import static ch.docuteam.packer.gui.ComponentNames.DuplicatesTableDialog_REMOVE;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.util.mets.DuplicateFinder;
import ch.docuteam.darc.util.mets.DuplicateNodeFile;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.translations.I18N;

import com.google.common.collect.ListMultimap;

/**
 * Shows duplicates with the same checksum in a table, with different checksums in different tables. Remove
 * duplicates.
 *
 * @author l.dumitrescu
 */
@SuppressWarnings("serial")
public class DuplicatesTableDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final ListMultimap<String, DuplicateNodeFile> duplicatesListMultimap;

    private final List<DuplicatesTable> tableList = new ArrayList<DuplicatesTable>();

    private final JButton okButton;

    private final JButton cancelButton;

    private final JButton clearSelectionButton;

    private final SIPView sipView;

    public DuplicatesTableDialog(final NodeAbstract nodeAbstract, final SIPView sipView) {
        super(sipView, I18N.translate("TitleRemoveDuplicatesDialog"), true);

        this.sipView = sipView;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        duplicatesListMultimap = DuplicateFinder.getDuplicates(nodeAbstract);

        okButton = new JButton(I18N.translate("ButtonDuplicateSelectionRemove"));
        okButton.setToolTipText(I18N.translate("ToolTipDuplicateSelectionRemove"));
        okButton.setName(DuplicatesTableDialog_REMOVE);
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                removeDuplicateButtonClicked();
            }
        });

        // cancelButton
        cancelButton = new JButton(I18N.translate("ButtonCancel"));
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                cancelButtonClicked();
            }
        });

        clearSelectionButton = new JButton(I18N.translate("ButtonDuplicateSelectionClear"));
        clearSelectionButton.setToolTipText(I18N.translate("ToolTipDuplicateSelectionClear"));
        clearSelectionButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                clearButtonClicked();
            }
        });

        final GridBagPanel gridBag = new GridBagPanel(new EmptyBorder(1, 1, 1, 1), new Insets(5, 5, 5, 5));
        gridBag.add(new JLabel(I18N.translate("LabelSelectDuplicateForRemoval")), 1, 1, 1, 4,
                GridBagConstraints.WEST);

        final GridBagPanel innerGridBag = new GridBagPanel(new BevelBorder(1), new Insets(1, 1, 1, 1));

        // for each checksum add a table
        final Iterator<String> keyIterator = duplicatesListMultimap.keySet().iterator();
        int rowIndex = 1;
        while (keyIterator.hasNext()) {
            final String nextKey = keyIterator.next();
            final List<DuplicateNodeFile> valueList = duplicatesListMultimap.get(nextKey);
            final DuplicatesTable currentTable = new DuplicatesTable(valueList, false);
            tableList.add(currentTable);
            innerGridBag.add(currentTable, rowIndex, rowIndex, 1, 1, GridBagConstraints.NORTH,
                    GridBagConstraints.NONE, 1, 0.1);
            rowIndex++;
        }
        gridBag.add(new JScrollPane(innerGridBag), 2, 2, 1, 4, GridBagConstraints.NORTH, GridBagConstraints.BOTH, 1,
                0.1);

        final Box buttonBox = new Box(BoxLayout.X_AXIS);
        buttonBox.add(clearSelectionButton);
        buttonBox.add(Box.createHorizontalStrut(50));
        buttonBox.add(okButton);
        buttonBox.add(cancelButton);
        gridBag.add(buttonBox, 3, 3, 1, 4, GridBagConstraints.WEST);
        add(gridBag);

        setPreferredSize(new Dimension(900, 600));
        pack();
        setLocationRelativeTo(sipView);
        setVisible(true);
    }

    /**
     * Iterates through all tables to get the selected duplicates and deletes the duplicates.
     */
    private void removeDuplicateButtonClicked() {
        boolean anythingDeleted = false;
        for (final DuplicatesTable table : tableList) {
            final List<DuplicateNodeFile> duplicateList = table.getSelected();
            anythingDeleted |= DuplicateFinder.deleteAsDuplicates(duplicatesListMultimap, duplicateList);
        }
        // update GUI
        dispose();
        if (anythingDeleted) {
            sipView.updateTree();
        }
    }

    private void cancelButtonClicked() {
        // close dialog
        dispose();
    }

    private void clearButtonClicked() {
        for (final DuplicatesTable table : tableList) {
            table.clearSelection();
        }
    }

}
