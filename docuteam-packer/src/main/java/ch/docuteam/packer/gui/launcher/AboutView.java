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

import static ch.docuteam.packer.gui.PackerConstants.*;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import ch.docuteam.converter.OOConverter;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.os.OperatingSystem;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.string.DateFormatter;
import ch.docuteam.tools.translations.I18N;
import ch.docuteam.packer.admin.BuildInfo;

public class AboutView extends JDialog {

	protected JEditorPane docuteamLink;
	protected JEditorPane licenseLink;

	public AboutView(JFrame owner, String title) {
		super(owner, title, true);

		this.setIconImage(getImage(PACKER_PNG));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getRootPane().registerKeyboardAction(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutView.this.close();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.docuteamLink = new JEditorPane("text/html",
				"<span style='font-family:Arial'><a href='http://www.docuteam.ch'>Docuteam GmbH</a></span>");
		this.docuteamLink.setEditable(false);
		this.docuteamLink.setOpaque(false);
		this.docuteamLink.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
					AboutView.this.openURL(hle.getURL());
				}
			}
		});

		this.licenseLink = new JEditorPane("text/html",
				"<span style='font-family:Arial'><a href='http://www.gnu.org/licenses'>GNU General Public License</a></span>");
		this.licenseLink.setEditable(false);
		this.licenseLink.setOpaque(false);
		this.licenseLink.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
					AboutView.this.openURL(hle.getURL());
				}
			}
		});
	}

	public AboutView(JFrame owner) {
		this(owner, I18N.translate("TitleAbout") + BuildInfo.getProduct());

		GridBagPanel gridBagPanel1 = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(5, 5, 5, 5));
		gridBagPanel1.add(new JLabel(getImageIcon("Logo_docuteam_packer.png")), 0, 0, 0, 2,
				GridBagConstraints.EAST);
		gridBagPanel1.add(new JLabel(BuildInfo.getProduct()), 1, 0, GridBagConstraints.WEST);
		gridBagPanel1.add(new JLabel(BuildInfo.getVersion()), 1, 1, GridBagConstraints.WEST);
		gridBagPanel1.add(new JLabel(BuildInfo.getLastChange()), 1, 2, GridBagConstraints.EAST);
		
		gridBagPanel1.add(new JLabel("Copyright (C) " + DateFormatter.getCurrentDateTimeString("yyyy") + " by: "), 5, 5,
				0, 1, GridBagConstraints.WEST);
		gridBagPanel1.add(this.docuteamLink, 5, 2, GridBagConstraints.EAST);
		gridBagPanel1.add(new JLabel("License: "), 6, 0, GridBagConstraints.WEST);
		gridBagPanel1.add(this.licenseLink, 6, 6, 1, 2, GridBagConstraints.EAST);
		gridBagPanel1.add(new JLabel("OS: "), 7, 0, GridBagConstraints.WEST);
		gridBagPanel1.add(new JLabel(OperatingSystem.osName() + " " + OperatingSystem.osVersion()), 7, 7, 1, 2,
				GridBagConstraints.EAST);
		gridBagPanel1.add(new JLabel("JVM: "), 8, 0, GridBagConstraints.WEST);
		gridBagPanel1.add(new JLabel(System.getProperty("java.vendor") + " " + System.getProperty("java.version")), 8,
				8, 1, 2, GridBagConstraints.EAST);

		GridBagPanel gridBagPanel2 = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(5, 5, 5, 5));
		gridBagPanel2.add(new JLabel("<html><b><u>OpenOffice Installations:</u></b></html>"), 0, 0, 0, 3,
				GridBagConstraints.CENTER);
		gridBagPanel2.add(new JLabel("Local:"), 2, 2, GridBagConstraints.CENTER);
		gridBagPanel2.add(new JLabel("Remote:"), 2, 3, GridBagConstraints.CENTER);
		gridBagPanel2.add(new JLabel("Windows:"), 3, 1, GridBagConstraints.EAST);
		gridBagPanel2.add(new JLabel(OOConverter.isInstalledLocallyForWindows() ? "X" : ""), 3, 2,
				GridBagConstraints.CENTER);
		gridBagPanel2.add(new JLabel(OOConverter.getRemotePathForWindows()), 3, 3,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 1, 0);
		gridBagPanel2.add(new JLabel("OS X:"), 4, 1, GridBagConstraints.EAST);
		gridBagPanel2.add(new JLabel(OOConverter.isInstalledLocallyForOSX() ? "X" : ""), 4, 2,
				GridBagConstraints.CENTER);
		gridBagPanel2.add(new JLabel(OOConverter.getRemotePathForOSX()), 4, 3, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, 1, 0);
		gridBagPanel2.add(new JLabel("Linux:"), 5, 1, GridBagConstraints.EAST);
		gridBagPanel2.add(new JLabel(OOConverter.isInstalledLocallyForLinux() ? "X" : ""), 5, 2,
				GridBagConstraints.CENTER);
		gridBagPanel2.add(new JLabel(OOConverter.getRemotePathForLinux()), 5, 3, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, 1, 0);

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(gridBagPanel1);
		box.add(gridBagPanel2);
		this.add(box);

		this.setPreferredSize(new Dimension(450, 500));
		this.setResizable(true);
		this.pack();
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	protected void openURL(URL url) {
		try {
			Desktop.getDesktop().browse(url.toURI());
		} catch (java.lang.Exception ex) {
			Logger.error(ex.getMessage(), ex);
		}
	}

	protected void close() {
		this.setVisible(false);
		this.dispose();
	}

}
