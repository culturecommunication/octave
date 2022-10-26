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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.mets.structmap.NodeAbstract.SubmitStatus;
import ch.docuteam.darc.mets.structmap.NodeFolder;
import ch.docuteam.packer.gui.sipView.tableModel.TreeTableModel;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableNode;

public class TreeTableTransferHandler extends TransferHandler {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final SIPView sipView;

    public TreeTableTransferHandler(final SIPView sipView) {
        super();
        this.sipView = sipView;
    }

    /**
     * Allow move only
     */
    @Override
    public int getSourceActions(final JComponent c) {
        // MOVE/COPY/COPY_OR_MOVE/LINK/NONE
        return COPY_OR_MOVE;
    }

    /**
     * Create an instance of our own Transferable out of the drag source component.
     */
    @Override
    protected Transferable createTransferable(final JComponent c) {
        return new TreeNodeListTransferable((JXTreeTable) c);
    }

    /**
     * Only on drop and insert, not on paste. Allow only our own data flavor and file lists. Allow dragging multiple
     * items. Check source nodes and target node. Allows insert between folders/files. Allows drop on folder.
     */
    @Override
    public boolean canImport(final TransferSupport transferSupport) {
        // If this document doesn't allow file operations, disable D&D:
        if (!sipView.getDocument().areFileOperationsAllowed()) {
            return false;
        }

        // Accept only drop:
        if (!transferSupport.isDrop()) {
            return false;
        }

        // Accept only file lists or our own data flavor:
        if (!(transferSupport.isDataFlavorSupported(TreeNodeListTransferable.DocuteamPackerTreeNodeListDataFlavor) ||
                transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor))) {
            return false;
        }

        // Check target to allow DnD:
        final int targetIndex = ((JTable.DropLocation) transferSupport.getDropLocation()).getRow();

        // This happens when the drag target is outside of the table:
        if (targetIndex == -1) {
            return false;
        }

        // Target node:
        final JXTreeTable treeTableNode = (JXTreeTable) transferSupport.getComponent();
        final TreePath pathForRow = treeTableNode.getPathForRow(targetIndex);
        if (pathForRow == null) {
            return false;
        }
        final NodeAbstract targetNode = (NodeAbstract) pathForRow.getLastPathComponent();

        // Don't allow drop if target or any of its predecessors is not readable
        // or not writable:
        if (!targetNode.fileExists() || !targetNode.canRead() || !targetNode.canWrite() || targetNode
                .hasPredecessorNotReadableOrWritableByCurrentUser()) {
            return false;
        }

        // Now distinguish the different data flavors:
        if (transferSupport.isDataFlavorSupported(TreeNodeListTransferable.DocuteamPackerTreeNodeListDataFlavor)) {
            // Moving nodes within packer:

            final String targetSIP = sipView.getDocument().getSIPFolder();
            TreeNodeListTransferable transferData = null;
            String sourceSIP = "";
            try {
                transferData = (TreeNodeListTransferable) transferSupport.getTransferable()
                        .getTransferData(TreeNodeListTransferable.DocuteamPackerTreeNodeListDataFlavor);
                sourceSIP = transferData.getSipPath();
            } catch (final UnsupportedFlavorException ex) {
                Logger.debug(ex.getMessage(), ex);
                return false;
            } catch (final IOException ex) {
                Logger.debug(ex.getMessage(), ex);
                return false;
            }

            // Don't allow dragging between different SIPs:
            if (!targetSIP.equals(sourceSIP)) {
                return false;
            }

            // Source parent node:
            final TreeTableNode sourceParent = transferData.getDraggedNodes().get(0).getParent();

            // Don't allow dragging onto own parent:
            if (sourceParent == targetNode) {
                return false;
            }

            // Loop through all source nodes and test each of them:
            for (final NodeAbstract node : transferData.getDraggedNodes()) {
                // Don't allow root as drag source (root can not be moved):
                if (node.isRoot()) {
                    return false;
                }

                // Make sure that all selected source nodes have the same
                // parent. if not, reject the drag:
                if (node.getParent() != sourceParent) {
                    return false;
                }

                // Don't allow dragging onto one of the source nodes:
                if (node == targetNode) {
                    return false;
                }

                // Don't allow dragging onto a descendant of one of the source
                // nodes:
                if (node.isFolder() && ((NodeFolder) node).isPredecessorOf(targetNode)) {
                    return false;
                }

                // Don't allow dragging if the node is being/has been submitted:
                if (node.getSubmitStatus().equals(SubmitStatus.SubmitRequestPending) || node.getSubmitStatus().equals(
                        SubmitStatus.Submitted)) {
                    return false;
                }

                // Don't allow dragging if the node itself or any of its
                // predecessors is not readable or not writable:
                if (!node.fileExists() || !node.canRead() || !node.canWrite() || node
                        .hasPredecessorNotReadableOrWritableByCurrentUser()) {
                    return false;
                }

                // Don't allow dragging if the node itself or any of its
                // descendants is not readable or not writable:
                if (node.isFolder() && ((NodeFolder) node).hasDescendantNotReadableOrWritableByCurrentUser()) {
                    return false;
                }
            }

            return true;
        } else if (transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            // Don't allow drop on files:
            if (targetNode.isFile()) {
                return false;
            }

            // When dragging from the file system, show Drag-Copy cursor (not
            // the default Drag-Move cursor):
            transferSupport.setDropAction(COPY);

            sipView.selectNode(targetNode.getAdmId());

            // Inserting files and folders from the file system or any other
            // fileList provider:
            // Always allow file lists:
            return true;
        }

