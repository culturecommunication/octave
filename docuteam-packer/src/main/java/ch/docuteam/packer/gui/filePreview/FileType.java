/**
 *  Copyright (C) since 2017 at Docuteam GmbH
 *  <p>
 *	This program is free software: you can redistribute it and/or modify <br>
 *	it under the terms of the GNU General Public License version 3 <br>
 *	as published by the Free Software Foundation. 
 *  <p>
 *	This program is distributed in the hope that it will be useful, <br>
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of <br>
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the <br>
 *	GNU General Public License for more details.
 *  <p>
 *	You should have received a copy of the GNU General Public License <br>
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 */
package ch.docuteam.packer.gui.filePreview;

import java.util.Iterator;

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.out.Logger;

enum FileType {
    Folder, Text, HTML, PDF, OOConvertable, GraphicsNative, GraphicsImageIO, GraphicsJAIConvertable, GraphicsJAIConvertableImageRead, GraphicsJIMIConvertable, GraphicsImageMagickConvertable, Unknown;

    static FilePreviewConfigurator filePreviewConfigurator;

    static {
        if (filePreviewConfigurator == null) {
            filePreviewConfigurator = new FilePreviewConfigurator();
        }
    }

    static FileType check(NodeAbstract node) {
        if (node.isFolder()) {
            return Folder;
        }

        FileType fileType = Unknown;

        // First PUID:
        String formatKey = node.getFormatKey();
        if (formatKey != null) {
            fileType = getFileType(formatKey);
            if (fileType != null && !Unknown.equals(fileType)) {
                return fileType;
            }
        }

        // Then check MIME Type:
        String mimeType = node.getMimeType();
        if (mimeType != null) {
            fileType = getFileType(mimeType);
            if (fileType != null && !Unknown.equals(fileType)) {
                return fileType;
            }
        }

        // Finally check file extension:
        String extension = FileUtil.asFileNameExtension(node.getLabel());
        if (extension != null) {
            extension = extension.toLowerCase();
            fileType = getFileType(extension);
            if (fileType != null && !Unknown.equals(fileType)) {
                return fileType;
            }
        }

        return Unknown;
    }

    static FileType getFileType(String formatKeyOrMimeTypeOrExtension) {
        FileType fileType = Unknown;
        String propertyValue = filePreviewConfigurator.getPropertyValue(formatKeyOrMimeTypeOrExtension);
        if (propertyValue != null) {
            try {
                fileType = FileType.valueOf(propertyValue);
            } catch (Exception e) {
                Logger.warn("getFileType - cannot find FileType for " + formatKeyOrMimeTypeOrExtension);
            }
        }
        return fileType;
    }
}
