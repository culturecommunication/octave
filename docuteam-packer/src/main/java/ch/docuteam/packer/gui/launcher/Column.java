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

package ch.docuteam.packer.gui.launcher;

import ch.docuteam.packer.gui.FileProperty;
import ch.docuteam.tools.translations.I18N;

public enum Column {

    SIP, LOCKED_BY, SIZE, LAST_CHANGE_DATE, IS_READ_ONLY;

    public String getName() {
        switch (this) {
            case SIP:
                return I18N.translate("HeaderLauncherSIP");
            case LOCKED_BY:
                return I18N.translate("HeaderLauncherLockedBy");
            case SIZE:
                return I18N.translate("HeaderLauncherSize");
            case LAST_CHANGE_DATE:
                return I18N.translate("HeaderLauncherLastChangeDate");
            case IS_READ_ONLY:
                return I18N.translate("HeaderLauncherIsReadOnly");
            default:
                return "";
        }
    }

    public Class<?> getColumnClass() {
        switch (this) {
            case SIZE:
                return Long.class;
            case LAST_CHANGE_DATE:
                return Long.class;
            case IS_READ_ONLY:
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    public Object getValue(final FileProperty fileProperty) {
        switch (this) {
            case SIP:
                return fileProperty.getName();
            case LOCKED_BY:
                return fileProperty.getLockedBy();
            case SIZE:
                return fileProperty.getSize();
            case LAST_CHANGE_DATE:
                return fileProperty.getLastModified();
            case IS_READ_ONLY:
                return fileProperty.isLocked();
            default:
                return null;
        }
    }

}
