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

import javax.swing.AbstractAction;
import javax.swing.Icon;

import ch.docuteam.packer.gui.launcher.LauncherView;

public abstract class AbstractDocuAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected LauncherView owner;

    public AbstractDocuAction(final String name, final Icon icon, final LauncherView owner) {
        super(name, icon);
        this.owner = owner;
    }

}
