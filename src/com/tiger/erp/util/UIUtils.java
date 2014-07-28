package com.tiger.erp.util;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.jdatepicker.JDateComponentFactory;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import com.tiger.erp.common.TableModelRefresh;
import com.tiger.erp.comp.OperationCellEditor;
import com.tiger.erp.comp.OperationCellRenderer;

public class UIUtils {

    public static final int ROW_HIGHT = 30;
    public static final int PAGE_HIGHT = 25;
    
    static ClassLoader  classLoader =  UIUtils.class.getClassLoader();

    public static URL getURL(String path) {
        return classLoader.getResource(path);
    }

    public static ImageIcon createIcon(String path) {
        return new ImageIcon(getURL("images/" + path));
    }

    public static JPanel createSaveButtonPanel(ActionListener als, ActionListener alc) {
        JPanel jp = new JPanel();
        jp.setLayout(null);
        JButton js =  createSaveButton(als);
        js.setBounds(150, 0, 100, ROW_HIGHT);
        jp.add(js);
        JButton jc = createCancelButton(alc);
        jc.setBounds(300, 0, 100, ROW_HIGHT);
        jp.add(jc);
        return jp;
    }
    
    public static JPanel createSearchButtonPanel(ActionListener als, ActionListener alr) {
        JPanel jp = new JPanel();
        jp.setLayout(null);
        JButton js =  createSearchButton(als);
        js.setBounds(150, 0, 100, ROW_HIGHT);
        jp.add(js);
        JButton jr = createResetButton(alr);
        jr.setBounds(300, 0, 100, ROW_HIGHT);
        jp.add(jr);
        return jp;
    }
    
    public static JButton createSearchButton() {
        return createButton(MessageUtils.getProperty("label.search"), "scan.png");
    }
    
    public static JButton createSearchButton(ActionListener al) {
        return createButton(MessageUtils.getProperty("label.search"), "scan.png",al);
    }
    
    public static JButton createResetButton() {
        return createButton(MessageUtils.getProperty("label.reset"), "reset.png");
    }
    
    public static JButton createResetButton(ActionListener al) {
        return createButton(MessageUtils.getProperty("label.reset"), "reset.png",al);
    }

    public static JButton createSaveButton() {
        return createButton(MessageUtils.getProperty("label.save"), "save.gif");
    }

    public static JButton createSaveButton(ActionListener al) {
        return createButton(MessageUtils.getProperty("label.save"), "save.gif", al);
    }

    public static JButton createCancelButton() {
        return createButton(MessageUtils.getProperty("label.cancel"), "cancel.png");
    }

    public static JButton createCancelButton(ActionListener al) {
        return createButton(MessageUtils.getProperty("label.cancel"), "cancel.png", al);
    }

    public static JButton createButton(String text, String icon) {
        return new JButton(text, UIUtils.createIcon(icon));
    }

    public static JButton createButton(String text, String icon, ActionListener al) {
        JButton button = new JButton(text, UIUtils.createIcon(icon));
        button.addActionListener(al);
        return button;
    }

    public static JButton createExcelButton(ActionListener al) {
        JButton button =  UIUtils.createButton(MessageUtils.getProperty("label.export"), "export.png", al);
        return button;
    }
    
    public static JMenu createMenu(String text, String icon) {
        JMenu jMenu = new JMenu(text);
        jMenu.setIcon(UIUtils.createIcon(icon));
        return jMenu;
    }

    public static JMenuItem createMenuItem(String text, String icon) {
        return new JMenuItem(text, UIUtils.createIcon(icon));
    }

    public static JPanel createLabelPanel(String label) {
        JPanel jp = new JPanel();
        jp.setLayout(null);
        JLabel jl = new JLabel(label);
        jl.setFont(createFont(16));
        jl.setBounds(0, 0, 200, ROW_HIGHT);
        jp.add(jl);
        return jp;
    }

