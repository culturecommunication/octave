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

import static ch.docuteam.packer.gui.ComponentNames.FOOTER_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.POPUP_MENU;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CREATE_NEW_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_MENU;
import static ch.docuteam.packer.gui.ComponentNames.SIP_OPEN_IN_WORKSPACE_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.WORKSPACE_MENU;
import static ch.docuteam.packer.gui.ComponentNames.WORKSPACE_SELECT_FOLDER_FILE_CHOOSER;
import static ch.docuteam.packer.gui.ComponentNames.WORKSPACE_SELECT_FOLDER_MENU_ITEM;
import static ch.docuteam.packer.gui.PackerConstants.DEFAULT_LEVELS_CONFIG_FILE_NAME;
import static ch.docuteam.packer.gui.PackerConstants.DEFAULT_SCREEN_SIZE_WITHOUT_WORKSPACE_MANAGER;
import static ch.docuteam.packer.gui.PackerConstants.DEFAULT_SCREEN_SIZE_WITH_WORKSPACE_MANAGER;
import static ch.docuteam.packer.gui.PackerConstants.DELETE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.DOWNLOAD_PNG;
import static ch.docuteam.packer.gui.PackerConstants.DO_INITIALLY_OPEN_WORKSPACEMANAGER;
import static ch.docuteam.packer.gui.PackerConstants.MIGRATE_FILE_KEEP_ORIGINAL;
import static ch.docuteam.packer.gui.PackerConstants.NEW_FROM_CSV_PNG;
import static ch.docuteam.packer.gui.PackerConstants.NEW_FROM_TEMPLATE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.NEW_SIP_DEFAULTS_TO_ZIPPED;
import static ch.docuteam.packer.gui.PackerConstants.NEW_SIP_DEFAULT_DELETE_SOURCES;
import static ch.docuteam.packer.gui.PackerConstants.OPEN_FOLDER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.OPEN_PNG;
import static ch.docuteam.packer.gui.PackerConstants.OPERATOR;
import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.PROPERTY_FILE_NAME;
import static ch.docuteam.packer.gui.PackerConstants.PROPERTY_FILE_PATH_OS_SUFFIX;
import static ch.docuteam.packer.gui.PackerConstants.REDISPLAY_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SCREEN_POSITION;
import static ch.docuteam.packer.gui.PackerConstants.SEARCH_PNG;
import static ch.docuteam.packer.gui.PackerConstants.USER_HOME;
import static ch.docuteam.packer.gui.PackerConstants.USE_SYSTEM_LOOK_AND_FEEL;
import static ch.docuteam.packer.gui.PackerConstants.WORKSPACE_MANAGER_HIDE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.WORKSPACE_MANAGER_SHOW_PNG;
import static ch.docuteam.packer.gui.PackerConstants.ZIP_EXT;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import ch.docuteam.converter.OOConverter;
import ch.docuteam.converter.PDFToolsConverter;
import ch.docuteam.converter.exceptions.PDFToolsConverterBadVMArgumentException;
import ch.docuteam.darc.ingest.AIPCreator;
import ch.docuteam.darc.ingest.AIPCreatorProxy;
import ch.docuteam.darc.mdconfig.LevelOfDescription;
import ch.docuteam.darc.mets.Document;
import ch.docuteam.darc.mets.Document.Mode;
import ch.docuteam.darc.sa.SubmissionAgreement;
import ch.docuteam.darc.sa.SubmissionAgreement.Overview;
import ch.docuteam.mapping.csv.CsvToSipImporter;
import ch.docuteam.mapping.csv.ImportResult;
import ch.docuteam.mapping.csv.SipCreationArgs;
import ch.docuteam.packer.admin.BuildInfo;
import ch.docuteam.packer.gui.FileProperty;
import ch.docuteam.packer.gui.filePreview.FilePreviewer;
import ch.docuteam.packer.gui.launcher.actions.AboutAction;
import ch.docuteam.packer.gui.launcher.actions.HelpAction;
import ch.docuteam.packer.gui.launcher.actions.OpenDocuteamHomepageAction;
import ch.docuteam.packer.gui.launcher.actions.OpenExceptionWindowAction;
import ch.docuteam.packer.gui.launcher.actions.OpenSIPInWorkspaceAction;
import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.packer.gui.util.Util;
import ch.docuteam.tools.exception.ExceptionCollector;
import ch.docuteam.tools.file.ConfigurationFileLoader;
import ch.docuteam.tools.file.FileChecksumCalculator.Algorithm;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.file.MetadataProviderDROID;
import ch.docuteam.tools.file.PropertiesWithResolvedEnvVars;
import ch.docuteam.tools.file.exception.DROIDCouldNotInitializeException;
import ch.docuteam.tools.file.exception.FileUtilExceptionListException;
import ch.docuteam.tools.gui.ScrollableMessageDialog;
import ch.docuteam.tools.gui.SmallPeskyMessageWindow;
import ch.docuteam.tools.gui.SystemOutView;
import ch.docuteam.tools.id.DOIGenerator;
import ch.docuteam.tools.os.OperatingSystem;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.string.StringUtil;
import ch.docuteam.tools.translations.I18N;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author denis
 */
public class LauncherView extends JFrame {

    // The following static fields contain default values - they might be
    // overwritten when reading the property file.
    // In addition, some of these fields can be overwritten using command line
    // parameters:

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String levelsConfigFileName = DEFAULT_LEVELS_CONFIG_FILE_NAME;

    private String configDirectory = "./config";

    private PropertiesWithResolvedEnvVars properties;

    private String sipDirectory;

    private String dataDirectory;

    private String templateDirectory;

    private String exportsDirectory;

    private String reportsDirectory;

    private String reportsDestinationDirectory;

    private boolean newSIPDeleteSourcesByDefault;

    private boolean newSIPZippedByDefault;

    private boolean migrateFileKeepOriginal;

    private String lastUsedOpenOrSaveDirectory;

    protected FileProperty selectedSIP;

    protected List<SIPView> openedSIPViews = new Vector<SIPView>();

    protected List<FileProperty> sipsWithSavingInProgress = new Vector<FileProperty>();

    /*
     * An ACTION_HIDE_KEY can be added to actions to hide them from the GUI. For this each action which needs to
     * support this behavior should get an ACTION_HIDE_KEY at instantiation. All actions which are to be hidden must
     * be mentioned with the actionsNotVisible property in the docuteamPacker.properties file.
     */
    public static final String ACTION_HIDE_KEY = "actionHideKey";

    private List<String> hiddenActionKeyList = null;

    // General actions:
    protected Action aboutAction;

    protected Action openExceptionWindowAction;

    protected OpenDocuteamHomepageAction openDocuteamHomepageAction;

    protected Action helpAction;

    protected Action quitAction;

    // SA actions:
    protected Action updateSAsFromServerAction;

    // SIP actions:
    protected Action createNewSIPAction;

    protected Action createNewSIPFromTemplateAction;

    protected Action createNewSIPFromCSVAction;

    protected Action openSIPAction;

    protected Action openSIPReadWriteNoFileOpsAction;

    protected Action openSIPReadOnlyAction;

    // Workspace actions:
    protected Action showWorkspaceManagerAction;

    protected Action hideWorkspaceManagerAction;

    protected Action rereadWorkspaceFolderAction;

    protected Action selectWorkspaceFolderAction;

    protected Action searchWorkspaceAction;

    protected Action checkIngestFeedbackAction;

    private final Action openSIPInWorkspaceAction;

    private final Action openSIPInWorkspaceReadWriteNoFileOpsAction;

    private final Action openSIPInWorkspaceReadOnlyAction;

    protected Action renameSIPInWorkspaceAction;

    protected Action copySIPInWorkspaceAction;

    protected Action deleteSIPInWorkspaceAction;

    // Visual:

    private final JPopupMenu popupMenu;

    protected JMenuBar menuBar;

    protected JMenu programMenu;

    protected JMenu workspaceMenu;

    protected JMenu sipMenu;

    protected JMenu saMenu;

    protected JMenu windowMenu;

    protected JPanel northPanel;

    protected JPanel workspaceManagerPanel;

    protected JButton showWorkspaceManagerButton;

    protected JButton hideWorkspaceManagerButton;

    protected SIPTable sipTable;

    private final JTextField footerTextField;

    // The following fields contain default values - they will be overwritten
    // when the user resizes the view.

    protected Dimension ScreenSizeWithoutWorkspaceManager = DEFAULT_SCREEN_SIZE_WITHOUT_WORKSPACE_MANAGER;

    protected Dimension ScreenSizeWithWorkspaceManager = DEFAULT_SCREEN_SIZE_WITH_WORKSPACE_MANAGER;

    public String getSipDirectory() {
        return sipDirectory;
    }

    public void setSipDirectory(final String sipDirectory) {
        this.sipDirectory = sipDirectory;
    }

    public String getLastUsedOpenOrSaveDirectory() {
        return lastUsedOpenOrSaveDirectory;
    }

