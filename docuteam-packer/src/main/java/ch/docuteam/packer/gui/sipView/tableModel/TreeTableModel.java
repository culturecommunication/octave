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
package ch.docuteam.packer.gui.sipView.tableModel;

import java.util.Arrays;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import ch.docuteam.darc.mets.Document;
import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.mets.structmap.NodeAbstract.SubmitStatus;
import ch.docuteam.tools.translations.I18N;


public class TreeTableModel extends DefaultTreeTableModel {

	public TreeTableModel(Document document) {
		super((document == null) ? null : document.getStructureMap().getRoot(),
				Arrays.asList(I18N.translate("HeaderName"), I18N.translate("HeaderTreeViewSizeKB"),
						I18N.translate("HeaderTreeViewSize%"), I18N.translate("HeaderTreeViewSize%"),
						I18N.translate("HeaderTreeViewMandatoryStatus"), I18N.translate("HeaderTreeViewSubmitStatus")));
	}

	@Override
	public int getColumnCount() {
		return 6;
	}

	@Override
	public int getHierarchicalColumn() {
		return 0;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case 0:
			return String.class;
		case 1:
			return Long.class;
		case 2:
			return Integer.class;
		case 3:
			return Object.class; // Must be Object so that the size gets
									// rendered correctly as a bar
		case 4:
			return Boolean.class;
		case 5:
			return SubmitStatus.class;
		}

		return null;
	}

	@Override
	public Object getValueAt(Object node, int column) {
		NodeAbstract f = (NodeAbstract) node;

		switch (column) {
		case 0:
			return f.getLabel();
		case 1:
			return f.getSize() / 1024;
		case 2:
			return f.getRelativeSize();
		case 3:
			return f.getRelativeSize();
		case 4:
			return f.hasDynamicMetadataElementInstancesWhichAreMandatoryButNotSet();
		case 5:
			return f.getSubmitStatus();
		}

		return null;
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		return false;
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((NodeAbstract) parent).getChildAt(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((NodeAbstract) parent).getChildCount();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((NodeAbstract) parent).getIndex((NodeAbstract) child);
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((NodeAbstract) node).isFile();
	}

	/**
	 * Refresh this tree section in the view tree.
	 * 
	 * @param path
	 */
	public void refreshTreeStructure(TreePath path) {
		modelSupport.fireTreeStructureChanged(path);
	}

	/**
	 * Refresh this node in the view tree.
	 * 
	 * @param path
	 */
	public void refreshNode(TreePath path) {
		modelSupport.firePathChanged(path);
	}

}
