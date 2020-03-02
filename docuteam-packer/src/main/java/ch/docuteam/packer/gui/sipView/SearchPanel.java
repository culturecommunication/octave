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

import static ch.docuteam.packer.gui.PackerConstants.CLEAR_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SEARCH_NEXT_PNG;
import static ch.docuteam.packer.gui.PackerConstants.SEARCH_PNG;
import static ch.docuteam.packer.gui.PackerConstants.*;
import static ch.docuteam.packer.gui.ComponentNames.*;

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

	private SIPView sipView;
	protected JTextField searchTextField;
	protected JTextField hitCountTextField;

	protected Action searchAction;
	protected Action clearSearchTextFieldAction;
	protected Action selectNextHitAction;
	protected Action selectPreviousHitAction;

	protected List<NodeAbstract> hits = new Vector<NodeAbstract>();
	protected int currentHitSelectionIndex = -1;

	protected SearchPanel(SIPView sipView) {
		super(new BorderLayout());
		this.sipView = sipView;
		this.searchTextField = new JTextField();
		searchTextField.setName(SIP_SEARCH_TEXT_FIELD);
		this.searchTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchPanel.this.searchButtonClicked();
			}
		});
		this.searchTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SearchPanel.this.enableOrDisableActions();
			}
		});
		this.searchTextField.setToolTipText(I18N.translate("ToolTipSearchTextField"));

		this.searchTextField.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				"SearchNext");
		this.searchTextField.getActionMap().put("SearchNext", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchPanel.this.selectNextButtonClicked();
			}
		});

		this.searchTextField.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				"SearchPrevious");
		this.searchTextField.getActionMap().put("SearchPrevious", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchPanel.this.selectPreviousButtonClicked();
			}
		});

		this.searchAction = new AbstractAction(I18N.translate("ActionSearch"), getImageIcon(SEARCH_PNG)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchPanel.this.searchButtonClicked();
			}
		};
		this.searchAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearch"));
		this.searchAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		this.clearSearchTextFieldAction = new AbstractAction(I18N.translate("ActionSearchClearTextField"),
				getImageIcon(CLEAR_PNG)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchPanel.this.clearSearchTextFieldButtonClicked();
			}
		};
		this.clearSearchTextFieldAction.putValue(Action.SHORT_DESCRIPTION,
				I18N.translate("ToolTipSearchClearTextField"));
		this.clearSearchTextFieldAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));

		this.selectNextHitAction = new AbstractAction(I18N.translate("ActionSearchSelectNext"),
				getImageIcon(SEARCH_NEXT_PNG)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchPanel.this.selectNextButtonClicked();
			}
		};
		this.selectNextHitAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchSelectNext"));
		this.selectNextHitAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK));

		this.selectPreviousHitAction = new AbstractAction(I18N.translate("ActionSearchSelectPrevious"),
				getImageIcon("SearchPrevious.png")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchPanel.this.selectPreviousButtonClicked();
			}
		};
		this.selectPreviousHitAction.putValue(Action.SHORT_DESCRIPTION, I18N.translate("ToolTipSearchSelectPrevious"));
		this.selectPreviousHitAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK));

		this.hitCountTextField = new JTextField();
		this.hitCountTextField.setEnabled(false);
		this.hitCountTextField.setColumns(4);

		JButton searchButton = new JButton(this.searchAction);
		searchButton.setName(SIP_SEARCH_BUTTON);
		searchButton.setHideActionText(true);
		JButton clearSearchTextFieldButton = new JButton(this.clearSearchTextFieldAction);
		clearSearchTextFieldButton.setName(SIP_CLEAR_SEARCH_BUTTON);
		clearSearchTextFieldButton.setHideActionText(true);
		JButton selectNextHitButton = new JButton(this.selectNextHitAction);
		selectNextHitButton.setName(SIP_SEARCH_NEXT_BUTTON);
		selectNextHitButton.setHideActionText(true);
		JButton selectPreviousHitButton = new JButton(this.selectPreviousHitAction);
		selectPreviousHitButton.setHideActionText(true);
		selectPreviousHitButton.setName(SIP_SEARCH_PREVIOUS_BUTTON);

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(searchButton);
		buttonBox.add(clearSearchTextFieldButton);
		buttonBox.add(selectNextHitButton);
		buttonBox.add(selectPreviousHitButton);
		buttonBox.add(this.hitCountTextField);

		this.add(this.searchTextField, BorderLayout.CENTER);
		this.add(buttonBox, BorderLayout.EAST);

		this.enableOrDisableActions();
	}

	private void searchButtonClicked() {
		if (this.searchTextField.getText().trim().isEmpty()) {
			this.hits.clear();
			this.currentHitSelectionIndex = -1;
		} else {
			this.hits = sipView.getDocument().searchForAllQuoted(this.searchTextField.getText());

			if (this.hits.isEmpty()) {
				this.currentHitSelectionIndex = -1;

				sipView.setFooterText(I18N.translate("MessageSearchNothingFound"), true);
			} else {
				this.currentHitSelectionIndex = 0;
				this.selectNode(this.hits.get(this.currentHitSelectionIndex));

				sipView.setFooterText(I18N.translate("MessageSearchFound", this.hits.size()));
			}
		}

		this.enableOrDisableActions();
	}

	private void clearSearchTextFieldButtonClicked() {
		this.searchTextField.setText("");
		this.searchButtonClicked();
	}

	private void selectNextButtonClicked() {
		if (this.hits.isEmpty())
			return;

		if (this.currentHitSelectionIndex < this.hits.size() - 1)
			++this.currentHitSelectionIndex;
		this.selectNode(this.hits.get(this.currentHitSelectionIndex));

		this.enableOrDisableActions();
	}

	private void selectPreviousButtonClicked() {
		if (this.hits.isEmpty())
			return;

		if (this.currentHitSelectionIndex > 0)
			--this.currentHitSelectionIndex;
		this.selectNode(this.hits.get(this.currentHitSelectionIndex));

		this.enableOrDisableActions();
	}

	private void selectNode(NodeAbstract node) {
		if (node == null)
			return;

		sipView.selectNode(node);
	}

	private void enableOrDisableActions() {
		if (this.searchTextField.getText().isEmpty()) {
			this.clearSearchTextFieldAction.setEnabled(false);
			this.searchAction.setEnabled(false);
		} else {
			this.clearSearchTextFieldAction.setEnabled(true);
			this.searchAction.setEnabled(true);
		}

		if (this.hits.isEmpty()) {
			this.selectNextHitAction.setEnabled(false);
			this.selectPreviousHitAction.setEnabled(false);
		} else {
			if (this.currentHitSelectionIndex > 0)
				this.selectPreviousHitAction.setEnabled(true);
			else
				this.selectPreviousHitAction.setEnabled(false);

			if (this.currentHitSelectionIndex < this.hits.size() - 1)
				this.selectNextHitAction.setEnabled(true);
			else
				this.selectNextHitAction.setEnabled(false);
		}

		this.updateHitCountTextField();
	}

	private void updateHitCountTextField() {
		this.hitCountTextField.setText((this.currentHitSelectionIndex + 1) + "/" + this.hits.size());
	}

}