    public static TitledBorder createTitledBorder(String label) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(label);
        titledBorder.setTitleFont(createFont(16));
        return titledBorder;
    }

    public static JTextField createTextField(String label, String value, Container parent) {
        JPanel jp = createLabelPanel(label);
        JTextField jt = new JTextField(value);
        jt.setBounds(200, 0, 200, ROW_HIGHT);
        jp.add(jt);
        parent.add(jp);
        return jt;
    }
    
    public static JTextArea createTextArea(String label, String value,Container parent) {
        JPanel jp = new JPanel();
        jp.setLayout(null);
        JLabel jl = new JLabel(label);
        jl.setFont(createFont(16));
        jl.setBounds(0, 0, 200, ROW_HIGHT*3);
        jp.add(jl);
        JTextArea jt = new JTextArea(value,3,10);
        jt.setLineWrap(true);
        jt.setAutoscrolls(true);
        jt.setBounds(200, 0, 200, ROW_HIGHT*3);
        jp.add(jt);
        parent.add(jp);
        return jt;
    }

    
    public static JComboBox createComboBox(String label, String[] value, Container parent) {
        JPanel jp = createLabelPanel(label);
        JComboBox cb = new JComboBox(value);
        jp.add(cb);
        cb.setBounds(200, 0, 200, ROW_HIGHT);
        parent.add(jp);
        return cb;
    }
    public static JComboBox createComboBox(String label, Vector value, Container parent) {
        JPanel jp = createLabelPanel(label);
        JComboBox cb = new JComboBox(value);
        jp.add(cb);
        cb.setBounds(200, 0, 200, ROW_HIGHT);
        parent.add(jp);
        return cb;
    }
    
    public static JDatePickerImpl createDatePicker(String label, Date value, Container parent) {
        JPanel jp = createLabelPanel(label);
        JDatePickerImpl datePicker = (JDatePickerImpl) JDateComponentFactory.createJDatePicker(new UtilDateModel(value));
        jp.add(datePicker);
        datePicker.getJFormattedTextField().setEditable(false);
        //datePicker.getJFormattedTextField().setSize(200, ROW_HIGHT);
        datePicker.setBounds(200, 0, 200, ROW_HIGHT);
        parent.add(jp);
        return datePicker;
    }
    public static JRadioButton[] createRadioButton(String label, String[] value, Container parent) {
        JPanel jp = createLabelPanel(label);
        if (value != null) {
            JRadioButton[] rbs = new JRadioButton[value.length];
            ButtonGroup bgroup = new ButtonGroup();
            for (int i = 0; i < value.length; i++) {
                JRadioButton rb = new JRadioButton(value[i]);
                rb.setBounds(200 + i * 100, 0, 100, ROW_HIGHT);
                bgroup.add(rb);
                jp.add(rb);
                rbs[i] = rb;
            }
            parent.add(jp);
            return rbs;
        }
        return null;
    }
    
    public static JRadioButton[] createRadioButton(String label, Vector value, Container parent) {
        JPanel jp = createLabelPanel(label);
        if (value != null) {
            JRadioButton[] rbs = new JRadioButton[value.size()];
            ButtonGroup bgroup = new ButtonGroup();
            for (int i = 0; i < value.size(); i++) {
                JRadioButton rb = new JRadioButton(value.get(i).toString());
                rb.setBounds(200 + i * 100, 0, 100, ROW_HIGHT);
                bgroup.add(rb);
                jp.add(rb);
                rbs[i] = rb;
            }
            parent.add(jp);
            return rbs;
        }
        return null;
    }

    public static JFileChooser createFileChooser(String description, String... extensions) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
        chooser.setFileFilter(filter);
        return chooser;
    }
    
    public static JFileChooser createFileChooser(String label, String value, final Container parent) {
        JPanel jp = createLabelPanel(label);
        final JFileChooser chooser = new JFileChooser();
        final JTextField jt = new JTextField(value);
        jt.setBounds(200, 0, 300, ROW_HIGHT);
        jt.setEditable(false);
        jp.add(jt);
        JButton button = new JButton(MessageUtils.getProperty("label.browse"));
        button.setBounds(500, 0, 80, ROW_HIGHT);
        jp.add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooser.showOpenDialog(parent);
            }
        });
        chooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chooser.getSelectedFile() != null) {
                    jt.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        parent.add(jp);
        return chooser;
    }

    public static JPanel createTableCtrlPanel(final JTable table,boolean showInsert,boolean showUpRow,boolean showDownRow) {
        JPanel jp = new JPanel();
        jp.setSize(500, PAGE_HIGHT);
        jp.setLocation(10, 345);
        jp.setLayout(null);
        int i = 0;
        JButton add = createButton(MessageUtils.getProperty("label.addRow"), "add.gif", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.addRow(new Vector());
            }
        });
        add.setBounds((PAGE_HIGHT * 3 + 15)*i++, 0, PAGE_HIGHT * 3+10, PAGE_HIGHT);
        jp.add(add);
        JButton delete = createButton(MessageUtils.getProperty("label.deleteRow"), "delete.gif", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] indexs = table.getSelectedRows();
                for (int i = indexs.length - 1; i >= 0; i--) {
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    tableModel.removeRow(indexs[i]);
                }
            }
        });
        delete.setBounds((PAGE_HIGHT * 3 + 15)*i++, 0, PAGE_HIGHT * 3+10, PAGE_HIGHT);
        jp.add(delete);
        if(showInsert) {
            JButton insert = createButton(MessageUtils.getProperty("label.insertRow"), "insert.gif", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = table.getSelectedRow();
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    int count = tableModel.getRowCount();
                    index = index == -1 ? count : index;
                    tableModel.insertRow(index, new Vector());
                }
            });
            insert.setBounds((PAGE_HIGHT * 3 + 15)*i++, 0, PAGE_HIGHT * 3+10, PAGE_HIGHT);
            jp.add(insert);
        }
        if(showUpRow) {
            JButton upRow = createButton(MessageUtils.getProperty("label.upRow"), "up.gif", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = table.getSelectedRow();
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    if (index > 0) {
                        tableModel.moveRow(index, index, index - 1);
                        table.setRowSelectionInterval(index - 1, index - 1);
                    }
                }
            });
            upRow.setBounds((PAGE_HIGHT * 3 + 15)*i++, 0, PAGE_HIGHT * 3+10, PAGE_HIGHT);
            jp.add(upRow);
        }
        if(showDownRow) {
            JButton downRow = createButton(MessageUtils.getProperty("label.downRow"), "down.gif", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = table.getSelectedRow();
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    int count = tableModel.getRowCount();
                    if (index > -1 && index < count-1) {
                        tableModel.moveRow(index, index, index + 1);
                        table.setRowSelectionInterval(index + 1, index + 1);
                    }
                }
            });
            downRow.setBounds((PAGE_HIGHT * 3 + 15)*i++, 0, PAGE_HIGHT * 3+10, PAGE_HIGHT);
            jp.add(downRow);
        }
        return jp;
    }

    /*
    public static JPanel createPageToolBar(JLabel jl, ActionListener firstAl, ActionListener prewAl, ActionListener nextAl,
            ActionListener lastAl) {
        JPanel jp = new JPanel();
        jp.setSize(150, PAGE_HIGHT);
        jp.setLocation(250, 345);
        jp.setLayout(null);
        int i = 0;
        JButton first = createButton("", "first.gif", firstAl);
        first.setBounds(PAGE_HIGHT *i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        jp.add(first);
        JButton prew = createButton("", "prev.gif", prewAl);
        jp.add(prew);
        prew.setBounds(PAGE_HIGHT *i++,0, PAGE_HIGHT, PAGE_HIGHT);
        jl.setHorizontalAlignment(JLabel.CENTER);
        jl.setFont(createFont(16));
        jl.setBounds(PAGE_HIGHT *i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        jp.add(jl);
        JButton next = createButton("", "next.gif", nextAl);
        next.setBounds(PAGE_HIGHT *i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        jp.add(next);
        JButton last = createButton("", "last.gif", lastAl);
        last.setBounds(PAGE_HIGHT *i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        jp.add(last);
        return jp;
    }
    */
    public static JPanel createPageToolBar(final JTable table, final TableModelRefresh model, final int total, final int pageSize) {
        JPanel jp = new JPanel();
        final JLabel page = new JLabel("1");
        jp.setSize(200, PAGE_HIGHT);
        jp.setLayout(null);
        int i = 0;
        JButton first = createButton("", "first.gif", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 model.refresh(1, pageSize);
                page.setText("1");
            }
        });
        first.setBounds(PAGE_HIGHT * i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        jp.add(first);
        JButton prew = createButton("", "prev.gif", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pageStr = page.getText();
                int pageInt = Integer.valueOf(pageStr);
                if (pageInt - 1 < 1) {
                   model.refresh(1, pageSize);
                    page.setText(1 + "");
                } else {
                    model.refresh(pageInt - 1, pageSize);
                    page.setText(pageInt - 1 + "");
                }
            }
        });
        jp.add(prew);
        prew.setBounds(PAGE_HIGHT * i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        page.setHorizontalAlignment(JLabel.CENTER);
        page.setFont(createFont(16));
        page.setBounds(PAGE_HIGHT * i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        jp.add(page);
        JLabel of = new JLabel(MessageUtils.getProperty("label.of"));
        of.setHorizontalAlignment(JLabel.CENTER);
        of.setFont(createFont(16));
        of.setBounds(PAGE_HIGHT * i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        jp.add(of);
        JLabel count = new JLabel((total - 1) / pageSize + 1 + "");
        count.setHorizontalAlignment(JLabel.CENTER);
        count.setFont(createFont(16));
        count.setBounds(PAGE_HIGHT * i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        jp.add(count);
        JButton next = createButton("", "next.gif", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pageStr = page.getText();
                int pageInt = Integer.valueOf(pageStr);
                if (pageInt + 1 > (total - 1) / pageSize + 1) {
                    model.refresh((total - 1) / pageSize + 1, pageSize);
                    page.setText((total - 1) / pageSize + 1 + "");
                } else {
                    model.refresh(pageInt + 1, pageSize);
                    page.setText(pageInt + 1 + "");
                }
            }
        });
        next.setBounds(PAGE_HIGHT * i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        jp.add(next);
        JButton last = createButton("", "last.gif", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.refresh((total - 1) / pageSize + 1, pageSize);
                page.setText((total - 1) / pageSize + 1 + "");
            }
        });
        last.setBounds(PAGE_HIGHT * i++, 0, PAGE_HIGHT, PAGE_HIGHT);
        jp.add(last);
        return jp;
    }

    public static Font createFont(int size) {
        return createFont(Font.BOLD, size);
    }

    public static Font createFont(int style, int size) {
        return createFont("宋体", style, size);
    }

    public static Font createFont(String name, int style, int size) {
        return new Font(name, style, size);
    }

    public static JFileChooser createImageFileChooser(String label, String value, final Container parent) {
        JFileChooser chooser = createFileChooser(label, value, parent);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF & PNG Images", "jpg", "gif", "png");
        chooser.setFileFilter(filter);
        return chooser;
    }

    public static void showInfoDialog(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, MessageUtils.getProperty("label.information"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDialog(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, MessageUtils.getProperty("label.error"),
                JOptionPane.ERROR_MESSAGE);
    }
    
    public static int  showConfirmDialog(Component parentComponent, String message) {
        return JOptionPane.showConfirmDialog(parentComponent, message, MessageUtils.getProperty("label.information"), JOptionPane.YES_NO_OPTION);
    }

    public static void addSystemTray(String iconPath, String tooltip) {
        try {
            SystemTray.getSystemTray().add(new TrayIcon(UIUtils.createIcon(iconPath).getImage(), tooltip));
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }

    public static void addSystemTray(String iconPath, String tooltip, final Window win) {
        PopupMenu popupMenu = new PopupMenu();// 弹出菜单  
        MenuItem open = new MenuItem(MessageUtils.getProperty("label.openMainPanel"));
        MenuItem exit = new MenuItem(MessageUtils.getProperty("label.exit"));
        popupMenu.add(open);
        popupMenu.add(exit);
        // 为弹出菜单项添加事件  
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                win.setVisible(true);
            }
        });
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        try {
            SystemTray.getSystemTray().add(new TrayIcon(UIUtils.createIcon(iconPath).getImage(), tooltip, popupMenu));
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }

    public static void addSystemTray(Image icon, String tooltip, final Window win) {
        PopupMenu popupMenu = new PopupMenu();// 弹出菜单  
        MenuItem open = new MenuItem(MessageUtils.getProperty("label.openMainPanel"));
        MenuItem exit = new MenuItem(MessageUtils.getProperty("label.exit"));
        popupMenu.add(open);
        popupMenu.add(exit);
        // 为弹出菜单项添加事件  
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                win.setVisible(true);
            }
        });
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        try {
            SystemTray.getSystemTray().add(new TrayIcon(icon, tooltip, popupMenu));
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }

    public static void hideTableColumn(JTable table) {  
        hideTableColumn(table, 0);
    }  
    
    public static void hideTableColumn(JTable table, int column) {  
        TableColumnModel columns = table.getColumnModel();  
        TableColumn column_id_data = columns.getColumn(column);  
        column_id_data.setMaxWidth(0);  
        column_id_data.setPreferredWidth(0);  
        column_id_data.setMinWidth(0);  
          
        TableColumn column_id_header = table.getTableHeader().getColumnModel()  
                .getColumn(column);  
        column_id_header.setMaxWidth(0);  
        column_id_header.setPreferredWidth(0);  
        column_id_header.setMinWidth(0);  
    }  
    
    public static void visibleSave(JComponent comp,boolean b) {
        Component[] comps = comp.getComponents();
        for (Component component : comps) {
            if(component instanceof JButton) {
                JButton save =  (JButton)component;
                if(save.getText().equals(MessageUtils.getProperty("label.save"))) {
                    save.setVisible(b);
                }
            }else {
                if(component instanceof JComponent) {
                    visibleSave((JComponent) component, b);
                }
            }
        }
    }
    
    public static void enabled(JComponent comp,boolean b) {
        Component[] comps = comp.getComponents();
        for (Component component : comps) {
            if(component instanceof JComboBox) {
                ((JComboBox)component).setEnabled(b);
            }else if(component instanceof JCheckBox) {
                ((JCheckBox)component).setEnabled(b);
            }else if(component instanceof JTextField) {
                ((JTextField)component).setEnabled(b);
            }else if(component instanceof JTextArea) {
                ((JTextArea)component).setEnabled(b);
            }else if(component instanceof JRadioButton) {
                ((JRadioButton)component).setEnabled(b);
            }else if(component instanceof JButton) {
                JButton button =  (JButton)component;
                if(button.getText().equals(MessageUtils.getProperty("label.save"))) {
                    button.setVisible(b);
                }else if (button.getText().equals(MessageUtils.getProperty("label.cancel"))) {
                   continue;
                } else {
                    ((JButton)component).setEnabled(b);
                }
            }else {
                if(component instanceof JComponent) {
                    enabled((JComponent) component, b);
                }
            }
        }
    }
    
    public static Integer getSelectId(JTable table) {
        int row = table.getSelectedRow();
        if(row == -1 ) {
            return row;
        }else {
            return (Integer)table.getValueAt(row, 0);
        }
    }

    public static void createOperationForTable(JTable table, ActionListener ale, ActionListener alv, ActionListener ald) {
        TableColumn operationColumn = table.getColumnModel().getColumn(table.getColumnCount()-1);  
        operationColumn.setCellRenderer(new OperationCellRenderer());
        operationColumn.setCellEditor(new OperationCellEditor(ale,alv,ald));
    }

    public static Component createOperationPanel(ActionListener ale, ActionListener alv, ActionListener ald) {
        JPanel jp = new JPanel();
        JButton update = UIUtils.createButton("", "edit.gif", ale);
        JButton view = UIUtils.createButton("", "view.png", alv);
        JButton delete = UIUtils.createButton("", "delete.gif", ald);
        jp.setLayout(null);
        update.setBounds(2, 2, 30, 25);
        jp.add(update);
        view.setBounds(42, 2, 30, 25);
        jp.add(view);
        delete.setBounds(82, 2, 30, 25);
        jp.add(delete);
        return jp;
    }
}
