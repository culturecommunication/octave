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
package ch.docuteam.packer.gui.sipView.cellRenderer;

import static ch.docuteam.packer.gui.PackerConstants.ICON_LEVEL_NOT_ALLOWED;
import static ch.docuteam.packer.gui.PackerConstants.ICON_LEVEL_UNKNOWN;
import static ch.docuteam.packer.gui.PackerConstants.ICON_NOT_ALLOWED_BY_SA;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.mets.structmap.NodeFile;
import ch.docuteam.darc.mets.structmap.NodeFolder;
import ch.docuteam.tools.translations.I18N;


public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		MyTreeCellRenderer component = (MyTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel,
				expanded, leaf, row, hasFocus);

		if (value == null)
			return component;

		NodeAbstract node = (NodeAbstract) value;

		component.setText(node.getLabel());
		StringBuilder toolTipBuilder = new StringBuilder("<html>");
		toolTipBuilder.append(I18N.translate("HeaderName")).append(" ").append(node.getLabel()).append("<br>")
				.append(I18N.translate("LabelTitle")).append(" ").append(node.getUnitTitle()).append("<br>")
				.append(I18N.translate("LabelLevel")).append(" ")
				.append(node.getLevel() != null ? node.getLevel().getName() : "?").append("<br>");

		try {
			if (!node.isAllowedBySA()) {
				component.setIcon(ICON_NOT_ALLOWED_BY_SA);
				toolTipBuilder.append("(").append(I18N.translate("ToolTipIconNotAllowedBySA")).append(")");
			} else if (!node.doesParentAllowMyLevel()) {
				//	allow broken levels logic if it was caused by a migration action keeping originals (and therefore creating an additional intermediary level)
				if (node.isFile() 
						&& !(((NodeFile)node).getMigrationDerivedNode() == null && ((NodeFile)node).getMigrationSourceNode() == null)) {
					ImageIcon icon = node.getLevel().getIcon();
					if (icon == null) {
						component.setIcon(ICON_LEVEL_UNKNOWN);
						toolTipBuilder.append("(").append(I18N.translate("ToolTipLevelIconCouldNotBeFound")).append(")");
					} else {
						component.setIcon(icon);
					}
				} else {
					component.setIcon(ICON_LEVEL_NOT_ALLOWED);
					toolTipBuilder.append("(")
							.append(I18N.translate("ToolTipLevelIconParentDoesntAllow",
									((NodeFolder) node.getParent()).getLevel().getName(), node.getLevel().getName()))
							.append(")");
				}
			} else if (node.getLevel() == null) {
				component.setIcon(ICON_LEVEL_UNKNOWN);
			} else {
				ImageIcon icon = node.getLevel().getIcon();
				if (icon == null) {
					component.setIcon(ICON_LEVEL_UNKNOWN);
					toolTipBuilder.append("(").append(I18N.translate("ToolTipLevelIconCouldNotBeFound")).append(")");
				} else {
					component.setIcon(icon);
				}
			}
		} catch (Exception x) {
			component.setIcon(ICON_LEVEL_UNKNOWN);
			toolTipBuilder.append("(").append(I18N.translate("ToolTipLevelIconCouldNotBeLoaded")).append(")");
		}

		if (!node.fileExists()) {
			toolTipBuilder.append("<br>*****&nbsp;&nbsp;&nbsp;").append(I18N.translate("ToolTipFileIsMissing"));
		} else if (!node.canRead()) {
			toolTipBuilder.append("<br>*****&nbsp;&nbsp;&nbsp;").append(I18N.translate("ToolTipFileIsNotReadable"));
		} else if (!node.canWrite()) {
			toolTipBuilder.append("<br>*****&nbsp;&nbsp;&nbsp;").append(I18N.translate("ToolTipFileIsReadOnly"));
		}

		component.setToolTipText(toolTipBuilder.toString());

		return component;
	}

}
