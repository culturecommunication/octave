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

package ch.docuteam.packer.gui.sipView;

import static ch.docuteam.mapping.util.ExporterUtil.PACKER_CONSTANTS_OPERATOR;
import static ch.docuteam.packer.gui.ComponentNames.SIP_DELETE_ITEM_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_EAD_EXPORT_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_METADATA_EXPORT_MENU;
import static ch.docuteam.packer.gui.ComponentNames.SIP_NORMALIZE_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_REPLACE_FILE_MENU_ITEM;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SAVE_CURRENT_BUTTON;
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
import static ch.docuteam.packer.gui.PackerConstants.DELETE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.EXCEPTION_COLLECTOR_NO_EXCEPTION;
import static ch.docuteam.packer.gui.PackerConstants.EXCEPTION_COLLECTOR_PREFIX;
import static ch.docuteam.packer.gui.PackerConstants.HOME_PNG;
import static ch.docuteam.packer.gui.PackerConstants.ICON_LEVEL_UNKNOWN;
import static ch.docuteam.packer.gui.PackerConstants.OPERATOR;
import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.REDISPLAY_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SAVE_PNG;
import static ch.docuteam.packer.gui.PackerConstants.USER_HOME;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import javax.xml.transform.TransformerException;

import ch.docuteam.darc.exceptions.CantCreateTemplateWithRootFileException;
import ch.docuteam.darc.exceptions.CantSetSubmitStatusNotAllowedException;
import ch.docuteam.darc.exceptions.CantSetSubmitStatusRecursiveException;
import ch.docuteam.darc.exceptions.DocumentIsReadOnlyException;
import ch.docuteam.darc.exceptions.FileAlreadyExistsException;
import ch.docuteam.darc.exceptions.FileOperationNotAllowedException;
import ch.docuteam.darc.exceptions.FileOrFolderIsInUseException;
import ch.docuteam.darc.exceptions.MetadataElementCantAddException;
import ch.docuteam.darc.exceptions.MetadataElementCantDeleteException;
import ch.docuteam.darc.exceptions.NodeWithLevelNotRemovableException;
import ch.docuteam.darc.exceptions.OriginalSIPIsMissingException;
import ch.docuteam.darc.exceptions.ZIPDoesNotContainMETSFileException;
import ch.docuteam.darc.ingest.AIPCreatorProxy;
import ch.docuteam.darc.mdconfig.LevelMetadataElement;
import ch.docuteam.darc.mdconfig.LevelOfDescription;
import ch.docuteam.darc.mdconfig.MetadataElementInstance;
import ch.docuteam.darc.mets.Document;
import ch.docuteam.darc.mets.Document.Mode;
import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.mets.structmap.NodeAbstract.SubmitStatus;
import ch.docuteam.darc.mets.structmap.NodeFile;
import ch.docuteam.darc.mets.structmap.NodeFolder;
import ch.docuteam.darc.premis.Event;
import ch.docuteam.darc.util.CSVExport;
import ch.docuteam.darc.util.XML2PDF;
import ch.docuteam.darc.util.XMLTransformer;
import ch.docuteam.darc.util.file.FileNameNormalizer;
import ch.docuteam.mapping.ExporterSEDA;
import ch.docuteam.packer.gui.FileProperty;
import ch.docuteam.packer.gui.filePreview.FilePreviewer;
import ch.docuteam.packer.gui.launcher.LauncherView;
import ch.docuteam.packer.gui.sipView.actions.AppendMigratedFileAction;
import ch.docuteam.packer.gui.sipView.actions.CheckFixityAction;
import ch.docuteam.packer.gui.sipView.actions.ConvertFilesAction;
import ch.docuteam.packer.gui.sipView.actions.DeleteFileContentAction;
import ch.docuteam.packer.gui.sipView.actions.DeleteItemAction;
import ch.docuteam.packer.gui.sipView.actions.ExploreAction;
import ch.docuteam.packer.gui.sipView.cellRenderer.HasMandatoryMetadataFieldsNotSetCellRenderer;
import ch.docuteam.packer.gui.sipView.cellRenderer.MyTreeCellRenderer;
import ch.docuteam.packer.gui.sipView.cellRenderer.RelativeSizeBarTableCellRenderer;
import ch.docuteam.packer.gui.sipView.cellRenderer.SubmitStatusTableCellRenderer;
import ch.docuteam.packer.gui.sipView.tableModel.EventDetailTableModel;
import ch.docuteam.packer.gui.sipView.tableModel.EventListTableModel;
import ch.docuteam.packer.gui.sipView.tableModel.FileDataViewTableModel;
import ch.docuteam.packer.gui.sipView.tableModel.MetadataTableModel;
import ch.docuteam.packer.gui.sipView.tableModel.TreeTableModel;
import ch.docuteam.packer.gui.util.Util;
import ch.docuteam.tools.exception.ExceptionCollector;
import ch.docuteam.tools.exception.ExceptionCollectorException;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.file.exception.FileUtilExceptionListException;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.gui.JTableWithDynamicToolTipTexts;
import ch.docuteam.tools.gui.ScrollableMessageDialog;
import ch.docuteam.tools.gui.SmallPeskyMessageWindow;
import ch.docuteam.tools.os.OperatingSystem;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.out.Tracer;
import ch.docuteam.tools.translations.I18N;

import org.dom4j.DocumentException;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

/**
 * @author denis
 */
