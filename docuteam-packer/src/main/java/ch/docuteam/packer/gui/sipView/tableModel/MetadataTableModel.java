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
package ch.docuteam.packer.gui.sipView.tableModel;

import static ch.docuteam.packer.gui.PackerConstants.*;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

import ch.docuteam.darc.exceptions.MetadataElementValidatorException;
import ch.docuteam.darc.mdconfig.CSVMetadataValue;
import ch.docuteam.darc.mdconfig.LevelMetadataElement;
import ch.docuteam.darc.mdconfig.MetadataElementInstance;
import ch.docuteam.darc.mdconfig.RDFMetadataValue;
import ch.docuteam.darc.mdconfig.MetadataElement.VALUE_TYPE;
import ch.docuteam.darc.mets.structmap.NodeAbstract;
import ch.docuteam.darc.util.KeyAndValue;
import ch.docuteam.packer.gui.sipView.SIPView;
import ch.docuteam.packer.gui.sipView.StyledComboBoxUI;
import ch.docuteam.packer.gui.sipView.TextAreaTableCellEditor;
import ch.docuteam.packer.gui.sipView.cellRenderer.UnfocusableCellRenderer;
import ch.docuteam.tools.gui.TableModelWithSpecificCellEditorPerRow;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

public class MetadataTableModel extends AbstractTableModel implements TableModelWithSpecificCellEditorPerRow {

	private SIPView sipView;

	private UnfocusableCellRenderer unfocusableCellRenderer = new UnfocusableCellRenderer();

	private TextAreaTableCellEditor textAreaRenderer = new TextAreaTableCellEditor();
	private TextAreaTableCellEditor textAreaEditor = new TextAreaTableCellEditor();
	private DefaultCellEditor comboBoxEditor = new DefaultCellEditor(new StyledComboBox());
	private TableCellEditor autocompleteEditor;
	

	public MetadataTableModel(SIPView sipView) {
		// Initialize the ComboBoxEditor:
		comboBoxEditor.setClickCountToStart(CLICK_COUNT_TO_START);
		this.sipView = sipView;
	}

	private NodeAbstract fileStructureNode;
	private List<MetadataElementInstance> metadataElementInstances;

	/**
	 * Return the specific tooltip text of this MetadataElementInstance
	 * 
	 * @param i
	 * @return
	 */
	public String getToolTipText(int i) {
		return metadataElementInstances.get(i).getToolTipText();
	}

	/**
	 * Return the MetadataElement at the list position i.
	 * 
	 * @param i
	 * @return
	 */
	public MetadataElementInstance get(int i) {
		return metadataElementInstances.get(i);
	}

	public void setFileStructureNode(NodeAbstract fileStructureNode) {
		if (fileStructureNode == null) {
			fileStructureNode = null;
			metadataElementInstances = null;
		} else {
			this.fileStructureNode = fileStructureNode;
			metadataElementInstances = fileStructureNode.getDynamicMetadataElementInstancesToBeDisplayed();
		}

		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		if (metadataElementInstances == null)
			return 0;

		return metadataElementInstances.size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return I18N.translate("HeaderMetadataAttributes");
		case 1:
			return I18N.translate("HeaderMetadataName");
		case 2:
			return I18N.translate("HeaderMetadataValue");
		}

		return null;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (metadataElementInstances == null)
			return null;

