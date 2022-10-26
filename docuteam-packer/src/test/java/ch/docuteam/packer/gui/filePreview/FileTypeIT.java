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
package ch.docuteam.packer.gui.filePreview;

import static org.junit.Assert.assertEquals;

import ch.docuteam.tools.translations.I18N;

import org.junit.Before;
import org.junit.Test;

/**
 * Initial Date: 14.05.2018 <br>
 * 
 * @author l.dumitrescu
 */
public class FileTypeIT {

    @Before
    public void setup() {
        I18N.initialize("de", "translations.Translations");
    }

    @Test
    public void getFileType_PDF() {
        final FileType fileType1 = FileType.getFileType("fmt/14");
        assertEquals(FileType.PDF, fileType1);
        //
        final FileType fileType2 = FileType.getFileType("application/pdf");
        assertEquals(FileType.PDF, fileType2);

        final FileType fileType3 = FileType.getFileType("pdf");
        assertEquals(FileType.PDF, fileType3);
    }

    @Test
    public void getFileType_OpenDocument_Presentation() {
        final FileType fileType1 = FileType.getFileType("fmt/138");
        assertEquals(FileType.OOConvertable, fileType1);
        //
        final FileType fileType2 = FileType.getFileType("application/vnd.oasis.opendocument.presentation");
        assertEquals(FileType.OOConvertable, fileType2);

        final FileType fileType3 = FileType.getFileType("odp");
        assertEquals(FileType.OOConvertable, fileType3);
    }

}