@SuppressWarnings("serial")
public class SIPView extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // filename patterns: [sourcetype_][report-label][_outputformat].xsl
    private static final String REPORT_STYLESHEET_PREFIX_SOURCE_TYPE_METS = "METS_";

    private static final String REPORT_STYLESHEET_PREFIX_SOURCE_TYPE_EAD = "EAD_";

    private static final String REPORT_STYLESHEET_SUFFIX_OUTPUT_FORMAT_CSV = "_CSV";

    private static final String OUTPUT_FORMAT_CSV = "csv";

    private static final String OUTPUT_FORMAT_PDF = "pdf";

    protected static final Color ColorFGForNonReadableFile = Color.WHITE;

    protected static final Color ColorBGForNonReadableFile = Color.LIGHT_GRAY;

    // OS default
    protected static final Color ColorFGForNonWritableFile = null;

    // Pale red
    protected static final Color ColorBGForNonWritableFile = new Color(253, 152, 154);

    // protected static final Color ColorFGForNonWritableFile = Color.WHITE;
    // protected static final Color ColorBGForNonWritableFile = new Color(255, 255, 155); // Pale yellow

    // The following static fields contain default values - they might be
    // overwritten when reading the property file.
    // In addition, some of these fields can be overwritten using command line
    // parameters:

    protected static String DefaultFrameTitle = "";

    protected static Boolean openFullScreen = false;

    public static boolean saveWithBackups = true;

    // If not overridden by the property file, center on screen
    protected static Integer screenPosX = null;

    protected static Integer screenPosY = null;

    // If not overridden by the property file, use this window width and height
    protected static Integer screenSizeX = 1000;

    protected static Integer screenSizeY = 700;

    private static Properties properties;

    protected LauncherView launcherView;

    protected JXTreeTable treeTable;

    protected JTableWithDynamicToolTipTexts dataTable;

    protected JTable eventTable;

    protected JTableWithDynamicToolTipTexts eventDetailTable;

    protected MetadataTable metadataTable;

    private JScrollPane metadataTableScrollPane;

    protected JLabel infoLabel;

    protected JLabel fileDataPropertiesLabel;

    protected JLabel fileDateEventsLabel;

    protected JLabel fileDataEventsDetailsLabel;

    protected JTextField metaTitleTextField;

    protected JTextField metaLevelTextField;

    protected JButton logoButton;

    protected JTextField footerTextField;

    protected JSplitPane splitPane;

    protected JTabbedPane tabbedPane;

    protected FilePreviewer previewPanel;

    protected Action insertAction;

    protected Action saveAction;

    protected Action closeAction;

    protected Action saveAsAction;

    protected Action createFolderAction;

    protected Action renameItemAction;

    protected Action replaceFileAction;

    protected AppendMigratedFileAction appendMigratedFileAction;

    protected Action deleteItemAction;

    protected Action deleteItemDontAskAction;

    protected DeleteFileContentAction deleteFileContentAction;

    protected Action openSAExternallyAction;

    protected Action testOrAssignSAAction;

    protected Action openAssignLevelsByLayerViewAction;

    protected Action openAssignLevelsByLabelViewAction;

    protected Action exportAsEADFileAction;

    protected Action exportAsCSVFileAction;

    private Action exportAsSEDA21Action;

    private Action exportAsSEDA1Action;

    protected Action openDocuteamHomepageAction;

    protected Action expandAllAction;

    protected Action collapseAllAction;

    protected Action removeMetadataElementAction;

    protected Action insertMetadataElementAction;

    protected Action redisplayNodeAction;

    protected Action systemOutDocumentAction;

    protected Action exportAction;

    protected Action saveAsTemplateAction;

    protected CheckFixityAction checkFixityAction;

    protected ExploreAction exploreAction;

    protected Action submitRequestAction;

    protected Action submitRetractAction;

    protected Action submitCheckAction;

    protected Action submitAction;

    protected Action sortAction;

    private ConvertFilesAction convertAction;

    private AbstractAction normalizeAction;

    private Action removeTrashAction;

    private Action removeDuplicatesAction;

    protected JComboBox<LevelMetadataElement> selectMetadataElementComboBox;

    protected List<Action> setLevelActions;

    protected JMenu fileMenu;

    protected JMenu mdExportSubMenu;

    private JMenu sipExportSubMenu;

    protected JMenu searchMenu;

    protected JMenu viewMenu;

    protected JMenu itemMenu;

    protected JMenu itemLevelsSubMenu;

    protected JMenu saMenu;

    protected JMenu reportsSubMenu;

    protected JPopupMenu popupMenu;

    protected int popupMenuStartOfLevelsSubMenu;

    /**
     * This sipPath is used as an ID, to prevent several views on the same SIP.
     */
    protected FileProperty fileProperty;

    protected Document document;

    protected NodeAbstract selectedNode;

    protected int selectedIndex = -1;

    protected SIPView(final LauncherView launcherView) {
        super(DefaultFrameTitle);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                closeButtonClicked();
            }
        });
        setIconImage(getImage(PACKER_PNG));

        this.launcherView = launcherView;

        // Tables:

        treeTable = new JXTreeTable(new TreeTableModel(null));
        treeTable.setName(SIP_VIEW_TREE);

        // HighlightPredicate for non-writable elements:
        final HighlightPredicate nonWritablePredicate = new HighlightPredicate() {

            @Override
            public boolean isHighlighted(final Component renderer, final ComponentAdapter adapter) {
                try {
                    return !((NodeAbstract) treeTable.getPathForRow(adapter.row).getLastPathComponent()).canWrite();
                } catch (final NullPointerException ex) {
                    return false;
                }
            }
        };
        treeTable.addHighlighter(
                new ColorHighlighter(nonWritablePredicate, ColorBGForNonWritableFile, ColorFGForNonWritableFile));

        // HighlightPredicate for non-readable elements:
        final HighlightPredicate nonReadablePredicate = new HighlightPredicate() {

            @Override
            public boolean isHighlighted(final Component renderer, final ComponentAdapter adapter) {
                try {
                    final NodeAbstract node = (NodeAbstract) treeTable.getPathForRow(adapter.row)
                            .getLastPathComponent();
                    return !node.fileExists() || !node.canRead();
                } catch (final NullPointerException ex) {
                    return false;
                }
            }
        };
        treeTable.addHighlighter(
                new ColorHighlighter(nonReadablePredicate, ColorBGForNonReadableFile, ColorFGForNonReadableFile));

        treeTable.setEnabled(true);
        treeTable.setAutoCreateColumnsFromModel(false);
        treeTable.setRootVisible(true);
        treeTable.setShowsRootHandles(true);
        treeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        treeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                treeViewSelectionChanged(e);
            }
        });
        treeTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                treeViewSelectionWasClicked(e);
            }
        });
        treeTable.setDragEnabled(true);
        treeTable.setDropMode(DropMode.ON_OR_INSERT_ROWS);
        treeTable.setTransferHandler(new TreeTableTransferHandler(this));
        treeTable.setTreeCellRenderer(new MyTreeCellRenderer());
        treeTable.getColumn(0).setPreferredWidth(300);
        treeTable.getColumn(1).setPreferredWidth(30);
        treeTable.getColumn(2).setPreferredWidth(10);
        treeTable.getColumn(3).setPreferredWidth(100);
        treeTable.getColumn(3).setCellRenderer(new RelativeSizeBarTableCellRenderer(treeTable.getColumn(3)));
        treeTable.getColumn(4).setMaxWidth(10);
        treeTable.getColumn(4).setCellRenderer(new HasMandatoryMetadataFieldsNotSetCellRenderer());
        treeTable.getColumn(5).setMaxWidth(12);
        treeTable.getColumn(5).setCellRenderer(new SubmitStatusTableCellRenderer());

        // Add key bindings for expanding and collapsing nodes using the
        // cursor-left and cursor-right keys:
        treeTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Collapse");
        treeTable.getActionMap().put("Collapse", new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                collapseCurrentNode();
            }
        });

        treeTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "Expand");
        treeTable.getActionMap().put("Expand", new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                expandCurrentNode();
            }
        });

        treeTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                "Collapse all");
        treeTable.getActionMap().put("Collapse all", new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                treeTable.collapseAll();
            }
        });

        treeTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                "Expand all");
        treeTable.getActionMap().put("Expand all", new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                expandAll();
            }
        });

        dataTable = new JTableWithDynamicToolTipTexts(new FileDataViewTableModel(), 1);
        dataTable.setEnabled(false);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(125);
        dataTable.getColumnModel().getColumn(0).setMinWidth(125);
        dataTable.setGridColor(Color.LIGHT_GRAY);

        eventTable = new JTable(new EventListTableModel());
        eventTable.setName(SIP_VIEW_EVENT_TABLE);
        eventTable.setEnabled(true);
        eventTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(final ListSelectionEvent e) {
                        eventTableSelectionChanged(e);
                    }
                });
        eventTable.setGridColor(Color.LIGHT_GRAY);

        eventDetailTable = new JTableWithDynamicToolTipTexts(new EventDetailTableModel(), 1);
        eventDetailTable.setEnabled(false);
        eventDetailTable.getColumnModel().getColumn(0).setMaxWidth(100);
        eventDetailTable.getColumnModel().getColumn(0).setMinWidth(100);
        eventDetailTable.setGridColor(Color.LIGHT_GRAY);

        metadataTable = new MetadataTable(new MetadataTableModel(this), 2);
        metadataTable.setEnabled(true);
        metadataTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(final ListSelectionEvent e) {
                        metadataTableSelectionChanged(e);
                    }
                });
        metadataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        metadataTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        metadataTable.getColumnModel().getColumn(0).setMaxWidth(30);
        metadataTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        metadataTable.getColumnModel().getColumn(1).setMaxWidth(300);
        metadataTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        metadataTable.setGridColor(Color.LIGHT_GRAY);
        metadataTable.setAutoscrolls(true);

        // TextFields:

        metaTitleTextField = new JTextField();
        metaTitleTextField.addFocusListener(
                new FocusAdapter() {

                    @Override
                    public void focusLost(final FocusEvent e) {
                        metaTitleTextFieldWasChanged();
                    }
                });
        metaTitleTextField.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        metaTitleTextFieldWasChanged();
                    }
                });

        metaLevelTextField = new JTextField();
        metaLevelTextField.setEditable(false); // This is always read-only

        // ComboBoxes:

        selectMetadataElementComboBox = new JComboBox<LevelMetadataElement>();
        selectMetadataElementComboBox.setEnabled(false);
        selectMetadataElementComboBox.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        enableOrDisableActions();
                    }
                });
        selectMetadataElementComboBox.setToolTipText(I18N.translate("ToolTipSelectMetadataElement"));

        // Actions:

        saveAction = new AbstractAction(I18N.translate("ButtonSave"), getImageIcon(SAVE_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                saveButtonClicked(e);
            }
        };
        saveAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSave"));
        saveAction.setEnabled(true);

        saveAsAction = new AbstractAction(I18N.translate("ButtonSaveAs"), getImageIcon(SAVE_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                saveAsButtonClicked();
            }
        };
        saveAsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK));
        saveAsAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSaveAs"));
        saveAsAction.setEnabled(true);

        closeAction = new AbstractAction(I18N.translate("ButtonClose"), getImageIcon("Close.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                closeButtonClicked();
            }
        };
        closeAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        closeAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipClose"));
        closeAction.setEnabled(true);

        insertAction = new AbstractAction(I18N.translate("ButtonInsert"), getImageIcon("Insert.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                insertFileOrFolderButtonClicked();
            }
        };
        insertAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipInsert"));
        insertAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()));
        insertAction.setEnabled(false);

        createFolderAction = new AbstractAction(I18N.translate("ButtonCreateFolder"), getImageIcon("AddFolder.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                createFolderButtonClicked();
            }
        };
        createFolderAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipCreateFolder"));
        createFolderAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()));
        createFolderAction.setEnabled(false);

        renameItemAction = new AbstractAction(I18N.translate("ButtonRenameItem"), getImageIcon("Rename.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                renameItemButtonClicked();
            }
        };
        renameItemAction.putValue(Action.ACCELERATOR_KEY, this.launcherView.getKeyStroke(KeyEvent.VK_R,
                InputEvent.ALT_DOWN_MASK, KeyEvent.VK_F2, InputEvent.ALT_DOWN_MASK, KeyEvent.VK_F2,
                InputEvent.CTRL_DOWN_MASK));
        renameItemAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipRenameItem"));
        renameItemAction.setEnabled(false);

        sortAction = new AbstractAction(I18N.translate("ButtonSort"), getImageIcon("Sort.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                sortButtonClicked();
            }
        };
        sortAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSort"));
        sortAction.setEnabled(false);

        normalizeAction = new AbstractAction(I18N.translate("ButtonNormalizeFileNames"), getImageIcon(
                "Normalize.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                normalizeButtonClicked();
            }
        };
        normalizeAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipButtonNormalizeFileNames"));
        normalizeAction.setEnabled(false);

        deleteItemAction = new DeleteItemAction(this);
        deleteItemAction.setEnabled(false);
        deleteItemAction.putValue(LauncherView.ACTION_HIDE_KEY, "deleteItemAction");

        deleteItemDontAskAction = new AbstractAction(I18N.translate("ButtonDeleteItemDontAsk"), getImageIcon(
                "Delete.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                deleteItemDontAskButtonClicked();
            }
        };
        deleteItemDontAskAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK));
        deleteItemDontAskAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipDeleteItemDontAsk"));
        deleteItemDontAskAction.setEnabled(false);
        deleteItemDontAskAction.putValue(LauncherView.ACTION_HIDE_KEY, "deleteItemDontAskAction");

        deleteFileContentAction = new DeleteFileContentAction(this);
        deleteFileContentAction.setEnabled(false);
        deleteFileContentAction.putValue(LauncherView.ACTION_HIDE_KEY, "deleteFileContentAction");

        replaceFileAction = new AbstractAction(I18N.translate("ButtonReplaceFile"), getImageIcon("Replace.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                replaceFileButtonClicked();
            }
        };
        replaceFileAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipReplaceFile"));
        replaceFileAction.setEnabled(false);

        appendMigratedFileAction = new AppendMigratedFileAction(this);
        appendMigratedFileAction.setEnabled(false);

        openSAExternallyAction = new AbstractAction(I18N.translate("ButtonOpenSAExternally"), getImageIcon(
                "View.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                openSAExternallyButtonClicked();
            }
        };
        openSAExternallyAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipOpenSAExternally"));
        openSAExternallyAction.setEnabled(false);

        testOrAssignSAAction = new AbstractAction(I18N.translate("ButtonTestOrAssignSA"), getImageIcon(
                "CheckSA.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                testOrAssignSAButtonClicked();
            }
        };
        testOrAssignSAAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipTestOrAssignSA"));
        testOrAssignSAAction.setEnabled(false);

        openAssignLevelsByLayerViewAction = new AbstractAction(I18N.translate("ButtonOpenAssignLevelsByLayerView"),
                getImageIcon("Structure.png")) {

            /**
                     *
                     */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                openAssignLevelsByLayerViewButtonClicked();
            }
        };
        openAssignLevelsByLayerViewAction.putValue(Action.SHORT_DESCRIPTION,
                I18N.translate("ToolTipOpenAssignLevelsByLayerView"));
        openAssignLevelsByLayerViewAction.setEnabled(true);

        openAssignLevelsByLabelViewAction = new AbstractAction(I18N.translate("ButtonOpenAssignLevelsByLabelView"),
                getImageIcon("Structure.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                openAssignLevelsByLabelViewButtonClicked();
            }
        };
        openAssignLevelsByLabelViewAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate(
                "ToolTipOpenAssignLevelsByLabelView"));
        openAssignLevelsByLabelViewAction.setEnabled(true);

        exportAsEADFileAction = new AbstractAction(I18N.translate("ButtonExportAsEADFile"), getImageIcon(
                "ExportAsEADFile.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                exportAsEADFileButtonClicked();
            }
        };
        exportAsEADFileAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipExportAsEADFile"));
        exportAsEADFileAction.setEnabled(true);

        exportAsCSVFileAction = new AbstractAction(I18N.translate("ButtonExportAsCSVFile"), getImageIcon(
                "ExportAsCSVFile.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                exportAsCSVFileButtonClicked();
            }
        };
        exportAsCSVFileAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipExportAsCSVFile"));
        exportAsCSVFileAction.setEnabled(true);

        exportAsSEDA21Action = new AbstractAction(I18N.translate("ExportAsSEDA21Action"), getImageIcon(
                "ExportAsSEDA21.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                exportAsSEDA_ActionButtonClicked(new ch.docuteam.mapping.seda21.Exporter(),
                        "TitleSaveSIPAsSEDA21_SIP", "ExportAsSEDA21ActionSuccess", "ExportAsSEDA21ActionFailure");
            }
        };
        exportAsSEDA21Action.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipExportAsSEDA21Action"));
        exportAsSEDA21Action.setEnabled(true);

        exportAsSEDA1Action = new AbstractAction(I18N.translate("ExportAsSEDA1Action"), getImageIcon(
                "ExportAsSEDA1.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                exportAsSEDA_ActionButtonClicked(new ch.docuteam.mapping.seda1.Exporter(), "TitleSaveSIPAsSEDA1_SIP",
                        "ExportAsSEDA1ActionSuccess", "ExportAsSEDA1ActionFailure");
            }
        };
        exportAsSEDA1Action.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipExportAsSEDA1Action"));
        exportAsSEDA1Action.setEnabled(true);

        exportAction = new AbstractAction(I18N.translate("ButtonExport"), getImageIcon("Export.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                exportButtonClicked();
            }
        };
        exportAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipExport"));
        exportAction.setEnabled(false);

        openDocuteamHomepageAction = new AbstractAction(I18N.translate("ButtonOpenDocuteamHomepage"), getImageIcon(
                HOME_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                openDocuteamHomepage();
            }
        };
        openDocuteamHomepageAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipOpenDocuteamHomepage"));
        openDocuteamHomepageAction.setEnabled(true);

        systemOutDocumentAction = new AbstractAction("SystemOut Document") {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                systemOutDocument();
            }
        };

        saveAsTemplateAction = new AbstractAction(I18N.translate("ButtonSaveAsTemplate"), getImageIcon(SAVE_PNG)) {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                saveAsTemplateButtonClicked();
            }
        };
        if (!OperatingSystem.isLinux()) {
            saveAsTemplateAction.putValue(Action.ACCELERATOR_KEY, this.launcherView.getKeyStroke(KeyEvent.VK_S,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.ALT_DOWN_MASK));
        }
        saveAsTemplateAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSaveAsTemplate"));
        saveAsTemplateAction.setEnabled(true);

        expandAllAction = new AbstractAction(I18N.translate("ButtonExpandAll"), getImageIcon("ExpandAll.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                expandAll();
            }
        };
        expandAllAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
                InputEvent.ALT_DOWN_MASK));
        expandAllAction.setEnabled(false);

        collapseAllAction = new AbstractAction(I18N.translate("ButtonCollapseAll"), getImageIcon("CollapseAll.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                treeTable.collapseAll();
            }
        };
        collapseAllAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
                InputEvent.ALT_DOWN_MASK));
        collapseAllAction.setEnabled(false);

        removeMetadataElementAction = new AbstractAction(I18N.translate("ButtonRemoveMetadataElement"), getImageIcon(
                DELETE_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                removeMetadataElement();
            }
        };
        removeMetadataElementAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate(
                "ToolTipRemoveMetadataElement"));
        removeMetadataElementAction.setEnabled(false);

        insertMetadataElementAction = new AbstractAction(I18N.translate("ButtonInsertMetadataElement"), getImageIcon(
                "Insert.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                insertMetadataElement();
            }
        };
        insertMetadataElementAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate(
                "ToolTipInsertMetadataElement"));
        insertMetadataElementAction.setEnabled(false);

        redisplayNodeAction = new AbstractAction(I18N.translate("ButtonRedisplayNode"), getImageIcon(REDISPLAY_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                redisplayNode();
            }
        };
        redisplayNodeAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipRedisplayNode"));
        redisplayNodeAction.putValue(Action.ACCELERATOR_KEY, launcherView.getKeyStroke(KeyEvent.VK_F5, 0,
                KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        checkFixityAction = new CheckFixityAction(this);
        checkFixityAction.setEnabled(false);

        convertAction = new ConvertFilesAction(this);
        convertAction.putValue(LauncherView.ACTION_HIDE_KEY, "convertFilesAction");
        convertAction.setEnabled(false);

        exploreAction = new ExploreAction(this);
        exploreAction.putValue(LauncherView.ACTION_HIDE_KEY, "exploreAction");

        submitRequestAction = new AbstractAction(I18N.translate("ActionSubmitRequest"), getImageIcon(
                "SubmitRequest.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setSubmitRequestButtonClicked();
            }
        };
        submitRequestAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSubmitRequest"));

        submitRetractAction = new AbstractAction(I18N.translate("ActionSubmitRetract"), getImageIcon(
                "SubmitRetract.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setSubmitRetractButtonClicked();
            }
        };
        submitRetractAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSubmitRetract"));

        submitCheckAction = new AbstractAction(I18N.translate("ActionSubmitCheck"), getImageIcon("SubmitCheck.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                checkSubmission();
            }
        };
        submitCheckAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSubmitCheck"));

        submitAction = new AbstractAction(I18N.translate("ActionSubmit"), getImageIcon("Submit.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                submit();
            }
        };
        submitAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSubmit"));

        removeTrashAction = new AbstractAction(I18N.translate("ActionRemoveTrash"), getImageIcon("RemoveTrash.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                removeTrash();
            }
        };
        removeTrashAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipRemoveTrash"));
        removeTrashAction.setEnabled(false);

        removeDuplicatesAction = new AbstractAction(I18N.translate("ActionRemoveDuplicates"), getImageIcon(
                "RemoveDuplicates.png")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                removeDuplicates();
            }
        };
        removeDuplicatesAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipRemoveDuplicates"));
        removeDuplicatesAction.setEnabled(false);

        // Buttons:

        final JButton saveButton = new JButton(saveAction);
        saveButton.setName(SIP_SAVE_CURRENT_BUTTON);
        saveButton.setHideActionText(true);
        saveButton.setRequestFocusEnabled(false);

        final JButton insertFileOrFolderButton = new JButton(insertAction);
        insertFileOrFolderButton.setHideActionText(true);
        insertFileOrFolderButton.setName(SIP_VIEW_INSERT_FILE_OR_FOLDER_BUTTON);
        insertFileOrFolderButton.setRequestFocusEnabled(false);

        final JButton createFolderButton = new JButton(createFolderAction);
        createFolderButton.setHideActionText(true);
        createFolderButton.setRequestFocusEnabled(false);

        final JButton renameItemButton = new JButton(renameItemAction);
        renameItemButton.setHideActionText(true);
        renameItemButton.setRequestFocusEnabled(false);

        final JButton sortButton = new JButton(sortAction);
        sortButton.setName(SIP_SORT_BUTTON);
        sortButton.setHideActionText(true);
        sortButton.setRequestFocusEnabled(false);

        final JButton normalizeButton = new JButton(normalizeAction);
        normalizeButton.setName(SIP_NORMALIZE_BUTTON);
        normalizeButton.setHideActionText(true);
        normalizeButton.setRequestFocusEnabled(false);

        final JButton removeMetadataElementButton = new JButton(removeMetadataElementAction);
        removeMetadataElementButton.setHideActionText(true);
        removeMetadataElementButton.setRequestFocusEnabled(false);

        final JButton insertMetadataElementButton = new JButton(insertMetadataElementAction);
        insertMetadataElementButton.setHideActionText(true);
        insertMetadataElementButton.setRequestFocusEnabled(false);

        logoButton = new JButton(getImageIcon("Logo_docuteam_packer.png"));
        logoButton.setEnabled(true);
        logoButton.setHideActionText(true);
        logoButton.setRequestFocusEnabled(false);
        logoButton.setContentAreaFilled(false);
        logoButton.setBorderPainted(false);
        logoButton.setToolTipText(I18N.translate("ToolTipOpenDocuteamHomepage"));
        logoButton.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        openDocuteamHomepage();
                    }
                });

        // Text Fields:

        footerTextField = new JTextField();
        footerTextField.setEditable(false);
        footerTextField.setName(SIP_VIEW_FOOTER_TEXT_FIELD);

        // Labels:

        infoLabel = new JLabel();
        infoLabel.setName(SIP_VIEW_INFO_LABEL);

        fileDataPropertiesLabel = new JLabel(I18N.translate("LabelFileDataProperties"));
        fileDateEventsLabel = new JLabel(I18N.translate("LabelFileDataEvents"));
        fileDataEventsDetailsLabel = new JLabel(I18N.translate("LabelFileDataEventDetails"));

        // Complex components:

        final SearchPanel searchPanel = new SearchPanel(this);

        // Construct the View:

        // Menus:

        // Initialize the export submenu:
        mdExportSubMenu = new JMenu(I18N.translate("MenuMetadataExport"));
        mdExportSubMenu.setName(SIP_METADATA_EXPORT_MENU);
        mdExportSubMenu.setIcon(getImageIcon("Export.png"));
        mdExportSubMenu.setToolTipText(I18N.translate("ToolTipMetadataExport"));
        final JMenuItem eadExportMenuItem = new JMenuItem(exportAsEADFileAction);
        eadExportMenuItem.setName(SIP_EAD_EXPORT_MENU_ITEM);
        mdExportSubMenu.add(eadExportMenuItem);
        // add dynamic EAD exports
        final String[] xslt = new File(launcherView.getExportsDirectory()).list(new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".xsl");
            }
        });
        // If none, set the exortsSubMenu to disabled; otherwise to enabled:
        if (xslt != null && xslt.length > 0) {
            for (final String export : xslt) {
                final String exportName = export.substring(0, export.length() - 4);
                final AbstractAction action = new AbstractAction(exportName, getImageIcon("ExportAsEADFile.png")) {

                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        createExport(exportName);
                    }
                };
                mdExportSubMenu.add(new JMenuItem(action));
            }
        }
        mdExportSubMenu.add(new JMenuItem(exportAsCSVFileAction));

        sipExportSubMenu = new JMenu(I18N.translate("MenuSIPExport"));
        sipExportSubMenu.setIcon(getImageIcon("SIPExport.png"));
        sipExportSubMenu.setToolTipText(I18N.translate("ToolTipSIPExport"));
        sipExportSubMenu.add(new JMenuItem(exportAsSEDA21Action));
        sipExportSubMenu.add(new JMenuItem(exportAsSEDA1Action));

        // Initialize the (dynamic) reports submenu:
        reportsSubMenu = new JMenu(I18N.translate("MenuReports"));
        reportsSubMenu.setIcon(getImageIcon("Reports.png"));
        reportsSubMenu.setToolTipText(I18N.translate("ToolTipReports"));
        // Check all xsl-files in the "Reports" folder:
        final String[] reports = new File(launcherView.getReportsDirectory()).list(new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".xsl");
            }
        });
        // If none, set the reportsSubMenu to disabled; otherwise to enabled:
        if (reports == null || reports.length == 0) {
            reportsSubMenu.setEnabled(false);
        } else {
            for (final String report : reports) {
                final String reportName = report.substring(0, report.length() - 4);
                final AbstractAction action = new AbstractAction(I18N.translate(reportName), getImageIcon(
                        "Report.png")) {

                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        createReport(I18N.translate(reportName), reportName);
                    }
                };
                reportsSubMenu.add(new JMenuItem(action));
            }
        }

        fileMenu = new JMenu(I18N.translate("MenuFile"));
        fileMenu.setName(SIP_VIEW_FILE_MENU);
        fileMenu.setIcon(getImageIcon("MenuFile.png"));
        fileMenu.add(new JMenuItem(saveAction));

        final JMenuItem saveAsMenuItem = new JMenuItem(saveAsAction);
        saveAsMenuItem.setName(SIP_VIEW_SAVE_AS_MENU_ITEM);
        fileMenu.add(saveAsMenuItem);

        final JMenuItem saveAsTemplateMenuItem = new JMenuItem(saveAsTemplateAction);
        saveAsTemplateMenuItem.setName(SIP_VIEW_SAVE_AS_TEMPLATE_MENU_ITEM);
        fileMenu.add(saveAsTemplateMenuItem);

        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(closeAction));
        fileMenu.addSeparator();
        fileMenu.add(mdExportSubMenu);
        fileMenu.add(reportsSubMenu);
        fileMenu.add(sipExportSubMenu);
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(checkFixityAction));
        addMenuItem(fileMenu, exploreAction);

        fileMenu.addSeparator();
        final JMenuItem removeDuplicatesMenuItem = new JMenuItem(removeDuplicatesAction);
        removeDuplicatesMenuItem.setName(SIP_VIEW_REMOVE_DUPLICATES_MENU_ITEM);
        fileMenu.add(removeDuplicatesMenuItem);

        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(removeTrashAction));

        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(submitCheckAction));
        fileMenu.add(new JMenuItem(submitAction));
        if (launcherView.isInDevelopMode()) {
            fileMenu.addSeparator();
            fileMenu.add(new JMenuItem(systemOutDocumentAction));
        }

        searchMenu = new JMenu(I18N.translate("MenuSearch"));
        searchMenu.setIcon(getImageIcon("MenuSearch.png"));
        searchMenu.add(new JMenuItem(searchPanel.searchAction));
        searchMenu.add(new JMenuItem(searchPanel.clearSearchTextFieldAction));
        searchMenu.addSeparator();
        searchMenu.add(new JMenuItem(searchPanel.selectNextHitAction));
        searchMenu.add(new JMenuItem(searchPanel.selectPreviousHitAction));

        itemMenu = new JMenu(I18N.translate("MenuItem"));
        itemMenu.setName(SIP_VIEW_ELEMENT_MENU);
        itemMenu.setIcon(getImageIcon("MenuItem.png"));
        itemMenu.add(new JMenuItem(insertAction));

        final JMenuItem createFolderMenuItem = new JMenuItem(createFolderAction);
        createFolderMenuItem.setName(SIP_VIEW_CREATE_FOLDER_MENU_ITEM);
        itemMenu.add(createFolderMenuItem);

        itemMenu.add(new JMenuItem(renameItemAction));
        itemMenu.add(new JMenuItem(sortAction));
        itemMenu.add(new JMenuItem(normalizeAction));

        itemMenu.addSeparator();
        itemMenu.add(new JMenuItem(replaceFileAction));
        itemMenu.add(new JMenuItem(appendMigratedFileAction));
        addMenuItem(itemMenu, convertAction);
        itemMenu.addSeparator();

        addMenuItem(itemMenu, deleteItemAction);
        addMenuItem(itemMenu, deleteItemDontAskAction);
        addMenuItem(itemMenu, deleteFileContentAction);

        if (launcherView.isActionVisible(deleteItemAction) ||
                launcherView.isActionVisible(deleteItemDontAskAction) ||
                launcherView.isActionVisible(deleteFileContentAction)) {
            itemMenu.addSeparator();
        }

        itemMenu.add(new JMenuItem(openAssignLevelsByLayerViewAction));
        itemMenu.add(new JMenuItem(openAssignLevelsByLabelViewAction));
        itemMenu.addSeparator();
        itemMenu.add(new JMenuItem(submitRequestAction));
        itemMenu.add(new JMenuItem(submitRetractAction));
        itemMenu.addSeparator();
        itemMenu.add(new JMenuItem(exportAction));

        popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem(insertAction));
        popupMenu.add(new JMenuItem(createFolderAction));

        final JMenuItem renameMenuItem = new JMenuItem(renameItemAction);
        renameMenuItem.setName(SIP_VIEW_RENAME_FOLDER_MENU_ITEM);
        popupMenu.add(renameMenuItem);

        final JMenuItem sortItem = new JMenuItem(sortAction);
        sortItem.setName(SIP_VIEW_SORT_FOLDER_MENU_ITEM);
        popupMenu.add(sortItem);

        popupMenu.add(normalizeAction);
        popupMenu.addSeparator();

        final JMenuItem replaceFileMenuItem = new JMenuItem(replaceFileAction);
        replaceFileMenuItem.setName(SIP_REPLACE_FILE_MENU_ITEM);
        popupMenu.add(replaceFileMenuItem);

        popupMenu.add(new JMenuItem(appendMigratedFileAction));
        addMenuItem(popupMenu, convertAction, Optional.empty());
        popupMenu.addSeparator();

        addMenuItem(popupMenu, deleteItemAction, Optional.of(SIP_DELETE_ITEM_MENU_ITEM));
        addMenuItem(popupMenu, deleteItemDontAskAction, Optional.empty());
        addMenuItem(popupMenu, deleteFileContentAction, Optional.empty());

        if (launcherView.isActionVisible(deleteItemAction) ||
                launcherView.isActionVisible(deleteItemDontAskAction) ||
                launcherView.isActionVisible(deleteFileContentAction)) {
            popupMenu.addSeparator();
        }

        popupMenuStartOfLevelsSubMenu = popupMenu.getComponentCount();
        initializeLevelsSubMenu();

        saMenu = new JMenu(I18N.translate("MenuSA"));
        saMenu.setIcon(getImageIcon("MenuSA.png"));
        saMenu.add(new JMenuItem(openSAExternallyAction));
        saMenu.add(new JMenuItem(testOrAssignSAAction));

        viewMenu = new JMenu(I18N.translate("MenuView"));
        viewMenu.setName(SIP_VIEW_MENU);
        viewMenu.setIcon(getImageIcon("MenuView.png"));

        final JMenuItem expandMenuItem = new JMenuItem(expandAllAction);
        expandMenuItem.setName(SIP_VIEW_EXPAND_MENU_ITEM);
        viewMenu.add(expandMenuItem);

        final JMenuItem collapseMenuItem = new JMenuItem(collapseAllAction);
        collapseMenuItem.setName(SIP_VIEW_COLLAPSE_MENU_ITEM);
        viewMenu.add(collapseMenuItem);
        viewMenu.addSeparator();
        viewMenu.add(new JMenuItem(redisplayNodeAction));

        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(searchMenu);
        menuBar.add(itemMenu);
        menuBar.add(saMenu);
        menuBar.add(viewMenu);
        menuBar.setVisible(true);
        setJMenuBar(menuBar);

        // ---------- Header Panel:

        final JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        final Box buttonsPanel = new Box(BoxLayout.X_AXIS);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(Box.createHorizontalStrut(20));
        buttonsPanel.add(insertFileOrFolderButton);
        buttonsPanel.add(createFolderButton);
        buttonsPanel.add(renameItemButton);
        buttonsPanel.add(sortButton);
        buttonsPanel.add(normalizeButton);
        addButtonForAction(buttonsPanel, deleteItemAction);

        buttonsPanel.add(Box.createHorizontalStrut(20));
        buttonsPanel.add(infoLabel);
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(logoButton);

        headerPanel.add(buttonsPanel, BorderLayout.CENTER);

        // ---------- Main View left: the Tree view:

        final JPanel mainLeftPanel = new JPanel(new BorderLayout());
        mainLeftPanel.add(searchPanel, BorderLayout.NORTH);
        mainLeftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        mainLeftPanel.add(new JScrollPane(treeTable), BorderLayout.CENTER);

        // ---------- Main View right: Tabbed Pane:

        // ---------- DataTab: File Data, Event List, Event Details:

        final JPanel dataNorthPanel = new JPanel();
        // Top to bottom layout
        dataNorthPanel.setLayout(new BoxLayout(dataNorthPanel, BoxLayout.Y_AXIS));
        dataNorthPanel.add(fileDataPropertiesLabel);
        dataNorthPanel.add(Box.createVerticalStrut(10));
        dataNorthPanel.add(dataTable);
        dataNorthPanel.add(Box.createVerticalStrut(10));
        dataNorthPanel.add(fileDateEventsLabel);
        dataNorthPanel.add(Box.createVerticalStrut(10));

        final JPanel dataSouthPanel = new JPanel();
        // Top to bottom layout
        dataSouthPanel.setLayout(new BoxLayout(dataSouthPanel, BoxLayout.Y_AXIS));
        dataSouthPanel.add(Box.createVerticalStrut(10));
        dataSouthPanel.add(fileDataEventsDetailsLabel);
        dataSouthPanel.add(Box.createVerticalStrut(10));
        dataSouthPanel.add(eventDetailTable);

        final JPanel dataView = new JPanel(new BorderLayout());
        dataView.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dataView.add(dataNorthPanel, BorderLayout.NORTH);
        dataView.add(new JScrollPane(eventTable), BorderLayout.CENTER);
        dataView.add(dataSouthPanel, BorderLayout.SOUTH);

        // ---------- MetadataTab:

        final GridBagPanel metadataNorthPanel = new GridBagPanel(new EmptyBorder(10, 0, 10, 0), new Insets(0, 5, 0,
                0));
        metadataNorthPanel.add(new JLabel(I18N.translate("LabelTitle")), 1, 0, GridBagConstraints.WEST);
        metadataNorthPanel.add(metaTitleTextField, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1,
                0);
        metadataNorthPanel.add(new JLabel(I18N.translate("LabelLevel")), 2, 0, GridBagConstraints.WEST);
        metadataNorthPanel.add(metaLevelTextField, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1,
                0);

        final GridBagPanel metadataSouthPanel = new GridBagPanel(new EmptyBorder(0, 0, 0, 0), new Insets(5, 0, 0, 0));
        metadataSouthPanel.add(new JLabel(I18N.translate("LabelSelectMetadataElement")), 1, 1, 0, 3,
                GridBagConstraints.SOUTHWEST);
        metadataSouthPanel.add(selectMetadataElementComboBox, 2, 0, GridBagConstraints.SOUTHWEST);
        metadataSouthPanel.add(insertMetadataElementButton, 2, 1, GridBagConstraints.SOUTHWEST);
        metadataSouthPanel.add(new JLabel(), 2, 2, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, 1, 0);
        metadataSouthPanel.add(removeMetadataElementButton, 2, 3, GridBagConstraints.SOUTHEAST);

        final JPanel metadataView = new JPanel(new BorderLayout());
        metadataView.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        metadataView.add(metadataNorthPanel, BorderLayout.NORTH);
        metadataTableScrollPane = new JScrollPane(metadataTable);
        metadataTableScrollPane.setWheelScrollingEnabled(true);
        metadataTableScrollPane.setAutoscrolls(true);
        metadataView.add(metadataTableScrollPane, BorderLayout.CENTER);
        metadataView.add(metadataSouthPanel, BorderLayout.SOUTH);

        // ---------- PreviewTab: File Preview:

        previewPanel = new FilePreviewer();

        // Combine the three tabs into a tabbedPane:

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                previewSelectedItemIfNecessary();
            }
        });
        tabbedPane.addTab(I18N.translate("TitleFileData"), null, dataView);
        tabbedPane.addTab(I18N.translate("TitleMetadata"), null, metadataView);
        tabbedPane.addTab(I18N.translate("TitleFilePreview"), null, previewPanel);

        // Put the tabbedPane into the main right panel:

        final JPanel mainRightPanel = new JPanel(new BorderLayout());
        mainRightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10)); // This looks better under Windows
                                                                                  // but worse under Mac OS
        mainRightPanel.add(tabbedPane);

        // Combine main left and main right panels in the main center view:

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, mainLeftPanel, mainRightPanel);

        // ---------- Footer:

        final JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        footerPanel.add(footerTextField);

        // ---------- Now arrange all in the main panel:

        add(headerPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        setName(SIP_VIEW_FRAME);
    }

    private void addButtonForAction(final Box buttonsPanel, final Action action) {
        if (launcherView.isActionVisible(action)) {
            final JButton button = new JButton(action);
            button.setHideActionText(true);
            button.setRequestFocusEnabled(false);
            buttonsPanel.add(button);
        }
    }

    /**
     * Adds a menu item only of this action is visible.
     * 
     * @param itemMenu
     * @param action
     */
    private void addMenuItem(final JMenu itemMenu, final Action action) {
        if (launcherView.isActionVisible(action)) {
            itemMenu.add(new JMenuItem(action));
        }
    }

    /**
     * @param itemMenu
     * @param action
     * @param Optional name used for GUI tests.
     */
    private void addMenuItem(final JPopupMenu itemMenu, final Action action, final Optional<String> name) {
        if (launcherView.isActionVisible(action)) {
            final JMenuItem jMenuItem = new JMenuItem(action);
            if (name.isPresent()) {
                jMenuItem.setName(name.get());
            }
            itemMenu.add(jMenuItem);
        }
    }

    public LauncherView getLauncherView() {
        return launcherView;
    }

    public JXTreeTable getTreeTable() {
        return treeTable;
    }

    public NodeAbstract getSelectedNode() {
        return selectedNode;
    }

    /**
     * Open a new SIPView with the SIP passed. If admIdToSelect is not null, select the node with this admId.
     * 
     * @param zipOrMETSFilePath
     * @param mode
     * @return
     */
    public static SIPView open(final LauncherView launcherView, final FileProperty fileProperty, final Mode mode,
            final String admIdToSelect) {
        final SIPView sipView = new SIPView(launcherView);
        sipView.fileProperty = fileProperty;

        // Display the window in the desired size:
        if (openFullScreen) {
            sipView.setLocation(0, 0);
            sipView.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        } else {
            sipView.setPreferredSize(new Dimension(screenSizeX, screenSizeY));
            sipView.pack();

            if (screenPosX != null && screenPosY != null) {
                sipView.setLocation(screenPosX, screenPosY);
            } else {
                sipView.setLocationRelativeTo(null); // Centered on screen
            }
        }

        sipView.setDividerLocation(sipView.getWidth() / 2);
        sipView.treeTable.requestFocusInWindow();

        sipView.setVisible(true);

        // Fill the view with contents:
        if (fileProperty.getFile().getName().toLowerCase().endsWith(".zip")) {
            sipView.read(fileProperty.getFile().getAbsolutePath(), mode, admIdToSelect);
        } else {
            sipView.read(fileProperty.getFile().getAbsolutePath() + "/" + Document.DEFAULT_METS_FILE_NAME, mode,
                    admIdToSelect);
        }

        return sipView;
    }

    /**
     * This method is called only once on packer startup. Assume that PropertyFile was already initialized with the
     * correct file path.
     * 
     * @param propertyFileName
     */
    public static void initialize(Properties propertiesIn) {
        properties = propertiesIn;

        saveWithBackups = Boolean.parseBoolean(properties.getProperty("docuteamPacker.versioning", "true"));
        Logger.info("    saveWithBackups: " + saveWithBackups);
        try {
            Document.setKeepBackupsCount(
                    new Integer(properties.getProperty("docuteamPacker.versioning.keepBackupsCount")));
        } catch (final NumberFormatException e) {
            Document.setKeepBackupsCount(null);
        }
        Logger.info("    keepBackupsCount: " + Document.getKeepBackupsCount());

        openFullScreen = Boolean
                .parseBoolean(properties.getProperty("docuteamPacker.openFullScreen", openFullScreen.toString()));
        try {
            screenSizeX = new Integer(properties.getProperty("docuteamPacker.screenSize.x", screenSizeX.toString()));
        } catch (final NumberFormatException e) {
        } // Ignore it and leave the default values
        try {
            screenSizeY = new Integer(properties.getProperty("docuteamPacker.screenSize.y", screenSizeY.toString()));
        } catch (final NumberFormatException e) {
        }
        try {
            screenPosX = new Integer(properties.getProperty("docuteamPacker.screenPos.x"));
        } catch (final NumberFormatException e) {
        }
        try {
            screenPosY = new Integer(properties.getProperty("docuteamPacker.screenPos.y"));
        } catch (final NumberFormatException e) {
        }
        Logger.info("    openFullScreen: " + openFullScreen);
        Logger.info("    ScreenSizeX: " + screenSizeX);
        Logger.info("    ScreenSizeY: " + screenSizeY);
        Logger.info("    ScreenPosX: " + screenPosX);
        Logger.info("    ScreenPosY: " + screenPosY);
    }

    public Document getDocument() {
        return document;
    }

    public FileProperty getFileProperty() {
        return fileProperty;
    }

    /**
     * Initialize the (dynamic) levels submenu. This is done on startup and every time after a SIP was read.
     */
    protected void initializeLevelsSubMenu() {
        if (itemLevelsSubMenu != null) {
            itemMenu.remove(itemLevelsSubMenu);
        }

        // Remove all menu items from the popup menu after the "static" part of the popup menu (= all "level" items):
        for (int i = popupMenu.getComponentCount(); i > popupMenuStartOfLevelsSubMenu; i--) {
            popupMenu.remove(i - 1);
        }

        setLevelActions = new Vector<Action>(10);
        itemLevelsSubMenu = new JMenu(I18N.translate("MenuLevels"));
        itemLevelsSubMenu.setIcon(getImageIcon("AssignLevel.png"));
        if (document != null) {
            for (final LevelOfDescription level : document.getLevels().getAll()) {
                final ImageIcon icon = level.getIcon() != null ? level.getIcon() : ICON_LEVEL_UNKNOWN;
                final AbstractAction action = new AbstractAction(level.getName(), icon) {

                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        setLevelButtonClicked(e);
                    }
                };

                setLevelActions.add(action);
                itemLevelsSubMenu.add(new JMenuItem(action));
                popupMenu.add(new JMenuItem(action));
            }
        }

        itemMenu.add(itemLevelsSubMenu, popupMenuStartOfLevelsSubMenu);

        popupMenu.addSeparator();
        popupMenu.add(new JMenuItem(submitRequestAction));
        popupMenu.add(new JMenuItem(submitRetractAction));
    }

    public TreeTableModel getTreeTableModel() {
        return (TreeTableModel) treeTable.getTreeTableModel();
    }

    /**
     * Read the SIP. If admIdToSelect is not null, select the node with this admId.
     * 
     * @param zipOrMETSFilePath
     * @param mode
     * @param admIdToSelect
     */
    public void read(final String zipOrMETSFilePath, final Mode mode, final String admIdToSelect) {
        new SwingWorker<Integer, Object>() {

            @Override
            public Integer doInBackground() {
                final String canonicalZIPOrMETSFileName = FileUtil.asCanonicalFileName(zipOrMETSFilePath);

                setFooterText(I18N.translate("MessageFooterOpeningFile") + canonicalZIPOrMETSFileName + "...");
                final SmallPeskyMessageWindow waitWindow = SmallPeskyMessageWindow.openBlocking(SIPView.this,
                        I18N.translate("MessageTempReadingSIP"));

                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    ExceptionCollector.clear();
                    switch (mode) {
                        case ReadWrite: {
                            document = Document.openReadWrite(canonicalZIPOrMETSFileName, OPERATOR, waitWindow);
                            break;
                        }
                        case ReadWriteNoFileOps: {
                            document = Document.openReadWriteFilesLocked(canonicalZIPOrMETSFileName, OPERATOR,
                                    waitWindow);
                            break;
                        }
                        case ReadOnly: {
                            document = Document.openReadOnly(canonicalZIPOrMETSFileName, OPERATOR, waitWindow);
                            break;
                        }
                        case Locked:
                            break;
                        case Undefined:
                            break;
                        default:
                            break;
                    }

                    setFooterText(canonicalZIPOrMETSFileName);
                    // Put the ORIGINAL metsFileName into the title, NOT the
                    // working copy fileName!
                    setTitle(canonicalZIPOrMETSFileName);

                    if (!ExceptionCollector.isEmpty()) {
                        // Close the waitWindow here otherwise it interferes
                        // with the MessageDialog:
                        waitWindow.close();
                        new ScrollableMessageDialog(SIPView.this, I18N.translate("TitleWarningsOccurred"),
                                ExceptionCollector.getAllTranslatedMessages(EXCEPTION_COLLECTOR_NO_EXCEPTION,
                                        EXCEPTION_COLLECTOR_PREFIX), getImageIcon(PACKER_PNG));
                    }
                } catch (final ZIPDoesNotContainMETSFileException e) {
                    // Don't printStackTrace!
                    document = null;

                    setFooterText(I18N.translate("MessageFooterCantCreateFile") + canonicalZIPOrMETSFileName);
                    setTitle(DefaultFrameTitle);

                    // Close the waitWindow here otherwise it interferes with
                    // the MessageDialog:
                    waitWindow.close();
                    JOptionPane.showMessageDialog(SIPView.this, e.toString(), I18N.translate("TitleCantReadSIP"),
                            JOptionPane.ERROR_MESSAGE);
                } catch (final java.lang.Exception e) {
                    Logger.error(e.getMessage(), e);
                    document = null;

                    setFooterText(I18N.translate("MessageFooterCantCreateFile") + canonicalZIPOrMETSFileName);
                    setTitle(DefaultFrameTitle);

                    // Close the waitWindow here otherwise it interferes with
                    // the MessageDialog:
                    waitWindow.close();
                    JOptionPane.showMessageDialog(SIPView.this, e.toString(), I18N.translate("TitleCantReadSIP"),
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    // At this point, document may or may not be null:
                    if (document == null) {
                        closeButtonClicked();
                    } else {
                        populateView(document);
                    }

                    waitWindow.close(); // In case it was not closed yet...
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                    toFront();
                    requestFocus();

                    // ETH-161
                    try {
                        if (Boolean.valueOf(properties.getProperty("docuteamPacker.SIPView.TreeExpandAll", "false"))
                                .booleanValue()) {
                            expandAll();
                        }
                    } catch (final NumberFormatException nfe) {
                    } // Ignore it and leave the default values
                      // ETH-161

                    selectNode(admIdToSelect);

                    // ETH-161
                    int selectedTab = 0;
                    try {
                        selectedTab = Integer.valueOf(properties.getProperty("docuteamPacker.SIPView.DefaultTab",
                                "1"))
                                .intValue() - 1;
                        selectedTab = selectedTab >= 0 && selectedTab <= 2 ? selectedTab : 0;
                    } catch (final NumberFormatException nfe) {
                        // Ignore it and leave the default values
                    }
                    tabbedPane.setSelectedIndex(selectedTab);
                    // ETH-161
                }

                return 0;
            }
        }.execute();
    }

    protected void saveCopyAs(final String filePath, final boolean doUnlockAndCleanupAfterwards) {
        if (document == null) {
            return;
        }

        if (!document.canWrite()) {
            JOptionPane.showMessageDialog(SIPView.this, I18N.translate("MessageIsReadOnlyCantSave"),
                    I18N.translate("TitleCantSaveSIP"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        new SwingWorker<Integer, Object>() {

            @Override
            public Integer doInBackground() {
                final SmallPeskyMessageWindow waitWindow = SmallPeskyMessageWindow.openBlocking(SIPView.this,
                        I18N.translate("MessageTempSavingSIP"));
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    // This prevents the LauncherView to open this document
                    // while being saved:
                    launcherView.savingDocumentInProgress(SIPView.this);

                    saveSIP(filePath, doUnlockAndCleanupAfterwards);

                    setFooterText(I18N.translate("MessageFooterSaved") + document.getFilePath());
                    // Put the ORIGINALem metsFileName into the title, NOT the
                    // working copy fileName!
                    setTitle(document.getOriginalSIPFolder());
                } catch (final FileOrFolderIsInUseException e) {
                    waitWindow.close();
                    JOptionPane.showMessageDialog(SIPView.this,
                            I18N.translate("MessageFileOrFolderIsInUseException", e.getOriginalSIPName(),
                                    e.getSecurityCopySIPName(), FileUtil.asFileName(e.getOriginalSIPName()),
                                    FileUtil.asFileName(e.getSecurityCopySIPName())),
                            I18N.translate("TitleCantSaveSIP"), JOptionPane.ERROR_MESSAGE);
                } catch (final OriginalSIPIsMissingException e) {
                    waitWindow.close();
                    JOptionPane.showMessageDialog(SIPView.this,
                            I18N.translate("MessageOriginalSIPIsMissingException", e.getOriginalSIPFolderPath()),
                            I18N.translate("TitleCantCopySIP"), JOptionPane.WARNING_MESSAGE);
                } catch (final Exception e) {
                    Logger.error(e.getMessage(), e);

                    waitWindow.close();
                    JOptionPane.showMessageDialog(SIPView.this, e.toString(), I18N.translate("TitleCantSaveSIP"),
                            JOptionPane.ERROR_MESSAGE);
                    setFooterText(I18N.translate("MessageFooterCantSave") + document.getFilePath());
                } finally {
                    launcherView.savingDocumentFinished(SIPView.this);

                    waitWindow.close();
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                    enableOrDisableActions();

                    toFront();
                    treeTable.requestFocus();
                }

                return 0;
            }
        }.execute();
    }

    private void saveSIP(final String filePath, final boolean doUnlockAndCleanupAfterwards) throws IOException,
            FileUtilExceptionListException, DocumentIsReadOnlyException, FileOrFolderIsInUseException,
            OriginalSIPIsMissingException {

        if (filePath == null || filePath.isEmpty()) {
            // This is the regular save:
            Logger.debug("Saving document");

            // Save the SIP:
            if (saveWithBackups) {
                document.saveWithBackup();
            } else {
                document.saveWithoutBackup();
            }
        } else {
            // This is save-as:
            Logger.debug("Saving document as: " + filePath);

            // Save SIP or ZIP to a different location:
            document.saveTo(filePath);
        }

        // I have to reread the SIPTable in the launcher view
        // because I might have locked or unlocked a SIP in
        // the workspace when saving the SIP to a different place,
        // or I might have added a new SIP:
        launcherView.getSipTable().rereadSIPTable();

        // Unlock and cleanup document in necessary:
        if (doUnlockAndCleanupAfterwards) {
            try {
                document.unlockIfNecessary();
                document.cleanupWorkingCopy();
            } catch (final Exception e) {
                Logger.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(SIPView.this, e.toString(),
                        "Unable to cleanup working folder due to errors",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected void previewSelectedItemIfNecessary() {
        // If the preview tab is not visible AND the separate preview window is
        // not open, do nothing:
        if (!(tabbedPane.getSelectedIndex() == 2 || previewPanel.isPreviewInSeparateWindow())) {
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            previewPanel.setNode(selectedNode);
        } finally {
            previewPanel.validate();
            previewPanel.repaint();

            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    protected void importFilesAndFolders(final List<File> files) {
        // Import all files and/or folders recursively:
        new SwingWorker<Integer, Object>() {

            @Override
            public Integer doInBackground() {
                final SmallPeskyMessageWindow waitWindow = SmallPeskyMessageWindow.openBlocking(SIPView.this, I18N
                        .translate("MessageTempInsertingFolder"));

                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    NodeAbstract lastInsertedNode = null;
                    try {
                        ExceptionCollector.clear();

                        for (final File f : files) {
                            Logger.debug("Inserting: " + f);

                            final String filePath = f.getPath();
                            lastInsertedNode = ((NodeFolder) selectedNode).insertFileOrFolder(filePath);
                        }

                        Util.showAllFromExceptionCollector(waitWindow, SIPView.this);
                    } catch (final java.lang.Exception e) {
                        if (launcherView.isInDevelopMode()) {
                            Logger.error(e.getMessage(), e);
                        }

                        waitWindow.close(); // In case it was not closed yet...
                        JOptionPane.showMessageDialog(SIPView.this, e.toString(), I18N.translate(
                                "TitleCantInsertFileOrFolder"), JOptionPane.ERROR_MESSAGE);
                    }

                    getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedIndex));
                    selectNode(lastInsertedNode);

                    toFront();
                    requestFocus();

                    if (launcherView.isInDevelopMode()) {
                        traceTree();
                    }
                } finally {
                    waitWindow.close(); // In case it was not closed yet...
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

                return 0;
            }
        }.execute();
    }

    public void populateView(final Document documentOrNull) {
        initializeLevelsSubMenu();

        treeTable.setTreeTableModel(new TreeTableModel(documentOrNull));
        treeTable.getSelectionModel().addSelectionInterval(0, 0);
    }

    public void setFooterText(final String text) {
        setFooterText(text, false);
    }

    protected void setFooterText(final String text, final boolean isEmphasized) {
        footerTextField.setForeground(isEmphasized ? Color.RED : Color.BLACK);
        footerTextField.setText(text);
    }

    protected void collapseCurrentNode() {
        treeTable.collapseRow(treeTable.getSelectedRow());
    }

    protected void expandCurrentNode() {
        treeTable.expandRow(treeTable.getSelectedRow());
    }

    /**
     * Listen to row selections in the tree view:
     * 
     * @param e: The event transporting all necessary info
     */
    protected void treeViewSelectionChanged(final ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        // When changing the tree selection, commit (or cancel if commit fails)
        // any open cell editor in the metadataTable:
        metadataTable.commitOrCancelCurrentCellEditor();

        selectedIndex = treeTable.getSelectedRow();
        selectedNode = null;

        // Nothing selected in the tree view:
        if (treeTable.getSelectedRowCount() == 0) {
            Logger.debug("No Selection: " + selectedIndex);

            setFooterText("");
            updateView();
            previewSelectedItemIfNecessary();
            return;
        }

        // Multiple selection in the tree view:
        if (treeTable.getSelectedRowCount() >= 2) {
            // NOTE: On multiple selection, selectedNode is NULL!!! That is to
            // display nothing in the tabs.
            final StringBuilder footerText = new StringBuilder();
            for (final int i : treeTable.getSelectedRows()) {
                footerText.append(", ")
                        .append(((NodeAbstract) treeTable.getPathForRow(i).getLastPathComponent()).getLabel());
            }
            setFooterText(footerText.substring(2)); // Cut off the leading ", "

            Logger.debug("Multiple selection: " + selectedIndex + ": " + footerTextField.getText());

            clearView();
            previewSelectedItemIfNecessary();
            return;
        }

        // From here on: exactly one item is selected:

        final TreePath pathForSelectedRow = treeTable.getPathForRow(selectedIndex);
        // pathForSelectedRow is null when the tree part where the selection was
        // in, gets collapsed.
        if (pathForSelectedRow == null) {
            Logger.debug("PathForSelectedRow is null: " + selectedIndex);

            setFooterText("");
            clearView();
            previewSelectedItemIfNecessary();
            return;
        }

        selectedNode = (NodeAbstract) pathForSelectedRow.getLastPathComponent();

        Logger.debug("Selected Node: " + selectedIndex + ": " + selectedNode);

        setFooterText(selectedNode.getPathString() + (selectedNode.isFolder() ? "/" : ""));

        updateView();
        fillInsertMetadataElementComboBox();
        previewSelectedItemIfNecessary();

        // Select the oldest event:
        final int eventsSize = selectedNode.getMyEvents().size();
        if (eventsSize > 0) {
            eventTable.getSelectionModel().setSelectionInterval(eventsSize - 1, eventsSize - 1);
        }
    }

    /**
     * Special mouse clicks (like double-click or right-click) are handled here.
     * 
     * @param e
     */
    protected void treeViewSelectionWasClicked(final MouseEvent e) {
        if (e.getClickCount() == 2) {
            // Double-Click:

            // Open external preview:
            previewPanel.openFileExternallyButtonClicked(selectedNode);
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            // Right-Click:

            // Find out which row was right-clicked:
            final int rowNumber = treeTable.rowAtPoint(e.getPoint());

            // Check if the row which was right-clicked is already selected
            boolean isSelected = false;
            for (final int i : treeTable.getSelectedRows()) {
                if (i == rowNumber) {
                    isSelected = true;
                }
            }

            // Select this row if it is not part of the current selection:
            if (!isSelected) {
                treeTable.getSelectionModel().setSelectionInterval(rowNumber, rowNumber);
            }

            // Show popup menu:
            popupMenu.show(treeTable, e.getX(), e.getY());
        }
        // else: ignore. Single clicks are handled in treeViewSelectionChanged().
    }

    /**
     * Listen to row selections in the event table:
     * 
     * @param e: The event transporting all necessary infos
     */
    protected void eventTableSelectionChanged(final ListSelectionEvent e) {
        final Integer selectedEventIndex = ((ListSelectionModel) e.getSource()).getLeadSelectionIndex();
        final NodeAbstract listTableModel = ((EventListTableModel) eventTable.getModel()).getFileStructureNode();

        if (listTableModel == null || selectedEventIndex < 0 || selectedEventIndex >= listTableModel.getMyEvents()
                .size()) {
            ((EventDetailTableModel) eventDetailTable.getModel()).setEvent(null);
        } else {
            final Event selectedEvent = listTableModel.getMyEvent(selectedEventIndex);
            ((EventDetailTableModel) eventDetailTable.getModel()).setEvent(selectedEvent);
        }
    }

    protected void metadataTableSelectionChanged(final ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        enableOrDisableActions();
        scrollToSelectedRow();
    }

    private void scrollToSelectedRow() {
        try {
            final int row = metadataTable.getSelectedRow();
            final Rectangle rect = metadataTable.getCellRect(row, 0, true);
            final Point pt = metadataTableScrollPane.getViewport().getViewPosition();
            rect.translate(-pt.x, -pt.y);
            metadataTableScrollPane.getViewport().scrollRectToVisible(rect);
        } catch (final Exception e) {
            // if the scrolling fails, just ignore
        }
    }

    /**
     * The [Close...] button was clicked. This method gets called too when the window is closing, when reading a SIP
     * didn't succeed, and when a quit is requested from somebody outside. Returning false means that saving was
     * cancelled.
     */
    public boolean closeButtonClicked() {
        // If this view is disabled, reject the attempt to close it:
        if (!isEnabled()) {
            JOptionPane.showMessageDialog(this, I18N.translate("MessageCantCloseSIP"), I18N.translate(
                    "TitleCloseSIP"),
                    JOptionPane.YES_OPTION);
            return false;
        }

        if (document != null) {
            // This is because the save() method DOES already unlock and
            // cleanup:
            boolean doUnlockAndCleanupAfterwards = true;

            flushCurentEditing();

            if (document.isModified()) {
                // Ask whether to save the loaded document:
                final int answer = JOptionPane.showConfirmDialog(this,
                        "'" + document.getSIPName() + "':\n" + I18N.translate("QuestionSaveModified"),
                        I18N.translate("TitleCloseSIP"), JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                switch (answer) {
                    case JOptionPane.YES_OPTION: {
                        // Save current document, unlock and cleanup after saving,
                        // then continue closing:
                        doUnlockAndCleanupAfterwards = false;
                        saveCopyAs(null, true);
                        break;
                    }
                    case JOptionPane.NO_OPTION: {
                        // Don't save current document but continue closing:
                        break;
                    }
                    case JOptionPane.CANCEL_OPTION: {
                        // Cancel, don't continue:
                        return false;
                    }
                    default: {
                        // Cancel, don't continue:
                        return false;
                    }
                }
            }

            if (doUnlockAndCleanupAfterwards) {
                try {
                    document.unlockIfNecessary();
                    document.cleanupWorkingCopy();
                } catch (final Exception e) {
                    Logger.error(e.getMessage(), e);
                    JOptionPane.showMessageDialog(SIPView.this, e.toString(),
                            "Unable to cleanup working folder due to errors", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        launcherView.unregister(this);

        setVisible(false);
        dispose();

        return true;
    }

    /*
     * Persist data that is currently being edited
     */
    protected void flushCurentEditing() {
        if (metaTitleTextField.isFocusOwner()) {
            metaTitleTextFieldWasChanged();
        }
        metadataTable.commitOrCancelCurrentCellEditor();
    }

    /**
     * The [Save] button was clicked:
     */
    protected void saveButtonClicked(final ActionEvent actionEvent) {
        flushCurentEditing();

        // If the shift-key is held while clicking the save-button: ask for a file name to save a copy to:
        if ((actionEvent.getModifiers() & ActionEvent.SHIFT_MASK) != 0) {
            saveAsButtonClicked();
        } else {
            saveCopyAs(null, false);
        }
    }

    /**
     * The [Save as...] button was clicked:
     */
    protected void saveAsButtonClicked() {
        flushCurentEditing();

        final JFileChooser fileChooser = new JFileChooser(launcherView.getLastUsedOpenOrSaveDirectory());
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle(I18N.translate("TitleSaveSIPAs"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(false);

        final File suggestedFile = getFileProperty().getFile();
        File selectedFile;
        while (true) {
            fileChooser.setSelectedFile(suggestedFile);
            final int answer = fileChooser.showSaveDialog(this);
            if (JFileChooser.CANCEL_OPTION == answer) {
                return;
            }
            selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.exists()) {
                break;
            }
            JOptionPane.showMessageDialog(this, I18N.translate("MessageNameAlreadyExists", selectedFile.getName(),
                    selectedFile.getParentFile()),
                    I18N.translate("TitleCantCopySIP"), JOptionPane.WARNING_MESSAGE);
        }
        saveCopyAs(selectedFile.getPath(), false);
    }

    /**
     * The [Insert] button was clicked:
     */
    protected void insertFileOrFolderButtonClicked() {
        final JFileChooser fileChooser = new JFileChooser(launcherView.getDataDirectory());
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return; // Cancel-button clicked
        }

        // Remember the insert directory:
        launcherView.setDataDirectory(FileUtil.asCanonicalFileName(fileChooser.getSelectedFile().getParent()));

        // The term getSelectedFile().getParent() CAN return an empty String under Windows (e.g. when "C:/" is
        // selected). Hence:
        if (launcherView.getDataDirectory().isEmpty()) {
            launcherView.setDataDirectory(FileUtil.asCanonicalFileName(ch.docuteam.tools.os.OperatingSystem
                    .userHome()));
        }

        importFilesAndFolders(Arrays.asList(fileChooser.getSelectedFiles()));
    }

    /**
     * The [Create Folder] button was clicked:
     */
    protected void createFolderButtonClicked() {
        String title = I18N.translate("TitleNewFolder");
        String message = I18N.translate("MessageEnterNewFolderName");
        String textFieldContent = "";
        int messageType = JOptionPane.QUESTION_MESSAGE;

        NodeFolder newFolder = null;
        String newFolderName;
        Boolean ok;
        do {
            newFolderName = (String) new ElementNamingDialog().showInputDialog(this, message, title, messageType,
                    null, null, textFieldContent, 540, 150);
            if (newFolderName == null) {
                return;
            }
            newFolderName = newFolderName.trim();
            if (newFolderName.length() == 0) {
                return;
            }

            ok = false;

            if (!FileUtil.isFileNameAllowed(newFolderName)) {
                title = I18N.translate("TitleCantCreateNewFolder");
                message = I18N.translate("MessageBadLettersInFilename") + "\n" + I18N.translate(
                        "MessageEnterNewFolderName");
                messageType = JOptionPane.WARNING_MESSAGE;
                textFieldContent = newFolderName;
                continue;
            }

            try {
                newFolder = ((NodeFolder) selectedNode).createNewFolder(newFolderName);
            } catch (final FileAlreadyExistsException ex) {
                title = I18N.translate("TitleCantCreateNewFolder");
                message = I18N.translate("MessageNameAlreadyExists", newFolderName, selectedNode.getLabel()) + "\n" +
                        I18N.translate("MessageEnterNewFolderName");
                messageType = JOptionPane.WARNING_MESSAGE;
                textFieldContent = newFolderName;
                continue;
            } catch (final java.lang.Exception ex) {
                title = I18N.translate("TitleCantCreateNewFolder");
                message = ex.getMessage() + "\n" + I18N.translate("MessageEnterNewFolderName");
                messageType = JOptionPane.WARNING_MESSAGE;
                textFieldContent = newFolderName;
                continue;
            }

            ok = true;
        } while (!ok);

        getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedIndex));

        selectNode(newFolder);

        if (launcherView.isInDevelopMode()) {
            traceTree();
        }
    }

    protected void normalizeButtonClicked() {
        final int answer = JOptionPane.showConfirmDialog(this, I18N.translate("QuestionNormalizeAll"),
                I18N.translate("TitleQuestionNormalizeAll"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            // This SwingWorker is needed in case this is a long running task
            new SwingWorker<Map<List<NodeAbstract>, List<NodeAbstract>>, Object>() {

                Map<List<NodeAbstract>, List<NodeAbstract>> resultMap = new HashMap<>();

                @Override
                public Map<List<NodeAbstract>, List<NodeAbstract>> doInBackground() {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    final int selectedRowCount = treeTable.getSelectedRowCount();

                    final FileNameNormalizer fileNameNormalizer = new FileNameNormalizer(launcherView
                            .getConfigDirectory());
                    if (selectedRowCount == 1 && selectedNode != null) {
                        final int selRow = treeTable.getSelectedRow();

                        resultMap = fileNameNormalizer.normalizeFilesAndFolderNamesAndRename(selectedNode);
                        getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selRow).getParentPath());
                    } else if (selectedRowCount > 1) {
                        // several selections
                        final int[] selectedRows = treeTable.getSelectedRows();
                        final List<NodeAbstract> selectedNodes = new ArrayList<>();
                        for (final int selectedRow : selectedRows) {
                            final NodeAbstract node = (NodeAbstract) treeTable.getPathForRow(selectedRow)
                                    .getLastPathComponent();
                            selectedNodes.add(node);
                        }
                        resultMap = fileNameNormalizer.normalizeFilesAndFolderNamesAndRename(selectedNodes);
                        // GUI update
                        for (final int selectedRow : selectedRows) {
                            getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedRow)
                                    .getParentPath());
                        }
                    }
                    return resultMap;
                }

                @Override
                protected void done() {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                    final List<NodeAbstract> renamedNodes = new ArrayList<>();
                    final List<NodeAbstract> notNormalizedNodes = new ArrayList<>();
                    final Iterator<List<NodeAbstract>> keyIterator = resultMap.keySet().iterator();
                    while (keyIterator.hasNext()) {
                        final List<NodeAbstract> nextKey = keyIterator.next();
                        renamedNodes.addAll(nextKey);
                        notNormalizedNodes.addAll(resultMap.get(nextKey));
                    }

                    final List<String> notNormalizedLabels = new ArrayList<String>();
                    final String[] bigGUIList = new String[30]; // needed to show the list in GUI

                    for (final NodeAbstract node : notNormalizedNodes) {
                        final String label = node.getPathString() + " => " + node.getLabel();
                        notNormalizedLabels.add(label);
                    }
                    if (notNormalizedLabels.size() > 0) {
                        // message: how many items were normalized and which were not normalized
                        JOptionPane.showInputDialog(splitPane, I18N.translate("MessageCannotNormalize"), I18N
                                .translate("MessageNormalizeAllSuccessful", renamedNodes.size()),
                                JOptionPane.INFORMATION_MESSAGE, null, notNormalizedLabels.toArray(bigGUIList), null);
                    } else {
                        // message: how many items were normalized
                        JOptionPane.showMessageDialog(splitPane, I18N.translate("MessageNormalizeAllSuccessful",
                                renamedNodes.size()));
                    }

                }
            }.execute();
        }
    }

    /**
     * The [Rename Item] button was clicked:
     */
    protected void renameItemButtonClicked() {
        String title = I18N.translate("TitleRenameItem");
        String message = I18N.translate("MessageEnterNewItemName");
        String textFieldContent = selectedNode.getLabel().trim();
        int messageType = JOptionPane.QUESTION_MESSAGE;

        String newItemName;
        Boolean ok;
        do {
            newItemName = (String) new ElementNamingDialog().showInputDialog(this, message, title, messageType, null,
                    null, textFieldContent, 540, 150);
            if (newItemName == null) {
                return;
            }
            newItemName = newItemName.trim();
            if (newItemName.length() == 0) {
                return;
            }

            ok = false;

            if (!FileUtil.isFileNameAllowed(newItemName)) {
                title = I18N.translate("TitleCantRenameItem");
                message = I18N.translate("MessageBadLettersInFilename") + "\n" + I18N.translate(
                        "MessageEnterNewItemName");
                messageType = JOptionPane.WARNING_MESSAGE;
                textFieldContent = newItemName;
                continue;
            }

            try {
                selectedNode.rename(newItemName);
            } catch (final java.lang.Exception ex) {
                title = I18N.translate("TitleCantRenameItem");
                message = ex.getMessage() + "\n" + I18N.translate("MessageEnterNewItemName");
                messageType = JOptionPane.WARNING_MESSAGE;
                textFieldContent = selectedNode.getLabel();
                continue;
            }

            ok = true;
        } while (!ok);

        getTreeTableModel().refreshNode(treeTable.getPathForRow(selectedIndex));
        enableOrDisableActions();

        updateView();
        final int lastEventIndex = selectedNode.getMyEvents().size() - 1;
        eventTable.getSelectionModel().setSelectionInterval(lastEventIndex, lastEventIndex);

        if (launcherView.isInDevelopMode()) {
            traceTree();
        }
    }

    /**
     * The [Delete Item] button was clicked (with or without shift key):
     */
    protected void deleteItemButtonClicked(final ActionEvent actionEvent) {
        // If the shift-key is held while clicking the delete-button: DON'T ask.

        if ((actionEvent.getModifiers() & ActionEvent.SHIFT_MASK) == 0) {
            // The shift-key was pressed:

            final int selectedRowCount = treeTable.getSelectedRowCount();
            if (selectedRowCount == 0) {
                return;
            } else if (selectedRowCount == 1) {
                // Single selection:

                if (selectedNode.isFolder() && selectedNode.getChildCount() != 0) {
                    if (JOptionPane.showConfirmDialog(this, I18N.translate("QuestionDeleteWithAllSubElements"), I18N
                            .translate("TitleDeleteFolder"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                        return;
                    }
                } else {
                    if (JOptionPane.showConfirmDialog(this, I18N.translate("QuestionDeleteItem"), I18N.translate(
                            "TitleDeleteItem"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            } else {
                // Multiple selection:

                if (JOptionPane.showConfirmDialog(this, I18N.translate("QuestionDeleteMultipleItems",
                        selectedRowCount), I18N.translate("TitleDeleteMultipleItems"),
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }

        deleteItemDontAskButtonClicked();
    }

    /**
     * The [Delete Item Dont Ask] button was clicked:
     */
    public void deleteItemDontAskButtonClicked() {
        final int selectedRowCount = treeTable.getSelectedRowCount();
        if (selectedRowCount == 0) {
            return;
        } else if (selectedRowCount == 1) {
            // Single selection:

            if (selectedNode.isRoot()) {
                JOptionPane.showMessageDialog(this, I18N.translate("MessageCantDeleteRootItem"),
                        I18N.translate("TitleCantDeleteItem"), JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                selectedNode.delete();
            } catch (final FileOperationNotAllowedException ex) {
                // This can actually never happen: this ex gets thrown only when
                // a SIP was opened in read-only mode.
                Logger.error(ex.getMessage(), ex);
            } catch (final Exception e) {
                JOptionPane.showMessageDialog(this, e.toString(), I18N.translate("TitleCantDeleteItem"),
                        JOptionPane.ERROR_MESSAGE);
            }

            getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedIndex).getParentPath());
        } else {
            // Multiple selection: check if all selected items are files. If at
            // least one of them is a folder, reject:

            for (final int i : treeTable.getSelectedRows()) {
                if (((NodeAbstract) treeTable.getPathForRow(i).getLastPathComponent()).isFolder()) {
                    JOptionPane.showMessageDialog(this, I18N.translate("MessageCantDeleteMultipleFolders"),
                            I18N.translate("TitleCantDeleteMultipleFolders"), JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // I have to make a copy of the selection list because after a
            // "refreshTreeStructure()", the selection has changed:
            final int[] selectedRows = treeTable.getSelectedRows();

            // I have to go backwards through the list because the index number
            // of the selected items changes when deleting going forward:
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                try {
                    ((NodeAbstract) treeTable.getPathForRow(selectedRows[i]).getLastPathComponent()).delete();
                } catch (final FileOperationNotAllowedException ex) {
                    // This can actually never happen: this ex gets thrown only
                    // when a SIP was opened in read-only mode.
                    Logger.error(ex.getMessage(), ex);
                } catch (final Exception e) {
                    JOptionPane.showMessageDialog(this, e.toString(), I18N.translate("TitleCantDeleteItem"),
                            JOptionPane.ERROR_MESSAGE);
                    break;
                }

                getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedRows[i]).getParentPath());
            }
        }

        enableOrDisableActions();

        if (launcherView.isInDevelopMode()) {
            traceTree();
        }
    }

    protected void replaceFileButtonClicked() {
        if (selectedNode == null) {
            return;
        }
        if (selectedNode.isFolder()) {
            return;
        }

        final JFileChooser fileChooser = new JFileChooser(launcherView.getDataDirectory());
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        // fileChooser.setApproveButtonText("Select");
        fileChooser.setDialogTitle(I18N.translate("TitleReplaceFile"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return; // Cancel-button clicked
        }

        // Remember the insert directory:
        launcherView.setDataDirectory(FileUtil.asCanonicalFileName(fileChooser.getSelectedFile().getParent()));

        // The term getSelectedFile().getParent() CAN return an empty String
        // under Windows (e.g. when "C:/" is selected). Hence:
        if (launcherView.getDataDirectory().isEmpty()) {
            launcherView
                    .setDataDirectory(FileUtil.asCanonicalFileName(USER_HOME));
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            ((NodeFile) selectedNode).replaceByFile(fileChooser.getSelectedFile().getCanonicalPath());
        } catch (final FileOperationNotAllowedException ex) {
            // This can actually never happen: this ex gets thrown only when a SIP was opened in read-only mode.
            Logger.error(ex.getMessage(), ex);
        } catch (final Exception e) {
            JOptionPane.showMessageDialog(this, e.toString(), I18N.translate("TitleCantReplaceFile"),
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        final int rememberSelectedIndex = selectedIndex;
        getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedIndex).getParentPath());
        treeTable.getSelectionModel().setSelectionInterval(rememberSelectedIndex, rememberSelectedIndex);
    }

    protected void migrateFileManuallyButtonClicked() {
        // If an item is selected AND this item is a folder, then open the
        // AssignLevelOfDescriptionsToTreeLevelsDialog for this item.
        if (selectedNode == null || selectedNode.isFile()) {
            return;
        }

        if (!new AssignLevelsByLayerDialog(this, selectedNode).goButtonWasClicked) {
            return;
        }

        // Refresh the selected node:
        getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedIndex));
        enableOrDisableActions();
    }

    protected void openSAExternallyButtonClicked() {
        try {
            document.getSubmissionAgreement().openAsHTMLExternally();
        } catch (final Exception e) {
            JOptionPane.showMessageDialog(this, I18N.translate("MessageCantOpenSAExternally", e.toString()), I18N
                    .translate("TitleCantOpenSAExternally"), JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void testOrAssignSAButtonClicked() {
        final TestOrAssignSADialog dialog = new TestOrAssignSADialog(this);
        if (!dialog.setSAButtonClicked) {
            return;
        }

        getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(0));
        enableOrDisableActions();
    }

    protected void openAssignLevelsByLayerViewButtonClicked() {
        // If an item is selected AND this item is a folder, then open the
        // AssignLevelOfDescriptionsToTreeLevelsDialog for this item.
        if (selectedNode == null || selectedNode.isFile()) {
            return;
        }

        if (!new AssignLevelsByLayerDialog(this, selectedNode).goButtonWasClicked) {
            return;
        }

        // Refresh the selected node:
        getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedIndex));
        enableOrDisableActions();
    }

    protected void openAssignLevelsByLabelViewButtonClicked() {
        // If an item is selected AND this item is a folder, then open the AssignLevelOfDescriptionsToTreeLevelsDialog
        // for this item.
        if (selectedNode == null) {
            return;
        }
        if (selectedNode.isFile()) {
            return;
        }

        if (!new AssignLevelsByLabelDialog(this, selectedNode).goButtonWasClicked) {
            return;
        }

        // Refresh the selected node:
        getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedIndex));
        enableOrDisableActions();
    }

    protected void setLevelButtonClicked(final ActionEvent e) {
        ExceptionCollector.clear();

        final LevelOfDescription level = document.getLevels().get(e.getActionCommand());

        // Multiple selection in the tree view: change the levelOfDescription of all selected nodes:
        if (treeTable.getSelectedRowCount() >= 2) {
            for (final int i : treeTable.getSelectedRows()) {
                try {
                    ((NodeAbstract) treeTable.getPathForRow(i).getLastPathComponent()).setLevel(level);
                } catch (final Exception ex) {
                    ch.docuteam.tools.exception.Exception.remember(ex);
                }

                getTreeTableModel().refreshNode(treeTable.getPathForRow(i));
            }

            return;
        }

        // Single selection: set the level of the selected node:
        try {
            selectedNode.setLevel(level);
        } catch (final Exception ex) {
            ch.docuteam.tools.exception.Exception.remember(ex);
        }

        // Refresh the selected node:
        getTreeTableModel().refreshNode(treeTable.getPathForRow(selectedIndex));
        fillInsertMetadataElementComboBox();

        // Update the view and select the last event in the event list:
        updateView();
        final int lastEventIndex = selectedNode.getMyEvents().size() - 1;
        eventTable.getSelectionModel().setSelectionInterval(lastEventIndex, lastEventIndex);

        if (!ExceptionCollector.isEmpty()) {
            ExceptionCollector.systemOut();
        }
    }

    protected void setSubmitRequestButtonClicked() {
        ExceptionCollector.clear();

        // change the submitStatus of all selected nodes recursively, in both directions of the tree if not set
        // already:
        for (final int i : treeTable.getSelectedRows()) {
            final NodeAbstract nodeCurrent = (NodeAbstract) treeTable.getPathForRow(i).getLastPathComponent();

            // check if the SubmitStatus can be set upwards in the hierarchy:
            NodeAbstract nodeParent = nodeCurrent;
            while (!nodeParent.isRoot() && !((NodeAbstract) nodeParent.getParent()).getSubmitStatus().equals(
                    SubmitStatus.SubmitRequested) && !((NodeAbstract) nodeParent.getParent()).getSubmitStatus()
                            .equals(SubmitStatus.Submitted)) {
                nodeParent = (NodeAbstract) nodeParent.getParent();
                try {
                    nodeParent.setSubmitStatus_check(SubmitStatus.SubmitRequested);
                } catch (final CantSetSubmitStatusNotAllowedException ex) {
                    ch.docuteam.tools.exception.Exception.remember(ex);
                }
            }

            // check if the SubmitStatus can be set downwards in the hierarchy:
            try {
                nodeCurrent.setSubmitStatusRecursivelyAllOrNone_check(SubmitStatus.SubmitRequested);
            } catch (final CantSetSubmitStatusRecursiveException ex) {
                ch.docuteam.tools.exception.Exception.remember(ex);
            }

            // print any problems that occurred, or – if no problems were found – apply the changes
            if (!ExceptionCollector.isEmpty()) {
                ExceptionCollector.systemOut();
            } else {
                // set the SubmitStatus downwards in the hierarchy:
                nodeCurrent.setSubmitStatusRecursivelyAllOrNone_force(SubmitStatus.SubmitRequested);
                // set the SubmitStatus upwards in the hierarchy if not set already:
                nodeParent = nodeCurrent;
                while (!nodeParent.isRoot() && !((NodeAbstract) nodeParent.getParent()).getSubmitStatus().equals(
                        SubmitStatus.SubmitRequested) && !((NodeAbstract) nodeParent.getParent()).getSubmitStatus()
                                .equals(SubmitStatus.Submitted)) {
                    nodeParent = (NodeAbstract) nodeParent.getParent();
                    nodeParent.setSubmitStatus_force(SubmitStatus.SubmitRequested);
                }

                getTreeTableModel().refreshNode(treeTable.getPathForRow(i));
            }
        }

        // Update the view and select the last event in the event list:
        updateView();
    }

    protected void setSubmitRetractButtonClicked() {
        ExceptionCollector.clear();

        // change the submitStatus of all selected nodes recursively:
        for (final int i : treeTable.getSelectedRows()) {
            try {
                ((NodeAbstract) treeTable.getPathForRow(i).getLastPathComponent())
                        .setSubmitStatusRecursivelyAllOrNone_check(SubmitStatus.SubmitUndefined);
                ((NodeAbstract) treeTable.getPathForRow(i).getLastPathComponent())
                        .setSubmitStatusRecursivelyAllOrNone_force(SubmitStatus.SubmitUndefined);
            } catch (final Exception ex) {
                ch.docuteam.tools.exception.Exception.remember(ex);
            }

            getTreeTableModel().refreshNode(treeTable.getPathForRow(i));
        }

        // Update the view and select the last event in the event list:
        updateView();

        if (!ExceptionCollector.isEmpty()) {
            ExceptionCollector.systemOut();
        }
    }

    // TODO merge this specific method for the default EAD export into the flexible method createExport
    protected void exportAsEADFileButtonClicked() {
        final JFileChooser fileChooser = new JFileChooser(launcherView.getLastUsedOpenOrSaveDirectory());
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle(I18N.translate("TitleExportAsEADFile"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        final File suggestedFile = new File(launcherView.getLastUsedOpenOrSaveDirectory(), document.getSIPName() +
                ".xml");
        fileChooser.setSelectedFile(suggestedFile);

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return; // if not approved
        }

        try {
            document.createEADFile(fileChooser.getSelectedFile().getPath());
        } catch (final Exception ex) {
            Logger.error(ex.getMessage(), ex);
        }
    }

    protected void createExport(final String exportName) {
        final String eadFilePath = FileUtil.getTempFolder() + "/" + document.getSIPName() + ".xml";
        final String exportStylesheet = launcherView.getExportsDirectory() + File.separator + exportName + ".xsl";

        // Ask for the report destination path:
        final FileDialog fileDialog = new FileDialog(this, I18N.translate("TitleExportAsEADFile"), FileDialog.SAVE);
        fileDialog.setDirectory(new File(launcherView.getLastUsedOpenOrSaveDirectory()).getAbsolutePath());
        fileDialog.setFile(document.getSIPName() + "_" + exportName + ".xml");
        fileDialog.setLocationRelativeTo(this);

        fileDialog.setVisible(true);
        if (fileDialog.getFile() == null) {
            return;
        }

        final String exportFilePath = fileDialog.getDirectory() + fileDialog.getFile();

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Generate generic EAD file:
            document.createEADFile(eadFilePath);

            XMLTransformer.transformXML(eadFilePath, exportStylesheet, exportFilePath);
        } catch (DocumentException | ExceptionCollectorException | IOException e) {
            Logger.error(I18N.translate("MessageCouldNotCreateOutput", eadFilePath), e);
        } catch (final TransformerException e) {
            Logger.error(I18N.translate("MessageCouldNotCreateOutput", exportFilePath), e);
        } finally {
            try {
                // Delete the generated EAD file:
                FileUtil.delete(eadFilePath);
            } catch (final FileUtilExceptionListException e) {
                Logger.error(e.getMessage(), e);
            }

            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    protected void exportAsCSVFileButtonClicked() {
        final FileDialog fileDialog = new FileDialog(this, I18N.translate("TitleExportAsCSVFile"), FileDialog.SAVE);
        fileDialog.setDirectory(new File(launcherView.getLastUsedOpenOrSaveDirectory()).getAbsolutePath());
        fileDialog.setFile(document.getSIPName() + ".csv");
        fileDialog.setLocationRelativeTo(this);
        fileDialog.setVisible(true);

        if (fileDialog.getFile() == null) {
            return;
        }

        try {
            CSVExport.export(document, fileDialog.getDirectory() + fileDialog.getFile());
        } catch (final Exception ex) {
            Logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * Export to SEDA after saving this SIP.
     */
    private void exportAsSEDA_ActionButtonClicked(final ExporterSEDA exporter, final String dialogTitleKey,
            final String successfulMessageKey, final String failedMessageKey) {

        final JFileChooser fileChooser = new JFileChooser(launcherView.getLastUsedOpenOrSaveDirectory());
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle(I18N.translate(dialogTitleKey));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return; // Cancel-button clicked
        }

        final SwingWorker<Boolean, Boolean> worker = new SwingWorker<Boolean, Boolean>() {

            boolean successfullyExported = false;

            Path sedaSIPPath = null;

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // save SIP before export
                    saveSIP(null, false);

                    sedaSIPPath = fileChooser.getSelectedFile().toPath();
                    final FileProperty fileProperty = getFileProperty();

                    successfullyExported = exporter.exportMetsToSEDA(fileProperty.getFile().toPath(), sedaSIPPath,
                            PACKER_CONSTANTS_OPERATOR);
                    return successfullyExported;
                } catch (final Exception e) {
                    Logger.error("error at exportAsSEDA_ActionButtonClicked: " + e);
                }
                return false;
            }

            @Override
            protected void done() {
                // GUI feedback in any case: successful or not! -> JOptionPane
                if (successfullyExported) {
                    JOptionPane.showMessageDialog(splitPane, I18N.translate(successfulMessageKey) + " " + sedaSIPPath
                            .toString());
                } else {
                    JOptionPane.showMessageDialog(splitPane, I18N.translate(failedMessageKey));
                }
            }

        };
        worker.execute();
    }

    /**
     * For now, only one item at a time can be exported. Later, we may allow multiple items to be exported, provided
     * that they all are within the same folder.
     */
    protected void exportButtonClicked() {
        if (selectedNode == null || treeTable.getSelectedRowCount() >= 2) {
            return;
        }

        final JFileChooser fileChooser = new JFileChooser(launcherView.getDataDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(I18N.translate("TitleSelectExportDestinationFolder"));
        fileChooser.setMultiSelectionEnabled(false);
        final int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        final String destinationFolder = fileChooser.getSelectedFile().getPath();

        // Check if this file or folder already exists in the destination folder
        // - if yes, ask if to overwrite:
        if (new File(destinationFolder + "/" + selectedNode.getFile().getName()).exists()) {
            if (JOptionPane.showConfirmDialog(this, I18N.translate("QuestionExportOverwriteFile"),
                    I18N.translate("TitleExportItem"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            FileUtil.copyToFolderMerging(selectedNode.getFile(), new File(destinationFolder));
        } catch (final Exception ex) {
            Logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * The [Save as Template...] button was clicked:
     */
    protected void saveAsTemplateButtonClicked() {
        if (document == null) {
            return;
        }

        String title = I18N.translate("TitleCreateTemplate");
        String message = I18N.translate("MessageEnterNewTemplateName");
        int messageType = JOptionPane.QUESTION_MESSAGE;
        String textFieldContent = new File(document.getSIPFolder().trim()).getName();

        Boolean ok;
        do {
            String newItemName = (String) JOptionPane.showInputDialog(this, message, title, messageType, null, null,
                    textFieldContent);
            if (newItemName == null) {
                return;
            }
            newItemName = newItemName.trim();
            if (newItemName.length() == 0) {
                return;
            }

            ok = true;

            if (!FileUtil.isFileNameAllowed(newItemName)) {
                ok = false;
                title = I18N.translate("TitleCantCreateTemplate");
                message = I18N.translate("MessageBadLettersInFilename") + "\n" + I18N.translate(
                        "MessageEnterNewTemplateName");
                messageType = JOptionPane.WARNING_MESSAGE;
                textFieldContent = newItemName;
                continue;
            }

            if (new File(launcherView.getTemplateDirectory() + "/" + newItemName).exists()) {
                final int confirm = JOptionPane.showConfirmDialog(this, I18N.translate(
                        "MessageOverwriteExistingTemplate"));

                switch (confirm) {
                    case JOptionPane.YES_OPTION:
                        break; // Save/overwrite template: leave this loop and continue
                    case JOptionPane.NO_OPTION: {
                        // Enter another template name
                        ok = false;
                        title = I18N.translate("TitleCreateTemplate");
                        message = I18N.translate("MessageEnterNewTemplateName");
                        messageType = JOptionPane.QUESTION_MESSAGE;
                        textFieldContent = new File(document.getSIPFolder()).getName();
                        continue;
                    }
                    default:
                        return; // Exit this method
                }
            }

            try {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                document.saveAsTemplate(launcherView.getTemplateDirectory() + "/" + newItemName);

                enableOrDisableActions();
                setFooterText(I18N.translate("MessageFooterTemplateSaved") + newItemName);
            } catch (final CantCreateTemplateWithRootFileException ex) {
                JOptionPane.showMessageDialog(this, I18N.translate("MessageCantCreateTemplateWithRootFile"));
                return;
            } catch (final java.lang.Exception ex) {
                ok = false;
                title = I18N.translate("TitleCantCreateTemplate");
                message = ex.getMessage() + "\n" + I18N.translate("MessageEnterNewTemplateName");
                messageType = JOptionPane.WARNING_MESSAGE;
                textFieldContent = document.getName();
            } finally {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

        } while (!ok);
    }

    protected void openDocuteamHomepage() {
        if (!java.awt.Desktop.isDesktopSupported()) {
            System.err.println("Desktop is not supported");
            return;
        }

        final java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            System.err.println("Desktop doesn't support the browse action");
            return;
        }

        try {
            desktop.browse(new java.net.URI("http://www.docuteam.ch"));
        } catch (final java.lang.Exception ex) {
            Logger.error(ex.getMessage(), ex);
        }
    }

    protected void expandAll() {
        treeTable.expandAll();
    }

    protected void insertMetadataElement() {
        final LevelMetadataElement lme = (LevelMetadataElement) selectMetadataElementComboBox.getSelectedItem();

        try {
            selectedNode.addDynamicMetadataElementInstanceWithName(lme.getId());
        } catch (final MetadataElementCantAddException ex) {
            JOptionPane.showMessageDialog(this, I18N.translate("MessageCantAddMetadataElement"), lme.getId(),
                    JOptionPane.ERROR_MESSAGE);
        } catch (final Exception ex) {
            Logger.error(ex.getMessage(), ex);
        }

        fillInsertMetadataElementComboBox();
        updateView();
    }

    protected void removeMetadataElement() {
        final MetadataElementInstance me = ((MetadataTableModel) metadataTable.getModel())
                .get(metadataTable.getSelectedRow());

        try {
            selectedNode.deleteDynamicMetadataElementInstanceWithName(me.getName(), me.getIndex());
        } catch (final MetadataElementCantDeleteException ex) {
            JOptionPane.showMessageDialog(this, I18N.translate("MessageCantDeleteMetadataElement"), me.getName(),
                    JOptionPane.ERROR_MESSAGE);
        } catch (final Exception ex) {
            Logger.error(ex.getMessage(), ex);
        }

        fillInsertMetadataElementComboBox();
        updateView();
    }

    protected void metaTitleTextFieldWasChanged() {
        if (metaTitleTextField.getText().equals(selectedNode.getUnitTitle())) {
            return;
        }

        selectedNode.setUnitTitle(metaTitleTextField.getText());
        updateView();
    }

    protected void redisplayNode() {
        if (selectedIndex == -1) {
            return;
        }

        getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedIndex).getParentPath());
        treeTable.expandRow(selectedIndex);
        treeTable.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
    }

    /**
     * Select in the treeTable the node with the given admId. If this node doesn't exist, ignore it.
     * 
     * @param admId
     */
    public void selectNode(final String admId) {
        Logger.debug("Select admId: " + admId);

        final NodeAbstract node = document.getStructureMap().getRoot().searchId(admId);
        if (node == null) {
            return;
        }

        selectNode(node);
    }

    /**
     * Select in the treeTable the node node. If node is null, ignore it.
     * 
     * @param treePath
     */
    protected void selectNode(final NodeAbstract node) {
        Logger.debug("Select Node: " + node);

        if (node == null) {
            return;
        }

        selectNode(node.getTreePath());
    }

    /**
     * Select in the treeTable the node at treePath. If treePath is null, ignore it.
     * 
     * @param treePath
     */
    private void selectNode(final TreePath treePath) {
        Logger.debug("Select Path: " + treePath);

        if (treePath == null) {
            return;
        }

        treeTable.getTreeSelectionModel().setSelectionPath(treePath);
        treeTable.scrollPathToVisible(treePath);
    }

    private void checkSubmission() {
        final List<String> submitCheckMessages = AIPCreatorProxy.getAIPCreator().checkSubmission(document);

        if (submitCheckMessages.isEmpty()) {
            JOptionPane.showMessageDialog(this, I18N.translate("MessageSubmitCheckSuccessful"),
                    I18N.translate("HeaderSubmitCheck"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            new ScrollableMessageDialog(this, I18N.translate("HeaderSubmitCheck"),
                    translateAndFormat(I18N.translate("MessageSubmitCheckFailed"), submitCheckMessages));
        }
    }

    protected void submit() {

        if (document.isModified()) {
            // Ask whether to save the loaded document:
            final int answer = JOptionPane.showConfirmDialog(this,
                    "'" + document.getSIPName() + "':\n" + I18N.translate("QuestionSaveModified"),
                    I18N.translate("TitleCloseSIP"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            switch (answer) {
                case JOptionPane.YES_OPTION: {
                    // Save current document, unlock and cleanup after saving,
                    // then continue closing:
                    saveCopyAs(null, false);
                    break;
                }
                case JOptionPane.NO_OPTION: {
                    // Don't save current document but continue closing:
                    return;
                }
                case JOptionPane.CANCEL_OPTION: {
                    // Cancel, don't continue:
                    return;
                }
                default: {
                    // Cancel, don't continue:
                    return;
                }
            }
        }

        if (JOptionPane.showConfirmDialog(this, I18N.translate("MessageSubmitAsk"),
                I18N.translate("HeaderSubmit"), JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        Logger.debug("Submitting SIP: '" + document.getSIPName() + "'");

        new SwingWorker<Integer, Object>() {

            @Override
            public Integer doInBackground() {
                final SmallPeskyMessageWindow w = SmallPeskyMessageWindow.openBlocking(SIPView.this,
                        I18N.translate("MessageTempSubmitting"));

                List<String> submitCheckMessages = new ArrayList<String>();
                try {
                    submitCheckMessages = AIPCreatorProxy.getAIPCreator().submit(document);
                } catch (final Exception e) {
                    submitCheckMessages.add("MessageSubmitExceptionOccurred '" + e.getMessage() + "'");
                } finally {
                    w.close();
                }

                if (submitCheckMessages.isEmpty()) {
                    JOptionPane.showMessageDialog(SIPView.this,
                            I18N.translate("MessageSubmitDone"));
                    final Path originalSipPath = Paths.get(document.getOriginalSIPFolder());
                    if (!Files.exists(originalSipPath)) {
                        launcherView.unregister(SIPView.this);

                        setVisible(false);
                        dispose();
                        launcherView.getSipTable().getSipTableModel().removeSipFromView(fileProperty);
                    } else {
                        launcherView.refreshSIPTable();
                    }
                } else {
                    new ScrollableMessageDialog(SIPView.this, I18N.translate("HeaderSubmit"),
                            translateAndFormat(I18N.translate("MessageSubmitCheckFailed"), submitCheckMessages));
                }
                return 0;
            }
        }.execute();

        return;
    }

    /**
     * @param reportName
     * @param stylesheetName
     */
    protected void createReport(final String reportName, final String stylesheetName) {
        String outputFormat = OUTPUT_FORMAT_PDF; // default
        if (stylesheetName.endsWith(REPORT_STYLESHEET_SUFFIX_OUTPUT_FORMAT_CSV)) {
            outputFormat = OUTPUT_FORMAT_CSV;
        }

        final String reportFilePath = getReportFilePath(reportName, "." + outputFormat);
        if (!reportFilePath.isEmpty() && reportName != null) {
            String tempFilePath = null;

            try {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                final String reportStylesheet = launcherView.getReportsDirectory() + File.separator + stylesheetName +
                        ".xsl";
                String xmlFilePath = null;
                if (stylesheetName.startsWith(REPORT_STYLESHEET_PREFIX_SOURCE_TYPE_EAD)) {
                    // Generate EAD file:
                    xmlFilePath = FileUtil.getTempFolder() + "/" + document.getSIPName() + ".xml";
                    document.createEADFile(xmlFilePath);
                    tempFilePath = xmlFilePath; // must be deleted in finally
                } else if (stylesheetName.startsWith(REPORT_STYLESHEET_PREFIX_SOURCE_TYPE_METS)) {
                    xmlFilePath = document.getFilePath();
                } else {
                    Logger.warn("cannot find report stylesheet, should start with " +
                            REPORT_STYLESHEET_PREFIX_SOURCE_TYPE_EAD + " or " +
                            REPORT_STYLESHEET_PREFIX_SOURCE_TYPE_METS);
                }

                if (xmlFilePath != null && OUTPUT_FORMAT_PDF.equals(outputFormat)) {
                    // Create the report out of the generated EAD/METS file:
                    XML2PDF.createPDF(xmlFilePath, reportStylesheet, reportFilePath);
                } else if (xmlFilePath != null && OUTPUT_FORMAT_CSV.equals(outputFormat)) {
                    XMLTransformer.transformXML(xmlFilePath, reportStylesheet, reportFilePath);
                }

            } catch (final Exception ex) {
                Logger.error(I18N.translate("MessageCouldNotCreateOutput", reportFilePath), ex);
            } finally {
                try {
                    if (tempFilePath != null) {
                        // Delete the generated EAD file:
                        FileUtil.delete(tempFilePath);
                    }
                } catch (final FileUtilExceptionListException ex) {
                    Logger.error("cannot delete tempFilePath: " + tempFilePath, ex);
                }

                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    /**
     * @param reportName
     * @param extension
     * @return
     */
    private String getReportFilePath(final String reportName, final String extension) {
        String reportFilePath = "";
        // Ask for the report destination path:
        final FileDialog fileDialog = new FileDialog(this, I18N.translate("LabelSaveReportAs"), FileDialog.SAVE); // change
                                                                                                                  // with
                                                                                                                  // swing
                                                                                                                  // FileDialog
        fileDialog.setDirectory(getReportDestinationDirPath());
        fileDialog.setFile(document.getSIPName() + "_" + reportName + extension);
        fileDialog.setVisible(true);
        if (fileDialog.getFile() != null) {
            reportFilePath = fileDialog.getDirectory() + fileDialog.getFile();
        }
        return reportFilePath;
    }

    private String getReportDestinationDirPath() {
        return launcherView.getReportsDestinationDirectory();
    }

    protected String translateAndFormat(final CantSetSubmitStatusRecursiveException ex) {
        return translateAndFormat(
                I18N.translate("MessageSettingSubmitStatusFailed", I18N.translate(ex.getSubmitStatus().name())),
                ex.getRejectMessages());
    }

    protected String translateAndFormat(final String firstRow, final List<String> messageList) {
        // The message strings are expected to look like this:
        // "MessageToTranslate<Space>AdditionalInfos".
        // The AdditionalInfos are optional and may contain more phrases to translate, which are
        // expected to be in the format "I18N{PhraseToTranslate}".
        final StringBuilder messageString = new StringBuilder(firstRow);
        for (final String s : messageList) {
            final int separatorIndex = s.indexOf(" ");

            String message;
            if (separatorIndex > 0) {
                message = I18N.translate(s.substring(0, separatorIndex));
                String additionalInfos = s.substring(separatorIndex);

                // Replace every occurrence of "I18N{something}" within
                // additionalInfos by the translation of "something":
                if (additionalInfos.contains("I18N{") && additionalInfos.contains("}")) {
                    int startIndex = 0;
                    do {
                        final int keywordStartIndex = additionalInfos.indexOf("I18N{", startIndex);
                        if (keywordStartIndex == -1) {
                            break;
                        }
                        final int keywordEndIndex = additionalInfos.indexOf("}", keywordStartIndex);
                        if (keywordEndIndex == -1) {
                            break;
                        }

                        final String keyword = additionalInfos.substring(keywordStartIndex + "I18N{".length(),
                                keywordEndIndex);
                        final String keywordTranslated = I18N.translate(keyword);
                        additionalInfos = additionalInfos.replace(
                                additionalInfos.substring(keywordStartIndex, keywordEndIndex + "}".length()),
                                keywordTranslated);

                        startIndex = keywordStartIndex + keywordTranslated.length();
                    } while (true);
                }

                messageString.append(additionalInfos).append(": ");
            } else {
                message = I18N.translate(s);
            }

            messageString.append(message).append("\n");
        }

        return messageString.toString();
    }

    protected void setDividerLocation(final int width) {
        splitPane.setDividerLocation(width);
    }

    public void enableOrDisableActions() {
        // First disable all actions, later enable specific actions if appropriate:

        // Document actions:
        saveAction.setEnabled(false);
        saveAsAction.setEnabled(false);
        normalizeAction.setEnabled(false);
        checkFixityAction.setEnabled(false);
        closeAction.setEnabled(false);
        openSAExternallyAction.setEnabled(false);
        testOrAssignSAAction.setEnabled(false);
        exportAsEADFileAction.setEnabled(false);
        exportAction.setEnabled(false);
        expandAllAction.setEnabled(false);
        collapseAllAction.setEnabled(false);
        saveAsTemplateAction.setEnabled(false);
        submitCheckAction.setEnabled(false);
        submitAction.setEnabled(false);
        exportAsEADFileAction.setEnabled(false);
        exportAsCSVFileAction.setEnabled(false);
        removeTrashAction.setEnabled(false);
        removeDuplicatesAction.setEnabled(false);
        // Don't enable or disable this submenu here -
        // this would require to check files in the "templates/reports" folder each time
        // reportsSubMenu.setEnabled(false);

        // Node actions:
        deleteItemAction.setEnabled(false);
        deleteItemDontAskAction.setEnabled(false);
        renameItemAction.setEnabled(false);
        sortAction.setEnabled(false);
        replaceFileAction.setEnabled(false);
        appendMigratedFileAction.setEnabled(false);
        createFolderAction.setEnabled(false);
        insertAction.setEnabled(false);
        openAssignLevelsByLayerViewAction.setEnabled(false);
        openAssignLevelsByLabelViewAction.setEnabled(false);
        redisplayNodeAction.setEnabled(false);
        exportAction.setEnabled(false);
        convertAction.setEnabled(false);
        submitRequestAction.setEnabled(false);
        submitRetractAction.setEnabled(false);

        // Disable actions for setting the level, later enable allowed levels if appropriate:
        itemLevelsSubMenu.setEnabled(false);
        for (final Action setLevelAction : setLevelActions) {
            setLevelAction.setEnabled(false);
        }

        // Disable actions and fields for dynamic metadata, later enable them if appropriate:
        metaTitleTextField.setEnabled(false);
        selectMetadataElementComboBox.setEnabled(false);
        insertMetadataElementAction.setEnabled(false);
        removeMetadataElementAction.setEnabled(false);

        if (document == null) {
            Logger.warn("Document is null???");

            showInInfoLabel("");
            return;
        }

        // Now enable special actions selectively:

        saveAsAction.setEnabled(!document.isAtLeastOneFileNotReadable());
        checkFixityAction.enableOrDisable();
        convertAction.enableOrDisable();
        closeAction.setEnabled(true);

        if (properties.getProperty("docuteamPacker.SA.BASE.URL") != null) {
            openSAExternallyAction.setEnabled(true);
        }
        exportAsEADFileAction.setEnabled(true);
        expandAllAction.setEnabled(true);
        collapseAllAction.setEnabled(true);
        saveAsTemplateAction.setEnabled(true);
        exportAsEADFileAction.setEnabled(true);
        exportAsCSVFileAction.setEnabled(true);

        final boolean documentIsWritable = document.canWrite();
        if (!documentIsWritable) {
            // If document is readOnly or locked, don't show the message
            // "MessageMandatoryFieldsNotSet",
            // but only one of "LabelIsLocked" or "LabelIsReadOnly":
            if (document.isLocked()) {
                showInInfoLabel(I18N.translate("LabelIsLocked") + document.getLockedBy(), true);
            } else if (document.isReadOnly()) {
                showInInfoLabel(I18N.translate("LabelIsReadOnly"), true);
            } else {
                showInInfoLabel("");
            }
        } else {
            saveAction.setEnabled(true);
            testOrAssignSAAction.setEnabled(true);
            removeTrashAction.setEnabled(true);
            removeDuplicatesAction.setEnabled(true);

            // The submit actions are only possible if the AIPCreator is initialized:
            if (AIPCreatorProxy.isUsable()) {
                submitCheckAction.setEnabled(true);
                submitAction.setEnabled(true);
            }

            // If document is NOT readOnly, show one of "LabelIsModified",
            // "MessageMandatoryFieldsNotSet", or nothing:
            if (document.isModified()) {
                showInInfoLabel(I18N.translate("LabelIsModified"));
            } else if (document.hasNodesWithDynamicMetadataElementInstancesWhichAreMandatoryButNotSet()) {
                showInInfoLabel(I18N.translate("MessageMandatoryFieldsNotSet"), true);
            } else if (document.isReadWriteNoFileOps()) {
                showInInfoLabel(I18N.translate("LabelIsReadWriteNoFileOps"), true);
            } else {
                showInInfoLabel("");
            }
        }

        // Check out selected item(s):

        // No selection:
        if (treeTable.getSelectedRowCount() == 0) {
            return;
        }

        // enabling handled separately in action itself
        deleteFileContentAction.enableOrDisable();
        appendMigratedFileAction.enableOrDisable();
        exploreAction.enableOrDisable();

        // Multiple selection:
        if (treeTable.getSelectedRowCount() >= 2) {
            if (!documentIsWritable) {
                return;
            }

            // Don't allow delete if any of the selected nodes has descendants
            // or predecessors not writable by current user:
            boolean canDelete = document.isReadWrite();
            boolean hasUnsubmitted = false;
            boolean hasSubmissionRequests = false;
            final Map<Action, Boolean> enabledLevelActions = new HashMap<>();
            for (final int i : treeTable.getSelectedRows()) {
                final NodeAbstract selectedNode = (NodeAbstract) treeTable.getPathForRow(i).getLastPathComponent();

                canDelete = canDelete && !selectedNode.isRoot() && selectedNode.fileExists() && selectedNode
                        .canRead() && selectedNode.canWrite() && !selectedNode
                                .hasPredecessorNotWritableByCurrentUser() && (!selectedNode.isFolder() || selectedNode
                                        .isFolder() && !((NodeFolder) selectedNode)
                                                .hasDescendantNotWritableByCurrentUser()) && selectedNode
                                                        .doesSubmitStatusAllowEditing();

                hasUnsubmitted = hasUnsubmitted || selectedNode.getSubmitStatus().equals(
                        SubmitStatus.SubmitUndefined) || selectedNode.getSubmitStatus().equals(
                                SubmitStatus.SubmitFailed);

                hasSubmissionRequests = hasSubmissionRequests || selectedNode.getSubmitStatus().equals(
                        SubmitStatus.SubmitRequested);
                final boolean submitted = selectedNode.getSubmitStatus().equals(SubmitStatus.Submitted);
                for (final Action setLevelAction : setLevelActions) {
                    final boolean currentLevelActionAllowed = selectedNode
                            .doesParentAllowSubLevel((String) setLevelAction.getValue(Action.NAME)) && !submitted;
                    final Boolean levelActionAccumulated = enabledLevelActions.get(setLevelAction) == null ? true
                            : enabledLevelActions.get(setLevelAction);
                    final Boolean levelActionAllowed = levelActionAccumulated && currentLevelActionAllowed;
                    enabledLevelActions.put(setLevelAction, levelActionAllowed);
                    // setLevelAction.setEnabled();
                }
            }

            deleteItemAction.setEnabled(canDelete);
            deleteItemDontAskAction.setEnabled(canDelete);
            submitRequestAction.setEnabled(hasUnsubmitted);
            submitRetractAction.setEnabled(hasSubmissionRequests);

            // Enable setting any level:
            itemLevelsSubMenu.setEnabled(true);
            for (final Action setLevelAction : setLevelActions) {
                setLevelAction.setEnabled(enabledLevelActions.get(setLevelAction));
            }

            normalizeAction.setEnabled(canNormalize());
            return;
        }

        // From here on: exactly one node is selected:

        // pathForSelectedRow is null when the tree part where the selection was in, gets collapsed.
        final TreePath pathForSelectedRow = treeTable.getPathForRow(selectedIndex);
        if (pathForSelectedRow == null) {
            return;
        }
        if (selectedNode == null) {
            return;
        }

        if (!documentIsWritable) {
            // Document is not writable:
            redisplayNodeAction.setEnabled(selectedNode.isFolder());
            exportAction.setEnabled(true);

            return;
        }

        if (selectedNode.isFolder()) {
            // Node is folder:

            redisplayNodeAction.setEnabled(true);

            if (!selectedNode.fileExists() || !selectedNode.canRead()) {
                ;
            } else if (!selectedNode.canWrite()) {
                // Folder is not writable:
                exportAction.setEnabled(true);
            } else if (selectedNode.hasPredecessorNotWritableByCurrentUser()) {
                // A predecessor folder is not writable:
                openAssignLevelsByLayerViewAction.setEnabled(true);
                openAssignLevelsByLabelViewAction.setEnabled(true);
                exportAction.setEnabled(true);

                enableDynamicMetadataActions();
                enableOnlyAllowedSetLevelActions();
            } else if (((NodeFolder) selectedNode).hasDescendantNotWritableByCurrentUser()) {
                // Folder has read-only children:
                final boolean structureEnabled = document.isReadWrite();
                final boolean metadataEnabled = document.getMode().equals(Mode.ReadWrite) || document.getMode()
                        .equals(Mode.ReadWriteNoFileOps);
                // boolean enabled = document.isReadWrite();
                renameItemAction.setEnabled(structureEnabled);
                sortAction.setEnabled(structureEnabled);
                createFolderAction.setEnabled(structureEnabled);
                insertAction.setEnabled(structureEnabled);
                openAssignLevelsByLayerViewAction.setEnabled(metadataEnabled);
                openAssignLevelsByLabelViewAction.setEnabled(metadataEnabled);
                exportAction.setEnabled(true);

                enableDynamicMetadataActions();
                enableOnlyAllowedSetLevelActions();
            } else if (!selectedNode.doesSubmitStatusAllowEditing()) {
                // Submit status doesn't allow editing:
                createFolderAction.setEnabled(true);
                insertAction.setEnabled(true);
                exportAction.setEnabled(true);
                if (selectedNode.getSubmitStatus().equals(SubmitStatus.SubmitRequested)) {
                    submitRetractAction.setEnabled(true);
                }
            } else {
                // Folder is readable and writable:
                final boolean structureEnabled = document.isReadWrite();
                final boolean metadataEnabled = document.getMode().equals(Mode.ReadWrite) || document.getMode()
                        .equals(Mode.ReadWriteNoFileOps);
                if (!selectedNode.isRoot()) {
                    deleteItemAction.setEnabled(structureEnabled);
                    deleteItemDontAskAction.setEnabled(structureEnabled);
                }

                submitRequestAction.setEnabled(true);

                renameItemAction.setEnabled(structureEnabled);
                sortAction.setEnabled(structureEnabled);
                createFolderAction.setEnabled(structureEnabled);
                insertAction.setEnabled(structureEnabled);
                openAssignLevelsByLayerViewAction.setEnabled(metadataEnabled);
                openAssignLevelsByLabelViewAction.setEnabled(metadataEnabled);
                exportAction.setEnabled(true);
                normalizeAction.setEnabled(structureEnabled);

                enableDynamicMetadataActions();
                enableOnlyAllowedSetLevelActions();
            }
        } else {
            // Node is file:

            if (!selectedNode.fileExists() || !selectedNode.canRead()) {
                ;
            } else if (!selectedNode.canWrite()) {
                // File is not writable:
                exportAction.setEnabled(true);
            } else if (selectedNode.hasPredecessorNotWritableByCurrentUser()) {
                // A predecessor folder is not writable:
                exportAction.setEnabled(true);

                enableDynamicMetadataActions();
                enableOnlyAllowedSetLevelActions();
            } else if (!selectedNode.doesSubmitStatusAllowEditing()) {
                // Submit status doesn't allow editing:
                exportAction.setEnabled(true);
                if (selectedNode.getSubmitStatus().equals(SubmitStatus.SubmitRequested)) {
                    submitRetractAction.setEnabled(true);
                }
            } else {
                // File is readable and writable:
                final boolean enabled = document.isReadWrite();
                if (!selectedNode.isRoot()) {
                    deleteItemAction.setEnabled(enabled);
                    deleteItemDontAskAction.setEnabled(enabled);
                }

                submitRequestAction.setEnabled(true);

                renameItemAction.setEnabled(enabled);
                sortAction.setEnabled(false);
                normalizeAction.setEnabled(enabled);
                exportAction.setEnabled(enabled);
                replaceFileAction.setEnabled(enabled);

                enableDynamicMetadataActions();
                enableOnlyAllowedSetLevelActions();
            }
        }
    }

    private boolean canNormalize() {
        // Don't allow normalize if any of the selected nodes has descendants
        // or predecessors not writable by current user:
        boolean canNormalize = document.isReadWrite();

        for (final int i : treeTable.getSelectedRows()) {
            final NodeAbstract selectedNode = (NodeAbstract) treeTable.getPathForRow(i).getLastPathComponent();

            canNormalize = canNormalize && !selectedNode.isRoot() && selectedNode.fileExists() && selectedNode
                    .canRead() && selectedNode.canWrite() && !selectedNode.hasPredecessorNotWritableByCurrentUser() &&
                    (!selectedNode.isFolder() || selectedNode.isFolder() && !((NodeFolder) selectedNode)
                            .hasDescendantNotWritableByCurrentUser()) && selectedNode.doesSubmitStatusAllowEditing();
        }
        return canNormalize;
    }

    /**
     * Enable setting only allowed levels
     */
    protected void enableOnlyAllowedSetLevelActions() {
        itemLevelsSubMenu.setEnabled(true);
        for (final Action setLevelAction : setLevelActions) {
            setLevelAction.setEnabled(selectedNode.doesParentAllowSubLevel((String) setLevelAction.getValue(
                    Action.NAME)));
        }
    }

    /**
     * Enable dynamic metadata buttons and fields
     */
    protected void enableDynamicMetadataActions() {
        metaTitleTextField.setEnabled(true);
        selectMetadataElementComboBox.setEnabled(true);
        insertMetadataElementAction.setEnabled(selectMetadataElementComboBox.getSelectedItem() != null);
        final int selectedMetadataElementIndex = metadataTable.getSelectedRow();
        if (selectedMetadataElementIndex == -1) {
            removeMetadataElementAction.setEnabled(false);
        } else {
            try {
                removeMetadataElementAction.setEnabled(((MetadataTableModel) metadataTable.getModel())
                        .get(selectedMetadataElementIndex).canBeDeleted());
            } catch (final Exception ex) {
                Logger.error(ex.getMessage(), ex);
            }
        }
    }

    protected void updateView() {
        ((FileDataViewTableModel) dataTable.getModel()).setFileStructureNode(selectedNode);
        ((EventListTableModel) eventTable.getModel()).setFileStructureNode(selectedNode);
        ((MetadataTableModel) metadataTable.getModel()).setFileStructureNode(selectedNode);

        if (selectedNode == null) {
            metaTitleTextField.setText(null);
            metaLevelTextField.setText(null);
        } else {
            metaTitleTextField.setText(selectedNode.getUnitTitle());
            metaTitleTextField.setCaretPosition(0);
            metaLevelTextField.setText(selectedNode.getLevel().getName());
        }

        enableOrDisableActions();
    }

    protected void clearView() {
        ((FileDataViewTableModel) dataTable.getModel()).setFileStructureNode(null);
        ((EventListTableModel) eventTable.getModel()).setFileStructureNode(null);
        ((MetadataTableModel) metadataTable.getModel()).setFileStructureNode(null);
        metaTitleTextField.setText(null);
        metaLevelTextField.setText(null);

        enableOrDisableActions();
    }

    static protected Comparator<LevelMetadataElement> getLevelMetadataElementComparator() {
        final String metadataOrder = properties.getProperty("docuteamPacker.SIPView.metadataOrder", "none");
        if (metadataOrder.equals("alphabetical")) {
            return (lme1, lme2) -> I18N.translate(lme1.getId())
                    .compareTo(I18N.translate(lme2.getId()));
        } else {
            return (lme1, lme2) -> 0;
        }
    }

    protected void fillInsertMetadataElementComboBox() {
        selectMetadataElementComboBox.removeAllItems();
        selectMetadataElementComboBox.addItem(null);

        final List<LevelMetadataElement> sortedLevelMetadataElementsList = selectedNode
                .getDynamicMetadataElementsWhichCanBeAdded();
        Collections.sort(sortedLevelMetadataElementsList, SIPView.getLevelMetadataElementComparator());

        for (final LevelMetadataElement lme : sortedLevelMetadataElementsList) {
            selectMetadataElementComboBox.addItem(lme);
        }
    }

    protected void showInInfoLabel(final String message) {
        showInInfoLabel(message, false);
    }

    protected void showInInfoLabel(final String message, final boolean emphasized) {
        infoLabel.setForeground(emphasized ? Color.RED : Color.BLACK);
        infoLabel.setText(message);
    }

    protected void systemOutDocument() {
        Logger.info(document);
    }

    protected void traceTree() {
        Tracer.trace(((NodeAbstract) getTreeTableModel().getRoot()).treeString(0));
    }

    private void sortButtonClicked() {
        if (selectedNode != null && selectedIndex != -1 && selectedNode instanceof NodeFolder) {
            ((NodeFolder) selectedNode).setOrderRecursive(-1, true);
            getTreeTableModel().refreshTreeStructure(treeTable.getPathForRow(selectedIndex));
        }
    }

    private void removeTrash() {
        try {
            saveSIP(null, false);
            final Optional<LevelOfDescription> optionalTrashLevel = document.getLevels().getTrashLevel();
            if (!optionalTrashLevel.isPresent()) {
                JOptionPane.showMessageDialog(this, I18N.translate("MessageTitleNoTrashLevelDefined"), I18N.translate(
                        "MessageNoTrashLevelDefined"), JOptionPane.WARNING_MESSAGE);
            } else {
                document = document.removeAllWithLevel(optionalTrashLevel.get());
                populateView(getDocument());
            }
        } catch (final NodeWithLevelNotRemovableException e) {
            JOptionPane.showMessageDialog(this, I18N.translate("MessageTitleRemoveTrashNotPossible"), I18N.translate(
                    "MessageRemoveTrashNotPossible"), JOptionPane.WARNING_MESSAGE);
        } catch (final Exception e) {
            Logger.error("removeTrash failed: ", e);
        }
    }

    private void removeDuplicates() {
        try {
            saveSIP(null, false);

            new DuplicatesTableDialog(document.getStructureMap().getRoot(), this);

        } catch (IOException | FileUtilExceptionListException | DocumentIsReadOnlyException |
                FileOrFolderIsInUseException | OriginalSIPIsMissingException e) {
            Logger.error("removeDuplicates failed: ", e);
        }
    }

    public void updateTree() {
        final TreePath rootPath = treeTable.getPathForRow(-1);
        getTreeTableModel().refreshTreeStructure(rootPath);
        updateView();
        JOptionPane.showMessageDialog(this, I18N.translate("MessageRemovedDuplicates"), I18N.translate(
                "MessageTitleRemovedDuplicates"), JOptionPane.INFORMATION_MESSAGE);
    }

}
