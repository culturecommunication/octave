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

package ch.docuteam.packer.gui.sipView.actions;

import static ch.docuteam.packer.gui.PackerConstants.DELETE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.tools.translations.I18N;

public class DeleteItemAction extends AbstractSIPViewAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DeleteItemAction(final SIPView sipView) {
        super(I18N.translate("ButtonDeleteItem"), getImageIcon(DELETE_PNG), sipView);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipDeleteItem"));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {

        // If the shift-key is held while clicking the delete-button: DON'T ask.

        if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 0) {
            // The shift-key was pressed:

            final int selectedRowCount = sipView.getTreeTable().getSelectedRowCount();
            if (selectedRowCount == 0) {
                return;
            } else if (selectedRowCount == 1) {
                // Single selection:

                if (sipView.getSelectedNode().isFolder() && sipView.getSelectedNode().getChildCount() != 0) {
                    if (JOptionPane.showConfirmDialog(sipView, I18N.translate("QuestionDeleteWithAllSubElements"),
                            I18N.translate("TitleDeleteFolder"),
                            JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                        return;
                    }
                } else {
                    if (JOptionPane.showConfirmDialog(sipView, I18N.translate("QuestionDeleteItem"),
                            I18N.translate("TitleDeleteItem"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            } else {
                // Multiple selection:

                if (JOptionPane.showConfirmDialog(sipView,
                        I18N.translate("QuestionDeleteMultipleItems", selectedRowCount),
                        I18N.translate("TitleDeleteMultipleItems"),
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }

        sipView.deleteItemDontAskButtonClicked();

    }

    @Override
    public void enableOrDisable() {
        sipView.getDocument().isReadWrite();

    }

}
