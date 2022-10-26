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
package ch.docuteam.packer.gui.filePreview;

import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.os.SystemProcess;
import ch.docuteam.tools.os.SystemProcessCantLaunchApplicationException;
import ch.docuteam.tools.translations.I18N;

/**
 * @author denis
 */
public class FilePreviewer extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JFrame filePreviewFrame;

    private final JPanel filePreviewPanelContainer;

    private final JButton openFileExternallyButton;

    private final JButton openFileSeparatelyButton;

    private NodeAbstract node;

    public FilePreviewer() {
        super(new BorderLayout());

        openFileExternallyButton = new JButton(I18N.translate("ButtonOpenFileExternally"),
                getImageIcon("View.png"));
        openFileExternallyButton.setEnabled(false);
        openFileExternallyButton.setToolTipText(I18N.translate("ToolTipOpenFileExternally"));
        openFileExternallyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                FilePreviewer.this.openFileExternallyButtonClicked(node);
            }
        });

        openFileSeparatelyButton = new JButton(I18N.translate("ButtonOpenFileSeparately"),
                getImageIcon("PreviewInWindow.png"));
        openFileSeparatelyButton.setEnabled(false);
        openFileSeparatelyButton.setToolTipText(I18N.translate("ToolTipOpenFileSeparately"));
        openFileSeparatelyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                FilePreviewer.this.openFileSeparatelyButtonClicked(node);
            }
        });

        filePreviewPanelContainer = new JPanel(new BorderLayout());
        filePreviewPanelContainer.add(new JPanel());

        final Box buttonBox = new Box(BoxLayout.X_AXIS);
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(openFileExternallyButton);
        buttonBox.add(openFileSeparatelyButton);
        buttonBox.add(Box.createHorizontalGlue());

        this.add(buttonBox, BorderLayout.NORTH);
        this.add(filePreviewPanelContainer, BorderLayout.CENTER);
    }

    public static void setCacheSizeLimit(final int cacheSizeLimit) {
        FilePreviewPanel.setCacheSizeLimit(cacheSizeLimit);
    }

    public static int getCacheSizeLimit() {
        return FilePreviewPanel.getCacheSizeLimit();
    }

    public void setNode(final NodeAbstract node) {
        this.node = node;

        filePreviewPanelContainer.removeAll();
        if (this.node != null) {
            filePreviewPanelContainer.add(FilePreviewPanel.create(node, this), BorderLayout.CENTER);
        }

        enableOrDisableButtons();
    }

    public boolean isPreviewInSeparateWindow() {
        return filePreviewFrame != null && filePreviewFrame.isVisible();
    }

    /**
     * This method is public so that it can be called from outside (e.g. by a double-click on a node).
     */
    public void openFileExternallyButtonClicked(final NodeAbstract node) {
        if (node == null) {
            return;
        }
        if (node.isFolder()) {
            return;
        }

        final String tempFileName = FileUtil.getTempFolder() + "/" + node.getFile().getName();
        try {
            if (!new File(tempFileName).exists()) {
                FileUtil.copyToOverwriting(node.getAbsolutePathString(), tempFileName);
                new File(tempFileName).setWritable(false);
            }

            SystemProcess.openExternally(tempFileName);
        } catch (final SystemProcessCantLaunchApplicationException e) {
            JOptionPane.showMessageDialog(this, I18N.translate("MessageCantOpenFileExternally"),
                    I18N.translate("TitleCantOpenFileExternally"), JOptionPane.ERROR_MESSAGE);
        } catch (final Exception e) {
            JOptionPane.showMessageDialog(this, e.toString(), I18N.translate("TitleCantOpenFileExternally"),
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            new File(tempFileName).deleteOnExit();
        }
    }

    @Override
    public void repaint() {
        if (isPreviewInSeparateWindow()) {
            // If the preview is in the separate window, repaint it:
            filePreviewFrame.validate();
            filePreviewFrame.repaint();
        } else {
            // Otherwise repaint me:
            super.repaint();
        }
    }

    private void openFileSeparatelyButtonClicked(final NodeAbstract node) {
        // Open a new JFrame containing the filePreviewPanelContainer:

        if (filePreviewFrame == null) {
            filePreviewFrame = new JFrame();
            filePreviewFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            filePreviewFrame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(final WindowEvent e) {
                    FilePreviewer.this.closeWindowButtonClicked();
                }
            });

            filePreviewFrame.setSize(new Dimension(800, 800));
            filePreviewFrame.setLocationRelativeTo(null);
        }

        // If it is already open, bring it to the front:
        if (filePreviewFrame.isVisible()) {
            filePreviewFrame.toFront();
            filePreviewFrame.requestFocus();
            return;
        }

        this.remove(filePreviewPanelContainer);
        validate();
        this.repaint();

        filePreviewFrame.add(filePreviewPanelContainer);
        filePreviewFrame.setVisible(true);
        filePreviewFrame.toFront();
        filePreviewFrame.requestFocus();
    }

    private void closeWindowButtonClicked() {
        // Close the separate JFrame and place the filePreviewPanelContainer
        // back into myself:

        filePreviewFrame.setVisible(false);
        filePreviewFrame.remove(filePreviewPanelContainer);

        this.add(filePreviewPanelContainer, BorderLayout.CENTER);
        validate();
        this.repaint();

        enableOrDisableButtons();
    }

    private void enableOrDisableButtons() {
        // If the current node is null, disable all buttons:
        if (node == null) {
            openFileExternallyButton.setEnabled(false);
            openFileSeparatelyButton.setEnabled(false);
            return;
        }

        // If the current node is a folder, disable the
        // "openFileExternallyButton":
        if (node.isFolder()) {
            openFileExternallyButton.setEnabled(false);
        } else {
            openFileExternallyButton.setEnabled(true);
        }

        openFileSeparatelyButton.setEnabled(true);
    }

    // Package visibility, must be seen by MainView:
}
