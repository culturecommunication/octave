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

import static ch.docuteam.packer.gui.PackerConstants.INFO_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import ch.docuteam.packer.gui.launcher.AboutView;
import ch.docuteam.packer.gui.launcher.LauncherView;
import ch.docuteam.tools.translations.I18N;

public class AboutAction extends AbstractDocuAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AboutAction(final LauncherView owner) {
        super(I18N.translate("ActionAbout"), getImageIcon(INFO_PNG), owner);
        putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipAbout"));
        putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        openAboutWindow();

    }

    // TODO hope there is no regression because of above comment
    public void openAboutWindow() {
        new AboutView(owner);
    }
}
