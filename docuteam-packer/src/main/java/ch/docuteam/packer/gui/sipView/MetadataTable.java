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

import static ch.docuteam.packer.gui.PackerConstants.URL_PATTERN;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;

import ch.docuteam.packer.gui.sipView.tableModel.MetadataTableModel;
import ch.docuteam.tools.file.exception.FileIsNotADirectoryException;
import ch.docuteam.tools.gui.JTableWithSpecificCellEditorPerRow;
import ch.docuteam.tools.os.SystemProcess;
import ch.docuteam.tools.os.SystemProcessCantLaunchApplicationException;
import ch.docuteam.tools.os.SystemProcessException;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

// TODO what is this class for
public class MetadataTable extends JTableWithSpecificCellEditorPerRow {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final int FontSizeMagicNumber = 5;

    private final Font Font = (Font) UIManager.get("Table.font");

    private final int FontSize = Font.getSize() + FontSizeMagicNumber;

    public MetadataTable(final MetadataTableModel tableModel, final int... toolTipTextColumnIndexes) {
        super(tableModel, toolTipTextColumnIndexes);

        // listen for ctrl-click on the table and – in case the cell contains an
        // url – open it
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.isControlDown()) {
                    final MetadataTable target = (MetadataTable) e.getSource();
                    final String cellValue = target.getModel().getValueAt(target.rowAtPoint(e.getPoint()), target
                            .columnAtPoint(e.getPoint())).toString();
                    if (cellValue.matches(URL_PATTERN)) {
                        try {
                            SystemProcess.openExternally(cellValue);
                        } catch (IOException | InterruptedException | SystemProcessException |
                                FileIsNotADirectoryException | URISyntaxException |
                                SystemProcessCantLaunchApplicationException ex) {
                            Logger.error("Could not open URL", ex);
                        }
                    }
                }
            }
        });

        // Disallow rearranging of columns:
        getTableHeader().setReorderingAllowed(false);
    }

    @Override
    public String getToolTipText(final MouseEvent e) {
        switch (columnAtPoint(e.getPoint())) {
            case 0:
                return I18N.translate("ToolTipDynamicMetadataTypes");
            case 1:
                return ((MetadataTableModel) dataModel).getToolTipText(rowAtPoint(e.getPoint()));
        }

        // Otherwise: show the content of the table cell as tooltip text:
        return super.getToolTipText(e);
    }

    @Override
    public void tableChanged(final TableModelEvent e) {
        super.tableChanged(e);
        final MetadataTableModel model = (MetadataTableModel) e.getSource();
        for (int i = 0; i < model.getRowCount(); i++) {
            int rows = model.get(i).getLevelMetadataElement().getDisplayRows();
            if (rows == 0) {
                rows = 1;
            }
            final int requiredRowHeight = FontSize * rows;
            if (requiredRowHeight > 0 && requiredRowHeight != getRowHeight(i)) {
                setRowHeight(i, requiredRowHeight);
            }
        }
    }

}