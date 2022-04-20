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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.IIOImage;
import javax.media.jai.JAI;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import ch.docuteam.converter.OOConverter;
import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.id.UniqueID;
import ch.docuteam.tools.os.SystemProcess;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.string.StringUtil;
import ch.docuteam.tools.translations.I18N;

import com.sun.jimi.core.Jimi;
import com.twelvemonkeys.contrib.exif.EXIFUtilities;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.common.views.DocumentViewController;

public class FilePreviewPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String TempPDFFileNameTemplate = FileUtil.getTempFolder() + "/FileConverter/Temp_%s.pdf";

    private static final String TempPNGFileNameTemplate = FileUtil.getTempFolder() + "/FileConverter/Temp_%s.png";

    private static final int DefaultCacheSizeLimit = 100;

    private static final String Tab = "&nbsp;&nbsp;&nbsp;&nbsp;";

    // The temporary files:
    private static List<String> TempPDFFileNames = new Vector<String>();

    private static List<String> TempPNGFileNames = new Vector<String>();

    // This is the cache:
    private static Map<NodeAbstract, FilePreviewPanel> Cache = new HashMap<NodeAbstract, FilePreviewPanel>();

    // This is the default cacheSize limit - the actual limit can be changed using setCacheSizeLimit():
    private static int CacheSizeLimit = DefaultCacheSizeLimit;

    // This list contains the order in which the cache elements have been retrieved:
    private static List<NodeAbstract> CacheHitOrder = new ArrayList<NodeAbstract>(CacheSizeLimit);

    private FilePreviewPanel(final NodeAbstract node) {
        super(new BorderLayout());

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (node == null) {
                Logger.debug("No item selected");

                this.add(new JPanel());
                return;
            }

            if (!node.fileExists() || !node.canRead()) {
                Logger.debug("Item '" + node.getLabel() + "' could not be read");

                final JLabel previewNotAvailable = new JLabel(I18N.translate("LabelPreviewCantReadFile"),
                        SwingConstants.CENTER);
                previewNotAvailable.setOpaque(true);
                previewNotAvailable.setBackground(Color.LIGHT_GRAY);
                this.add(previewNotAvailable);

                return;
            }

            final String fileName = node.getFile().getAbsolutePath();
            final FileType fileType = FileType.check(node);

            switch (fileType) {
                case Folder: {
                    Logger.debug("Item '" + node.getLabel() + "' identified as folder");

                    final StringBuilder pathString = new StringBuilder("<html>" + I18N.translate(
                            "LabelPreviewFolder") + "<br>");
                    int indent = 0;
                    for (final String pathElement : StringUtil.split(node.getPathString(), "/")) {
                        pathString.append("<br>");
                        for (int i = 0; i < indent; i++) {
                            pathString.append(Tab);
                        }
                        pathString.append(pathElement);
                        indent++;
                    }
                    pathString.append("</html>");

                    final JLabel previewFolder = new JLabel(pathString.toString());
                    previewFolder.setHorizontalAlignment(SwingConstants.CENTER);
                    previewFolder.setOpaque(true);
                    this.add(new JScrollPane(previewFolder));
                    break;
                }
                case Text: {
                    Logger.debug("Item '" + node.getLabel() + "' identified as text file");

                    this.add(new JScrollPane(createTextAreaWithFile(fileName)));

                    break;
                }
                case HTML: {
                    Logger.debug("Item '" + node.getLabel() + "' identified as HTML file");

                    final JLabel previewHTML = new JLabel();
                    previewHTML.setVerticalAlignment(SwingConstants.TOP);
                    previewHTML.setText(FileUtil.getFileContentAsString(fileName));
                    this.add(new JScrollPane(previewHTML));
                    break;
                }
                case PDF: {
                    Logger.debug("Item '" + node.getLabel() + "' identified as PDF file");

                    this.add(new PDFPreviewer(this, fileName));
                    break;
                }
                case OOConvertable: {
                    Logger.debug("Item '" + node.getLabel() + "' identified as OO file");

                    if (OOConverter.shallNotBeUsed()) {
                        final JLabel previewNotAvailable = new JLabel(I18N.translate("LabelOOConverterNotAvailable"),
                                SwingConstants.CENTER);
                        previewNotAvailable.setOpaque(true);
                        previewNotAvailable.setBackground(Color.LIGHT_GRAY);
                        this.add(previewNotAvailable);
                        break;
                    }

                    try {
                        final String tempPDFFileName = String.format(TempPDFFileNameTemplate, UniqueID.getString());
                        OOConverter.convert2PDF(fileName, tempPDFFileName);

                        TempPDFFileNames.add(tempPDFFileName);
                        this.add(new PDFPreviewer(this, tempPDFFileName));
                    } catch (final Exception e) {
                        Logger.error(e.getMessage(), e);
                        this.add(new JScrollPane(createPreviewError(e.toString())));
                    }
                    break;
                }
                case GraphicsNative: {
                    Logger.debug("Item '" + node.getLabel() + "' identified as native graphics file");

                    this.add(new GraphicsPreviewer(this, new ImageIcon(fileName)));
                    break;
                }
                case GraphicsImageIO: {
                    Logger.debug("Item '" + node.getLabel() + "' identified as ImageIO graphics file");

                    try {
                        IIOImage img = EXIFUtilities.readWithOrientation(new File(fileName));
                        this.add(new GraphicsPreviewer(this, new ImageIcon((BufferedImage) img.getRenderedImage())));
                    } catch (final IOException e) {
                        Logger.error(e.getMessage(), e);
                        this.add(new JScrollPane(createPreviewError(e.toString())));
                    }
                    break;
                }
                case GraphicsJAIConvertableImageRead: {
                    Logger.debug("Item '" + node.getLabel() + "' identified as JAI graphics file");

                    try {
                        this.add(new GraphicsPreviewer(this, new ImageIcon(JAI.create("ImageRead", fileName)
                                .getAsBufferedImage())));
                    } catch (final Exception e) {
                        Logger.error(e.getMessage(), e);
                        this.add(new JScrollPane(createPreviewError(e.toString())));
                    }
                    break;
                }
                case GraphicsJIMIConvertable: {
                    Logger.debug("Item '" + node.getLabel() + "' identified as JIMI graphics file");

                    try {
                        this.add(new GraphicsPreviewer(this, new ImageIcon(Jimi.getImage(fileName))));
                    } catch (final Exception e) {
                        Logger.error(e.getMessage(), e);
                        this.add(new JScrollPane(createPreviewError(e.toString())));
                    }
                    break;
                }
                case GraphicsImageMagickConvertable: {
                    Logger.debug("Item '" + node.getLabel() + "' identified as ImageMagick graphics file");

                    try {
                        final String tempPNGFileName = String.format(TempPNGFileNameTemplate, UniqueID.getString());

                        // TODO: Do not use absolute path to executable, use migration-config.xml
                        final String[] commandLine = new String[] { "/opt/local/bin/convert", "-compress", "none",
                            fileName, new File(tempPNGFileName).getAbsolutePath() };
                        final int errorCode = SystemProcess.execute(commandLine);
                        if (errorCode != 0) {
                            throw new Exception("File conversion of '" + fileName +
                                    "' to PNG format returned error code: " + errorCode);
                        }

                        TempPNGFileNames.add(tempPNGFileName);
                        this.add(new GraphicsPreviewer(this, new ImageIcon(tempPNGFileName)));
                    } catch (final Exception e) {
                        Logger.error(e.getMessage(), e);
                        this.add(new JScrollPane(createPreviewError(e.toString())));
                    }
                    break;
                }
                case Unknown:
                default: {
                    Logger.debug("Item '" + node.getLabel() + "' could not identify type");

                    this.add(new UnknownFormatPreviewer(fileName));
                    break;
                }
            }
        } finally {
            validate();
            this.repaint();

            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public static FilePreviewPanel create(final NodeAbstract node, final JPanel container) {
        // If cache is turned off:
        if (CacheSizeLimit == 0) {
            return new FilePreviewPanel(node);
        }

        if (Cache.containsKey(node)) {
            // Cached:
            Logger.info("Cache hit: " + node.getLabel());

            // Remove this node from the CacheHitOrder list, wherever it is (it will be inserted at the beginning of
            // the list later):
            CacheHitOrder.remove(CacheHitOrder.indexOf(node));
        } else {
            // Not yet cached:
            if (CacheHitOrder.size() >= CacheSizeLimit) {
                // Cache limit size reached: remove "oldest" node:
                Logger.debug("Dropped from cache: " + CacheHitOrder.get(0).getLabel());

                Cache.remove(CacheHitOrder.get(0));
                CacheHitOrder.remove(0);
            }

            // Add node to the cache:
            Logger.debug("Insert into cache: " + node.getLabel());

            Cache.put(node, new FilePreviewPanel(node));
        }

        // Put this node at the beginning of the CacheHitOrder list:
        CacheHitOrder.add(node);

        Logger.debug("Cache size: " + Cache.size() + "/" + CacheSizeLimit);

        return Cache.get(node);
    }

    public static void setCacheSizeLimit(final int newCacheSizeLimit) {
        if (newCacheSizeLimit < 0) {
            throw new IllegalArgumentException("Cache size limit can not be set to a negative value");
        }

        Logger.debug("Setting FilePreviewer cache size to " + newCacheSizeLimit);

        if (newCacheSizeLimit == 0) {
            Cache.clear();
            CacheHitOrder.clear();
        } else if (newCacheSizeLimit < CacheSizeLimit) {
            // Remove "oldest" nodes:
            for (int i = 0; i < Math.min(CacheSizeLimit - newCacheSizeLimit, CacheHitOrder.size()); i++) {
                Cache.remove(CacheHitOrder.get(0));
                CacheHitOrder.remove(0);
            }
        }

        CacheSizeLimit = newCacheSizeLimit;
    }

    public static int getCacheSizeLimit() {
        return CacheSizeLimit;
    }

    public static void cleanupTemporaryFiles() {
        PDFPreviewer.cleanup();

        Logger.debug("PNGs to cleanup: " + TempPNGFileNames.size());
        for (final String s : TempPNGFileNames) {
            Logger.debug("Cleaning up: " + s);

            try {
                FileUtil.setWritable(s);
            } catch (final Throwable ex) {
                // TODO Auto-generated catch block
                Logger.error(ex.getMessage(), ex);
            }
            try {
                FileUtil.delete(s);
            } catch (final Throwable ex) {
                // TODO Auto-generated catch block
                Logger.error(ex.getMessage(), ex);
            }
        }

        Logger.debug("PDFs to cleanup: " + TempPDFFileNames.size());
        for (final String s : TempPDFFileNames) {
            Logger.debug("Cleaning up: " + s);

            try {
                FileUtil.setWritable(s);
            } catch (final Throwable ex) {
                // TODO Auto-generated catch block
                Logger.error(ex.getMessage(), ex);
            }
            try {
                FileUtil.delete(s);
            } catch (final Throwable ex) {
                // TODO Auto-generated catch block
                Logger.error(ex.getMessage(), ex);
            }
        }
    }

    private static JTextArea createPreviewError(final String text) {
        // I need this JTextArea only in case of an error:
        final JTextArea previewError = new JTextArea();
        previewError.setEditable(false);
        previewError.setLineWrap(true);
        previewError.setWrapStyleWord(true);
        previewError.setBackground(Color.YELLOW);
        previewError.setTabSize(4);
        previewError.setText(text);
        previewError.setCaretPosition(0);

        return previewError;
    }

    private static JTextArea createTextAreaWithFile(final String fileName) {
        final JTextArea previewText = new JTextArea();
        previewText.setEditable(false);
        previewText.setLineWrap(true);
        previewText.setWrapStyleWord(true);
        previewText.setTabSize(4);
        previewText.setText(FileUtil.getFileContentAsString(fileName));
        previewText.setCaretPosition(0);

        return previewText;
    }

    private static float zoomFactorToScaleProportionally(final int imageWidth, final int imageHeight,
            final int displayWidth, final int displayHeight) {
        return Math.min((float) displayWidth / imageWidth, (float) displayHeight / imageHeight);
    }

    /**
     * The coding of this class follows closely the example ViewerComponentExample.java which is distributed with the
     * ICEpdf library.
     *
     * @author andreas
     */
    private static class PDFPreviewer extends JPanel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        // I have to close all open PDFPreviewers, otherwise the files being displayed remain locked!
        private static List<PDFPreviewer> AllPDFPreviewers = new Vector<PDFPreviewer>(100);

        private final SwingController controller;

        private PDFPreviewer(final FilePreviewPanel parent, final String fileName) {
            super(new BorderLayout());
            AllPDFPreviewers.add(this);

            controller = new SwingController();
            controller.setIsEmbeddedComponent(true);

            final DocuteamPackerSwingViewBuilder factory = new DocuteamPackerSwingViewBuilder(controller);

            final JToolBar toolBar = factory.buildCompleteToolBar(true);

            if (toolBar != null) {
                this.add(toolBar, BorderLayout.NORTH);
            }

            this.add(controller.getDocumentViewController().getViewContainer(), BorderLayout.CENTER);

            final JPanel statusPanel = factory.buildStatusPanel();
            if (statusPanel != null) {
                this.add(statusPanel, BorderLayout.SOUTH);
            }

            controller.openDocument(fileName);
            // ToDo This somehow doesn't have the intended effect yet:
            controller.setPageFitMode(DocumentViewController.PAGE_FIT_ACTUAL_SIZE, true);
        }

        /**
         * The reason for using a subclass is to be able to use the sometimes private methods of its superclass.
         *
         * @author andreas
         */
        protected class DocuteamPackerSwingViewBuilder extends SwingViewBuilder {

            public DocuteamPackerSwingViewBuilder(final SwingController c) {
                super(c);
            }

            /**
             * This reduces the toolbar to the necessary components for the preview.
             */
            @Override
            public JToolBar buildCompleteToolBar(final boolean embeddableComponent) {
                JToolBar toolbar = new JToolBar();
                commonToolBarSetup(toolbar, true);

                // Build the main set of toolbars based on the property file configuration
                addToToolBar(toolbar, buildPageNavigationToolBar());
                addToToolBar(toolbar, buildZoomToolBar());
                addToToolBar(toolbar, buildFitToolBar());

                // Set the toolbar back to null if no components were added
                // The result of this will properly disable the necessary menu items for controlling the toolbar
                if (toolbar.getComponentCount() == 0) {
                    toolbar = null;
                }

                if (viewerController != null && toolbar != null) {
                    viewerController.setCompleteToolBar(toolbar);
                }

                return toolbar;
            }
        }

        /**
         * If I don't close and dispose all controllers, the PDF files being displayed remain locked!
         */
        public static void cleanup() {
            for (final PDFPreviewer p : AllPDFPreviewers) {
                p.controller.closeDocument();
                p.controller.dispose();
            }
        }
    }

    private static class GraphicsPreviewer extends JLabel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final FilePreviewPanel parent;

        private final ImageIcon previewGraphicsImageIconOriginalSize;

        private GraphicsPreviewer(final FilePreviewPanel parent, final ImageIcon imageIcon) {
            super();

            this.parent = parent;

            setVerticalAlignment(SwingConstants.CENTER);
            setHorizontalAlignment(SwingConstants.CENTER);
            addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    GraphicsPreviewer.this.wasResized();
                }
            });

            previewGraphicsImageIconOriginalSize = imageIcon;
        }

        private void wasResized() {
            setIcon(scaleProportionallyTo(previewGraphicsImageIconOriginalSize, parent.getWidth(), parent
                    .getHeight()));
            this.repaint();
        }

        private ImageIcon scaleProportionallyTo(final ImageIcon originalImageIcon, final int displayWidth,
                final int displayHeight) {
            final int imageWidth = originalImageIcon.getIconWidth();
            final int imageHeight = originalImageIcon.getIconHeight();

            final float zoomFactor = zoomFactorToScaleProportionally(imageWidth, imageHeight, displayWidth,
                    displayHeight);
            if (zoomFactor >= 1) {
                return originalImageIcon;
            }

            final int zoomedImageWidth = (int) (zoomFactor * imageWidth);
            final int zoomedImageHeight = (int) (zoomFactor * imageHeight);

            return new ImageIcon(originalImageIcon.getImage().getScaledInstance(zoomedImageWidth, zoomedImageHeight,
                    Image.SCALE_FAST));
        }
    }

    private static class UnknownFormatPreviewer extends JPanel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private UnknownFormatPreviewer(final String fileName) {
            super(new BorderLayout());

            final JButton previewNotAvailableButton = new JButton(I18N.translate("LabelPreviewNotAvailable"));
            previewNotAvailableButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UnknownFormatPreviewer.this.showFileContentsAsText(fileName);
                }
            });
            previewNotAvailableButton.setBorder(null);
            previewNotAvailableButton.setOpaque(true);
            previewNotAvailableButton.setHorizontalAlignment(SwingConstants.CENTER);
            previewNotAvailableButton.setBackground(Color.LIGHT_GRAY);

            this.add(previewNotAvailableButton, BorderLayout.CENTER);
        }

        private void showFileContentsAsText(final String fileName) {
            removeAll();
            this.add(new JScrollPane(createTextAreaWithFile(fileName)), BorderLayout.CENTER);
            validate(); // Re-render this view
        }
    }

}
