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
package ch.docuteam.packer.assertj.swing;


import static org.assertj.swing.timing.Pause.pause;

import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.assertj.swing.core.Robot;
import org.assertj.swing.driver.ComponentDriver;
import org.assertj.swing.fixture.AbstractComponentFixture;
import org.jdesktop.swingx.JXTreeTable;

import ch.docuteam.tools.out.Logger;

public class JXTreeTableComponentFixture extends AbstractComponentFixture<JXTreeTableComponentFixture, JXTreeTable, ComponentDriver> {

	private JXTreeTable tree;
	
	public JXTreeTableComponentFixture(Class<JXTreeTableComponentFixture> selfType, Robot robot, JXTreeTable target) {
		super(selfType, robot, target);
		tree = (JXTreeTable)target;		
	}

	@Override
	protected ComponentDriver createDriver(Robot robot) {		
		return new ComponentDriver(robot);
	}
	
	/**
	 * Column index is 0.
	 * @param rowIndex
	 */
	public void changeSelection(int rowIndex) {
		tree.changeSelection(rowIndex, 0, true, false);
	}
	
	/**
	 * 
	 * @return rowIndex
	 */
	public int getSelectedRow() {
		return tree.getSelectedRow();
	}
	
	/**
	 * Column index is 0.
	 * @return selected row value as string
	 */
	public String getSelectedRowValue() {
		return tree.getModel().getValueAt(tree.getSelectedRow(), 0).toString();		
	}
	
	/**
	 * Column index is 0.
	 * @param rowValue
	 * @return rowIndex of the first row with the specified value, or -1 if none found
	 */
	public int getTreeRow(String rowValue) {		
		TableModel tableModel = tree.getModel();
		for(int i=0; i<tableModel.getRowCount(); i++) {
			String value = tree.getModel().getValueAt(i, 0).toString();
			if(rowValue.equals(value)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * //TODO: doesn't work
	 * @param path the array uniquely identifies the path to a node.
	 * @return
	 */
	public boolean isExpanded(Object[] path) {
		//Object[] path = {"folder", "subfolder1", "subfolder_same_name"};
		TreePath treePath = new TreePath(path);
		Logger.info("treePath: " + tree.getPathForRow(3)); 
		return tree.isExpanded(treePath);
	}
	
	public boolean isExpanded(int rowIndex) {
		return tree.isExpanded(rowIndex);
	}
	
	/**
	 * //TODO: doesn't work
	 * @param path
	 * @return
	 */
	public boolean isVisible(Object[] path) {
		TreePath treePath = new TreePath(path);
		return tree.isVisible(treePath);
		
	}
	
	public boolean findTreePathAfterExpandAll(int rowIndex) {
	    tree.expandAll();	    
	    pause(1000);
	    
	    TreePath treePath = tree.getPathForRow(rowIndex);
	    Logger.info(treePath);
	    return tree.isVisible(treePath);
	}
}
