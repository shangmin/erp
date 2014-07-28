package com.tiger.erp.comp;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.tiger.erp.util.UIUtils;

public class OperationCellRenderer implements TableCellRenderer {

    public OperationCellRenderer() {
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        return UIUtils.createOperationPanel(null,null,null);
    }
}
