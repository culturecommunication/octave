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

import static ch.docuteam.packer.gui.ComponentNames.DuplicatesTableDialog_REMOVE;
import static ch.docuteam.packer.gui.ComponentNames.FOOTER_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CLEAR_SEARCH_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CREATE_NEW_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CREATE_NEW_SIP_ROOT_NAME_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CREATE_OK_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CSV_IMPORT_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_DELETE_ITEM_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_DESTINATION_NAME_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_EAD_EXPORT_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_MENU;
import static ch.docuteam.packer.gui.ComponentNames.SIP_METADATA_EXPORT_MENU;
import static ch.docuteam.packer.gui.ComponentNames.SIP_NORMALIZE_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_OPEN_IN_WORKSPACE_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_REPLACE_FILE_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SAVE_CURRENT_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SEARCH_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SEARCH_NEXT_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SEARCH_PREVIOUS_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SEARCH_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_FROM_SOURCE_FILE_OR_FOLDER_RADIO_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SELECT_SOURCE_FILE_OR_FOLDER_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SORT_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_COLLAPSE_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_CREATE_FOLDER_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_ELEMENT_MENU;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_EVENT_TABLE;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_EXPAND_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_FILE_MENU;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_FOOTER_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_FRAME;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_INFO_LABEL;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_INSERT_FILE_OR_FOLDER_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_MENU;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_METADATA_UNITTITLE_TEXTFIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_REMOVE_DUPLICATES_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_RENAME_FOLDER_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_SAVE_AS_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_SAVE_AS_TEMPLATE_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_SORT_FOLDER_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_VIEW_TREE;
import static ch.docuteam.packer.gui.ComponentNames.WORKSPACE_MENU;
import static ch.docuteam.packer.gui.ComponentNames.WORKSPACE_SELECT_FOLDER_MENU_ITEM;
import static org.assertj.swing.timing.Pause.pause;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import ch.docuteam.packer.assertj.swing.AssertionHelper;
import ch.docuteam.packer.assertj.swing.CustomTableCellFinder;
import ch.docuteam.packer.assertj.swing.JXTreeTableComponentFixture;
import ch.docuteam.packer.assertj.swing.JXTreeTableComponentFixtureExtension;
import ch.docuteam.packer.gui.sipView.DuplicatesTableDialog;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.file.ResourceUtil;
import ch.docuteam.tools.file.exception.FileUtilExceptionListException;
import ch.docuteam.tools.gui.ScrollableMessageDialog;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

