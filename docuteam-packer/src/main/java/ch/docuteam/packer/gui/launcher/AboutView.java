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

import static ch.docuteam.packer.gui.PackerConstants.PACKER_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImage;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

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
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import ch.docuteam.packer.admin.BuildInfo;
import ch.docuteam.tools.gui.GridBagPanel;
import ch.docuteam.tools.os.OperatingSystem;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.string.DateFormatter;
import ch.docuteam.tools.translations.I18N;

public class AboutView extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected JEditorPane docuteamLink;

    protected JEditorPane licenseLink;

    public AboutView(final JFrame owner, final String title) {
        super(owner, title, true);

        setIconImage(getImage(PACKER_PNG));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                AboutView.this.close();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        docuteamLink = new JEditorPane("text/html",
                "<span style='font-family:Arial'><a href='http://www.docuteam.ch'>Docuteam GmbH</a></span>");
        docuteamLink.setEditable(false);
        docuteamLink.setOpaque(false);
        docuteamLink.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(final HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    AboutView.this.openURL(hle.getURL());
                }
            }
        });

        licenseLink = new JEditorPane("text/html",
                "<span style='font-family:Arial'><a href='http://www.gnu.org/licenses'>GNU General Public License</a></span>");
        licenseLink.setEditable(false);
        licenseLink.setOpaque(false);
        licenseLink.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(final HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    AboutView.this.openURL(hle.getURL());
                }
            }
        });
    }

    public AboutView(final JFrame owner) {
        this(owner, I18N.translate("TitleAbout") + BuildInfo.getProduct());

        final GridBagPanel gridBagPanel1 = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(5, 5, 5, 5));
        gridBagPanel1.add(new JLabel(getImageIcon("Logo_docuteam_packer.png")), 0, 0, 0, 2,
                GridBagConstraints.EAST);
        gridBagPanel1.add(new JLabel(BuildInfo.getProduct()), 1, 0, GridBagConstraints.WEST);
        gridBagPanel1.add(new JLabel(BuildInfo.getVersion()), 1, 1, GridBagConstraints.WEST);
        gridBagPanel1.add(new JLabel(BuildInfo.getLastChange()), 1, 2, GridBagConstraints.EAST);

        gridBagPanel1.add(new JLabel("Copyright (C) " + DateFormatter.getCurrentDateTimeString("yyyy") + " by: "), 5,
                5,
                0, 1, GridBagConstraints.WEST);
        gridBagPanel1.add(docuteamLink, 5, 2, GridBagConstraints.EAST);
        gridBagPanel1.add(new JLabel("License: "), 6, 0, GridBagConstraints.WEST);
        gridBagPanel1.add(licenseLink, 6, 6, 1, 2, GridBagConstraints.EAST);
        gridBagPanel1.add(new JLabel("OS: "), 7, 0, GridBagConstraints.WEST);
        gridBagPanel1.add(new JLabel(OperatingSystem.osName() + " " + OperatingSystem.osVersion()), 7, 7, 1, 2,
                GridBagConstraints.EAST);
        gridBagPanel1.add(new JLabel("JVM: "), 8, 0, GridBagConstraints.WEST);
        gridBagPanel1.add(new JLabel(System.getProperty("java.vendor") + " " + System.getProperty("java.version")), 8,
                8, 1, 2, GridBagConstraints.EAST);

        final GridBagPanel gridBagPanel2 = new GridBagPanel(new EmptyBorder(10, 10, 10, 10), new Insets(5, 5, 5, 5));
        gridBagPanel2.add(new JLabel("<html><b><u>OpenOffice Installations:</u></b></html>"), 0, 0, 0, 3,
                GridBagConstraints.CENTER);
        gridBagPanel2.add(new JLabel("Local:"), 2, 2, GridBagConstraints.CENTER);
        gridBagPanel2.add(new JLabel("Remote:"), 2, 3, GridBagConstraints.CENTER);
        gridBagPanel2.add(new JLabel("Windows:"), 3, 1, GridBagConstraints.EAST);

        final Box box = new Box(BoxLayout.Y_AXIS);
        box.add(gridBagPanel1);
        box.add(gridBagPanel2);
        this.add(box);

        setPreferredSize(new Dimension(450, 500));
        setResizable(true);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    protected void openURL(final URL url) {
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (final java.lang.Exception ex) {
            Logger.error(ex.getMessage(), ex);
        }
    }

    protected void close() {
        setVisible(false);
        dispose();
    }

}
