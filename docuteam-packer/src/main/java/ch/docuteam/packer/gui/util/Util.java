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
package ch.docuteam.packer.gui.util;

import static ch.docuteam.packer.gui.PackerConstants.EXCEPTION_COLLECTOR_NO_EXCEPTION;
import static ch.docuteam.packer.gui.PackerConstants.EXCEPTION_COLLECTOR_PREFIX;
import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.Window;

import ch.docuteam.tools.exception.ExceptionCollector;
import ch.docuteam.tools.gui.ScrollableMessageDialog;
import ch.docuteam.tools.gui.SmallPeskyMessageWindow;
import ch.docuteam.tools.translations.I18N;

/**
 * @author l.dumitrescu
 */
public class Util {

    public static void showAllFromExceptionCollector(final SmallPeskyMessageWindow waitWindow, final Window parent) {
        if (!ExceptionCollector.isEmpty()) {
            if (waitWindow != null) {
                waitWindow.close(); // In case it was not closed yet...
            }
            new ScrollableMessageDialog(parent, I18N.translate("TitleWarningsOccurred"),
                    ExceptionCollector.getAllTranslatedMessages(EXCEPTION_COLLECTOR_NO_EXCEPTION,
                            EXCEPTION_COLLECTOR_PREFIX), getImageIcon(PACKER_PNG));
        }
    }
}
