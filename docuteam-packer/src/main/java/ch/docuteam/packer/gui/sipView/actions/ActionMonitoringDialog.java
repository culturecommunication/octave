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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import ch.docuteam.packer.gui.util.CancellableOperation;
import ch.docuteam.tools.translations.I18N;

/**
 * Dialog window for displaying intermediary results a long operation. It offers the possibility to interrupt the
 * operation if wanted or simply close the dialog window after operation was canceled / terminated. Note: operation
 * canceling effect depends on how interface CancellableOperation is implemented.
 *
 * @author d.petric
 */
class ActionMonitoringDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private CancellableOperation operation;

    private final JTextArea textArea;

    private final JButton cancelButton;

    private final JButton closeButton;

    public void appendMessage(final String message) {
        textArea.append(message);
    }

    public void init() {
        textArea.setText(null);
        cancelButton.setVisible(true);
        closeButton.setVisible(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    public ActionMonitoringDialog(final JFrame parent, final String title) {
        super(parent, title, ModalityType.APPLICATION_MODAL);

        setLocationRelativeTo(parent);
        final JPanel messagePane = new JPanel();
        messagePane.setLayout(new BoxLayout(messagePane, BoxLayout.Y_AXIS));

        textArea = new JTextArea(20, 100);
        textArea.setEditable(false);
        final JScrollPane scrollPane = new JScrollPane(textArea);

        messagePane.add(scrollPane);
        getContentPane().add(messagePane);

        final JPanel buttonPane = new JPanel();
        cancelButton = new JButton(I18N.translate("ButtonCancel"));
        closeButton = new JButton(I18N.translate("ButtonClose"));

        buttonPane.add(closeButton);
        buttonPane.add(cancelButton);

        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (operation != null) {
                    operation.cancelOperation();
                }
            }
        });
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        getContentPane().add(buttonPane, BorderLayout.PAGE_END);

        init();
        pack();
    }

    public void setOver() {
        cancelButton.setVisible(false);
        closeButton.setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void setOperation(final CancellableOperation listener) {
        operation = listener;
    }
}