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

package ch.docuteam.packer.gui.sipView.cellRenderer;

import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import javax.swing.table.DefaultTableCellRenderer;

import ch.docuteam.darc.mets.structmap.NodeAbstract.SubmitStatus;
import ch.docuteam.tools.translations.I18N;

public class SubmitStatusTableCellRenderer extends DefaultTableCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void setValue(final Object object) {
        if (object == null) {
            return;
        }

        final SubmitStatus submitStatus = (SubmitStatus) object;
        switch (submitStatus) {
            case SubmitUndefined:
                setIcon(null);
                break;
            case SubmitRequested:
                setIcon(getImageIcon("SubmitRequestedFlag.png"));
                break;
            case SubmitRequestPending:
                setIcon(getImageIcon("SubmitRequestPendingFlag.png"));
                break;
            case SubmitFailed:
                setIcon(getImageIcon("SubmitFailedFlag.png"));
                break;
            case Submitted:
                setIcon(getImageIcon("SubmittedFlag.png"));
                break;
            default:
                setIcon(null);
                break;
        }

        setToolTipText(I18N.translate("HeaderPropertiesSubmitStatus") + " " + I18N.translate(submitStatus
                .toString()));
    }
}
