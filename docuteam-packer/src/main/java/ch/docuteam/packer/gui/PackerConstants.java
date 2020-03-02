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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;

import ch.docuteam.tools.file.ResourceUtil;
import ch.docuteam.tools.os.OperatingSystem;

public class PackerConstants {

	public static final String OPERATOR = "docuteam packer";
	
	public static final String ZIP_EXT = ".zip";
	public static final String ZIP = "ZIP";
	public static final String DEFAULT_PROPERTY_FILE_NAME = "./config/docuteamPacker.properties";
	public static final String DEFAULT_LEVELS_CONFIG_FILE_NAME = "./config/levels.xml";
	public static final String DEFAULT_METS_NAME = "mets.xml";

	public static final Dimension DEFAULT_SCREEN_SIZE_WITHOUT_WORKSPACE_MANAGER = new Dimension(800, 120);
	public static final Dimension DEFAULT_SCREEN_SIZE_WITH_WORKSPACE_MANAGER = new Dimension(900, 300);
	public static final Point SCREEN_POSITION = new Point(20, 20);
	public static final boolean DO_INITIALLY_OPEN_WORKSPACEMANAGER = true;

	public static final String PROPERTY_FILE_PATH_OS_SUFFIX = OperatingSystem.isWindows() ? ".Win"
			: (OperatingSystem.isMacOSX() ? ".OSX" : ".Linux");
	

	// configs
	public static final String USER_HOME = System.getProperty("user.home");
	public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
	public static final String USE_SYSTEM_LOOK_AND_FEEL = "true";
	public static final String NEW_SIP_DEFAULT_DELETE_SOURCES = "false";
	public static final String NEW_SIP_DEFAULTS_TO_ZIPPED = "true";
	public static final String MIGRATE_FILE_KEEP_ORIGINAL = "false";
	public static final String IS_IN_DEVELOP_MODE = "false";

	// Resources
	public static final String DELETE_PNG                 = "Delete.png";
	public static final String MIGRATE_PNG                = "Migrate.png";
	public static final String CONVERT_PNG                = "Convert.png";
	
	public static final String PACKER_PNG                 = "DocuteamPacker.png"; 
	public static final String SEARCH_PNG                 = "Search.png";
	public static final String SAVE_PNG                   = "Save.png";
    public static final String CHECKSUM_PNG               = "Sum.png";
    public static final String INFO_PNG                   = "Info.png";
	public static final String EXCEPTION_PNG              = "Exception.png";
	public static final String HOME_PNG                   = "Home.png";
	public static final String HELP_PNG                   = "Help.png";
	public static final String DOWNLOAD_PNG               = "Download.png";
	public static final String WORKSPACE_MANAGER_SHOW_PNG = "WorkspaceManagerShow.png";
	public static final String WORKSPACE_MANAGER_HIDE_PNG = "WorkspaceManagerHide.png";
	public static final String OPEN_FOLDER_PNG            = "OpenFolder.png";
	public static final String REDISPLAY_PNG              = "Redisplay.png";
	public static final String NEW_FROM_TEMPLATE_PNG      = "NewFromTemplate.png";
	public static final String OPEN_PNG                   = "Open.png";
	public static final String CLEAR_PNG                  = "Clear.png";
	public static final String SEARCH_NEXT_PNG            = "SearchNext.png";
	public static final String OPEN_IN_WORKSPACE_PNG      = "OpenInWorkspace.png";
	public static final String EXPLORE_PNG                = "Explore.png";
//	public static final String _PNG = ;
	
	
	// SIPView
//	public static final String _PNG = ;
	public static final ImageIcon ICON_LEVEL_UNKNOWN = getImageIcon("LevelUnknown.png");
	public static final ImageIcon ICON_LEVEL_NOT_ALLOWED = getImageIcon("LevelNotAllowed.png");
	public static final ImageIcon ICON_NOT_ALLOWED_BY_SA = getImageIcon("Mark.png");
	public static final String URL_PATTERN = "^(https?|ftp|file)://.*";
//	MetadataTableModel
	public static final int CLICK_COUNT_TO_START = 2;
	public static final int EDITABLE_COLUMN_INDEX = 2;
	
	//Translation keys for the ExceptionCollector
	public static final String EXCEPTION_COLLECTOR_NO_EXCEPTION = "NoExceptionMessage";
	public static final String EXCEPTION_COLLECTOR_PREFIX = "ExceptionMessagePrefix";
		
	public static ImageIcon getImageIcon (String filename) {		
		return ResourceUtil.getImageIcon(filename);
	}
	
	/**
	 * The static images should be found in "images" folder in classpath. 
	 * 
	 * @param filename
	 * @return
	 */
	public static Image getImage(String filename) {		
		return ResourceUtil.getImage(filename);
	}
}
