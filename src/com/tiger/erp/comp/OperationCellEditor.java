package com.tiger.erp.comp;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.tiger.erp.util.UIUtils;

public class OperationCellEditor extends AbstractCellEditor implements TableCellEditor {

    private ActionListener ale;
    
    private ActionListener alv;
    
    private ActionListener ald;
    
    
    public OperationCellEditor(ActionListener ale,ActionListener alv,ActionListener ald) {
        this.ale = ale;
        this.alv = alv;
        this.ald = ald;
    }
    
    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return UIUtils.createOperationPanel(ale,alv,ald);
    }

}
