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

package ch.docuteam.packer.gui.sipView.actions;

import static ch.docuteam.packer.gui.PackerConstants.CHECKSUM_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.tools.exception.ExceptionCollector;
import ch.docuteam.tools.gui.ScrollableMessageDialog;
import ch.docuteam.tools.translations.I18N;

public class CheckFixityAction extends AbstractSIPViewAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CheckFixityAction(final SIPView sipView) {
        super(I18N.translate("ButtonCheckFixity"), getImageIcon(CHECKSUM_PNG), sipView);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (sipView.getDocument().checkFixity()) {
            JOptionPane.showMessageDialog(sipView, I18N.translate("MessageFixityCheckSuccessful"),
                    I18N.translate("HeaderFixityCheck"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            new ScrollableMessageDialog(sipView, I18N.translate("HeaderFixityCheck"),
                    I18N.translate("MessageFixityCheckFailed") + "\n" + ExceptionCollector.getAll());
        }
    }

    @Override
    public void enableOrDisable() {
        // always enabled
        setEnabled(true);
    }

}
