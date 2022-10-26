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

import static ch.docuteam.packer.gui.PackerConstants.ZIP_EXT;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;

import ch.docuteam.darc.mets.Document;
import ch.docuteam.tools.translations.I18N;

public class SIPFileChooser extends JFileChooser {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final ImageIcon SIPFolderIcon = getImageIcon("Packet.png");
    // private static SIPFileChooser Singleton = null;

    // public static SIPFileChooser getInstance(LauncherView launcherView) {
    // if(Singleton == null){
    // Singleton = new SIPFileChooser(launcherView);
    // }
    // return Singleton;
    // }

    public SIPFileChooser(final String lastUsedOpenOrSaveDir) {
        super(lastUsedOpenOrSaveDir);

        setDialogType(JFileChooser.OPEN_DIALOG);
        setMultiSelectionEnabled(false);
        setDialogTitle(I18N.translate("TitleOpenSIP"));
        setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        setFileFilter(new javax.swing.filechooser.FileFilter() {

            @Override
            public boolean accept(final File file) {
                // No hidden files or folders:
                if (file.isHidden() || file.getName().startsWith(".")) {
                    return false;
                }
                if (file.isFile()) {
                    // Only zip files:
                    if (file.getName().toLowerCase().endsWith(ZIP_EXT)) {
                        return true;
                    }
                } else {
                    // Only folders that contain a mets.xml // ToDo: (this
                    // doesn't work: then all folders are invisible!):
                    // if (Document.isValidSIPFolder(file)) return true;
                    return true;
                }

                return false;
            }

            @Override
            public String getDescription() {
                return "SIP Folder or ZIP-File";
            }
        });

        setFileView(new FileView() {

            @Override
            public Boolean isTraversable(final File folder) {
                if (folder == null || !folder.exists() || !folder.canRead()) {
                    return false;
                }
                if (folder.isFile()) {
                    return false;
                }

                return !Document.isValidSIPFolder(folder);
            }

            /**
             * The icon that represents this file in the <code>JFileChooser</code>.
             */
            @Override
            public Icon getIcon(final File file) {
                if (file.isFile()) {
                    if (file.getName().toLowerCase().endsWith(ZIP_EXT)) {
                        return SIPFolderIcon;
                    }
                } else {
                    if (Document.isValidSIPFolder(file)) {
                        return SIPFolderIcon;
                    }
                }

                return null;
            }
        });
    }

}
