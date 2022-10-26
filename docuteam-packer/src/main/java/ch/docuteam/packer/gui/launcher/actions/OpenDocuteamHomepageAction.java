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

package ch.docuteam.packer.gui.launcher.actions;

import static ch.docuteam.packer.gui.PackerConstants.HOME_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.Desktop;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import ch.docuteam.packer.gui.launcher.LauncherView;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

public class OpenDocuteamHomepageAction extends AbstractDocuAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public OpenDocuteamHomepageAction(final LauncherView owner) {
        super(I18N.translate("ActionOpenDocuteamHomepage"), getImageIcon(HOME_PNG), owner);
        putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipOpenDocuteamHomepage"));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        openDocuteamHomepage();

    }

    // TODO this method here is used by the logo and this is probably not a good design
    public void openDocuteamHomepage() {
        if (!java.awt.Desktop.isDesktopSupported()) {
            System.err.println("Desktop is not supported");
            return;
        }

        final Desktop desktop = Desktop.getDesktop();

        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            System.err.println("Desktop doesn't support the browse action");
            return;
        }

        try {
            desktop.browse(new java.net.URI("http://www.docuteam.ch"));
        } catch (final java.lang.Exception ex) {
            Logger.error(ex.getMessage(), ex);
        }
    }

}