import org.apache.commons.io.FileUtils;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.data.TableCellFinder;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JFileChooserFixture;
import org.jdesktop.swingx.util.OS;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LauncherViewSwingIT {

    private static final int ASSERTION_TIMEOUT = 5000;

    private static final String SAMPLE_SIP_ZIP_1 = "sipwithlongpathnames.zip";

    private static final String SAMPLE_SIP_1 = "sipwithlongpathnames";

    private static final String SAMPLE_SIP_ZIP_2 = "sampleSIP.zip";

    private static final String SAMPLE_SIP_NO_FILES = "sampleSIP_FilesMissing";

    private static final String SAMPLE_SIP_CREATE_SOURCE_FOLDER = "INPUT_FOLDER_FOR_PACKER";

    private static final String SAMPLE_FOLDER = "folder";

    private static final String SAMPLE_FILE = "samplePNG.png";

    private static final String SAMPLE_CSV_IMPORT_FILE = "sampleSIP_import.csv";

    private static String WORKSPACE_FOLDER; // temporary folder, must be deleted at tearDown

    private static String TEMPLATE_FOLDER; // temporary folder, must be deleted at tearDown

    private String INPUT_FOLDER;

    private String INPUT_FOLDER_SMALL;

    private FrameFixture window;

    private FrameFixture sipWindow;

    private Robot robot;

    @BeforeClass
    public static void setUpOnce() {
        // TODO: some tests fail if the FailOnThreadViolationRepaintManager is installed because the SwingWorker
        // constrains
        // (Swing components should be accessed on the Event Dispatch Thread only) are not fulfilled

        // Install FailOnThreadViolationRepaintManager to check that all access to Swing components is performed in
        // the EDT
        // FailOnThreadViolationRepaintManager.install();
    }

    /**
     * GUI setup.
     */
    // @Override //if extends AssertJSwingJUnitTestCase
    protected void onSetUp() {
        final LauncherView frame = GuiActionRunner.execute(() -> new LauncherView(""));
        window = new FrameFixture(frame);
        window.show(); // shows the frame to test
    }

    @Before
    public void setUp() throws IOException {
        dataSetup();

        onSetUp();
    }

    /**
     * creates a temporary WORKSPACE_FOLDER dir and copies a SAMPLE_SIP_ZIP in it.
     * 
     * @throws IOException
     */
    private void dataSetup() throws IOException {
        // workspace setup
        final Path tmpPath = Files.createTempDirectory(Paths.get(FileUtil.getTempFolder()), "packer_junit");

        if (WORKSPACE_FOLDER == null || !new File(WORKSPACE_FOLDER).exists()) {
            final File file = Files.createDirectories(tmpPath.resolve("workspace")).toFile();
            WORKSPACE_FOLDER = file.getCanonicalPath();
            Logger.info("WORKSPACE_FOLDER: " + WORKSPACE_FOLDER);
        }
        if (TEMPLATE_FOLDER == null || !new File(TEMPLATE_FOLDER).exists()) {
            final File file = Files.createDirectories(tmpPath.resolve("templates")).toFile();
            TEMPLATE_FOLDER = file.getCanonicalPath();
            Logger.info("TEMPLATE_FOLDER: " + TEMPLATE_FOLDER);
        }
        // assert that the workspace has at least one sip
        if (new File(WORKSPACE_FOLDER).exists() && !(new File(WORKSPACE_FOLDER).list().length > 0)) {
            FileUtils.copyFile(ResourceUtil.getResource("data/" + SAMPLE_SIP_ZIP_1), new File(WORKSPACE_FOLDER,
                    SAMPLE_SIP_ZIP_1));

            FileUtils.copyFile(ResourceUtil.getResource("data/" + SAMPLE_SIP_ZIP_2), new File(WORKSPACE_FOLDER,
                    SAMPLE_SIP_ZIP_2));

            FileUtils.copyDirectory(ResourceUtil.getResource("data/" + SAMPLE_SIP_NO_FILES), new File(
                    WORKSPACE_FOLDER, SAMPLE_SIP_NO_FILES));
        }
        // WORKSPACE_FOLDER = WORKSPACE_FOLDER.replaceAll("\\\\", "/");
        INPUT_FOLDER = ResourceUtil.getResourceCanonicalPath("data/" + SAMPLE_SIP_CREATE_SOURCE_FOLDER);
        INPUT_FOLDER_SMALL = ResourceUtil.getResourceCanonicalPath("data/" + SAMPLE_FOLDER);

        Logger.info("dataSetup done");
    }

    /**
     * It is very important to clean up resources used by AssertJ-Swing (keyboard, mouse and opened windows) after
     * each test.
     * <p>
     * Deletes the temp WORKSPACE_FOLDER and the AssertJ-Swing resources.
     * 
     * @throws IOException
     * @throws FileUtilExceptionListException
     */
    @After
    public void tearDown() throws IOException, FileUtilExceptionListException {
        window.cleanUp();
        if (robot != null) {
            robot.cleanUp();
        }
        if (sipWindow != null) {
            sipWindow.cleanUp();
        }

        FileUtil.delete(WORKSPACE_FOLDER);
        FileUtil.delete(TEMPLATE_FOLDER);

        pause(500);
        Logger.info("tearDown done");
    }

    /**
     * Choose WORKSPACE_FOLDER and assert this.
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_chooseWorkspace() throws InterruptedException {
        chooseWorkspace();

        // assert workspace selection - back/forward slashes
        AssertionHelper.assertTitle(window, FileUtil.asCanonicalFileName(WORKSPACE_FOLDER), ASSERTION_TIMEOUT);
    }

    /**
     * Chooses workspace: WORKSPACE_FOLDER.
     * 
     * @throws InterruptedException
     */
    private void chooseWorkspace() throws InterruptedException {
        // JMenu extends JMenuItem, so window.menuItem works also for menu
        window.menuItem(WORKSPACE_MENU).click();

        // select menu item: selectWorkspaceFolderAction
        window.menuItem(WORKSPACE_SELECT_FOLDER_MENU_ITEM).click();

        // choose workspace: WORKSPACE_FOLDER
        // JFileChooserFixture fileChooser = window.fileChooser(WORKSPACE_SELECT_FOLDER_FILE_CHOOSER);

        // ALWAYS USE FORWARD SLASHES FOR WRITING PATHS INTO fileNameTextBox (due to RobotEventGenerator)
        final String workspacePathname = WORKSPACE_FOLDER.replaceAll("\\\\", "/");
        Logger.info("workspacePathname: " + workspacePathname);

        chooseFolderOrFile(window, WORKSPACE_FOLDER, null);
    }

    /**
     * Save existing SIP.
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_saveDefaultSIP() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_1);

        assertTrue("ERROR: Save button not enabled or not found!", sipWindow.button(SIP_SAVE_CURRENT_BUTTON)
                .isEnabled());
        sipWindow.button(SIP_SAVE_CURRENT_BUTTON).click();

        // assert footer text
        final Pattern sipViewPattern = Pattern.compile(I18N.translate("MessageFooterSaved") + ".*" + SAMPLE_SIP_1 +
                ".*");

        AssertionHelper.assertText(sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD), sipViewPattern, ASSERTION_TIMEOUT);

        // assert frame title
        AssertionHelper.assertTitle(sipWindow, FileUtil.asCanonicalFileName(WORKSPACE_FOLDER + File.separator +
                SAMPLE_SIP_ZIP_1), ASSERTION_TIMEOUT);
    }

    /**
     * Save SIP as with new name.
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_saveAs() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_1);

        assertTrue("ERROR: Save button not enabled or not found!", sipWindow.button(SIP_SAVE_CURRENT_BUTTON)
                .isEnabled());
        sipWindow.menuItem(SIP_VIEW_FILE_MENU).click();
        sipWindow.menuItem(SIP_VIEW_SAVE_AS_MENU_ITEM).click();
        // cannot find because it is a java.awt.FileDialog instead of a JFileChooser -> refactored
        final String renamedSIP = "Renamed" + SAMPLE_SIP_1;
        chooseFileByEnteringFilePath(sipWindow, WORKSPACE_FOLDER + File.separator + renamedSIP, true);

        AssertionHelper.assertTitle(sipWindow, WORKSPACE_FOLDER + File.separator + renamedSIP, ASSERTION_TIMEOUT);
    }

    @Test
    public void test_saveAsTemplate() throws InterruptedException {
        final LauncherView launcher = window.targetCastedTo(LauncherView.class);
        launcher.setTemplateDirectory(TEMPLATE_FOLDER);
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_1);

        sipWindow.menuItem(SIP_VIEW_FILE_MENU).click();
        sipWindow.menuItem(SIP_VIEW_SAVE_AS_TEMPLATE_MENU_ITEM).click();

        final String sipTemplateName = "sipTemplate" + System.currentTimeMillis();
        sipWindow.optionPane().textBox().setText(sipTemplateName);
        sipWindow.optionPane().buttonWithText("OK").click();

        // assert footer text ("Vorlage erstellt: " + sipTemplateName)
        final Pattern sipViewPattern = Pattern.compile(I18N.translate("MessageFooterTemplateSaved") + ".*" +
                sipTemplateName + ".*");
        AssertionHelper.assertText(sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD), sipViewPattern, ASSERTION_TIMEOUT);
    }

    /**
     * Creates new SIP using the INPUT_FOLDER.
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_createNewSIP_fromFolder() throws InterruptedException {
        chooseWorkspace();
        robot = BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock();

        window.menuItem(SIP_MENU).click();
        window.menuItem(SIP_CREATE_NEW_MENU_ITEM).click();

        // select checkbox
        window.radioButton(SIP_SELECT_FROM_SOURCE_FILE_OR_FOLDER_RADIO_BUTTON).click();
        // select source file or folder
        window.button(SIP_SELECT_SOURCE_FILE_OR_FOLDER_BUTTON).click();
        // choose source folder
        // JFileChooserFixture fileChooser = window.fileChooser(SIP_SELECT_SOURCE_FOLDER_FILE_CHOOSER);
        chooseFileByEnteringFilePath(window, INPUT_FOLDER, false);

        // choose target folder
        assertTrue("ERROR: selectDestinationIsWorkspaceButton is not enabled!", window.button(
                SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON).isEnabled());
        window.button(SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON).doubleClick();

        // add sip name in a textBox
        final String uniqueSIPName = "SIPjunitout" + System.currentTimeMillis();
        window.textBox(SIP_DESTINATION_NAME_TEXT_FIELD).selectAll().setText(uniqueSIPName);

        // click save button
        assertTrue("ERROR: Save button is not enabled!", window.button(SIP_CREATE_OK_BUTTON).isEnabled());
        window.button(SIP_CREATE_OK_BUTTON).click();

        // close warn message as soon as it is shown
        WindowFinder.findDialog(ScrollableMessageDialog.COMPONENT_NAME).withTimeout(30000).using(robot);
        window.dialog(ScrollableMessageDialog.COMPONENT_NAME).close();

        // assert sip creation in the LauncherView (MessageFooterNewFile translation)
        final String newFileMessagePrefix = I18N.translate("MessageFooterNewFile");
        final Pattern pattern = Pattern.compile(newFileMessagePrefix + ".*" + uniqueSIPName + ".*");
        AssertionHelper.assertText(window.textBox(FOOTER_TEXT_FIELD), pattern, ASSERTION_TIMEOUT);

        sipWindow = WindowFinder.findFrame(SIP_VIEW_FRAME).using(robot);

        // assert footer text
        final Pattern sipViewPattern = Pattern.compile(I18N.translate("MessageFooterOpeningFile") + ".*" +
                uniqueSIPName + ".*");
        AssertionHelper.assertText(sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD), sipViewPattern, ASSERTION_TIMEOUT);

        // assert title
        final String outSIPPathName = WORKSPACE_FOLDER + File.separator + uniqueSIPName + ".zip";
        AssertionHelper.assertTitle(sipWindow, FileUtil.asCanonicalFileName(outSIPPathName), ASSERTION_TIMEOUT);

        // cannot find tree
        // sipWindow.tree(SIP_VIEW_TREE).requireSelection(0);
    }

    /**
     * Inserts a new folder (INPUT_FOLDER_SMALL) into existing SIP.
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_editSIP_insertFolder() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_1);

        sipWindow.button(SIP_VIEW_INSERT_FILE_OR_FOLDER_BUTTON).doubleClick();

        chooseFileByEnteringFilePath(sipWindow, INPUT_FOLDER_SMALL, false);

        // assert modified
        AssertionHelper.assertText(sipWindow.label(SIP_VIEW_INFO_LABEL), I18N.translate("LabelIsModified"),
                ASSERTION_TIMEOUT);
    }

    /**
     * choose file on frameFixture.
     * 
     * @param pathName
     * @throws InterruptedException
     * @throws IOException
     */
    private void chooseFileByEnteringFilePath(final FrameFixture frameFixture, String pathName,
            final boolean saveAction) throws InterruptedException {
        final JFileChooserFixture fileChooser = frameFixture.fileChooser();
        if (saveAction) {
            fileChooser.target().setDialogType(JFileChooser.SAVE_DIALOG);
        }

        if (pathName == null) {
            fileChooser.cancel();
        } else {
            // ALWAYS USE FORWARD SLASHES FOR WRITING PATHS INTO fileNameTextBox (due to RobotEventGenerator)
            pathName = pathName.replaceAll("\\\\", "/");
            if (OS.isMacOSX()) {
                final File selection = new File(pathName);
                fileChooser.setCurrentDirectory(selection.getParentFile());
                if (saveAction) {
                    fileChooser.fileNameTextBox().setText(selection.getName());
                } else {
                    fileChooser.selectFile(selection);
                }
            } else {
                fileChooser.fileNameTextBox().setText(pathName);
            }
            fileChooser.approve();
        }
    }

    /**
     * chooses folder and file in this folder, if filePath not null.
     * 
     * @param frameFixture
     * @param folderPath
     * @param filePath
     * @throws InterruptedException
     */
    private void chooseFolderOrFile(final FrameFixture frameFixture, final String folderPath, final String filePath)
            throws InterruptedException {

        final JFileChooserFixture fileChooser = frameFixture.fileChooser();

        File fileToSelect = new File(folderPath);
        if (filePath != null) {
            fileToSelect = new File(fileToSelect, filePath);
        }

        fileChooser.setCurrentDirectory(fileToSelect.getParentFile());

        fileChooser.selectFile(fileToSelect);
        fileChooser.approve();
    }

    /**
     * Check that editing metadata is allowed even if referenced files are missing/not readable
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_editSIPFilesMissing() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_NO_FILES);

        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());

        sipWindow.tabbedPane().selectTab(1);
        assertEquals("single-file", sipWindow.textBox(SIP_VIEW_METADATA_UNITTITLE_TEXTFIELD).text());
        assertTrue(sipWindow.textBox(SIP_VIEW_METADATA_UNITTITLE_TEXTFIELD).isEnabled());
        sipWindow.textBox(SIP_VIEW_METADATA_UNITTITLE_TEXTFIELD).setText("editedTitle");
        treeFixture.focus();

        // assert modified
        AssertionHelper.assertText(sipWindow.label(SIP_VIEW_INFO_LABEL), I18N.translate("LabelIsModified"),
                ASSERTION_TIMEOUT);
    }

    /**
     * Replaces a file from an existing SIP, and asserts that the action was successful.
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_editSIP_replaceFile() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());

        // sort nodes first, root is selected
        treeFixture.rightClick();
        sipWindow.menuItem(SIP_VIEW_SORT_FOLDER_MENU_ITEM).click();

        // select third row after root
        treeFixture.changeSelection(3);
        treeFixture.rightClick();
        sipWindow.menuItem(SIP_REPLACE_FILE_MENU_ITEM).click();

        // choose another file
        chooseFileByEnteringFilePath(sipWindow, ResourceUtil.getResourceCanonicalPath("data/" + SAMPLE_FILE), false);

        // assert modified
        AssertionHelper.assertText(sipWindow.label(SIP_VIEW_INFO_LABEL), I18N.translate("LabelIsModified"),
                ASSERTION_TIMEOUT);

        // select replaced and assert success
        // select third row after root
        treeFixture.changeSelection(3);
        // assert footer test
        final Pattern footerPattern = Pattern.compile(".*" + SAMPLE_FILE + ".*");
        AssertionHelper.assertText(sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD), footerPattern, ASSERTION_TIMEOUT);

        // assert PREMIS event
        assertEquals("Event table doesn't contain the Replacement event.", 1, sipWindow.table(SIP_VIEW_EVENT_TABLE)
                .cell("Replacement").row()); // event type
        sipWindow.table(SIP_VIEW_EVENT_TABLE).cell(TableCell.row(1).column(2)).requireValue("Success"); // event
                                                                                                        // outcome
    }

    private FrameFixture openSIPInWorkspace(final String sipName) throws InterruptedException {
        chooseWorkspace();

        final TableCellFinder cellFinder = new CustomTableCellFinder(sipName, 0);
        window.table().cell(cellFinder).rightClick();

        // click on menuItem
        window.menuItem(SIP_OPEN_IN_WORKSPACE_MENU_ITEM).click();

        // check that a new Frame (SIPView) was opened
        robot = BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock(); // .robotWithCurrentAwtHierarchy();
        sipWindow = WindowFinder.findFrame(SIP_VIEW_FRAME).using(robot);
        return sipWindow;
    }

    @Test
    public void test_viewSIP_expandAll_collapseAll() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        // assert that the tree is not fully expanded
        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());

        // Object[] path = {"folder", "subfolder1", "subfolder_same_name"};
        // assertFalse(treeFixture.isExpanded(path)); //doesn't work
        assertFalse(treeFixture.isExpanded(2));

        // sort nodes first, root is selected
        treeFixture.rightClick();
        sipWindow.menuItem(SIP_VIEW_SORT_FOLDER_MENU_ITEM).click();

        // select view menu
        sipWindow.menuItem(SIP_VIEW_MENU).click();

        // expand all
        sipWindow.menuItem(SIP_VIEW_EXPAND_MENU_ITEM).click();

        // assertTrue(treeFixture.isExpanded(path)); //doesn't work
        // assertTrue(treeFixture.isVisible(path)); //doesn't work
        assertTrue(treeFixture.isExpanded(2));
        treeFixture.changeSelection(2);
        AssertionHelper.assertSelectedRowValue("subfolder_same_name", treeFixture, ASSERTION_TIMEOUT);

        // collapse all
        sipWindow.menuItem(SIP_VIEW_COLLAPSE_MENU_ITEM).click();
        assertFalse(treeFixture.isExpanded(0));
    }

    @Test
    public void test_editSIP_deleteElement() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());
        // deselect root
        treeFixture.changeSelection(0);

        // select second row after root
        treeFixture.changeSelection(2);

        // get selected value
        final String rowValue = treeFixture.getSelectedRowValue();
        // Logger.info("rowValue: " + rowValue);
        assertEquals(treeFixture.getSelectedRow(), treeFixture.getTreeRow(rowValue));

        treeFixture.rightClick();
        sipWindow.menuItem(SIP_DELETE_ITEM_MENU_ITEM).click();
        // confirm deletion
        sipWindow.optionPane().yesButton().click();

        // assert modified
        AssertionHelper.assertText(sipWindow.label(SIP_VIEW_INFO_LABEL), I18N.translate("LabelIsModified"),
                ASSERTION_TIMEOUT);
        // assert no tree selection
        assertEquals("No tree selection expected", -1, treeFixture.getSelectedRow());
        // assert row deleted
        assertEquals(-1, treeFixture.getTreeRow(rowValue));
    }

    /**
     * Edit SIP, create folder, insert, rename folder.
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_createEmpty_editSIP_createFolder_renameFolder() throws InterruptedException {
        chooseWorkspace();

        window.menuItem(SIP_MENU).click();
        window.menuItem(SIP_CREATE_NEW_MENU_ITEM).click();

        final String brandNewSIP = "NewSIP" + System.currentTimeMillis();
        // choose SIP name
        window.textBox(SIP_CREATE_NEW_SIP_ROOT_NAME_TEXT_FIELD).setText(brandNewSIP);

        // choose target folder
        assertTrue("ERROR: selectDestinationIsWorkspaceButton is not enabled!", window.button(
                SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON).isEnabled());
        window.button(SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON).doubleClick();

        // click save button
        assertTrue("ERROR: Save button is not enabled!", window.button(SIP_CREATE_OK_BUTTON).isEnabled());
        window.button(SIP_CREATE_OK_BUTTON).click();

        // assert sip creation in the LauncherView (MessageFooterNewFile translation)
        final String newFileMessagePrefix = I18N.translate("MessageFooterNewFile");
        final Pattern pattern = Pattern.compile(newFileMessagePrefix + ".*" + brandNewSIP + ".*");
        AssertionHelper.assertText(window.textBox(FOOTER_TEXT_FIELD), pattern, ASSERTION_TIMEOUT);

        // check that a new Frame (SIPView) was opened
        robot = BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock(); // .robotWithCurrentAwtHierarchy();
        sipWindow = WindowFinder.findFrame(SIP_VIEW_FRAME).using(robot);

        // assert title
        final String outSIPPathName = WORKSPACE_FOLDER + File.separator + brandNewSIP + ".zip";
        AssertionHelper.assertTitle(sipWindow, FileUtil.asCanonicalFileName(outSIPPathName), ASSERTION_TIMEOUT);

        // create new folder
        sipWindow.menuItem(SIP_VIEW_ELEMENT_MENU).click();
        sipWindow.menuItem(SIP_VIEW_CREATE_FOLDER_MENU_ITEM).click();

        final String newFolderName = "NewFolder";
        sipWindow.dialog().textBox().setText(newFolderName);
        sipWindow.optionPane().buttonWithText("OK").click();

        // select newly created folder
        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());
        // select first row after root
        treeFixture.changeSelection(1);

        // assert footer text, new folder name should be present
        Pattern sipViewPattern = Pattern.compile(".*" + brandNewSIP + ".*" + newFolderName + ".*");
        AssertionHelper.assertText(sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD), sipViewPattern, ASSERTION_TIMEOUT);

        // rename folder
        // open POPUP_MENU
        treeFixture.rightClick();

        sipWindow.menuItem(SIP_VIEW_RENAME_FOLDER_MENU_ITEM).click();

        final String renamedFolderName = "RenamedFolder";
        sipWindow.dialog().textBox().setText(renamedFolderName);
        sipWindow.optionPane().buttonWithText("OK").click();

        // deselect current
        JXTreeTableComponentFixtureExtension.treeWithName(SIP_VIEW_TREE).createFixture(robot, sipWindow.target())
                .changeSelection(1);
        // select root
        JXTreeTableComponentFixtureExtension.treeWithName(SIP_VIEW_TREE).createFixture(robot, sipWindow.target())
                .changeSelection(0);
        // select first child again
        JXTreeTableComponentFixtureExtension.treeWithName(SIP_VIEW_TREE).createFixture(robot, sipWindow.target())
                .changeSelection(1);

        sipViewPattern = Pattern.compile(".*" + brandNewSIP + ".*" + renamedFolderName + ".*");
        AssertionHelper.assertText(sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD), sipViewPattern, ASSERTION_TIMEOUT);
    }

    @Test
    public void test_metadataCSVImport() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        sipWindow.menuItem(SIP_VIEW_FILE_MENU).click();
        sipWindow.menuItem(SIP_CSV_IMPORT_MENU_ITEM).click();

        chooseFolderOrFile(sipWindow, ResourceUtil.getResourceCanonicalPath("data/"), SAMPLE_CSV_IMPORT_FILE);

        // assert success message: JOptionPane
        JOptionPaneFinder.findOptionPane()
                .withTimeout(5000)
                .using(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock())
                .requireMessage(I18N.translate("ImportCsvSuccess"))
                .okButton()
                .click();

        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());
        // select root
        treeFixture.changeSelection(0);

        sipWindow.tabbedPane().selectTab(1);
        sipWindow.textBox(SIP_VIEW_METADATA_UNITTITLE_TEXTFIELD).requireText("importedTitle");
    }

    @Test
    public void test_metadataEADExport() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        sipWindow.menuItem(SIP_VIEW_FILE_MENU).click();
        sipWindow.menuItem(SIP_METADATA_EXPORT_MENU).click();
        sipWindow.menuItem(SIP_EAD_EXPORT_MENU_ITEM).click();

        // file chooser is visible
        final JFileChooserFixture fileChooser = sipWindow.fileChooser();
        fileChooser.requireVisible();
        fileChooser.setCurrentDirectory(new File(WORKSPACE_FOLDER));
        fileChooser.approve();

        // assert success - since no GUI notification in case of success, check the exported file
        final File exportedFile = new File(WORKSPACE_FOLDER, SAMPLE_SIP_ZIP_2 + ".xml");
        assertTrue(exportedFile.exists());
    }

    @Test
    public void test_searchInSIP() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        sipWindow.textBox(SIP_SEARCH_TEXT_FIELD).enterText("sample");

        sipWindow.button(SIP_SEARCH_BUTTON).click();

        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());
        AssertionHelper.assertSelectedRowValue("SamplePDF.pdf", treeFixture, ASSERTION_TIMEOUT);

        // clear and search again
        sipWindow.button(SIP_CLEAR_SEARCH_BUTTON).click();

        sipWindow.textBox(SIP_SEARCH_TEXT_FIELD).enterText("fundamentals");

        sipWindow.button(SIP_SEARCH_BUTTON).click();

        AssertionHelper.assertSelectedRowValue("2fundamentals_lt.gif", treeFixture, ASSERTION_TIMEOUT);

        // next match
        sipWindow.button(SIP_SEARCH_NEXT_BUTTON).click();

        AssertionHelper.assertSelectedRowValue("2fundamentals_lt.tif", treeFixture, ASSERTION_TIMEOUT);

        // previous match
        sipWindow.button(SIP_SEARCH_PREVIOUS_BUTTON).click();
        AssertionHelper.assertSelectedRowValue("2fundamentals_lt.gif", treeFixture, ASSERTION_TIMEOUT);

    }

    @Test
    public void test_pattern() {
        final String uniqueSIPName = "_SIP_junit_out_1490345885412";
        final String pathName =
                "Neue Datei: C:\\Users\\test\\AppData\\Local\\Temp\\packer_junit8606340865719849073\\workspace/INPUT_FOLDER_FOR_PACKER_SIP_junit_out_1490345885412.zip";
        assertTrue(Pattern.matches("a*b", "aaaaab"));
        assertTrue(Pattern.matches("Neue Datei:" + ".*" + uniqueSIPName + ".*", pathName));
    }

    @Test
    public void test_removeDuplicates() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        // check precondition: finds "FileWithUmlauts_üäö.txt" node at rowIndex
        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());
        final int rowIndex = 11;
        assertTrue(treeFixture.findTreePathAfterExpandAll(11));

        sipWindow.menuItem(SIP_VIEW_FILE_MENU).click();
        sipWindow.menuItem(SIP_VIEW_REMOVE_DUPLICATES_MENU_ITEM).click();

        robot = BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock();
        final DialogFixture duplicatesTableDialog = WindowFinder.findDialog(DuplicatesTableDialog.class).using(robot);

        duplicatesTableDialog.table().cell("false").select();
        duplicatesTableDialog.button(DuplicatesTableDialog_REMOVE).click();

        // assert success message: JOptionPane
        JOptionPaneFinder.findOptionPane()
                .withTimeout(60000)
                .using(robot)
                .requireMessage(I18N.translate("MessageRemovedDuplicates"))
                .okButton()
                .click();

        // assert that the node was removed
        assertFalse(treeFixture.findTreePathAfterExpandAll(rowIndex));
    }

    @Test
    public void test_sortStartingFromRoot() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        // assert precondition
        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());
        // deselect root
        treeFixture.changeSelection(0);
        // select first row after root
        treeFixture.changeSelection(1);
        AssertionHelper.assertSelectedRowValue("SamplePDF.pdf", treeFixture, ASSERTION_TIMEOUT);
        treeFixture.changeSelection(1);
        // select root again since the sorting needs a selected node
        treeFixture.changeSelection(0);

        // sort
        sipWindow.button(SIP_SORT_BUTTON).click();

        // sorting deselects all
        treeFixture.changeSelection(4);
        // "SamplePDF.pdf" node moved on the 4-th row
        AssertionHelper.assertSelectedRowValue("SamplePDF.pdf", treeFixture, ASSERTION_TIMEOUT);
        treeFixture.changeSelection(4);

        // sorting: goes: folders before files
        treeFixture.changeSelection(1);
        AssertionHelper.assertSelectedRowValue("subfolder1", treeFixture, ASSERTION_TIMEOUT);
        treeFixture.changeSelection(1);

        treeFixture.changeSelection(2);
        AssertionHelper.assertSelectedRowValue("subfolder2", treeFixture, ASSERTION_TIMEOUT);
    }

    @Test
    public void test_normalize() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        // assert precondition
        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());

        // normalize
        sipWindow.button(SIP_NORMALIZE_BUTTON).click();

        // confirm normalize action
        JOptionPaneFinder.findOptionPane().withTimeout(ASSERTION_TIMEOUT).using(robot).yesButton().click();

        // feedback - how many normalized and show which not normalized
        JOptionPaneFinder.findOptionPane().withTimeout(ASSERTION_TIMEOUT).using(robot)
                .requireTitle(I18N.translate("MessageNormalizeAllSuccessful", 11))
                .requireMessage(I18N.translate("MessageCannotNormalize"))
                .okButton().click();

        treeFixture.changeSelection(4);

        AssertionHelper.assertSelectedRowValue("PREFileWithUmlauts_ueaeoeSUF.txt", treeFixture, ASSERTION_TIMEOUT);

        // deselect previous
        treeFixture.changeSelection(4);
        assertTrue(treeFixture.findTreePathAfterExpandAll(10));

        treeFixture.changeSelection(10);
        // this node was not normalized
        AssertionHelper.assertSelectedRowValue("FileWithUmlauts_üäö_duplicate_éàé.txt", treeFixture,
                ASSERTION_TIMEOUT);
    }
}