    public void setLastUsedOpenOrSaveDirectory(final String lastUsedOpenOrSaveDirectory) {
        this.lastUsedOpenOrSaveDirectory = lastUsedOpenOrSaveDirectory;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(final String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public String getTemplateDirectory() {
        return templateDirectory;
    }

    public void setTemplateDirectory(final String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    public String getExportsDirectory() {
        return exportsDirectory;
    }

    public void setExportsDirectory(final String exportsDirectory) {
        this.exportsDirectory = exportsDirectory;
    }

    public String getReportsDirectory() {
        return reportsDirectory;
    }

    public void setReportsDirectory(final String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    public String getReportsDestinationDirectory() {
        return reportsDestinationDirectory;
    }

    public boolean isDeleteSourcesByDefault() {
        return newSIPDeleteSourcesByDefault;
    }

    public boolean isNewSIPZippedByDefault() {
        return newSIPZippedByDefault;
    }

    public boolean isMigrateFileKeepOriginal() {
        return migrateFileKeepOriginal;
    }

    public Action getOpenSIPInWorkspaceAction() {
        return openSIPInWorkspaceAction;
    }

    public Action getOpenSIPInWorkspaceReadWriteNoFileOpsAction() {
        return openSIPInWorkspaceReadWriteNoFileOpsAction;
    }

    public Action getOpenSIPInWorkspaceReadOnlyAction() {
        return openSIPInWorkspaceReadOnlyAction;
    }

    public JTextField getFooterTextField() {
        return footerTextField;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public SIPTable getSipTable() {
        return sipTable;
    }

    // TODO this method and this field should probably go away from here and remain only in sipTable
    public void setSelectedSIP(final FileProperty selectedSIP) {
        this.selectedSIP = selectedSIP;
    }

    public String getConfigDirectory() {
        return configDirectory;
    }

    /**
     * @param args
     * @throws DROIDCouldNotInitializeException
     */
    public static void main(final String[] args) {

        final CommandLine commandLine = getCommandLine(args);

        // Create and open the launcher:
        final LauncherView l = new LauncherView(commandLine);

        // TODO find another solution, this thread keeps the java process running after closing the application (at
        // least on windows)
        // Create shudown hook:
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                l.shutdown();
            }
        });
        l.setVisible(true);

        // Check ingest feedback on startup:
        l.checkIngestFeedback(false);

        // Open a SIP on startup (if specified in the commandLine):
        l.openSIP(commandLine);
    }

    private Properties getProperties() {
        return this.properties;
    }

    /**
     * just for testing
     *
     * @param args
     * @throws DROIDCouldNotInitializeException
     */
    protected LauncherView(final String args) {
        this(getCommandLine(args));
    }

    protected LauncherView(final CommandLine args) {

        // super(sipDirectory);
        initialize(args);
        setTitle(sipDirectory);

        // Because the quit event is handled using a windowListener below
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                requestQuit();
            }
        });
        // PackerConstants.getPackerConstants();
        // String filename = this.getClass().getResource(PACKER_PNG).getFile();

        Logger.info("PACKER_PNG: " + PACKER_PNG);
        setIconImage(getImage(PACKER_PNG));
        // setIconImage(getImageIcon(PACKER_PNG));

        // Tables:

        // Right-align numbers in "size" and "last modified" column:
        final DefaultTableCellRenderer rightAlignmentRenderer = new DefaultTableCellRenderer();
        rightAlignmentRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        sipTable = new SIPTable(this);

        // Text Fields:
        footerTextField = new JTextField();
        footerTextField.setName(FOOTER_TEXT_FIELD);
        footerTextField.setEditable(false);

        // General actions:
        quitAction = new AbstractAction(I18N.translate("ActionQuit"), getImageIcon("Quit.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                requestQuit();
            }
        };
        quitAction.putValue(Action.ACCELERATOR_KEY, getKeyStroke(KeyEvent.VK_Q,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        quitAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipQuit"));

        aboutAction = new AboutAction(this);

        // SA actions:
        updateSAsFromServerAction = new AbstractAction(I18N.translate("ActionLoadSAsFromServer"),
            getImageIcon(DOWNLOAD_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                updateSAsFromServer();
            }
        };
        updateSAsFromServerAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipLoadSAsFromServer"));

        // Workspace actions:
        showWorkspaceManagerAction = new AbstractAction(I18N.translate("ActionShowWorkspaceManager"),
            getImageIcon(WORKSPACE_MANAGER_SHOW_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                showWorkspaceManager(true);
            }
        };
        showWorkspaceManagerAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipShowWorkspaceManager"));

        hideWorkspaceManagerAction = new AbstractAction(I18N.translate("ActionHideWorkspaceManager"),
            getImageIcon(WORKSPACE_MANAGER_HIDE_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                showWorkspaceManager(false);
            }
        };
        hideWorkspaceManagerAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipHideWorkspaceManager"));

        selectWorkspaceFolderAction = new AbstractAction(I18N.translate("ActionSelectWorkspaceFolder"),
            getImageIcon(OPEN_FOLDER_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                selectWorkspaceFolder();
            }
        };
        selectWorkspaceFolderAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate(
            "ToolTipSelectWorkspaceFolder"));

        searchWorkspaceAction = new AbstractAction(I18N.translate("ActionSearchWorkspace"), getImageIcon(
            SEARCH_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                openSearchView();
            }
        };
        searchWorkspaceAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        searchWorkspaceAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchWorkspace"));

        rereadWorkspaceFolderAction = new AbstractAction(I18N.translate("ActionRereadWorkspaceFolder"),
            getImageIcon(REDISPLAY_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                rereadWorkspaceFolder();
            }
        };
        rereadWorkspaceFolderAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate(
            "ToolTipRereadWorkspaceFolder"));
        // rereadWorkspaceFolderAction.putValue(Action.ACCELERATOR_KEY,
        // getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), KeyEvent.VK_F5, 0));

        createNewSIPAction = new AbstractAction(I18N.translate("ActionCreateNew"),
            getImageIcon("New.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                createNewSIP();
            }
        };
        createNewSIPAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        createNewSIPAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipCreateNew"));

