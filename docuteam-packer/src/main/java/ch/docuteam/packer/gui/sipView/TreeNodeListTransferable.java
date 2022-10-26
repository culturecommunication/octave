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
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreePath;

import ch.docuteam.darc.mets.structmap.NodeAbstract;

import org.jdesktop.swingx.JXTreeTable;

public class TreeNodeListTransferable implements Transferable {

    // This is our own data flavor:
    protected static final DataFlavor DocuteamPackerTreeNodeListDataFlavor = new DataFlavor(Object.class,
            "DocuteamPackerTreeNodeListDataFlavor");

    protected static final DataFlavor[] SupportedDataFlavors = { DocuteamPackerTreeNodeListDataFlavor };

    /**
     * The sipPath is to distinguish the drag source from the drag target.
     */
    private final String sipPath;

    /**
     * These are the dragged nodes.
     */
    private final List<NodeAbstract> draggedNodes;

    /**
     * This is the dragged node's parent path. All dragged nodes must have the same parent - if not, the drag is
     * rejected. I need the parentPath for refreshing and expanding the path after the drag.
     */
    private final TreePath parentPath;

    protected TreeNodeListTransferable(final JXTreeTable treeTable) {
        draggedNodes = new Vector<NodeAbstract>();
        for (final int i : treeTable.getSelectedRows()) {
            draggedNodes.add((NodeAbstract) treeTable.getPathForRow(i).getLastPathComponent());
        }

        // I can safely assume that this.draggedNodes is not empty because - how
        // can one drag 0 nodes?:
        sipPath = draggedNodes.get(0).getDocument().getSIPFolder();
        parentPath = treeTable.getPathForRow(treeTable.getSelectedRows()[0]).getParentPath();
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return SupportedDataFlavors;
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        // This method seems never to be called???
        return DocuteamPackerTreeNodeListDataFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (DocuteamPackerTreeNodeListDataFlavor.equals(flavor)) {
            return this;
        }

        throw new UnsupportedFlavorException(flavor);
    }

    public TreePath getParentPath() {
        return parentPath;
    }

    public List<NodeAbstract> getDraggedNodes() {
        return draggedNodes;
    }

    public String getSipPath() {
        return sipPath;
    }

}
