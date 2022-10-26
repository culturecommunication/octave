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

import static ch.docuteam.packer.gui.PackerConstants.DEFAULT_METS_NAME;
import static ch.docuteam.packer.gui.PackerConstants.ZIP_EXT;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import ch.docuteam.darc.mets.Document;
import ch.docuteam.packer.gui.FileProperty;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

import org.apache.commons.io.FileUtils;

public class SIPTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final List<FileProperty> sips;

    LauncherView launcherView;

    private Path sipDir;

    public SIPTableModel(final LauncherView launcherView) {
        super();
        this.launcherView = launcherView;
        sips = new ArrayList<>();
        readDirContent();
        updateSizeAndLockedByColumns();

    }

    public void readDirContent() {
        sips.clear();
        sipDir = FileSystems.getDefault().getPath(launcherView.getSipDirectory());
        try (DirectoryStream<Path> sipsStream = Files.newDirectoryStream(sipDir, sipFileFilter)) {
            for (final Path file : sipsStream) {
                final FileProperty fProp = new FileProperty(file.toFile(), true);
                sips.add(fProp);
            }
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            Logger.error(e.getMessage(), e);
        }
        Collections.sort(sips);
    }

    // TODO shouldn't this be changed to
    // ch.docuteam.tools.file.FileFilter$METSFilesOrZIPs?
    // we will check later if the Filter from tools is used by other parts and
    // decide what to do with it, for now own implementation below
    Filter<Path> sipFileFilter = new Filter<Path>() {

        @Override
        public boolean accept(final Path entry) throws IOException {
            final boolean isReadable = Files.isReadable(entry);
            final boolean isHidden = Files.isHidden(entry) || entry.toString().startsWith(".");
            final boolean isFile = Files.isRegularFile(entry);
            final boolean isDir = Files.isDirectory(entry);
            final boolean isZip = entry.toString().toLowerCase().endsWith(ZIP_EXT);
            final boolean containsMets = Files.exists(entry.resolve(DEFAULT_METS_NAME));
            final boolean isACopy = entry.getFileName().toString().contains(Document.DOCUMENT_NAME_SUFFIX_PART_COPY);

            final boolean result = isReadable && !isHidden && (isFile && isZip || isDir && containsMets) && !isACopy;
            return result;
        }
    };

    // TODO this method is not reworked
    public void updateSizeAndLockedByColumns() {
        synchronized (sips) {
            for (int i = 0; i < sips.size(); i++) {
                final int a = i;
                // final FileProperty fp =
                Object value = getValueAt(i, Column.SIZE.ordinal());
                if (value.equals("?")) {
                    final SwingWorker<Long, Void> worker = new SwingWorker<Long, Void>() {

                        private Long size = null;

                        @Override
                        public Long doInBackground() {
                            size = Long.valueOf(FileUtils.sizeOfDirectory(sips.get(a).getFile()));
                            return size;
                        }

                        @Override
                        public void done() {
                            sips.get(a).setSize(size);
                            fireTableCellUpdated(a, Column.SIZE.ordinal());
                        }
                    };
                    worker.execute();
                }
                value = getValueAt(i, Column.LOCKED_BY.ordinal());
                if (value.equals("?")) {
                    final SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

                        private String lockedBy = null;

                        private Document document = null;

                        @Override
                        public String doInBackground() throws Exception {
                            lockedBy = Document.lockedByWhom(sips.get(a).getFile().getPath());
                            if (lockedBy == null) {
                                lockedBy = "";
                            } else {
                                if (lockedBy.equals(System.getProperty("user.name"))) {
                                    lockedBy = System.getProperty("user.name");
                                } else {
                                    document = Document.openReadOnly(sips.get(a).getFile().getPath(), "launcherView");
                                    try {
                                        if (sips.get(a).getDocument() != null && sips.get(a).getDocument()
                                                .hasSubmittedNodes()) {
                                            if (lockedBy != null) {
                                                lockedBy += "; " + I18N.translate_NoCheck("Submitted");
                                            } else {
                                                lockedBy = I18N.translate_NoCheck("Submitted");
                                            }
                                        }
                                    } catch (final Exception e) {
                                        Logger.warn(I18N.translate("TitleCantReadSIP") + ": " + sips.get(a).getFile()
                                                .getPath(), e);
                                    }
                                }
                            }
                            return lockedBy;
                        }

                        @Override
                        public void done() {
                            sips.get(a).setDocument(document);
                            sips.get(a).setLockedBy(lockedBy);
                            fireTableCellUpdated(a, Column.LOCKED_BY.ordinal());
                        }
                    };
                    worker.execute();
                }
            }
        }
    }

    @Override
    // TODO this method should probably not be used by anyone
    public int getRowCount() {
        return sips.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public String getColumnName(final int columnIndex) {
        if (columnIndex < Column.values().length) {
            return Column.values()[columnIndex].getName();
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        if (columnIndex < Column.values().length) {
            return Column.values()[columnIndex].getColumnClass();
        }
        return Object.class;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {

        final FileProperty fileProperty = sips.get(rowIndex);
        if (columnIndex < Column.values().length) {
            final Object result = Column.values()[columnIndex].getValue(fileProperty);
            return result;
        }
        return null;
    }

    public FileProperty getSipAtIndex(final int rowIndex) {
        if (rowIndex > -1 && rowIndex < sips.size()) {
            return sips.get(rowIndex);
        }
        return null;
    }

    public int getRowIndexOfSip(final FileProperty fileProperty) {
        return sips.indexOf(fileProperty);
    }

    public void delteSipAtIndex(final int i) {
        if (i > -1 && i < sips.size()) {
            sips.remove(i);
        }
    }

    public void removeSipFromView(final FileProperty fileProperty) {
        final int index = sips.indexOf(fileProperty);
        delteSipAtIndex(index);
        fireTableRowsDeleted(index, index);
    }

}
