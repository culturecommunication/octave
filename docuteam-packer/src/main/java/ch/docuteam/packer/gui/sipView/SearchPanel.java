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

import static ch.docuteam.packer.gui.ComponentNames.SIP_CLEAR_SEARCH_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SEARCH_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SEARCH_NEXT_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SEARCH_PREVIOUS_BUTTON;
import static ch.docuteam.packer.gui.ComponentNames.SIP_SEARCH_TEXT_FIELD;
import static ch.docuteam.packer.gui.PackerConstants.CLEAR_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SEARCH_NEXT_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SEARCH_PNG;
import static ch.docuteam.packer.gui.PackerConstants.getImageIcon;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.tools.translations.I18N;

public class SearchPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final SIPView sipView;

    protected JTextField searchTextField;

    protected JTextField hitCountTextField;

    protected Action searchAction;

    protected Action clearSearchTextFieldAction;

    protected Action selectNextHitAction;

    protected Action selectPreviousHitAction;

    protected List<NodeAbstract> hits = new Vector<NodeAbstract>();

    protected int currentHitSelectionIndex = -1;

    protected SearchPanel(final SIPView sipView) {
        super(new BorderLayout());
        this.sipView = sipView;
        searchTextField = new JTextField();
        searchTextField.setName(SIP_SEARCH_TEXT_FIELD);
        searchTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                SearchPanel.this.searchButtonClicked();
            }
        });
        searchTextField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(final KeyEvent e) {
                SearchPanel.this.enableOrDisableActions();
            }
        });
        searchTextField.setToolTipText(I18N.translate("ToolTipSearchTextField"));

        searchTextField.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                "SearchNext");
        searchTextField.getActionMap().put("SearchNext", new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                SearchPanel.this.selectNextButtonClicked();
            }
        });

        searchTextField.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                "SearchPrevious");
        searchTextField.getActionMap().put("SearchPrevious", new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                SearchPanel.this.selectPreviousButtonClicked();
            }
        });

        searchAction = new AbstractAction(I18N.translate("ActionSearch"), getImageIcon(SEARCH_PNG)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                SearchPanel.this.searchButtonClicked();
            }
        };
        searchAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearch"));
        searchAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        clearSearchTextFieldAction = new AbstractAction(I18N.translate("ActionSearchClearTextField"),
                getImageIcon(CLEAR_PNG)) {

            /**
                     *
                     */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                SearchPanel.this.clearSearchTextFieldButtonClicked();
            }
        };
        clearSearchTextFieldAction.putValue(Action.SHORT_DESCRIPTION,
                I18N.translate("ToolTipSearchClearTextField"));
        clearSearchTextFieldAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));

        selectNextHitAction = new AbstractAction(I18N.translate("ActionSearchSelectNext"),
                getImageIcon(SEARCH_NEXT_PNG)) {

            /**
                     *
                     */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                SearchPanel.this.selectNextButtonClicked();
            }
        };
        selectNextHitAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchSelectNext"));
        selectNextHitAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK));

        selectPreviousHitAction = new AbstractAction(I18N.translate("ActionSearchSelectPrevious"),
                getImageIcon("SearchPrevious.png")) {

            /**
                     *
                     */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                SearchPanel.this.selectPreviousButtonClicked();
            }
        };
        selectPreviousHitAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchSelectPrevious"));
        selectPreviousHitAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK));

        hitCountTextField = new JTextField();
        hitCountTextField.setEnabled(false);
        hitCountTextField.setColumns(4);

        final JButton searchButton = new JButton(searchAction);
        searchButton.setName(SIP_SEARCH_BUTTON);
        searchButton.setHideActionText(true);
        final JButton clearSearchTextFieldButton = new JButton(clearSearchTextFieldAction);
        clearSearchTextFieldButton.setName(SIP_CLEAR_SEARCH_BUTTON);
        clearSearchTextFieldButton.setHideActionText(true);
        final JButton selectNextHitButton = new JButton(selectNextHitAction);
        selectNextHitButton.setName(SIP_SEARCH_NEXT_BUTTON);
        selectNextHitButton.setHideActionText(true);
        final JButton selectPreviousHitButton = new JButton(selectPreviousHitAction);
        selectPreviousHitButton.setHideActionText(true);
        selectPreviousHitButton.setName(SIP_SEARCH_PREVIOUS_BUTTON);

        final Box buttonBox = new Box(BoxLayout.X_AXIS);
        buttonBox.add(searchButton);
        buttonBox.add(clearSearchTextFieldButton);
        buttonBox.add(selectNextHitButton);
        buttonBox.add(selectPreviousHitButton);
        buttonBox.add(hitCountTextField);

        this.add(searchTextField, BorderLayout.CENTER);
        this.add(buttonBox, BorderLayout.EAST);

        enableOrDisableActions();
    }

    private void searchButtonClicked() {
        if (searchTextField.getText().trim().isEmpty()) {
            hits.clear();
            currentHitSelectionIndex = -1;
        } else {
            hits = sipView.getDocument().searchForAllQuoted(searchTextField.getText());

            if (hits.isEmpty()) {
                currentHitSelectionIndex = -1;

                sipView.setFooterText(I18N.translate("MessageSearchNothingFound"), true);
            } else {
                currentHitSelectionIndex = 0;
                selectNode(hits.get(currentHitSelectionIndex));

                sipView.setFooterText(I18N.translate("MessageSearchFound", hits.size()));
            }
        }

        enableOrDisableActions();
    }

    private void clearSearchTextFieldButtonClicked() {
        searchTextField.setText("");
        searchButtonClicked();
    }

    private void selectNextButtonClicked() {
        if (hits.isEmpty()) {
            return;
        }

        if (currentHitSelectionIndex < hits.size() - 1) {
            ++currentHitSelectionIndex;
        }
        selectNode(hits.get(currentHitSelectionIndex));

        enableOrDisableActions();
    }

    private void selectPreviousButtonClicked() {
        if (hits.isEmpty()) {
            return;
        }

        if (currentHitSelectionIndex > 0) {
            --currentHitSelectionIndex;
        }
        selectNode(hits.get(currentHitSelectionIndex));

        enableOrDisableActions();
    }

    private void selectNode(final NodeAbstract node) {
        if (node == null) {
            return;
        }

        sipView.selectNode(node);
    }

    private void enableOrDisableActions() {
        if (searchTextField.getText().isEmpty()) {
            clearSearchTextFieldAction.setEnabled(false);
            searchAction.setEnabled(false);
        } else {
            clearSearchTextFieldAction.setEnabled(true);
            searchAction.setEnabled(true);
        }

        if (hits.isEmpty()) {
            selectNextHitAction.setEnabled(false);
            selectPreviousHitAction.setEnabled(false);
        } else {
            if (currentHitSelectionIndex > 0) {
                selectPreviousHitAction.setEnabled(true);
            } else {
                selectPreviousHitAction.setEnabled(false);
            }

            if (currentHitSelectionIndex < hits.size() - 1) {
                selectNextHitAction.setEnabled(true);
            } else {
                selectNextHitAction.setEnabled(false);
            }
        }

        updateHitCountTextField();
    }

    private void updateHitCountTextField() {
        hitCountTextField.setText(currentHitSelectionIndex + 1 + "/" + hits.size());
    }

}
