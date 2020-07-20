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

package ch.docuteam.packer.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.NumberFormat;

import ch.docuteam.darc.mets.Document;

public class FileProperty implements Comparable<FileProperty> {

    private final NumberFormat sizeFormatter;

    private final boolean isInWorkspace;

    private final File file;

    private Document document;

    private final String name;

    private String lockedBy;

    private Long size;

    private Long lastModified;

    public FileProperty(final File file, final boolean isInWorkspace) {
        sizeFormatter = NumberFormat.getInstance();
        this.isInWorkspace = isInWorkspace;
        this.file = file;
        Path path = Paths.get(file.getPath());
        name = path.getFileName().toString();
        BasicFileAttributes attrs = null;
        try {
            if (Files.isDirectory(path)) {
                // we regard the mets.xml and we don't get the size here
                path = path.resolve(Document.DEFAULT_METS_FILE_NAME);
                attrs = Files.readAttributes(path, BasicFileAttributes.class);
            } else {
                attrs = Files.readAttributes(path, BasicFileAttributes.class);
                size = attrs.size();
            }
            lastModified = attrs.lastModifiedTime().toMillis();
        } catch (final IOException e) {
            // TODO Not sure what should happen here
        }
    }

    public boolean isInWorkspace() {
        return isInWorkspace;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public void setDocument(final Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public void setLockedBy(final String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public String getLockedBy() {
        return lockedBy == null ? "?" : lockedBy;
    }

    public void setSize(final Long size) {
        this.size = size;
    }

    public String getSize() {
        return size == null ? "?" : sizeFormatter.format(size.longValue() / 1024);
    }

    public long getLastModified() {
        return lastModified;
    }

    public boolean isLocked() {
        return Document.isLockedBySomebodyElse(file);
    }

    @Override
    public int compareTo(final FileProperty other) {
        return getName().compareTo(other.getName());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileProperty other = (FileProperty) obj;
        if (file == null) {
            if (other.file != null) {
                return false;
            }
        } else if (!file.getName().equals(other.file.getName())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (file == null ? 0 : file.getName().hashCode());
        return result;
    }

}
