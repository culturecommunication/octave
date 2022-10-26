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

package ch.docuteam.packer.gui.sipView;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class ElementNamingDialog {

    public Object showInputDialog(final Component parentComponent, final Object message, final String title,
            final int messageType, final Icon icon,
            final Object[] selectionValues, final Object initialSelectionValue, final int width, final int height) {
        final JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.OK_CANCEL_OPTION, icon, null,
                null);

        pane.setWantsInput(true);
        pane.setSelectionValues(selectionValues);
        pane.setInitialSelectionValue(initialSelectionValue);
        pane.setComponentOrientation(parentComponent.getComponentOrientation());

        final JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setResizable(true);
        dialog.setSize(new Dimension(width, height));
        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        final String value = ((String) pane.getInputValue()).trim();

        return value.equals(JOptionPane.UNINITIALIZED_VALUE) ? null : value;
    }

}
