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

package ch.docuteam.packer.gui.launcher;

import static ch.docuteam.packer.gui.ComponentNames.DuplicatesTableDialog_REMOVE;
import static ch.docuteam.packer.gui.ComponentNames.FOOTER_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CLEAR_SEARCH_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CREATE_NEW_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CREATE_NEW_SIP_ROOT_NAME_TEXT_FIELD;
import static ch.docuteam.packer.gui.ComponentNames.SIP_CREATE_OK_BUTTON;
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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

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
import org.junit.Ignore;
import org.junit.Test;

public class LauncherViewSwingIT {

    private static final int DEFAULT_PAUSE_MILLISECONDS = 500;

    private static final String SAMPLE_SIP_ZIP_1 = "sipwithlongpathnames.zip";

    private static final String SAMPLE_SIP_1 = "sipwithlongpathnames";

    private static final String SAMPLE_SIP_ZIP_2 = "sampleSIP.zip";

    private static final String SAMPLE_SIP_CREATE_SOURCE_FOLDER = "INPUT_FOLDER_FOR_PACKER";

    private static final String SAMPLE_FOLDER = "folder";

    private static final String SAMPLE_FILE = "samplePNG.png";

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
            final File sipSrcFile = ResourceUtil.getResource("data/" + SAMPLE_SIP_ZIP_1);
            final File destFile = new File(WORKSPACE_FOLDER, SAMPLE_SIP_ZIP_1);
            FileUtils.copyFile(sipSrcFile, destFile);
            assertTrue("ERROR: destFile does not exist!", destFile.exists());