        createNewSIPFromTemplateAction = new AbstractAction(I18N.translate("ActionCreateNewFromTemplate"),
            getImageIcon(NEW_FROM_TEMPLATE_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                createNewSIPFromTemplate();
            }
        };
        createNewSIPFromTemplateAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.ALT_DOWN_MASK));
        createNewSIPFromTemplateAction.putValue(Action.SHORT_DESCRIPTION,
            I18N.translate("ToolTipCreateNewFromTemplate"));

        createNewSIPFromCSVAction = new AbstractAction(I18N.translate("ActionCreateNewFromCSV"),
                getImageIcon(NEW_FROM_CSV_PNG)) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                createNewSIPFromCSV();
            }
        };
        createNewSIPFromCSVAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK));
        createNewSIPFromCSVAction.putValue(Action.SHORT_DESCRIPTION,
            I18N.translate("ToolTipCreateNewFromCSV"));

        openSIPAction = new AbstractAction(I18N.translate("ActionOpen"), getImageIcon(OPEN_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                openSIP(Mode.ReadWrite);
            }
        };
        openSIPAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        openSIPAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipOpen"));

        openSIPReadWriteNoFileOpsAction = new AbstractAction(I18N.translate("ActionOpenReadWriteNoFileOps"),
            getImageIcon("OpenReadWriteNoFileOps.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                openSIP(Mode.ReadWriteNoFileOps);
            }
        };
        openSIPReadWriteNoFileOpsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.ALT_DOWN_MASK));
        openSIPReadWriteNoFileOpsAction.putValue(Action.SHORT_DESCRIPTION,
            I18N.translate("ToolTipOpenReadWriteNoFileOps"));

        openSIPReadOnlyAction = new AbstractAction(I18N.translate("ActionOpenReadOnly"),
            getImageIcon("OpenReadOnly.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                openSIP(Mode.ReadOnly);
            }
        };
        openSIPReadOnlyAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK));
        openSIPReadOnlyAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipOpenReadOnly"));

        openSIPInWorkspaceAction = new OpenSIPInWorkspaceAction(this);

        openSIPInWorkspaceReadWriteNoFileOpsAction = new AbstractAction(
            I18N.translate("ActionOpenInWorkspaceReadWriteNoFileOps"),
            getImageIcon("OpenInWorkspaceReadWriteNoFileOps.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                openSelectedSIPInWorkspace(Mode.ReadWriteNoFileOps);
            }
        };
        openSIPInWorkspaceReadWriteNoFileOpsAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK));
        openSIPInWorkspaceReadWriteNoFileOpsAction.putValue(Action.SHORT_DESCRIPTION,
            I18N.translate("ToolTipOpenInWorkspaceReadWriteNoFileOps"));

        openSIPInWorkspaceReadOnlyAction = new AbstractAction(I18N.translate("ActionOpenInWorkspaceReadOnly"),
            getImageIcon("OpenInWorkspaceReadOnly.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                openSelectedSIPInWorkspace(Mode.ReadOnly);
            }
        };
        openSIPInWorkspaceReadOnlyAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK));
        openSIPInWorkspaceReadOnlyAction.putValue(Action.SHORT_DESCRIPTION,
            I18N.translate("ToolTipOpenInWorkspaceReadOnly"));

        renameSIPInWorkspaceAction = new AbstractAction(I18N.translate("ActionRenameInWorkspace"),
            getImageIcon("Rename.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                renameSIP();
            }
        };
        renameSIPInWorkspaceAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipRenameSIP"));
        renameSIPInWorkspaceAction.putValue(Action.ACCELERATOR_KEY,
            getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK, KeyEvent.VK_F2, InputEvent.ALT_DOWN_MASK,
                KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK));

        copySIPInWorkspaceAction = new AbstractAction(I18N.translate("ActionCopyInWorkspace"),
            getImageIcon("Copy.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                copySIP();
            }
        };
        copySIPInWorkspaceAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipCopySIP"));
        copySIPInWorkspaceAction.putValue(Action.ACCELERATOR_KEY, getKeyStroke(KeyEvent.VK_C,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.ALT_DOWN_MASK));

        deleteSIPInWorkspaceAction = new AbstractAction(I18N.translate("ActionDeleteInWorkspace"),
            getImageIcon(DELETE_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                deleteSIP();
            }
        };
        deleteSIPInWorkspaceAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipDeleteSIP"));
        deleteSIPInWorkspaceAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

        checkIngestFeedbackAction = new AbstractAction(I18N.translate("ActionCheckIngestFeedback"),
            getImageIcon("SubmitCheckIngestFeedback.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                checkIngestFeedback(true);
            }
        };
        checkIngestFeedbackAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipCheckIngestFeedback"));
        checkIngestFeedbackAction.setEnabled(AIPCreatorProxy.isUsable());

        // Menus:

        popupMenu = new JPopupMenu();
        popupMenu.setName(POPUP_MENU);

        addMenuItem(popupMenu, openSIPInWorkspaceAction, SIP_OPEN_IN_WORKSPACE_MENU_ITEM);
        popupMenu.add(new JMenuItem(openSIPInWorkspaceReadWriteNoFileOpsAction));
        popupMenu.add(new JMenuItem(openSIPInWorkspaceReadOnlyAction));
        popupMenu.addSeparator();
        popupMenu.add(new JMenuItem(renameSIPInWorkspaceAction));
        popupMenu.add(new JMenuItem(copySIPInWorkspaceAction));
        popupMenu.add(new JMenuItem(deleteSIPInWorkspaceAction));

        programMenu = new JMenu(I18N.translate("TitleMain"));
        programMenu.setIcon(getImageIcon("DocuteamPackerSmall.png"));

        programMenu.add(new JMenuItem(aboutAction));

        helpAction = new HelpAction(this);
        programMenu.add(new JMenuItem(helpAction));

        openDocuteamHomepageAction = new OpenDocuteamHomepageAction(this);
        programMenu.add(new JMenuItem(openDocuteamHomepageAction));

        programMenu.addSeparator();
        programMenu.add(new JMenuItem(quitAction));

        workspaceMenu = new JMenu(I18N.translate("MenuWorkspace"));
        workspaceMenu.setName(WORKSPACE_MENU);
        workspaceMenu.setIcon(getImageIcon("Workspace.png"));

        final JMenuItem selectWorkspaceFolderMenuItem = new JMenuItem(selectWorkspaceFolderAction);
        selectWorkspaceFolderMenuItem.setName(WORKSPACE_SELECT_FOLDER_MENU_ITEM);
        workspaceMenu.add(selectWorkspaceFolderMenuItem);

        workspaceMenu.add(new JMenuItem(rereadWorkspaceFolderAction));
        workspaceMenu.addSeparator();
        workspaceMenu.add(new JMenuItem(showWorkspaceManagerAction));
        workspaceMenu.add(new JMenuItem(hideWorkspaceManagerAction));
        workspaceMenu.addSeparator();
        workspaceMenu.add(new JMenuItem(searchWorkspaceAction));
        workspaceMenu.addSeparator();
        workspaceMenu.add(new JMenuItem(checkIngestFeedbackAction));
        sipMenu = new JMenu(I18N.translate("MenuFile"));
        sipMenu.setName(SIP_MENU);
        sipMenu.setIcon(getImageIcon("MenuFile.png"));

        final JMenuItem createNewMenuItem = new JMenuItem(createNewSIPAction);
        createNewMenuItem.setName(SIP_CREATE_NEW_MENU_ITEM);
        sipMenu.add(createNewMenuItem);

        sipMenu.add(new JMenuItem(createNewSIPFromTemplateAction));
        sipMenu.add(new JMenuItem(createNewSIPFromCSVAction));
        sipMenu.addSeparator();
        sipMenu.add(new JMenuItem(openSIPAction));
        sipMenu.add(new JMenuItem(openSIPReadWriteNoFileOpsAction));
        sipMenu.add(new JMenuItem(openSIPReadOnlyAction));
        sipMenu.addSeparator();
        sipMenu.add(new JMenuItem(openSIPInWorkspaceAction));
        sipMenu.add(new JMenuItem(openSIPInWorkspaceReadWriteNoFileOpsAction));
        sipMenu.add(new JMenuItem(openSIPInWorkspaceReadOnlyAction));
        sipMenu.addSeparator();
        sipMenu.add(new JMenuItem(renameSIPInWorkspaceAction));
        sipMenu.add(new JMenuItem(copySIPInWorkspaceAction));
        sipMenu.add(new JMenuItem(deleteSIPInWorkspaceAction));
        saMenu = new JMenu(I18N.translate("MenuSA"));
        saMenu.setIcon(getImageIcon("MenuSA.png"));
        saMenu.add(new JMenuItem(updateSAsFromServerAction));

        windowMenu = new JMenu(I18N.translate("MenuWindow"));
        windowMenu.setIcon(getImageIcon("MenuWindow.png"));

        openExceptionWindowAction = new OpenExceptionWindowAction(this);
        windowMenu.add(new JMenuItem(openExceptionWindowAction));

        menuBar = new JMenuBar();
        menuBar.add(programMenu);
        menuBar.add(workspaceMenu);
        menuBar.add(sipMenu);
        // only show this menu if the necessary property is defined
        if (properties.containsKey("docuteamPacker.SA.BASE.URL")) {
            menuBar.add(saMenu);
        }
        menuBar.add(windowMenu);
        menuBar.setVisible(true);
        setJMenuBar(menuBar);

        // Buttons:

        final JButton createNewSIPButton = new JButton(createNewSIPAction);
        createNewSIPButton.setHideActionText(true);

        final JButton createNewSIPFromTemplateButton = new JButton(createNewSIPFromTemplateAction);
        createNewSIPFromTemplateButton.setHideActionText(true);

        final JButton openSIPButton = new JButton(openSIPAction);
        openSIPButton.setHideActionText(true);

        final JButton openSIPReadWriteNoFileOpsButton = new JButton(openSIPReadWriteNoFileOpsAction);
        openSIPReadWriteNoFileOpsButton.setHideActionText(true);

        final JButton openSIPReadOnlyButton = new JButton(openSIPReadOnlyAction);
        openSIPReadOnlyButton.setHideActionText(true);

        final JButton selectWorkspaceFolderButton = new JButton(selectWorkspaceFolderAction);
        selectWorkspaceFolderButton.setHideActionText(true);

        showWorkspaceManagerButton = new JButton(showWorkspaceManagerAction);
        showWorkspaceManagerButton.setHideActionText(true);

        hideWorkspaceManagerButton = new JButton(hideWorkspaceManagerAction);
        hideWorkspaceManagerButton.setHideActionText(true);

        final JButton openSIPInWorkspaceButton = new JButton(openSIPInWorkspaceAction);
        openSIPInWorkspaceButton.setHideActionText(true);

        final JButton openSIPInWorkspaceReadWriteNoFileOpsButton = new JButton(
            openSIPInWorkspaceReadWriteNoFileOpsAction);
        openSIPInWorkspaceReadWriteNoFileOpsButton.setHideActionText(true);

        final JButton openSIPInWorkspaceReadOnlyButton = new JButton(openSIPInWorkspaceReadOnlyAction);
        openSIPInWorkspaceReadOnlyButton.setHideActionText(true);

        final JButton renameSIPButton = new JButton(renameSIPInWorkspaceAction);
        renameSIPButton.setHideActionText(true);

        final JButton copySIPButton = new JButton(copySIPInWorkspaceAction);
        copySIPButton.setHideActionText(true);

        final JButton deleteSIPButton = new JButton(deleteSIPInWorkspaceAction);
        deleteSIPButton.setHideActionText(true);

        final JButton searchWorkspaceButton = new JButton(searchWorkspaceAction);
        searchWorkspaceButton.setHideActionText(true);

        final JButton logoButton = new JButton(getImageIcon("Logo_docuteam_packer.png"));
        logoButton.setEnabled(true);
        logoButton.setHideActionText(true);
        logoButton.setContentAreaFilled(false);
        logoButton.setBorderPainted(false);
        logoButton.setToolTipText(I18N.translate("ToolTipOpenDocuteamHomepage"));
        logoButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                openDocuteamHomepageAction.openDocuteamHomepage();
            }
        });

        final Box buttonBox = new Box(BoxLayout.X_AXIS);
        // buttonBox.add(showWorkspaceManagerButton);
        // buttonBox.add(hideWorkspaceManagerButton);
        // buttonBox.add(Box.createHorizontalStrut(10));
        // buttonBox.add(selectWorkspaceFolderButton);
        buttonBox.add(searchWorkspaceButton);
        buttonBox.add(Box.createHorizontalStrut(20));
        buttonBox.add(createNewSIPButton);
        buttonBox.add(createNewSIPFromTemplateButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(openSIPButton);
        buttonBox.add(openSIPReadWriteNoFileOpsButton);
        buttonBox.add(openSIPReadOnlyButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(openSIPInWorkspaceButton);
        buttonBox.add(openSIPInWorkspaceReadWriteNoFileOpsButton);
        buttonBox.add(openSIPInWorkspaceReadOnlyButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(renameSIPButton);
        buttonBox.add(copySIPButton);
        buttonBox.add(deleteSIPButton);

        northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        northPanel.add(buttonBox, BorderLayout.WEST);
        northPanel.add(logoButton, BorderLayout.EAST);

        // Footer:
        final JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        footerPanel.add(footerTextField);

        // Arrange all in the main panel:
        workspaceManagerPanel = new JPanel(new BorderLayout());
        workspaceManagerPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        workspaceManagerPanel.add(new JScrollPane(sipTable.getTable()), BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(workspaceManagerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        setLocation(SCREEN_POSITION);
        showWorkspaceManager(DO_INITIALLY_OPEN_WORKSPACEMANAGER);

        enableOrDisableActions();
    }

    /**
     * Retrieves the list of actions that should not be visible from docuteamPacker.actionsNotVisible property Note:
     * At action instantiation, each action which is expected to be not visible should get an actionID using
     * putValue(putValue(ACTION_HIDE_KEY, actionID) call.
     *
     * @return
     */
    private List<String> getHiddenActionKeys() {
        final String actionsNotVisible = properties.getPropertyOrDefaultIfEmpty("docuteamPacker.actionsNotVisible",
            "");
        return Arrays.asList(actionsNotVisible.split("\\s?,\\s?"));
    }

    /**
     * The action is visible, if it is not found in the list of the docuteamPacker.actionsNotVisible property.
     *
     * @param action
     * @return
     */
    public boolean isActionVisible(final Action action) {
        final String actionId = action.getValue(ACTION_HIDE_KEY) != null ? action.getValue(ACTION_HIDE_KEY).toString()
            : null;
        final boolean isActionVisible = !isActionNotVisible(actionId);
        return isActionVisible;
    }

    /**
     * Checks if actionId should be visible or not
     *
     * @param actionId
     * @return true if this actionId should not be visible, false otherwise.
     */
    public boolean isActionNotVisible(final String actionId) {
        return hiddenActionKeyList.contains(actionId);
    }

    /**
     * Creates a menuItem for an action, with a name and adds it to the popupMenu.
     *
     * @param popupMenu
     * @param action
     * @param name
     */
    private void addMenuItem(final JPopupMenu popupMenu, final Action action, final String name) {
        final JMenuItem jMenuItem = new JMenuItem(action);
        jMenuItem.setName(name);
        popupMenu.add(jMenuItem);
    }

    /**
     * Parse the command line arguments.
     */
    protected static CommandLine getCommandLine(final String... args) {
        final Options options = new Options();
        options.addOption("open", true, "(Optional) This SIP will be opened after startup");
        options.addOption("configDir", true, "(Optional) This directory is used as the configuration directory");
        options.addOption("help", false, "(Optional) What are the options? Display this text in the console");

        CommandLine commandLine = null;
        try {
            commandLine = new GnuParser().parse(options, args);
        } catch (final ParseException ex) {
            System.err.println("Command line parsing failed: " + ex.getMessage());
            new HelpFormatter().printHelp("docuteam packer", options);
            System.exit(10);
            return null;
        }

        if (commandLine.hasOption("help")) {
            new HelpFormatter().printHelp("java -jar docuteamPacker.jar", options);
            System.exit(11);
            return null;
        }

        return commandLine;
    }

    /**
     * Initialize the system.
     *
     * @throws DROIDCouldNotInitializeException
     */
    protected void initialize(final CommandLine commandLine) {
        final JFrame splashWindow = new JFrame();
        splashWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        splashWindow.setIconImage(getImage(PACKER_PNG));
        splashWindow.setUndecorated(true);
        splashWindow.add(
            new JLabel(BuildInfo.getVersion(), getImageIcon("SplashScreen.png"), SwingConstants.CENTER));
        splashWindow.setSize(300, 100);
        splashWindow.setLocationRelativeTo(null);

        try {
            splashWindow.setVisible(true);

            // 60 seconds until a tooltip text disappears should be enough:
            ToolTipManager.sharedInstance().setDismissDelay(60000);
            Toolkit.getDefaultToolkit().setDynamicLayout(true);

            Logger.info("Initializing docuteam packer:");

            final ConfigurationFileLoader configurationFileLoader = new ConfigurationFileLoader(commandLine.getOptionValue(
                "configDir"));

            // If a config directory is specified, take these configurations
            // from there (if those files ARE there - if not, use the default
            // folder = "./config"):
            // - docuteamPacker.properties
            // - levels.xml
            if (commandLine.hasOption("configDir")) {
                configDirectory = commandLine.getOptionValue("configDir");
                Logger.info("    configDir: " + configDirectory);

                // Specify the location of the levels file:
                final String levelsConfigFile = configDirectory + "/levels.xml";
                if (new File(levelsConfigFile).exists()) {
                    Logger.info("    Init levels from: " + levelsConfigFile);
                    levelsConfigFileName = levelsConfigFile;
                }
            } else {
                Logger.debug("Command line doesn't have a configDir, commandLine:" + commandLine.getArgList());
            }

            initializeProperties(new PropertiesWithResolvedEnvVars(configurationFileLoader.loadProperties(
                PROPERTY_FILE_NAME)));

            hiddenActionKeyList = getHiddenActionKeys();

            // TODO this here seems to be some encapsulation violation, why
            // initializing sipview before any thought of opening it
            SIPView.initialize(properties);
        } finally {
            splashWindow.setVisible(false);
            splashWindow.dispose();
        }
    }

    // This is called on startup only:
    protected void initializeProperties(final PropertiesWithResolvedEnvVars properties) {
        this.properties = properties;

        final String language = properties.getProperty("docuteamPacker.displayLanguage");
        if (language != null) {
            Locale.setDefault(new Locale(language));
            JComponent.setDefaultLocale(Locale.getDefault());
        }

        final String doiGenNextNumberFilePath_prop = "docuteamPacker.DOIGenerator.nextNumberFilePath";
        final String doiGenPrefix_prop = "docuteamPacker.DOIGenerator.prefix";

        String doiGenNextNumberFilePath = null;
        String doiGenPrefix = null;

        doiGenNextNumberFilePath = properties.getProperty(doiGenNextNumberFilePath_prop);
        doiGenPrefix = properties.getProperty(doiGenPrefix_prop);

        if (doiGenNextNumberFilePath != null) {
            DOIGenerator.initialize(doiGenPrefix, doiGenNextNumberFilePath);
        } else {
            if (doiGenPrefix != null) {
                DOIGenerator.initializePrefix(doiGenPrefix);
            }
        }

        SystemOutView.install();

        final boolean doOpenSystemOutViewOnOutput = "true"
            .equalsIgnoreCase(properties.getProperty("docuteamPacker.openSystemOutViewOnOutput", "true"));
        Logger.info("    openSystemOutViewOnOutput: " + doOpenSystemOutViewOnOutput);

        // Switch on or off the automatic popping-up of the SystemOutView on
        // output via property file:
        SystemOutView.setDoPopUpOnWrite(doOpenSystemOutViewOnOutput);

        final StringBuilder additionalInfoText = new StringBuilder("----------\n")
            .append(BuildInfo.getProduct())
            .append(" ")
            .append(BuildInfo.getVersion())
            .append(" (")
            .append(BuildInfo.getLastChange())
            .append(")\n----------\n")
            .append(OperatingSystem.osName())
            .append(" ")
            .append(OperatingSystem.osVersion())
            .append(" ")
            .append(System.getProperty("os.arch"))
            .append(" (")
            .append(OperatingSystem.userLanguage())
            .append(")\n----------\n")
            .append(System.getProperty("java.vendor"))
            .append(" ")
            .append(System.getProperty("java.version"))
            .append(")\n----------\n")
            .append(OperatingSystem.userName())
            .append("\n----------\n");

        for (final Object key : properties.keySet()) {
            additionalInfoText.append(key).append(" = ").append(properties.get(key)).append("\n");
        }
        additionalInfoText
            .append("----------\n");
        SystemOutView.setAdditionalInfoText(additionalInfoText.toString());

        final String displayLanguageFromPropertyFile = properties.getProperty("docuteamPacker.displayLanguage", "");
        final String usedDisplayLanguage = displayLanguageFromPropertyFile.isEmpty() ? OperatingSystem.userLanguage()
            : displayLanguageFromPropertyFile;
        I18N.initialize(usedDisplayLanguage, "translations.Translations");
        Logger.info("    displayLanguage: " + usedDisplayLanguage);

        final String defaultChecksumAlgorithm = properties.getProperty("docuteamPacker.defaultChecksumAlgorithm", "");
        if (!defaultChecksumAlgorithm.equals("")) {
            try {
                ch.docuteam.darc.premis.Object.setDefaultMessageDigestAlgorithm(Algorithm.lookup(
                    defaultChecksumAlgorithm));
                Logger.info("    defaultChecksumAlgorithm: " + defaultChecksumAlgorithm);
            } catch (final NoSuchAlgorithmException e) {
                Logger.error(I18N.translate("MessageNoSuchAlgorithmException", defaultChecksumAlgorithm,
                    ch.docuteam.darc.premis.Object.getDefaultMessageDigestAlgorithm().getCode()));
            }
        }

        newSIPDeleteSourcesByDefault = Boolean.parseBoolean(properties.getProperty(
            "docuteamPacker.newSIPDeleteSourcesByDefault", NEW_SIP_DEFAULT_DELETE_SOURCES));
        Logger.info("    newSIPDeleteSourcesByDefault: " + newSIPDeleteSourcesByDefault);

        newSIPZippedByDefault = Boolean.parseBoolean(properties.getProperty("docuteamPacker.newSIPDefaultsToZipped",
            NEW_SIP_DEFAULTS_TO_ZIPPED));
        Logger.info("    newSIPDefaultsToZipped: " + newSIPZippedByDefault);

        migrateFileKeepOriginal = Boolean.parseBoolean(properties.getProperty(
            "docuteamPacker.migrateFileKeepOriginal",
            MIGRATE_FILE_KEEP_ORIGINAL));
        Logger.info("    migrateFileKeepOriginal: " + migrateFileKeepOriginal);

        // Initialize various directories:

        // SIPDir, create if not existing
        sipDirectory = FileUtil.asCanonicalFileName(properties.getPropertyOrDefaultIfEmpty("docuteamPacker.SIPDir" +
            PROPERTY_FILE_PATH_OS_SUFFIX, USER_HOME));
        final Path sipDirPath = FileSystems.getDefault().getPath(sipDirectory);
        try {
            Files.createDirectories(sipDirPath);
        } catch (final IOException e) {
            Logger.error("error creating SIPDir: ", e);
        }
        lastUsedOpenOrSaveDirectory = sipDirectory;
        Logger.info("    SIPDirectory: " + sipDirectory);

        // dataDir, create if not existing
        dataDirectory = FileUtil.asCanonicalFileName(properties.getPropertyOrDefaultIfEmpty("docuteamPacker.dataDir" +
            PROPERTY_FILE_PATH_OS_SUFFIX, sipDirectory));
        try {
            final Path dataDirectoryPath = FileSystems.getDefault().getPath(dataDirectory);
            Files.createDirectories(dataDirectoryPath);
        } catch (final IOException e) {
            Logger.error("error creating dataDirectory: ", e);
        }
        Logger.info("    dataDirectory: " + dataDirectory);

        templateDirectory = FileUtil.asCanonicalFileName(properties.getPropertyOrDefaultIfEmpty(
            "docuteamPacker.templateDir" + PROPERTY_FILE_PATH_OS_SUFFIX, sipDirectory));
        Logger.info("    templateDirectory: " + templateDirectory);

        exportsDirectory = FileUtil.asCanonicalFileName(properties.getPropertyOrDefaultIfEmpty(
            "docuteamPacker.exportsDir" + PROPERTY_FILE_PATH_OS_SUFFIX, "./templates/exports/"));
        Logger.info("    exportsDirectory: " + exportsDirectory);

        reportsDirectory = FileUtil.asCanonicalFileName(properties.getPropertyOrDefaultIfEmpty(
            "docuteamPacker.reportsDir" + PROPERTY_FILE_PATH_OS_SUFFIX, "./templates/reports/"));
        Logger.info("    reportsDirectory: " + reportsDirectory);

        reportsDestinationDirectory = FileUtil.asCanonicalFileName(properties.getPropertyOrDefaultIfEmpty(
            "docuteamPacker.reportsDestinationDir" + PROPERTY_FILE_PATH_OS_SUFFIX,
            OperatingSystem
                .userHome() + "Desktop"));
        Logger.info("    reportsDestinationDirectory: " + reportsDestinationDirectory);

        Document.setBackupFolder(properties.getProperty("docuteamPacker.backupDir" + PROPERTY_FILE_PATH_OS_SUFFIX));
        Logger.info("    backupDirectory: " + Document.getBackupFolder());

        FileUtil.setTempFolder(properties.getPropertyOrDefaultIfEmpty("docuteamPacker.tempDir" +
            PROPERTY_FILE_PATH_OS_SUFFIX, OperatingSystem.javaTempDir() + "DocuteamPacker"));
        Logger.info("    tempDirectory: " + FileUtil.getTempFolder());

        Document.setLockFilesDirectory(properties.getProperty("docuteamPacker.lockFilesDir" +
            PROPERTY_FILE_PATH_OS_SUFFIX));
        Logger.info("    lockFilesDirectory: " + Document.getLockFilesDirectory());

        try {
            AIPCreatorProxy.initializeImpl(properties.getProperty("docuteamPacker.AIPCreator.className"));

            if (AIPCreatorProxy.isUsable()) {
                // forward the properties to the aipcreator implementation
                AIPCreatorProxy.initialize(properties);
            }
        } catch (final Exception e) {
            Logger.warn(I18N.translate("MessageAIPCreatorInitializationException", properties.getProperty(
                "docuteamPacker.AIPCreator.className")), e);
            // Leave the AIPCreatorProxy uninitialized
            AIPCreatorProxy.initializeImpl((AIPCreator) null);
        }

        // Initialize LevelOfDescriptions:
        Logger.info("    levels: " + levelsConfigFileName);
        LevelOfDescription.setInitializationFilePath(levelsConfigFileName);

        // Use specific DROID signature file if defined
        final String droidSigFilePath = properties.getProperty("docuteamPacker.droid.signatureFile", "");
        MetadataProviderDROID.setSignatureFileWithFallback(droidSigFilePath);
        // Use specific DROID container file if defined
        final String droidContFilePath = properties.getProperty("docuteamPacker.droid.containerFile", "");
        MetadataProviderDROID.setContainerSignatureFileWithFallback(droidContFilePath);
        // Use specific DROID extension setting if defined
        final String droidExtensionUsage = properties.getProperty("docuteamPacker.droid.extensionUsage", "");
        if (droidExtensionUsage != null && !droidExtensionUsage.isEmpty()) {
            Logger.info("    Init droid with extension usage: " + droidExtensionUsage);
            if (StringUtil.isNumeric(droidExtensionUsage)) {
                MetadataProviderDROID.setExtensionUsage(Integer.parseInt(droidExtensionUsage));
            } else {
                Logger.warn("Parameter must be a numerical value: " + droidExtensionUsage);
            }
        }

        // Initialize SA:
        SubmissionAgreement.initializeBaseURL(properties.getProperty("docuteamPacker.SA.BASE.URL", ""));
        Logger.info("    SABaseURL: " + SubmissionAgreement.getBaseURL());

        // Update SAs from server on startup only if the property "docuteamPacker.SA.getSAsFromServerOnStartup" is
        // "true":
        if ("true".equalsIgnoreCase(properties.getProperty("docuteamPacker.SA.getSAsFromServerOnStartup", "false"))) {
            Logger.info("    getSAsFromServerOnStartup: true");

            try {
                final List<String> saNamesToBeDeleted = updateSAsFromServer();

                // When in dev mode, don't delete any SAs
                if (!saNamesToBeDeleted.isEmpty()) {
                    // Ask whether to delete the excessive SAs.
                    // Don't use "JOptionPane.showConfirmDialog(...)" because
                    // that way we can't explicitly specify the position of the
                    // dialog.

                    final StringBuilder confirmMessage = new StringBuilder(
                        I18N.translate("QuestionDeleteExcessiveSAs") + "\n");
                    for (final String s : saNamesToBeDeleted) {
                        confirmMessage.append("\n").append(s);
                    }

                    final JOptionPane optionPane = new JOptionPane(confirmMessage, JOptionPane.QUESTION_MESSAGE,
                        JOptionPane.YES_NO_OPTION);
                    final JDialog dialog = optionPane.createDialog("Confirmation");
                    dialog.pack();

                    final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                    final Dimension dialogSize = dialog.getSize();
                    final int x = (screenSize.width - dialogSize.width) / 2;
                    final int y = (screenSize.height / 2 - dialogSize.height) / 2;
                    dialog.setLocation(x, y);
                    dialog.setVisible(true);

                    final Object result = optionPane.getValue();
                    if (result != null && ((Integer) result).intValue() == 0) {
                        // Delete the excessive SAs:
                        for (final String s : saNamesToBeDeleted) {
                            Logger.debug("Deleting SA: " + s);
                            FileUtil.delete(SubmissionAgreement.getSAFolder() + "/" + s);
                        }
                    }
                }
            } catch (final Exception ex) {
                Logger.warn("Could not update SAs from Server: " + ex.getMessage());
            }
        } else {
            Logger.info("    getSAsFromServerOnStartup: false");
        }

        OOConverter.initializeDontWait(properties.getProperty("docuteamPacker.OOConverter.path" +
            PROPERTY_FILE_PATH_OS_SUFFIX, ""));
        Logger.info("    ooConverterPath: " + OOConverter.getConverterPath());

        // Initialize 3-Heights Document Converter:
        final String pdftoolsURL = properties.getProperty("docuteamPacker.pdftools.url", "");
        if (!pdftoolsURL.isEmpty()) {
            Logger.getLogger().info("    3-Heights Document Converter URL: " + pdftoolsURL);
            try {
                PDFToolsConverter.initialize(pdftoolsURL);
            } catch (SocketException | PDFToolsConverterBadVMArgumentException e) {
                Logger.warn("Couldn't connect to the converter service", e);
            }
        }

        // Initialize FilePreviewer:
        final String filePreviewCacheSizeLimit = properties.getProperty("docuteamPacker.filePreviewer.cacheSizeLimit",
            "");
        if (!filePreviewCacheSizeLimit.isEmpty()) {
            FilePreviewer.setCacheSizeLimit(new Integer(filePreviewCacheSizeLimit));
        }
        Logger.info("    filePreviewCacheSizeLimit: " + FilePreviewer.getCacheSizeLimit());

        // Initialize UIManager:
        try {
            if (Boolean.parseBoolean(properties.getProperty("docuteamPacker.useSystemLookAndFeel",
                USE_SYSTEM_LOOK_AND_FEEL))) {
                // com.sun.java.swing.plaf.windows.WindowsLookAndFeel on Windows, com.apple.laf.AquaLookAndFeel on OS
                // X.
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                // Switches to Metal (= Default cross platform laf)
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (final Exception e) {
        } // Ignore it and set default LAF
        Logger.info("    lookAndFeel: " + UIManager.getLookAndFeel().getName());
    }

    /**
     * This is performed every time the application quits normally AND when it quits abnormally or the Java VM is
     * terminated. This is a safety net to cleanup the workspace, delete all locks, and remove temporary files. Veto
     * is not possible.
     */
    protected void shutdown() {
        try {
            for (final SIPView sipView : openedSIPViews) {
                if (sipView.getDocument() != null) {
                    sipView.getDocument().unlockIfNecessary();
                }
            }

            // The following calls seem to block the process from properly finishing (because Logger is used?)

            // TODO: is it correct to do the cleanup in the EDT (Event Dispatch Thread)?
            // OOConverter.disconnect();

            // TODO: is it correct to do the cleanup in the EDT (Event Dispatch Thread)?
            // FilePreviewPanel.cleanupTemporaryFiles();
        } catch (final Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }

    protected List<String> updateSAsFromServer() {
        try {
            return SubmissionAgreement.updateSAsFromServer();
        } catch (final MalformedURLException ex) {
            // MainFrame might be null here (on application startup):
            JOptionPane.showMessageDialog(this, I18N.translate("MessageCantUpdateSAsFromServer"),
                I18N.translate("TitleCantUpdateSAsFromServer"), JOptionPane.ERROR_MESSAGE);
        } catch (final Exception e) {
            Logger.error(e.getMessage(), e);
        }

        return new ArrayList<String>();
    }

    /**
     * The [Quit] button was clicked, or the [Window close] button was clicked, or <ctrl-q> was typed.
     * First confirm. If confirmed and if the current document was modified, ask if to save the document.
     */
    private boolean requestQuit() {
        Logger.debug("Requesting quit...");

        if (sipsWithSavingInProgress.size() > 0) {
            Logger.debug("Quit stopped by SIPs currently being saved:\n" + sipsWithSavingInProgress);
            return false;
        }

        // I have to use a copy of the openedSIPViews list otherwise I get a
        // ConcurrentModificationException
        // because the method "sipView.closeButtonClicked()" removes this
        // sipView from this list.
        // TODO investigate why is this like that
        final List<SIPView> openedSIPViews = new ArrayList<SIPView>(this.openedSIPViews);
        for (final SIPView sipView : openedSIPViews) {
            sipView.toFront();

            // If any of the sipViews cancelled, stop quitting:
            if (!sipView.closeButtonClicked()) {
                Logger.debug("Quit stopped by: '" + sipView.getFileProperty().getFile().getAbsolutePath() + "'");
                return false;
            }
        }

        SearchView.closeAndDispose();
        setVisible(false);
        dispose();

        // Set the tempFolder and all files and folders within to writable:
        FileUtil.setWritable(FileUtil.getTempFolder());

        System.exit(0); // This triggers the method shutdown()

        // This method has to return a boolean value even though I quit the
        // program before.
        return true;
    }

    /**
     * Remove this sipView from the list of opened sipViews and refresh the SIP table.
     *
     * @param sipView
     */
    public void unregister(final SIPView sipView) {
        // Remove the closed SIPView from the openedSIPViews list:
        if (openedSIPViews.contains(sipView)) {
            openedSIPViews.remove(sipView);
        }
        final FileProperty fileProperty = sipView.getFileProperty();
        if (fileProperty.isInWorkspace()) {
            final int row = sipTable.getSipTableModel().getRowIndexOfSip(fileProperty);
            if (row > -1) {
                // row found in the sipTable (if the workspace changed, it cannot be found)
                fileProperty.setLockedBy("");
                sipTable.getSipTableModel().fireTableCellUpdated(row, Column.LOCKED_BY.ordinal());
            }
        }
    }

    public KeyStroke getKeyStroke(final int keyEvent, final int modifiers) {
        return getKeyStroke(keyEvent, modifiers, keyEvent, modifiers);
    }

    public KeyStroke getKeyStroke(final int macKeyEvent, final int macModifiers, final int pcKeyEvent,
                                  final int pcModifiers) {
        return getKeyStroke(macKeyEvent, macModifiers, pcKeyEvent, pcModifiers, pcKeyEvent, pcModifiers);
    }

    public KeyStroke getKeyStroke(final int macKeyEvent, final int macModifiers, final int winKeyEvent,
                                  final int winModifiers,
                                  final int linuxKeyEvent, final int linuxModifiers) {
        if (OperatingSystem.isMacOSX()) {
            return KeyStroke.getKeyStroke(macKeyEvent, macModifiers);
        } else if (OperatingSystem.isWindows()) {
            return KeyStroke.getKeyStroke(winKeyEvent, winModifiers);
        } else if (OperatingSystem.isLinux()) {
            return KeyStroke.getKeyStroke(linuxKeyEvent, linuxModifiers);
        } else {
            return KeyStroke.getKeyStroke(winKeyEvent, winModifiers);
        }
    }

    /**
     * This document is just being saved. When wanting to open it, wait until saving is finished.
     *
     * @param document
     */
    public void savingDocumentInProgress(final SIPView sipView) {
        sipsWithSavingInProgress.add(sipView.getFileProperty());
    }

    /**
     * This document is finished being saved. When wanting to open it, now go on!
     *
     * @param document
     */
    public void savingDocumentFinished(final SIPView sipView) {
        sipsWithSavingInProgress.remove(sipView.getFileProperty());
    }

    /**
     * Refresh the SIP table. This is used when I know that the number of SIPs in the workspace was not changed.
     */
    public void refreshSIPTable() {
        final int sel = sipTable.getTable().getSelectedRow();
        sipTable.getSipTableModel().fireTableDataChanged();

        // Try to retain the current selection index. If this fails, ignore it.
        try {
            sipTable.getTable().getSelectionModel().setSelectionInterval(sel, sel);
        } catch (final Exception ex) {
            // TODO this looks odd
        }
    }

    protected void versionLabelClicked(final MouseEvent e) {
        for (final SIPView sipView : openedSIPViews) {
            Logger.info(sipView.getFileProperty().getFile().getPath());
        }
    }

    protected void showWorkspaceManager(final boolean doShow) {
        if (doShow) {
            // rereadSIPTable();

            showWorkspaceManagerAction.setEnabled(false);
            hideWorkspaceManagerAction.setEnabled(true);

            workspaceManagerPanel.setVisible(true);
            showWorkspaceManagerButton.setVisible(false);
            hideWorkspaceManagerButton.setVisible(true);

            // Remember the screen proportions before changing the view (except when the view is not yet visible):
            if (isVisible()) {
                getSize(ScreenSizeWithoutWorkspaceManager);
            }
            setPreferredSize(ScreenSizeWithWorkspaceManager);
            setResizable(true);
            pack();
        } else {
            sipTable.getTable().clearSelection();

            showWorkspaceManagerAction.setEnabled(true);
            hideWorkspaceManagerAction.setEnabled(false);

            workspaceManagerPanel.setVisible(false);
            showWorkspaceManagerButton.setVisible(true);
            hideWorkspaceManagerButton.setVisible(false);

            // Remember the screen proportions before changing the view (except when the view is not yet visible):
            if (isVisible()) {
                getSize(ScreenSizeWithWorkspaceManager);
            }
            setPreferredSize(ScreenSizeWithoutWorkspaceManager);
            setResizable(true);
            pack();
        }
    }

    /**
     * TODO: REFACTOR THIS!!! SwingWorker issue (PACKER-103)
     */
    protected void createNewSIP() {
        final CreateNewSIPDialog createNewSIPDialog = new CreateNewSIPDialog(this);
        if (!createNewSIPDialog.goButtonWasClicked) {

            return;
        }
        final String rootFolderName = createNewSIPDialog.rootFolderNameTextField.getText();
        final String sourceFileOrFolderName = createNewSIPDialog.sourceFileOrFolderTextField.getText();
        final boolean createEmptySIP = createNewSIPDialog.selectSIPEmptyRadioButton.isSelected();
        final boolean deleteSources = createNewSIPDialog.isDeleteSourcesCheckBoxSelected();
        final String destinationFolderName = createNewSIPDialog.destinationFolderTextField.getText();
        String destinationFileName = createNewSIPDialog.destinationNameTextField.getText();
        final boolean beZIP = createNewSIPDialog.beZIPCheckBox.isSelected();
        final Overview saOverview = (Overview) createNewSIPDialog.saComboBox.getSelectedItem();

        final String initialSourceFileOrFolder = createEmptySIP ? rootFolderName : sourceFileOrFolderName;
        if (initialSourceFileOrFolder.isEmpty() || destinationFileName.isEmpty()) {
            return;
        }

        if (beZIP) {
            if (!destinationFileName.toLowerCase().endsWith(ZIP_EXT)) {
                destinationFileName += ZIP_EXT;
            }
        } else {
            if (destinationFileName.toLowerCase().endsWith(ZIP_EXT)) {
                destinationFileName = destinationFileName.substring(0, destinationFileName.length() - 4);
            }
        }

        final String newSIPFileName = destinationFolderName + "/" + destinationFileName;

        new SwingWorker<Integer, Object>() {

            @Override
            public Integer doInBackground() {
                footerTextField.setText(I18N.translate("MessageFooterNewFile") + newSIPFileName + "...");
                final SmallPeskyMessageWindow waitWindow = SmallPeskyMessageWindow.openBlocking(LauncherView.this,
                    I18N.translate("MessageTempCreatingSIP"));

                Document document = null;
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    String selectedSAid = null;
                    String selectedDSSid = null;
                    if (saOverview != null) {
                        selectedSAid = saOverview.saId;
                        selectedDSSid = saOverview.dssId;
                    }

                    ExceptionCollector.clear();
                    document = createEmptySIP
                        ? Document.createNewWithRootFolderName(newSIPFileName, initialSourceFileOrFolder,
                        selectedSAid, selectedDSSid, OPERATOR, waitWindow)
                        : Document.createNewWithRoot(newSIPFileName, initialSourceFileOrFolder, selectedSAid,
                        selectedDSSid, OPERATOR, waitWindow);

                    footerTextField.setText(I18N.translate("MessageFooterNewFile") + newSIPFileName);

                    Util.showAllFromExceptionCollector(waitWindow, LauncherView.this);

                    if (deleteSources &&
                        JOptionPane.showConfirmDialog(LauncherView.this,
                            I18N.translate("QuestionDeleteSources"), I18N.translate("TitleDeleteSources"),
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        // TODO is it possible to move this to the trash instead of deleting directly?
                        try {
                            FileUtil.delete(initialSourceFileOrFolder);
                        } catch (final FileUtilExceptionListException e) {
                            JOptionPane.showMessageDialog(LauncherView.this, e.toString(), I18N.translate(
                                    "TitleCouldNotDeleteSources"),
                                JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (final java.lang.Exception e) {
                    Logger.error(e.getMessage(), e);
                    document = null;

                    footerTextField.setText(I18N.translate("MessageFooterCantCreateFile") + newSIPFileName);

                    // Close the waitWindow here otherwise it interferes with
                    // the MessageDialog:
                    waitWindow.close();
                    JOptionPane.showMessageDialog(LauncherView.this, e.toString(), I18N.translate(
                            "TitleCantCreateSIP"),
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    // At this point, document may or may not be null:
                    if (document != null) {
                        try {
                            document.unlockIfNecessary();
                            document.cleanupWorkingCopy();
                        } catch (final Exception e) {
                            Logger.error(e.getMessage(), e);
                            JOptionPane.showMessageDialog(LauncherView.this, e.toString(),
                                "Unable to cleanup working folder due to errors", JOptionPane.ERROR_MESSAGE);
                        }

                        sipTable.rereadSIPTable(); // Refresh the SIP list
                        final FileProperty fileProperty = findFilePropertyInSIPList(
                            new File(document.getOriginalSIPFolder()));
                        openSIP(fileProperty, Mode.ReadWrite, null);
                    }

                    waitWindow.close(); // In case it was not closed yet...
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

                return 0;
            }
        }.execute();
    }

    protected void createNewSIPFromTemplate() {
        final CreateNewSIPFromTemplateDialog createNewSIPFromTemplateDialog = new CreateNewSIPFromTemplateDialog(
            this);
        if (!createNewSIPFromTemplateDialog.goButtonWasClicked) {
            return;
        }

        final String templateName = (String) createNewSIPFromTemplateDialog.templateComboBox.getSelectedItem();
        final String destinationFolderName = createNewSIPFromTemplateDialog.destinationFolderTextField.getText();
        String destinationFileName = createNewSIPFromTemplateDialog.destinationNameTextField.getText();
        final boolean beZIP = createNewSIPFromTemplateDialog.beZIPCheckBox.isSelected();

        if (destinationFileName.isEmpty()) {
            return;
        }

        if (beZIP) {
            if (!destinationFileName.toLowerCase().endsWith(ZIP_EXT)) {
                destinationFileName += ZIP_EXT;
            }
        } else {
            if (destinationFileName.toLowerCase().endsWith(ZIP_EXT)) {
                destinationFileName = destinationFileName.substring(0, destinationFileName.length() - 4);
            }
        }

        final String newSIPFileName = destinationFolderName + "/" + destinationFileName;

        new SwingWorker<Integer, Object>() {

            @Override
            public Integer doInBackground() {
                footerTextField.setText(I18N.translate("MessageFooterNewFile") + newSIPFileName + "...");
                final SmallPeskyMessageWindow waitWindow = SmallPeskyMessageWindow.openBlocking(LauncherView.this,
                    I18N.translate("MessageTempCreatingSIP"));

                Document document = null;
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    ExceptionCollector.clear();
                    document = Document.createNewFromTemplate(templateDirectory + "/" + templateName, newSIPFileName,
                        OPERATOR);

                    footerTextField.setText(I18N.translate("MessageFooterNewFile") + newSIPFileName);

                    Util.showAllFromExceptionCollector(waitWindow, LauncherView.this);
                } catch (final java.lang.Exception e) {
                    Logger.error(e.getMessage(), e);
                    document = null;

                    footerTextField.setText(I18N.translate("MessageFooterCantCreateFile") + newSIPFileName);

                    // Close the waitWindow here otherwise it interferes with
                    // the MessageDialog:
                    waitWindow.close();
                    JOptionPane.showMessageDialog(LauncherView.this, e.toString(),
                        I18N.translate("TitleCantCreateSIP"), JOptionPane.ERROR_MESSAGE);
                } finally {
                    // At this point, document may or may not be null:
                    if (document != null) {
                        try {
                            document.unlockIfNecessary();
                            document.cleanupWorkingCopy();
                        } catch (final Exception e) {
                            Logger.error(e.getMessage(), e);
                            JOptionPane.showMessageDialog(LauncherView.this, e.toString(),
                                "Unable to cleanup working folder due to errors", JOptionPane.ERROR_MESSAGE);
                        }

                        final FileProperty fileProperty = findFilePropertyInSIPList(
                            new File(document.getOriginalSIPFolder()));
                        openSIP(fileProperty, Mode.ReadWrite, null);
                    }

                    waitWindow.close(); // In case it was not closed yet...
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

                return 0;
            }
        }.execute();
    }

    protected void createNewSIPFromCSV() {
        final CreateNewSIPFromCSVDialog createNewSIPFromCSVDialog = new CreateNewSIPFromCSVDialog(
            this);
        if (!createNewSIPFromCSVDialog.goButtonWasClicked) {
            return;
        }

        final Path configPath = Path.of(configDirectory, "csv-sip-mapping.xml");
        final Path sourceCSVPath = Path.of(createNewSIPFromCSVDialog.sourceFileTextField.getText());
        final String destinationFolderName = createNewSIPFromCSVDialog.destinationFolderTextField.getText();
        String destinationFileName = createNewSIPFromCSVDialog.destinationNameTextField.getText();
        final boolean beZIP = createNewSIPFromCSVDialog.beZIPCheckBox.isSelected();
        final Overview saOverview = (Overview) createNewSIPFromCSVDialog.saComboBox.getSelectedItem();

        if (destinationFileName.isEmpty()) {
            return;
        }

        if (beZIP) {
            if (!destinationFileName.toLowerCase().endsWith(ZIP_EXT)) {
                destinationFileName += ZIP_EXT;
            }
        } else {
            if (destinationFileName.toLowerCase().endsWith(ZIP_EXT)) {
                destinationFileName = destinationFileName.substring(0, destinationFileName.length() - ZIP_EXT.length());
            }
        }

        final String newSIPFileName = Path.of(destinationFolderName, destinationFileName).toString();

        new SwingWorker<Integer, Object>() {
            @Override
            public Integer doInBackground() {
                footerTextField.setText(I18N.translate("MessageFooterNewFile") + newSIPFileName + "...");
                final SmallPeskyMessageWindow waitWindow = SmallPeskyMessageWindow.openBlocking(LauncherView.this,
                    I18N.translate("MessageTempCreatingSIP"));
                ImportResult importResult = null;

                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    String selectedSAid = null;
                    String selectedDSSid = null;
                    if (saOverview != null) {
                        selectedSAid = saOverview.saId;
                        selectedDSSid = saOverview.dssId;
                    }

                    final SipCreationArgs sipCreationArgs = new SipCreationArgs(Path.of(newSIPFileName), selectedSAid, selectedDSSid);
                    ExceptionCollector.clear();
                    importResult = CsvToSipImporter.createSipFromCsv(sipCreationArgs, sourceCSVPath, configPath);
                    footerTextField.setText(I18N.translate("MessageFooterNewFile") + newSIPFileName);

                    Util.showAllFromExceptionCollector(waitWindow, LauncherView.this);
                } catch (final java.lang.Exception e) {
                    Logger.error(e.getMessage(), e);

                    footerTextField.setText(I18N.translate("MessageFooterCantCreateFile") + newSIPFileName);

                    waitWindow.close();
                    JOptionPane.showMessageDialog(LauncherView.this, e.toString(),
                        I18N.translate("TitleCantCreateSIP"), JOptionPane.ERROR_MESSAGE);
                } finally {
                    if (importResult != null) {
                        if (importResult.isSuccess()) {
                            JOptionPane.showMessageDialog(LauncherView.this, importResult.getSuccessMessagesWithPotentialWarnings(), I18N.translate("TitleImportCsvSuccess"), JOptionPane.INFORMATION_MESSAGE);

                            final FileProperty fileProperty = findFilePropertyInSIPList(new File(newSIPFileName));
                            openSIP(fileProperty, Mode.ReadWrite, null);
                        } else {
                            JOptionPane.showMessageDialog(LauncherView.this, I18N.translate(importResult.getError().getMessageKey(), importResult.getAdditionalErrorObjects()), I18N.translate("TitleImportCsvFailure"), JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    waitWindow.close();
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

                return 0;
            }
        }.execute();
    }

    // TODO this method is not well made
    protected FileProperty findFilePropertyInSIPList(final File file) {
        FileProperty fileProperty = null;
        for (int i = 0; i < sipTable.getSipTableModel().getRowCount(); i++) {
            final FileProperty entry = sipTable.getSipTableModel().getSipAtIndex(i);
            if (entry != null && entry.getFile().getAbsolutePath().equals(file.getAbsolutePath())) {
                fileProperty = entry;
                break;
            }
        }
        if (fileProperty == null) {
            fileProperty = new FileProperty(file, false);
        }
        return fileProperty;

    }

    /**
     * The [selectWorkspaceFolder] button was clicked:
     */
    protected void selectWorkspaceFolder() {
        final JFileChooser fileChooser = new JFileChooser(sipDirectory);
        fileChooser.setName(WORKSPACE_SELECT_FOLDER_FILE_CHOOSER);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            // Cancel-button clicked
            return;
        }

        sipDirectory = FileUtil.asCanonicalFileName(fileChooser.getSelectedFile());
        setTitle(sipDirectory);

        sipTable.rereadSIPTable();
        // sipTable.registerFileChangeWatchService();
    }

    /**
     * The [rereadWorkspaceFolder] button was clicked:
     */
    protected void rereadWorkspaceFolder() {
        sipTable.rereadSIPTable();
    }

    protected void openSearchView() {
        SearchView.open(this);
    }

    /*
     * Prompts user for entering a new name for a sip Returns - Optional.empty if renaming was canceled - entered new
     * name, otherwise
     */
    protected Optional<String> askNewSIPName(final FileProperty sip) {
        String title = I18N.translate("TitleRenameSIP");
        String message = I18N.translate("MessageEnterNewSIPName");
        int messageType = JOptionPane.QUESTION_MESSAGE;

        String newSIPName = "";

        final String textFieldContent = sip.getName();
        final String DOT_ZIP = ".zip";
        final boolean hasZipExtension = textFieldContent.endsWith(DOT_ZIP);

        boolean keepLooping = true;
        while (keepLooping) {
            // hiding .zip extension
            newSIPName = hasZipExtension ? textFieldContent.substring(0, textFieldContent.lastIndexOf(DOT_ZIP))
                : textFieldContent;
            newSIPName = (String) JOptionPane.showInputDialog(this, message, title, messageType, null, null,
                newSIPName);
            if (newSIPName == null) {
                // dialog was canceled
                return Optional.empty();
            }
            if (newSIPName.length() == 0) {
                // empty name !!!
                message = I18N.translate("MessageEnterNewSIPName");
                messageType = JOptionPane.QUESTION_MESSAGE;
                continue;
            }
            if (!hasZipExtension && newSIPName.endsWith(DOT_ZIP)) {
                message = I18N.translate("MessageFolderToZipNotOk") + "\n" + I18N.translate("MessageEnterNewSIPName");
                messageType = JOptionPane.WARNING_MESSAGE;
                continue;
            }
            // restoring .zip extension
            if (hasZipExtension && !newSIPName.endsWith(DOT_ZIP)) {
                newSIPName = newSIPName + DOT_ZIP;
            }
            if (!FileUtil.isFileNameAllowed(newSIPName)) {
                title = I18N.translate("TitleCantRenameSIP");
                message = I18N.translate("MessageBadSIPName") + "\n" + newSIPName + "\n" + I18N.translate(
                    "MessageEnterNewSIPName");
                messageType = JOptionPane.WARNING_MESSAGE;
                continue;
            }
            final File newSIP = new File(sip.getFile().getParent() + File.separator + newSIPName);
            if (newSIP.exists()) {
                title = I18N.translate("TitleCantRenameSIP");
                message = I18N.translate("MessageSIPAlreadyExists") + "\n" + newSIPName + "\n" + I18N.translate(
                    "MessageEnterNewSIPName");
                messageType = JOptionPane.WARNING_MESSAGE;
                continue;
            }
            keepLooping = false;
        }
        return Optional.of(newSIPName);
    }

    /**
     * Rename the currently selected SIP. If it is locked by anybody (including the current user), don't rename.
     */
    protected void renameSIP() {
        if (selectedSIP == null || Document.isLocked(selectedSIP.getFile())) {
            return;
        }
        do {
            final Optional<String> theNewSIPName = askNewSIPName(selectedSIP);
            if (!theNewSIPName.isPresent()) {
                return;
            }
            try {
                FileUtil.renameTo(selectedSIP.getFile(),
                    new File(selectedSIP.getFile().getParent() + File.separator + theNewSIPName.get()));
                break;
            } catch (final java.lang.Exception ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage() + "\n" + I18N.translate("MessageEnterNewSIPName"),
                    I18N.translate("TitleCantRenameSIP"),
                    JOptionPane.WARNING_MESSAGE);
                continue;
            }
        } while (true);
        sipTable.rereadSIPTable();
    }

    /**
     * Copy the currently selected SIP.
     */
    protected void copySIP() {
        if (selectedSIP == null) {
            return;
        }

        String title = I18N.translate("TitleCopySIP");
        String message = I18N.translate("MessageEnterNewSIPName");
        String textFieldContent = FileUtil.asFilePathWithoutExtension(selectedSIP.getName());

        String newItemName;
        Boolean ok;
        do {
            newItemName = CopySIPDialog.open(this, message, title, textFieldContent);
            if (newItemName == null || newItemName.length() == 0) {
                return;
            }

            ok = false;

            if (!FileUtil.isFileNameAllowed(newItemName)) {
                title = I18N.translate("TitleCantCopySIP");
                message = I18N.translate("MessageBadLettersInFilename") + "\n" + I18N.translate(
                    "MessageEnterNewSIPName");
                textFieldContent = FileUtil.asFilePathWithoutExtension(newItemName);
                continue;
            }

            final File newSIP = new File(selectedSIP.getFile().getParent() + "/" + newItemName);
            if (newSIP.exists()) {
                title = I18N.translate("TitleCantCopySIP");
                message = I18N.translate("MessageSIPAlreadyExists") + "\n" + I18N.translate("MessageEnterNewSIPName");
                textFieldContent = FileUtil.asFilePathWithoutExtension(newItemName);
                continue;
            }

            try {
                Document.copy(selectedSIP.getFile().getPath(), newSIP.getPath());
            } catch (final java.lang.Exception ex) {
                title = I18N.translate("TitleCantCopySIP");
                message = ex.getMessage() + "\n" + I18N.translate("MessageEnterNewSIPName");
                textFieldContent = FileUtil.asFilePathWithoutExtension(newItemName);

                try {
                    FileUtil.delete(newSIP);
                } catch (final FileUtilExceptionListException ex1) {
                }

                continue;
            }

            ok = true;
        } while (!ok);

        sipTable.rereadSIPTable();
    }

    /**
     * Delete the currently selected SIP. If it is locked by anybody (including the current user), don't delete.
     */
    protected void deleteSIP() {
        if (selectedSIP == null || Document.isLocked(selectedSIP.getFile())) {
            return;
        }

        if (JOptionPane.showConfirmDialog(this, I18N.translate("QuestionDeleteSIP"), I18N.translate("TitleDeleteSIP"),
            JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            FileUtil.delete(selectedSIP.getFile());
        } catch (final Exception e) {
            Logger.error(e.getMessage(), e);
        }

        sipTable.getSipTableModel().removeSipFromView(selectedSIP);
    }

    protected void checkIngestFeedback(final boolean withFeedbackIfNothingFound) {
        if (!AIPCreatorProxy.isUsable()) {
            return;
        }

        Logger.debug("Checking overall ingest feedback...");

        try {
            final List<String> feedbackFoundForSIPs = AIPCreatorProxy.getAIPCreator().checkIngestFeedback(
                sipDirectory);

            Logger.debug("Found ingest feedback: " + feedbackFoundForSIPs);

            if (feedbackFoundForSIPs == null || feedbackFoundForSIPs.isEmpty()) {
                if (withFeedbackIfNothingFound) {
                    JOptionPane.showMessageDialog(this, I18N.translate("MessageCheckIngestFeedbackEmpty"),
                        I18N.translate("HeaderCheckIngestFeedback"), JOptionPane.INFORMATION_MESSAGE);
                }

                return;
            }

            final StringBuilder feedbackFoundForSIPsMessage = new StringBuilder(
                I18N.translate("MessageCheckIngestFeedbackFound"));
            for (final String sipName : feedbackFoundForSIPs) {
                feedbackFoundForSIPsMessage.append("\n").append(sipName);
            }

            new ScrollableMessageDialog(this, I18N.translate("HeaderCheckIngestFeedback"),
                feedbackFoundForSIPsMessage.toString());

            // Refresh the SIP table:
            rereadWorkspaceFolder();

            // If a SIPView on a feedbacked SIP is open, refresh this SIPView:
            for (final String sipName : feedbackFoundForSIPs) {
                final String sipPath = sipDirectory + File.separator + sipName;

                for (final SIPView openSIPView : openedSIPViews) {
                    if (openSIPView.getFileProperty().getFile().getAbsolutePath().equals(sipPath)) {
                        openSIPView.read(sipPath, Mode.ReadOnly, null);
                    }
                }
            }
        } catch (final Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }

    /**
     * Open a SIP on startup (if specified). Note: Must be public because it might be called from a subclass via the
     * static variable "LauncherView".
     */
    protected void openSIP(final CommandLine commandLine) {
        // If an "open" parameter is supplied in the command line, open this file:
        if (commandLine.hasOption("open")) {
            Logger.debug("open: " + commandLine.getOptionValue("open"));
            final FileProperty fileProperty = findFilePropertyInSIPList(new File(commandLine.getOptionValue("open")));
            openSIP(fileProperty, Mode.ReadWrite, null);
        }
    }

    /**
     * Ask for a path then open this SIP.
     *
     * @param mode
     */
    protected void openSIP(final Mode mode) {
        final JFileChooser fileChooser = new SIPFileChooser(lastUsedOpenOrSaveDirectory);
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            // Cancel-button clicked
            return;
        }

        // Remember the SIP's parent directory:
        lastUsedOpenOrSaveDirectory = FileUtil.asCanonicalFileName(fileChooser.getSelectedFile().getParent());

        // Open this SIP:
        final FileProperty fileProperty = findFilePropertyInSIPList(fileChooser.getSelectedFile());
        openSIP(fileProperty, mode, null);
    }

    /**
     * Open the SIP denoted by fileProperty.getFile().getAbsolutePath(). If it is locked by anybody except the current
     * user, open readOnly. If this SIP is already open, bring this view to the front. If treePath is not null, try to
     * select the node at treePath.
     *
     * @param mode
     */
    protected void openSIP(final FileProperty fileProperty, final Mode mode, final String admIdToSelect) {
        // Don't allow opening document if it is just being saved:
        if (sipsWithSavingInProgress.contains(fileProperty)) {
            JOptionPane.showMessageDialog(this, I18N.translate("MessageCantOpenSIPWhileBeingSaved"),
                I18N.translate("TitleOpenSIP"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Logger.debug("Opening SIP: " + fileProperty.getFile().getAbsolutePath());

        // Already open? If yes, bring this view to the front:
        for (final SIPView openSIPView : openedSIPViews) {
            if (openSIPView.getFileProperty().equals(fileProperty)) {
                openSIPView.toFront();
                openSIPView.requestFocus();

                openSIPView.selectNode(admIdToSelect);

                return;
            }
        }

        final SIPView sipView = SIPView.open(this, fileProperty, mode, admIdToSelect);

        // Register this sipView:
        openedSIPViews.add(sipView);
        // Update the sipTable to show that this SIP is now locked by the
        // current user:
        final int row = sipTable.getSipTableModel().getRowIndexOfSip(fileProperty);
        // row can be -1 when a sip outside the workspace is open
        if (row > -1) {
            sipTable.getSipTableModel().getSipAtIndex(row).setLockedBy(OperatingSystem.userName());
            sipTable.getSipTableModel().fireTableCellUpdated(row, Column.LOCKED_BY.ordinal());
        }
    }

    /**
     * Open the currently selected SIP. If it is locked by anybody except the current user, don't open.
     *
     * @param mode
     */
    public void openSelectedSIPInWorkspace(final Mode mode) {
        if (selectedSIP == null) {
            return;
        }

        openSIP(selectedSIP, mode, null);
    }

    protected void openSIPInWorkspace(final String sipName, final Mode mode) {
        final String sipPath = sipDirectory + File.separator + sipName;
        final FileProperty fileProperty = findFilePropertyInSIPList(new File(sipPath));
        openSIP(fileProperty, mode, null);
    }

    public void openSIPInWorkspace(final String sipName, final Mode mode, final String admIdToSelect) {
        final String sipPath = sipDirectory + File.separator + sipName;
        final FileProperty fileProperty = findFilePropertyInSIPList(new File(sipPath));
        openSIP(fileProperty, mode, admIdToSelect);
    }

    public void enableOrDisableActions() {
        if (selectedSIP == null) {
            // Nothing selected:

            openSIPInWorkspaceAction.setEnabled(false);
            openSIPInWorkspaceReadWriteNoFileOpsAction.setEnabled(false);
            openSIPInWorkspaceReadOnlyAction.setEnabled(false);
            renameSIPInWorkspaceAction.setEnabled(false);
            copySIPInWorkspaceAction.setEnabled(false);
            deleteSIPInWorkspaceAction.setEnabled(false);
        } else {
            // A SIP is selected:

            if (Document.isLockedBySomebodyElse(selectedSIP.getFile())) {
                // Somebody else locked this SIP, so I can only open it in
                // read-only mode or copy it:
                openSIPInWorkspaceAction.setEnabled(false);
                openSIPInWorkspaceReadWriteNoFileOpsAction.setEnabled(false);
                openSIPInWorkspaceReadOnlyAction.setEnabled(true);
                renameSIPInWorkspaceAction.setEnabled(false);
                copySIPInWorkspaceAction.setEnabled(true);
                deleteSIPInWorkspaceAction.setEnabled(false);
            } else if (Document.isLocked(selectedSIP.getFile())) {
                // I locked this SIP myself, so I can't rename or delete it:
                openSIPInWorkspaceAction.setEnabled(true);
                openSIPInWorkspaceReadWriteNoFileOpsAction.setEnabled(true);
                openSIPInWorkspaceReadOnlyAction.setEnabled(true);
                renameSIPInWorkspaceAction.setEnabled(false);
                copySIPInWorkspaceAction.setEnabled(true);
                deleteSIPInWorkspaceAction.setEnabled(false);
            } else {
                // No locks, all is allowed:
                openSIPInWorkspaceAction.setEnabled(true);
                openSIPInWorkspaceReadWriteNoFileOpsAction.setEnabled(true);
                openSIPInWorkspaceReadOnlyAction.setEnabled(true);
                renameSIPInWorkspaceAction.setEnabled(true);
                copySIPInWorkspaceAction.setEnabled(true);
                deleteSIPInWorkspaceAction.setEnabled(true);
            }
        }
    }

}
