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

import static ch.docuteam.packer.gui.ComponentNames.SIP_TABLE;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import ch.docuteam.darc.mets.Document;
import ch.docuteam.darc.mets.Document.Mode;
import ch.docuteam.packer.gui.FileProperty;
import ch.docuteam.tools.translations.I18N;

public class SIPTable {

    private final LauncherView launcherView;

    private final JTable table;

    private final SIPTableModel sipTableModel;

    private FileProperty selectedSip;
    // Executor fileChangeWatcherExecutor; //outcommented because of COSMOS-396

    public JTable getTable() {
        return table;
    }

    // TODO Not sure if this field should be exposed
    public SIPTableModel getSipTableModel() {
        return sipTableModel;
    }

    public SIPTable(final LauncherView launcherView) {
        this.launcherView = launcherView;
        sipTableModel = new SIPTableModel(launcherView);
        table = new JTable(sipTableModel);
        table.setName(SIP_TABLE);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    listSelectionChanged(e);
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                listSelectionWasClicked(e);
            }
        });
        table.getColumnModel().getColumn(1).setMinWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setMaxWidth(500);
        table.getColumnModel().getColumn(2).setMinWidth(80);
        table.getColumnModel().getColumn(2).setMaxWidth(80);
        // Right-align numbers in "size" and "last modified" column:
        final DefaultTableCellRenderer rightAlignmentRenderer = new DefaultTableCellRenderer();
        rightAlignmentRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightAlignmentRenderer);
        table.getColumnModel().getColumn(3).setMinWidth(150);
        table.getColumnModel().getColumn(3).setMaxWidth(150);
        table.getColumnModel().getColumn(3).setCellRenderer(new DateTableCellRenderer());
        table.getColumnModel().getColumn(4).setMinWidth(40);
        table.getColumnModel().getColumn(4).setMaxWidth(40);
        // Make sipTable sortable:
        table.setAutoCreateRowSorter(true);

        // Table:
        // Use Enter to show underlying sip
        String open = "open.sip.readwrite";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, open);
        table.getActionMap().put(open, launcherView.getOpenSIPInWorkspaceAction());

        open = "open.sip.readwrite.no.file.ops.action";
        enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK);
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, open);
        table.getActionMap().put(open, launcherView.getOpenSIPInWorkspaceReadWriteNoFileOpsAction());

        open = "open.sip.readonly";
        enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK);
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, open);
        table.getActionMap().put(open, launcherView.getOpenSIPInWorkspaceReadOnlyAction());

        // fileChangeWatcherExecutor = new ScheduledThreadPoolExecutor(5);
        // registerFileChangeWatchService();
    }

    // TODO a cleaner solution might be possible with some kind of signaling to
    // the thread when the workspace changes, or the executor implementation
    // might also be not optimal and the while (true) too
    /*
     * public void registerFileChangeWatchService() { Runnable fileWatcherThread = new Runnable() {
     * @Override public void run() { try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
     * WatchKey key = FileSystems.getDefault().getPath(launcherView.getSipDirectory()).register(watcher, ENTRY_CREATE,
     * ENTRY_DELETE); while (true) { key = watcher.take(); for (WatchEvent<?> event : key.pollEvents()) { if
     * (event.kind() == ENTRY_MODIFY || event.kind() == ENTRY_CREATE || event.kind() == ENTRY_DELETE) { Path changed =
     * (Path) event.context(); if (!changed.toString().endsWith("lock")) { // rereadSIPTable(); } } } key.reset(); } }
     * catch (IOException | InterruptedException e) { Logger.error(e.getMessage(), e); } } };
     * fileChangeWatcherExecutor.execute(fileWatcherThread); }
     */

    protected void listSelectionChanged(final ListSelectionEvent e) {
        final int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            selectedSip = null;
            launcherView.setSelectedSIP(null);
        } else {
            if (selectedRow < sipTableModel.getRowCount()) {
                selectedSip = sipTableModel.getSipAtIndex(table.convertRowIndexToModel(selectedRow));
                launcherView.setSelectedSIP(selectedSip);
            } else {
                selectedSip = null;
                launcherView.setSelectedSIP(null);
            }
        }
        launcherView.getFooterTextField().setText(selectedSip == null ? "" : selectedSip.getFile().getPath());
        launcherView.enableOrDisableActions();
    }

    public void rereadSIPTable() {
        // TODO instead of rereading workspaces on demand, the workspace should be watched and updated if necessary.
        sipTableModel.readDirContent();
        sipTableModel.fireTableDataChanged();
        sipTableModel.updateSizeAndLockedByColumns();
        final int selectedSipNewRow = sipTableModel.getRowIndexOfSip(selectedSip);
        if (selectedSipNewRow > -1) {
            try {
                // reselect the row that was selected prior to rereading the table
                table.getSelectionModel().setSelectionInterval(selectedSipNewRow, selectedSipNewRow);
            } catch (final Exception ex) {
                // TODO something has to happen here, although it might be not necessary
            }
        }
    }

    protected void listSelectionWasClicked(final MouseEvent e) {
        if (e.getClickCount() == 2) {
            // Double-Click:
            if (Document.isLockedBySomebodyElse(selectedSip.getFile())) {
                // If it is locked, ask if to really open the view:
                if (JOptionPane.showConfirmDialog(launcherView, I18N.translate("LabelIsLocked") + Document
                        .lockedByWhom(selectedSip.getFile()), I18N.translate("TitleOpenSIP"),
                        JOptionPane.OK_CANCEL_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            if ((e.getModifiersEx() & (InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) == 0) {
                launcherView.openSelectedSIPInWorkspace(Mode.ReadWrite);
            } else if ((e.getModifiersEx() & (InputEvent.ALT_DOWN_MASK |
                    InputEvent.SHIFT_DOWN_MASK)) == InputEvent.ALT_DOWN_MASK) {
                launcherView.openSelectedSIPInWorkspace(Mode.ReadWriteNoFileOps);
            } else if ((e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK |
                    InputEvent.ALT_DOWN_MASK)) == InputEvent.SHIFT_DOWN_MASK) {
                launcherView.openSelectedSIPInWorkspace(Mode.ReadOnly);
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            // Right-Click:

            // Find out which row was right-clicked:
            final int rowNumber = table.rowAtPoint(e.getPoint());

            // Select this row:
            table.getSelectionModel().setSelectionInterval(rowNumber, rowNumber);

            // Show popup menu:
            launcherView.getPopupMenu().show(table, e.getX(), e.getY());
        }
        // else: ignore. Single clicks are handled in listSelectionChanged().
    }

}