		MetadataElementInstance mdei = metadataElementInstances.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return mdei.getAttributesString();
		case 1:
			return I18N.translate(mdei.getName());
		case 2:
			return mdei.getDisplayValue();
		}

		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex != EDITABLE_COLUMN_INDEX)
			return false;

		if (!sipView.getDocument().canWrite())
			return false;

		if (!fileStructureNode.fileExists() || !fileStructureNode.canRead() || !fileStructureNode.canWrite())
			return false;

		if (!fileStructureNode.doesSubmitStatusAllowEditing())
			return false;

		if (metadataElementInstances == null)
			return false;

		if (!metadataElementInstances.get(rowIndex).isReadOnly())
			return true;

		return false;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (metadataElementInstances == null)
			return;
		if (columnIndex != EDITABLE_COLUMN_INDEX)
			return;

		try {
			if (value == null || value.toString().trim().isEmpty()) {
			    //remove element if value is null
			    metadataElementInstances.get(rowIndex).deleteMetadataElementInstanceFromNode();
			    fireTableDataChanged();
			    return;
			}
				
			// There are 4 different types possible for value: String, KeyAndValue, RDFMetadataValue and CSVMetadataValue:
			if (value.getClass() == KeyAndValue.class) {
				metadataElementInstances.get(rowIndex).setValue(((KeyAndValue) value).getOriginalString());
			} else if(value instanceof RDFMetadataValue) {
			    metadataElementInstances.get(rowIndex).setValue((RDFMetadataValue)value);
			}  else if(value instanceof CSVMetadataValue) {
                metadataElementInstances.get(rowIndex).setValue((CSVMetadataValue)value);
            }
			else {
				metadataElementInstances.get(rowIndex).setValue(value.toString());
			}
		} catch (MetadataElementValidatorException ex) {
			JOptionPane.showMessageDialog(sipView, ex.getMessage());
		} catch (Exception ex) {
			Logger.warn("setValueAt failed: ", ex);
		}

		fireTableDataChanged();
	}

	/**
	 * This method returns a specific cell editor, depending on the row and
	 * column: If the row contains a MDElement whose allowedValues attribute is
	 * not null, return a ComboBox containing the supplied values as the cell
	 * editor. Otherwise return null, meaning the default cell editor.
	 * 
	 * @param rowIndex
	 * @return
	 */
	@Override
	public TableCellEditor getCellEditor(int rowIndex, int columnIndex) {
		if (metadataElementInstances == null)
			return null;

		LevelMetadataElement lme = metadataElementInstances.get(rowIndex).getLevelMetadataElement();

		// If the levelMetadataElement has several allowedValues, the editor is
		// the ComboBoxEditor
		// (takes precedence over the TextAreaEditor):
		List<KeyAndValue> allowedValues = lme.getMetadataElement().getAllowedValues();
		if (allowedValues != null && !allowedValues.isEmpty()) {
			JComboBox comboBox = (JComboBox) comboBoxEditor.getComponent();

			comboBox.removeAllItems();
			for (KeyAndValue item : allowedValues)
				comboBox.addItem(item);

			// If the first item of the allowedValues list is a "*", make this
			// ComboBox editable:
			if (allowedValues.get(0).getOriginalString().equals("*")) {
				comboBox.removeItemAt(0);
				comboBox.setEditable(true);
			} else {
				comboBox.setEditable(false);
			}
			return comboBoxEditor;
		}
		
		autocompleteEditor = getAutocompleteEditor(lme);
		if(autocompleteEditor!=null) {
		    return autocompleteEditor;
		}

		// If the levelMetadataElement has a displayRows value >= 2, the editor
		// is a TextAreaTableCellEditor:
		int displayRows = lme.getDisplayRows();
		if (displayRows >= 2)
			return textAreaEditor;

		// Otherwise return null - this causes JTable to return the default cell
		// editor.
		return null;
	}

		
	private TableCellEditor getAutocompleteEditor(LevelMetadataElement lme) {
        if(lme.getMetadataElement().getValueType().equals(VALUE_TYPE.skosFile)) {
            List<RDFMetadataValue> allowedValues = lme.getMetadataElement().getRdfAllowedValues();
            return getAutocompleteEditor(lme, allowedValues);
        } else if(lme.getMetadataElement().getValueType().equals(VALUE_TYPE.csvFile)) {
            List<CSVMetadataValue> allowedValues = lme.getMetadataElement().getCsvAllowedValues();
            return getAutocompleteEditor(lme, allowedValues);
        }       
        return null;
    }
	
	/**
     * Uses an AutoCompleteDecorator.
     * s. http://repast.sourceforge.net/docs/api/repastjava/org/jdesktop/swingx/autocomplete/package-summary.html
     * 
     * @param lme
     * @return a ComboBoxCellEditor
     */
    private <T> TableCellEditor getAutocompleteEditor(LevelMetadataElement lme, List<T> allowedValues) {
        if (allowedValues == null) {
            Logger.error("getAutocompleteEditor - allowedValues is null");
        } else if (allowedValues.isEmpty()) {
            Logger.warn("getAutocompleteEditor - allowedValues is empty: " + lme.getMetadataElement().getAccessorName());
        } else {
            JComboBox<T> comboBox = createMetadataValueComboBox(allowedValues);
            AutoCompleteDecorator.decorate(comboBox);
    
            ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboBox);
            return comboBoxCellEditor;
        }
        return null;
    }

    private <T> JComboBox<T> createMetadataValueComboBox(List<T> allowedValues) {
        JComboBox<T> comboBox = new JComboBox<T>();
        comboBox.addItem(null);
        for (T item : allowedValues) {
            comboBox.addItem(item);
        }
        comboBox.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {
                // nothing to do                
            }
            @Override
            public void focusLost(FocusEvent arg0) {                
                cancelCellEditing();
            }                          
        });
        return comboBox;
    }
    
    private void cancelCellEditing() {        
        autocompleteEditor.cancelCellEditing();
    } 

    /**
	 * This method returns a specific cell renderer, depending on the row and
	 * column: If the row contains a MDElement whose allowedValues attribute is
	 * not null, return a ComboBox containing the supplied values as the cell
	 * editor. Otherwise return null, meaning the default cell editor.
	 * 
	 * @param rowIndex
	 * @return
	 */
	@Override
	public TableCellRenderer getCellRenderer(int rowIndex, int columnIndex) {
		if (metadataElementInstances == null)
			return null;

		// If the levelMetadataElement has a displayRows value >= 2, the
		// renderer is a TextAreaTableCellEditor:
		int displayRows = metadataElementInstances.get(rowIndex).getLevelMetadataElement().getDisplayRows();
		if (displayRows < 2) {
			return unfocusableCellRenderer;
		} else {
			return textAreaRenderer;
		}
	}

	class StyledComboBox extends JComboBox {
		public StyledComboBox() {
			setUI(new StyledComboBoxUI());
		}
	}
}
