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
package ch.docuteam.packer.gui.launcher.actions;

import static ch.docuteam.packer.gui.PackerConstants.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import ch.docuteam.darc.mets.Document.Mode;
import ch.docuteam.packer.gui.launcher.LauncherView;
import ch.docuteam.tools.translations.I18N;

public class OpenSIPInWorkspaceAction extends AbstractDocuAction {

	public OpenSIPInWorkspaceAction(LauncherView owner) {
		super(I18N.translate("ActionOpenInWorkspace"),
				getImageIcon(OPEN_IN_WORKSPACE_PNG), owner);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipOpenInWorkspace"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		owner.openSelectedSIPInWorkspace(Mode.ReadWrite);

	}

}
