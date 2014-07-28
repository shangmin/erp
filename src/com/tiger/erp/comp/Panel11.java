package com.tiger.erp.comp;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import net.sourceforge.jdatepicker.impl.JDatePickerImpl;

import org.apache.log4j.Logger;

import com.tiger.erp.common.SystemConstant;
import com.tiger.erp.common.TableModelRefresh;
import com.tiger.erp.common.jdbc.JDBCException;
import com.tiger.erp.dao.DaoFactory;
import com.tiger.erp.dao.EmpDao;
import com.tiger.erp.po.Emp;
import com.tiger.erp.util.DateUtil;
import com.tiger.erp.util.ExcelExport;
import com.tiger.erp.util.MessageUtils;
import com.tiger.erp.util.StringUtils;
import com.tiger.erp.util.UIUtils;

public class Panel11 extends JPanel {
    
    public static final Logger LOGGER = Logger.getLogger(Panel11.class);

    MainFrame mainFrame;

    private EmpDao empDao;

    Emp emp = new Emp();

    public Panel11(final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        emp.setStatus(SystemConstant.IN);
        empDao = DaoFactory.getInstance().getEmpDao();
        this.setVisible(false);
        this.setLayout(new BorderLayout());
        //Table
        final Vector<Vector> data = empDao.getEmp(emp, 1, SystemConstant.PAGE_SIZE);
        final Vector<String> columnNames = getColumnNames();
        TableModel model = new DefaultTableModel(data, columnNames);
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(null);
        final JTable table = new JTable(model);
        table.getTableHeader().setFont(UIUtils.createFont(16));
        table.setRowHeight(30);
        rerenderTable(table);
        //Search
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(3, 2, 10, 10));
        searchPanel.setBorder(UIUtils.createTitledBorder(MessageUtils.getProperty("label.search")));
        final JTextField StaffNo = UIUtils.createTextField(MessageUtils.getProperty("label.staffNo"), "", searchPanel);
        final JTextField StaffName = UIUtils.createTextField(MessageUtils.getProperty("label.staffName"), "", searchPanel);
        final JComboBox dept = UIUtils.createComboBox(MessageUtils.getProperty("label.deptName"), empDao.getDept(), searchPanel);
        final JRadioButton[] status = UIUtils.createRadioButton(MessageUtils.getProperty("label.status"), new String[] {
                SystemConstant.IN, SystemConstant.OUT }, searchPanel);
        final JDatePickerImpl entryDate = UIUtils
                .createDatePicker(MessageUtils.getProperty("label.entryDate"), null, searchPanel);
        status[0].setSelected(true);
        JPanel searchButtonPanel = UIUtils.createSearchButtonPanel(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emp.setStaffNo(StaffNo.getText());
                emp.setStaffName(StaffName.getText());
                emp.setDeptId(dept.getSelectedIndex());
                emp.setStatus(status[0].getSelectedObjects() == null ? SystemConstant.OUT : SystemConstant.IN);
                if (StringUtils.isNotEmptyString(entryDate.getJFormattedTextField().getText())) {
                    emp.setEntryDate(DateUtil.parseDate(entryDate.getJFormattedTextField().getText()));
                }
                search(1,columnNames, table);
            }
        }, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StaffNo.setText("");
                StaffName.setText("");
                status[0].setSelected(false);
                status[1].setSelected(false);
                dept.setSelectedIndex(0);
                entryDate.getJFormattedTextField().setText("");
            }
        });
        final JButton export = UIUtils.createExcelButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser excelPath = UIUtils.createFileChooser("Excel files.", "xls", "xlsx");
                int operationType = excelPath.showOpenDialog(Panel11.this);
                if(operationType == JFileChooser.APPROVE_OPTION) {
                    if(excelPath.getSelectedFile().getName().endsWith("xls") || excelPath.getSelectedFile().getName().endsWith("xlsx")) {
                        writeToPath(excelPath.getSelectedFile());
                    } else {
                        UIUtils.showErrorDialog(Panel11.this, MessageUtils.getProperty("error.invalidFileName"));
                    }
                }
            }
        });
        export.setBounds(0, 0, 100, UIUtils.ROW_HIGHT);
        searchButtonPanel.add(export);
        searchPanel.add(searchButtonPanel);
        //List
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);
        JPanel pageToolBar = UIUtils.createPageToolBar(table, new TableModelRefresh() {
            @Override
            public void refresh(int page, int pageSize) {
              search(page, columnNames, table);
            }
        }, empDao.countEmp(emp), SystemConstant.PAGE_SIZE);
        searchPanel.setBounds(5, 5, 840, 150);
        scrollPane.setBounds(5, 170, 840, 330);
        pageToolBar.setLocation(300, 510);
        tablePanel.add(scrollPane);
        tablePanel.add(searchPanel);
        tablePanel.add(pageToolBar, BorderLayout.SOUTH);
        this.add(tablePanel, BorderLayout.CENTER);
    }

    protected void search(int page,Vector columnNames, JTable table) {
        final Vector<Vector> data = empDao.getEmp(emp, page, SystemConstant.PAGE_SIZE);
        TableModel model = new DefaultTableModel(data, columnNames);
        table.setModel(model);
        rerenderTable(table);
    }

    private void rerenderTable(final JTable table) {
        UIUtils.hideTableColumn(table);
        UIUtils.createOperationForTable(table, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoDetailPanel(true, table);
            }
        }, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoDetailPanel(false, table);
            }
        },new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int optionType = UIUtils.showConfirmDialog(Panel11.this, MessageUtils.getProperty("info.deleteCofirm"));
                    if(optionType == JOptionPane.YES_OPTION) {
                        delete(table);
                        search(1,getColumnNames(), table);
                    }
                } catch (Exception e1) {
                    LOGGER.error("error in delete.", e1);
                    UIUtils.showErrorDialog(Panel11.this, MessageUtils.getProperty("error.unknownError"));
                }
            }
        });
    }

    protected void delete(JTable table) throws JDBCException {
        Emp emp = empDao.find(UIUtils.getSelectId(table));
        emp.setOutDate(DateUtil.getSystemDate());
        emp.setStatus(SystemConstant.OUT);
        empDao.update(emp);
    }

    protected void gotoDetailPanel(boolean enabled, JTable table) {
        mainFrame.p12 = (Panel12) mainFrame.gotoDisplayPanel(new Panel12(mainFrame),mainFrame);
        try {
            if(UIUtils.getSelectId(table) == -1) {
                UIUtils.showErrorDialog(Panel11.this, MessageUtils.getProperty("error.selectOneRecord"));
            }else {
                mainFrame.p12.emp = empDao.find(UIUtils.getSelectId(table));
                mainFrame.p12.enabled = enabled;
                mainFrame.p12.formPanel();
            }
        } catch (Exception e1) {
            LOGGER.error("error in find.", e1);
            UIUtils.showErrorDialog(Panel11.this, MessageUtils.getProperty("error.unknownError"));
        }
    }

    private void writeToPath(File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            ExcelExport.exportExcel("emp",emp.getClass(),empDao.getEmp(emp), fos);
        } catch (Exception e1) {
            UIUtils.showErrorDialog(Panel11.this, MessageUtils.getProperty("error.unknownError"));
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    
    private Vector<String> getColumnNames() {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("");
        columnNames.add(MessageUtils.getProperty("label.staffNo"));
        columnNames.add(MessageUtils.getProperty("label.staffName"));
        columnNames.add(MessageUtils.getProperty("label.phone"));
        columnNames.add(MessageUtils.getProperty("label.status"));
        columnNames.add(MessageUtils.getProperty("label.entryDate"));
        columnNames.add(MessageUtils.getProperty("label.deptName"));
        columnNames.add(MessageUtils.getProperty("label.opration"));
        return columnNames;
    }

}