            FileUtils.copyFile(ResourceUtil.getResource("data/" + SAMPLE_SIP_ZIP_2), new File(WORKSPACE_FOLDER,
                    SAMPLE_SIP_ZIP_2));
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
        window.requireTitle(FileUtil.asCanonicalFileName(WORKSPACE_FOLDER));
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
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // assert footer text
        final Pattern sipViewPattern = Pattern.compile(I18N.translate("MessageFooterSaved") + ".*" + SAMPLE_SIP_1 +
                ".*");
        sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD).requireText(sipViewPattern);

        pause(DEFAULT_PAUSE_MILLISECONDS);
        // assert frame title
        sipWindow.requireTitle(FileUtil.asCanonicalFileName(WORKSPACE_FOLDER + File.separator + SAMPLE_SIP_ZIP_1));
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
        pause(DEFAULT_PAUSE_MILLISECONDS);
        sipWindow.menuItem(SIP_VIEW_SAVE_AS_MENU_ITEM).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);
        // cannot find because it is a java.awt.FileDialog instead of a JFileChooser -> refactored
        final String renamedSIP = "Renamed" + SAMPLE_SIP_1;
        chooseFileByEnteringFilePath(sipWindow, WORKSPACE_FOLDER + File.separator + renamedSIP, true);

        pause(DEFAULT_PAUSE_MILLISECONDS);
        // assert frame title
        sipWindow.requireTitle(WORKSPACE_FOLDER + File.separator + renamedSIP);
    }

    @Test
    public void test_saveAsTemplate() throws InterruptedException {
        final LauncherView launcher = window.targetCastedTo(LauncherView.class);
        launcher.setTemplateDirectory(TEMPLATE_FOLDER);
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_1);

        sipWindow.menuItem(SIP_VIEW_FILE_MENU).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);
        sipWindow.menuItem(SIP_VIEW_SAVE_AS_TEMPLATE_MENU_ITEM).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        final String sipTemplateName = "sipTemplate" + System.currentTimeMillis();
        sipWindow.optionPane().textBox().setText(sipTemplateName);
        sipWindow.optionPane().buttonWithText("OK").click();

        // assert footer text ("Vorlage erstellt: " + sipTemplateName)
        final Pattern sipViewPattern = Pattern.compile(I18N.translate("MessageFooterTemplateSaved") + ".*" +
                sipTemplateName + ".*");
        sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD).requireText(sipViewPattern);
    }

    /**
     * Creates new SIP using the INPUT_FOLDER.
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_createNewSIP_fromFolder() throws InterruptedException {
        chooseWorkspace();

        window.menuItem(SIP_MENU).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);
        window.menuItem(SIP_CREATE_NEW_MENU_ITEM).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

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
        window.button(SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON).doubleClick();// .click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // add sip name in a textBox
        final String uniqueSIPName = "SIPjunitout" + System.currentTimeMillis();
        window.textBox(SIP_DESTINATION_NAME_TEXT_FIELD).selectAll().setText(uniqueSIPName);
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // click save button
        assertTrue("ERROR: Save button is not enabled!", window.button(SIP_CREATE_OK_BUTTON).isEnabled());
        window.button(SIP_CREATE_OK_BUTTON).click();

        // wait 30 sec., this is a very long operation
        pause(30000);

        // close warn message
        window.dialog(ScrollableMessageDialog.COMPONENT_NAME).close();

        // assert sip creation in the LauncherView (MessageFooterNewFile translation)
        final String newFileMessagePrefix = I18N.translate("MessageFooterNewFile");
        final Pattern pattern = Pattern.compile(newFileMessagePrefix + ".*" + uniqueSIPName + ".*");
        window.textBox(FOOTER_TEXT_FIELD).requireText(pattern);

        robot = BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock();
        sipWindow = WindowFinder.findFrame(SIP_VIEW_FRAME).using(robot);

        // assert footer text
        final Pattern sipViewPattern = Pattern.compile(I18N.translate("MessageFooterOpeningFile") + ".*" +
                uniqueSIPName + ".*");
        sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD).requireText(sipViewPattern);

        // assert title
        pause(DEFAULT_PAUSE_MILLISECONDS);
        final String outSIPPathName = WORKSPACE_FOLDER + File.separator + uniqueSIPName + ".zip";
        sipWindow.requireTitle(FileUtil.asCanonicalFileName(outSIPPathName));

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
        pause(DEFAULT_PAUSE_MILLISECONDS);

        chooseFileByEnteringFilePath(sipWindow, INPUT_FOLDER_SMALL, false);
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // assert modified
        sipWindow.label(SIP_VIEW_INFO_LABEL).requireText(I18N.translate("LabelIsModified"));

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
        pause(DEFAULT_PAUSE_MILLISECONDS);

        if (pathName == null) {
            fileChooser.cancel();
        } else {
            // ALWAYS USE FORWARD SLASHES FOR WRITING PATHS INTO fileNameTextBox (due to RobotEventGenerator)
            pathName = pathName.replaceAll("\\\\", "/");
            if (OS.isMacOSX()) {
                final File selection = new File(pathName);
                fileChooser.setCurrentDirectory(selection.getParentFile());
                pause(DEFAULT_PAUSE_MILLISECONDS);
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
        pause(DEFAULT_PAUSE_MILLISECONDS);

        final JFileChooserFixture fileChooser = frameFixture.fileChooser();

        File fileToSelect = new File(folderPath);
        if (filePath != null) {
            fileToSelect = new File(fileToSelect, filePath);
        }

        fileChooser.setCurrentDirectory(fileToSelect.getParentFile());
        pause(DEFAULT_PAUSE_MILLISECONDS);

        fileChooser.selectFile(fileToSelect);
        fileChooser.approve();
        pause(DEFAULT_PAUSE_MILLISECONDS);
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
        pause(DEFAULT_PAUSE_MILLISECONDS);
        treeFixture.rightClick();
        sipWindow.menuItem(SIP_VIEW_SORT_FOLDER_MENU_ITEM).click();

        // select third row after root
        pause(DEFAULT_PAUSE_MILLISECONDS);
        treeFixture.changeSelection(3);
        treeFixture.rightClick();
        sipWindow.menuItem(SIP_REPLACE_FILE_MENU_ITEM).click();

        // choose another file
        chooseFileByEnteringFilePath(sipWindow, ResourceUtil.getResourceCanonicalPath("data/" + SAMPLE_FILE), false);

        pause(DEFAULT_PAUSE_MILLISECONDS);

        // assert modified
        sipWindow.label(SIP_VIEW_INFO_LABEL).requireText(I18N.translate("LabelIsModified"));

        // select replaced and assert success
        // select third row after root
        treeFixture.changeSelection(3);
        // assert footer test
        final Pattern footerPattern = Pattern.compile(".*" + SAMPLE_FILE + ".*");
        sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD).requireText(footerPattern);

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
        // window.table().cell(sipName).rightClick();

        // click on menuItem
        window.menuItem(SIP_OPEN_IN_WORKSPACE_MENU_ITEM).click();

        // check that a new Frame (SIPView) was opened
        robot = BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock(); // .robotWithCurrentAwtHierarchy();
        sipWindow = WindowFinder.findFrame(SIP_VIEW_FRAME).using(robot);
        pause(DEFAULT_PAUSE_MILLISECONDS);
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
        pause(DEFAULT_PAUSE_MILLISECONDS);
        treeFixture.rightClick();
        sipWindow.menuItem(SIP_VIEW_SORT_FOLDER_MENU_ITEM).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // select view menu
        sipWindow.menuItem(SIP_VIEW_MENU).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // expand all
        sipWindow.menuItem(SIP_VIEW_EXPAND_MENU_ITEM).click();

        // assertTrue(treeFixture.isExpanded(path)); //doesn't work
        // assertTrue(treeFixture.isVisible(path)); //doesn't work
        assertTrue(treeFixture.isExpanded(2));
        treeFixture.changeSelection(2);
        pause(DEFAULT_PAUSE_MILLISECONDS);
        assertEquals("subfolder_same_name", treeFixture.getSelectedRowValue());

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
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // select second row after root
        treeFixture.changeSelection(2);
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // get selected value
        final String rowValue = treeFixture.getSelectedRowValue();
        // Logger.info("rowValue: " + rowValue);
        assertEquals(treeFixture.getSelectedRow(), treeFixture.getTreeRow(rowValue));

        treeFixture.rightClick();
        sipWindow.menuItem(SIP_DELETE_ITEM_MENU_ITEM).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);
        // confirm deletion
        sipWindow.optionPane().yesButton().click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // assert modified
        sipWindow.label(SIP_VIEW_INFO_LABEL).requireText(I18N.translate("LabelIsModified"));
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
        pause(DEFAULT_PAUSE_MILLISECONDS);
        window.menuItem(SIP_CREATE_NEW_MENU_ITEM).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        final String brandNewSIP = "NewSIP" + System.currentTimeMillis();
        // choose SIP name
        window.textBox(SIP_CREATE_NEW_SIP_ROOT_NAME_TEXT_FIELD).setText(brandNewSIP);

        // choose target folder
        assertTrue("ERROR: selectDestinationIsWorkspaceButton is not enabled!", window.button(
                SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON).isEnabled());
        window.button(SIP_SELECT_DESTINATION_IS_WORKSPACE_BUTTON).doubleClick();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // click save button
        assertTrue("ERROR: Save button is not enabled!", window.button(SIP_CREATE_OK_BUTTON).isEnabled());
        window.button(SIP_CREATE_OK_BUTTON).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // assert sip creation in the LauncherView (MessageFooterNewFile translation)
        final String newFileMessagePrefix = I18N.translate("MessageFooterNewFile");
        final Pattern pattern = Pattern.compile(newFileMessagePrefix + ".*" + brandNewSIP + ".*");
        window.textBox(FOOTER_TEXT_FIELD).requireText(pattern);

        // check that a new Frame (SIPView) was opened
        robot = BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock(); // .robotWithCurrentAwtHierarchy();
        sipWindow = WindowFinder.findFrame(SIP_VIEW_FRAME).using(robot);

        // assert title
        pause(DEFAULT_PAUSE_MILLISECONDS);
        final String outSIPPathName = WORKSPACE_FOLDER + File.separator + brandNewSIP + ".zip";
        sipWindow.requireTitle(FileUtil.asCanonicalFileName(outSIPPathName));

        // create new folder
        sipWindow.menuItem(SIP_VIEW_ELEMENT_MENU).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);
        sipWindow.menuItem(SIP_VIEW_CREATE_FOLDER_MENU_ITEM).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        final String newFolderName = "NewFolder";
        sipWindow.dialog().textBox().setText(newFolderName);
        sipWindow.optionPane().buttonWithText("OK").click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // select newly created folder
        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());
        // select first row after root
        treeFixture.changeSelection(1);

        // assert footer text, new folder name should be present
        Pattern sipViewPattern = Pattern.compile(".*" + brandNewSIP + ".*" + newFolderName + ".*");
        sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD).requireText(sipViewPattern);

        // rename folder
        // open POPUP_MENU
        treeFixture.rightClick();

        sipWindow.menuItem(SIP_VIEW_RENAME_FOLDER_MENU_ITEM).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        final String renamedFolderName = "RenamedFolder";
        sipWindow.dialog().textBox().setText(renamedFolderName);
        sipWindow.optionPane().buttonWithText("OK").click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // deselect current
        JXTreeTableComponentFixtureExtension.treeWithName(SIP_VIEW_TREE).createFixture(robot, sipWindow.target())
                .changeSelection(1);
        pause(DEFAULT_PAUSE_MILLISECONDS);
        // select root
        JXTreeTableComponentFixtureExtension.treeWithName(SIP_VIEW_TREE).createFixture(robot, sipWindow.target())
                .changeSelection(0);
        pause(DEFAULT_PAUSE_MILLISECONDS);
        // select first child again
        JXTreeTableComponentFixtureExtension.treeWithName(SIP_VIEW_TREE).createFixture(robot, sipWindow.target())
                .changeSelection(1);
        pause(DEFAULT_PAUSE_MILLISECONDS);

        sipViewPattern = Pattern.compile(".*" + brandNewSIP + ".*" + renamedFolderName + ".*");
        sipWindow.textBox(SIP_VIEW_FOOTER_TEXT_FIELD).requireText(sipViewPattern);
    }

    @Ignore
    @Test
    public void test_createSIPWithAlternativeLevels() {
        fail("Not implemented");
    }

    @Ignore
    @Test
    public void test_updateSAsFromServer() {
        fail("Not implemented");
    }

    @Ignore
    @Test
    public void test_elementExport() {
        fail("Not implemented");
    }

    @Test
    public void test_metadataEADExport() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        sipWindow.menuItem(SIP_VIEW_FILE_MENU).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);
        sipWindow.menuItem(SIP_METADATA_EXPORT_MENU).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);
        sipWindow.menuItem(SIP_EAD_EXPORT_MENU_ITEM).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // file chooser is visible
        final JFileChooserFixture fileChooser = sipWindow.fileChooser();
        fileChooser.requireVisible();
        fileChooser.setCurrentDirectory(new File(WORKSPACE_FOLDER));
        fileChooser.approve();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // assert success - since no GUI notification in case of success, check the exported file
        final File exportedFile = new File(WORKSPACE_FOLDER, SAMPLE_SIP_ZIP_2 + ".xml");
        assertTrue(exportedFile.exists());
    }

    @Ignore
    @Test
    public void test_metadataCSVExport() {
        fail("Not implemented");
    }

    @Ignore
    @Test
    public void test_changeMetadataFieldsToDescription() {
        fail("Not implemented");
    }

    @Test
    public void test_searchInSIP() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);
        pause(DEFAULT_PAUSE_MILLISECONDS);

        sipWindow.textBox(SIP_SEARCH_TEXT_FIELD).enterText("sample");
        pause(DEFAULT_PAUSE_MILLISECONDS);

        sipWindow.button(SIP_SEARCH_BUTTON).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());
        assertEquals("the selected row is not SamplePDF.pdf", "SamplePDF.pdf", treeFixture.getSelectedRowValue());

        // clear and search again
        sipWindow.button(SIP_CLEAR_SEARCH_BUTTON).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        sipWindow.textBox(SIP_SEARCH_TEXT_FIELD).enterText("fundamentals");
        pause(DEFAULT_PAUSE_MILLISECONDS);

        sipWindow.button(SIP_SEARCH_BUTTON).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        assertEquals("the selected row is not 2fundamentals_lt.gif", "2fundamentals_lt.gif", treeFixture
                .getSelectedRowValue());

        // next match
        sipWindow.button(SIP_SEARCH_NEXT_BUTTON).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        assertEquals("the selected row is not 2fundamentals_lt.tif", "2fundamentals_lt.tif", treeFixture
                .getSelectedRowValue());

        // previous match
        sipWindow.button(SIP_SEARCH_PREVIOUS_BUTTON).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);
        assertEquals("the selected row is not 2fundamentals_lt.gif", "2fundamentals_lt.gif", treeFixture
                .getSelectedRowValue());

    }

    @Ignore
    @Test
    public void test_searchInWorkspace() {
        fail("Not implemented");
    }

    @Ignore
    @Test
    public void test_checksum() {
        fail("Not implemented");
    }

    @Ignore
    @Test
    public void test_exit() {
        fail("Not implemented");
    }

    @Ignore
    @Test
    public void test_pattern() {
        final String uniqueSIPName = "_SIP_junit_out_1490345885412";
        final String pathName =
                "Neue Datei: C:\\Users\\lavinia\\AppData\\Local\\Temp\\packer_junit8606340865719849073\\workspace/INPUT_FOLDER_FOR_PACKER_SIP_junit_out_1490345885412.zip";
        assertTrue(Pattern.matches("a*b", "aaaaab"));
        assertTrue(Pattern.matches("Neue Datei:" + ".*" + uniqueSIPName + ".*", pathName));
    }

    @Ignore // runs OK locally, but fails on Jenkins
    @Test
    public void test_removeDuplicates() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);

        pause(DEFAULT_PAUSE_MILLISECONDS);
        // check precondition: finds "FileWithUmlauts_üäö.txt" node at rowIndex
        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());
        final int rowIndex = 11;
        assertTrue(treeFixture.findTreePathAfterExpandAll(11));

        sipWindow.menuItem(SIP_VIEW_FILE_MENU).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);
        sipWindow.menuItem(SIP_VIEW_REMOVE_DUPLICATES_MENU_ITEM).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        robot = BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock();
        final DialogFixture duplicatesTableDialog = WindowFinder.findDialog(DuplicatesTableDialog.class).using(robot);
        pause(DEFAULT_PAUSE_MILLISECONDS);

        duplicatesTableDialog.table().cell("false").select();
        pause(DEFAULT_PAUSE_MILLISECONDS);
        duplicatesTableDialog.button(DuplicatesTableDialog_REMOVE).click();
        pause(60000);

        // assert success message: JOptionPane
        JOptionPaneFinder.findOptionPane().using(robot).requireMessage(I18N.translate("MessageRemovedDuplicates"))
                .okButton().click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // assert that the node was removed
        assertFalse(treeFixture.findTreePathAfterExpandAll(rowIndex));
    }

    @Test
    public void test_sortStartingFromRoot() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // assert precondition
        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());
        // deselect root
        treeFixture.changeSelection(0);
        pause(DEFAULT_PAUSE_MILLISECONDS);
        // select first row after root
        treeFixture.changeSelection(1);
        pause(DEFAULT_PAUSE_MILLISECONDS);
        assertEquals("the selected row is not SamplePDF.pdf", "SamplePDF.pdf", treeFixture.getSelectedRowValue());
        treeFixture.changeSelection(1);
        // select root again since the sorting needs a selected node
        treeFixture.changeSelection(0);

        // sort
        sipWindow.button(SIP_SORT_BUTTON).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // sorting deselects all
        treeFixture.changeSelection(4);
        pause(DEFAULT_PAUSE_MILLISECONDS);
        // "SamplePDF.pdf" node moved on the 4-th row
        assertEquals("the selected row is not SamplePDF.pdf", "SamplePDF.pdf", treeFixture.getSelectedRowValue());
        treeFixture.changeSelection(4);

        // sorting: goes: folders before files
        treeFixture.changeSelection(1);
        pause(DEFAULT_PAUSE_MILLISECONDS);
        assertEquals("the selected row is not subfolder1", "subfolder1", treeFixture.getSelectedRowValue());
        treeFixture.changeSelection(1);

        treeFixture.changeSelection(2);
        pause(DEFAULT_PAUSE_MILLISECONDS);
        assertEquals("the selected row is not subfolder2", "subfolder2", treeFixture.getSelectedRowValue());
    }

    @Test
    public void test_normalize() throws InterruptedException {
        sipWindow = openSIPInWorkspace(SAMPLE_SIP_ZIP_2);
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // assert precondition
        final JXTreeTableComponentFixture treeFixture = JXTreeTableComponentFixtureExtension.treeWithName(
                SIP_VIEW_TREE).createFixture(robot, sipWindow.target());

        // normalize
        sipWindow.button(SIP_NORMALIZE_BUTTON).click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // confirm normalize action
        JOptionPaneFinder.findOptionPane().using(robot).yesButton().click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // feedback - how many normalized and show which not normalized
        JOptionPaneFinder.findOptionPane().using(robot)
                .requireTitle(I18N.translate("MessageNormalizeAllSuccessful", 11))
                .requireMessage(I18N.translate("MessageCannotNormalize"))
                .okButton().click();
        pause(DEFAULT_PAUSE_MILLISECONDS);

        treeFixture.changeSelection(4);
        pause(DEFAULT_PAUSE_MILLISECONDS);

        assertEquals("the selected row is not PREFileWithUmlauts_ueuauoSUF.txt", "PREFileWithUmlauts_ueaeoeSUF.txt",
                treeFixture.getSelectedRowValue());
        pause(DEFAULT_PAUSE_MILLISECONDS);

        // deselect previous
        treeFixture.changeSelection(4);
        assertTrue(treeFixture.findTreePathAfterExpandAll(10));

        treeFixture.changeSelection(10);
        // this node was not normalized
        assertEquals("the selected row is not FileWithUmlauts_üäö_duplicate_éàé.txt",
                "FileWithUmlauts_üäö_duplicate_éàé.txt", treeFixture.getSelectedRowValue());
    }
}
