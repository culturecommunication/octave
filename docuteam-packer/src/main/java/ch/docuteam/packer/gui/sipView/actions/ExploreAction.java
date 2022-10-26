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

import static ch.docuteam.packer.gui.PackerConstants.EXPLORE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;

import ch.docuteam.darc.mets.Document;
import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.tools.os.SystemProcess;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

public class ExploreAction extends AbstractSIPViewAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ExploreAction(final SIPView sipView) {
        super(I18N.translate("ButtonExplore"), getImageIcon(EXPLORE_PNG), sipView);
        putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipExplore"));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Document document = sipView.getDocument();
        if (document == null) {
            return;
        }

        try {
            // Save document if necessary:
            if (document.isModified()) {
                document.saveWithoutBackup();
            }

            final String originalSIPFolder = document.getOriginalSIPFolder();

            // Open the file browser on the SIP directory:
            if (originalSIPFolder != null) {
                if (originalSIPFolder.endsWith(".zip") || originalSIPFolder.endsWith(".ZIP")) {
                    SystemProcess.openExternally(new File(originalSIPFolder).getParent());
                } else {
                    SystemProcess.openExternally(originalSIPFolder);
                }
            } else {
                SystemProcess.openExternally(new File(document.getFilePath()).getParent());
            }
        } catch (final java.lang.Exception ex) {
            Logger.error(I18N.translate("MessageExploreActionException"), ex);
        }
    }

    @Override
    public void enableOrDisable() {
        // always enabled
        setEnabled(true);
    }

}
