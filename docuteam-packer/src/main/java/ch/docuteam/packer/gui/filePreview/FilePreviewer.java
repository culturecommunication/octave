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

import static ch.docuteam.packer.gui.PackerConstants.*;

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

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.tools.file.FileUtil;
import ch.docuteam.tools.os.SystemProcess;
import ch.docuteam.tools.os.SystemProcessCantLaunchApplicationException;
import ch.docuteam.tools.translations.I18N;

/**
 * @author denis
 *
 */
public class FilePreviewer extends JPanel {

	private JFrame filePreviewFrame;
	private JPanel filePreviewPanelContainer;

	private JButton openFileExternallyButton;
	private JButton openFileSeparatelyButton;

	private NodeAbstract node;

	public FilePreviewer() {
		super(new BorderLayout());

		this.openFileExternallyButton = new JButton(I18N.translate("ButtonOpenFileExternally"),
				getImageIcon("View.png"));
		this.openFileExternallyButton.setEnabled(false);
		this.openFileExternallyButton.setToolTipText(I18N.translate("ToolTipOpenFileExternally"));
		this.openFileExternallyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FilePreviewer.this.openFileExternallyButtonClicked(FilePreviewer.this.node);
			}
		});

		this.openFileSeparatelyButton = new JButton(I18N.translate("ButtonOpenFileSeparately"),
				getImageIcon("PreviewInWindow.png"));
		this.openFileSeparatelyButton.setEnabled(false);
		this.openFileSeparatelyButton.setToolTipText(I18N.translate("ToolTipOpenFileSeparately"));
		this.openFileSeparatelyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FilePreviewer.this.openFileSeparatelyButtonClicked(FilePreviewer.this.node);
			}
		});

		this.filePreviewPanelContainer = new JPanel(new BorderLayout());
		this.filePreviewPanelContainer.add(new JPanel());

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(this.openFileExternallyButton);
		buttonBox.add(this.openFileSeparatelyButton);
		buttonBox.add(Box.createHorizontalGlue());

		this.add(buttonBox, BorderLayout.NORTH);
		this.add(this.filePreviewPanelContainer, BorderLayout.CENTER);
	}

	public static void setCacheSizeLimit(int cacheSizeLimit) {
		FilePreviewPanel.setCacheSizeLimit(cacheSizeLimit);
	}

	public static int getCacheSizeLimit() {
		return FilePreviewPanel.getCacheSizeLimit();
	}

	public void setNode(NodeAbstract node) {
		this.node = node;

		this.filePreviewPanelContainer.removeAll();
		if (this.node != null)
			this.filePreviewPanelContainer.add(FilePreviewPanel.create(node, this), BorderLayout.CENTER);

		this.enableOrDisableButtons();
	}

	public boolean isPreviewInSeparateWindow() {
		return this.filePreviewFrame != null && this.filePreviewFrame.isVisible();
	}

	/**
	 * This method is public so that it can be called from outside (e.g. by a
	 * double-click on a node).
	 */
	public void openFileExternallyButtonClicked(NodeAbstract node) {
		if (node == null)
			return;
		if (node.isFolder())
			return;

		String tempFileName = FileUtil.getTempFolder() + "/" + node.getFile().getName();
		try {
			if (!new File(tempFileName).exists()) {
				FileUtil.copyToOverwriting(node.getAbsolutePathString(), tempFileName);
				new File(tempFileName).setWritable(false);
			}

			SystemProcess.openExternally(tempFileName);
		} catch (SystemProcessCantLaunchApplicationException e) {
			JOptionPane.showMessageDialog(this, I18N.translate("MessageCantOpenFileExternally"),
					I18N.translate("TitleCantOpenFileExternally"), JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.toString(), I18N.translate("TitleCantOpenFileExternally"),
					JOptionPane.ERROR_MESSAGE);
		} finally {
			new File(tempFileName).deleteOnExit();
		}
	}

	@Override
	public void repaint() {
		if (this.isPreviewInSeparateWindow()) {
			// If the preview is in the separate window, repaint it:
			this.filePreviewFrame.validate();
			this.filePreviewFrame.repaint();
		} else {
			// Otherwise repaint me:
			super.repaint();
		}
	}

	private void openFileSeparatelyButtonClicked(NodeAbstract node) {
		// Open a new JFrame containing the filePreviewPanelContainer:

		if (this.filePreviewFrame == null) {
			this.filePreviewFrame = new JFrame();
			this.filePreviewFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.filePreviewFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					FilePreviewer.this.closeWindowButtonClicked();
				}
			});

			this.filePreviewFrame.setSize(new Dimension(800, 800));
			this.filePreviewFrame.setLocationRelativeTo(null);
		}

		// If it is already open, bring it to the front:
		if (this.filePreviewFrame.isVisible()) {
			this.filePreviewFrame.toFront();
			this.filePreviewFrame.requestFocus();
			return;
		}

		this.remove(this.filePreviewPanelContainer);
		this.validate();
		this.repaint();

		this.filePreviewFrame.add(this.filePreviewPanelContainer);
		this.filePreviewFrame.setVisible(true);
		this.filePreviewFrame.toFront();
		this.filePreviewFrame.requestFocus();
	}

	private void closeWindowButtonClicked() {
		// Close the separate JFrame and place the filePreviewPanelContainer
		// back into myself:

		this.filePreviewFrame.setVisible(false);
		this.filePreviewFrame.remove(this.filePreviewPanelContainer);

		this.add(this.filePreviewPanelContainer, BorderLayout.CENTER);
		this.validate();
		this.repaint();

		this.enableOrDisableButtons();
	}

	private void enableOrDisableButtons() {
		// If the current node is null, disable all buttons:
		if (this.node == null) {
			this.openFileExternallyButton.setEnabled(false);
			this.openFileSeparatelyButton.setEnabled(false);
			return;
		}

		// If the current node is a folder, disable the
		// "openFileExternallyButton":
		if (this.node.isFolder())
			this.openFileExternallyButton.setEnabled(false);
		else
			this.openFileExternallyButton.setEnabled(true);

		this.openFileSeparatelyButton.setEnabled(true);
	}

	// Package visibility, must be seen by MainView:
}