        // Reject any other data flavor:
        return false;
    }

    /**
     * Actually import or move the data. Distinguish file lists (importing) and our own data flavor (moving nodes
     * within the tree).
     */
    @Override
    public boolean importData(final TransferSupport transferSupport) {
        if (!this.canImport(transferSupport)) {
            return false;
        }

        // Import files or move nodes within the tree?
        boolean isOK = false;
        if (transferSupport.isDataFlavorSupported(TreeNodeListTransferable.DocuteamPackerTreeNodeListDataFlavor)) {
            // It is our own data flavor:
            isOK = moveNodesWithinSIP(transferSupport);
        } else if (transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            // It is a file list:
            isOK = importFileList(transferSupport);
        } else {
            return false; // Should actually never happen: flavor is neither
                          // javaFileListFlavor nor
                          // DocuteamPackerTreeNodeListDataFlavor.
        }

        return isOK;
    }

    /**
     * For cleanup after successful move. Not needed here for now.
     */
    @Override
    public void exportDone(final JComponent component, final Transferable t, final int action) {
    }

    /**
     * Move nodes within the tree. This method is only called when the DataFlavor is our own
     * (TreeNodeListTransferable.DocuteamPackerTreeNodeListDataFlavor).
     * 
     * @param transferSupport
     * @return
     */
    private boolean moveNodesWithinSIP(final TransferSupport transferSupport) {
        // Target component:
        final JXTreeTable treeTable = (JXTreeTable) transferSupport.getComponent();

        // Target tree path:
        final int targetRow = ((JTable.DropLocation) transferSupport.getDropLocation()).getRow();
        final TreePath targetPath = treeTable.getPathForRow(targetRow);
        Logger.debug("D&D TargetPath       = " + targetPath);

        final TreePath nextParentPath = targetPath.getParentPath();

        // Target node:
        final NodeAbstract targetNode = (NodeAbstract) targetPath.getLastPathComponent();
        Logger.debug("D&D TargetNode       = " + targetNode);

        final boolean insertRow = ((JTable.DropLocation) transferSupport.getDropLocation()).isInsertRow();
        final List<NodeAbstract> movedNodes = new ArrayList<>();

        // Source node's parent path:
        TreePath sourceParentPath;

        // Do the move:
        try {
            final TreeNodeListTransferable transferData = (TreeNodeListTransferable) transferSupport.getTransferable()
                    .getTransferData(TreeNodeListTransferable.DocuteamPackerTreeNodeListDataFlavor);

            // Source parent path (I know that all source nodes have the same parent):
            sourceParentPath = transferData.getParentPath();
            Logger.debug("D&D SourceParentPath = " + sourceParentPath);

            for (final NodeAbstract n : transferData.getDraggedNodes()) {
                if (insertRow) {
                    n.moveBeforeNode(targetNode);
                } else if (targetNode instanceof NodeFolder) {
                    n.moveTo((NodeFolder) targetNode);
                    movedNodes.add(n);
                }
            }
        } catch (final java.lang.Exception e) {
            JOptionPane.showMessageDialog(sipView, e.toString(), I18N.translate("TitleCantMoveItem"),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Refresh the view:
        final TreeTableModel model = (TreeTableModel) treeTable.getTreeTableModel();

        model.refreshTreeStructure(sourceParentPath);
        model.refreshTreeStructure(nextParentPath);

        treeTable.expandPath(sourceParentPath);
        treeTable.expandPath(nextParentPath);

        for (final NodeAbstract movedNode : movedNodes) {
            // expand new path
            final TreePath newTreePath = targetPath.pathByAddingChild(movedNode);
            treeTable.expandPath(newTreePath);
        }

        return true;
    }

    /**
     * Import files. This method is only called when the DataFlavor is DataFlavor.javaFileListFlavor.
     * 
     * @param transferSupport
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean importFileList(final TransferSupport transferSupport) {
        try {
            final List<File> droppedFiles = (List<File>) transferSupport.getTransferable()
                    .getTransferData(DataFlavor.javaFileListFlavor);
            Logger.debug("D&D files = " + droppedFiles);

            // Do the import:
            sipView.importFilesAndFolders(droppedFiles);
        } catch (final Exception ex) {
            Logger.error(ex.getMessage(), ex);
            return false;
        }

        return true;
    }

}
