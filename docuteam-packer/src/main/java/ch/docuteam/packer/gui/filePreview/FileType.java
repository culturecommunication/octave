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
package ch.docuteam.packer.gui.filePreview;

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.out.Logger;

enum FileType {
    Folder, Text, HTML, PDF, OOConvertable, GraphicsNative, GraphicsImageIO, GraphicsJAIConvertableImageRead,
    GraphicsJIMIConvertable, GraphicsImageMagickConvertable, Unknown;

    static FilePreviewConfigurator filePreviewConfigurator;

    static {
        if (filePreviewConfigurator == null) {
            filePreviewConfigurator = new FilePreviewConfigurator();
        }
    }

    static FileType check(final NodeAbstract node) {
        if (node.isFolder()) {
            return Folder;
        }

        FileType fileType = Unknown;

        // First PUID:
        final String formatKey = node.getFormatKey();
        if (formatKey != null) {
            fileType = getFileType(formatKey);
            if (fileType != null && !Unknown.equals(fileType)) {
                return fileType;
            }
        }

        // Then check MIME Type:
        final String mimeType = node.getMimeType();
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

    static FileType getFileType(final String formatKeyOrMimeTypeOrExtension) {
        FileType fileType = Unknown;
        final String propertyValue = filePreviewConfigurator.getPropertyValue(formatKeyOrMimeTypeOrExtension);
        if (propertyValue != null) {
            try {
                fileType = FileType.valueOf(propertyValue);
            } catch (final Exception e) {
                Logger.warn("getFileType - cannot find FileType for " + formatKeyOrMimeTypeOrExtension);
            }
        }
        return fileType;
    }
}
