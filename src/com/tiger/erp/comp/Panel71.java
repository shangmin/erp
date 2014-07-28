package com.tiger.erp.comp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.tiger.erp.common.TableModelRefresh;
import com.tiger.erp.dao.DaoFactory;
import com.tiger.erp.dao.SalaryDao;
import com.tiger.erp.util.MessageUtils;
import com.tiger.erp.util.UIUtils;

public class Panel71 extends JPanel {
    
    public static final Logger LOGGER = Logger.getLogger(Panel71.class);
    
    MainFrame mainFrame;

    private SalaryDao salaryDao;

    public Panel71( final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        salaryDao = DaoFactory.getInstance().getSalaryDao();
        this.setLocation(0, 30);
        this.setVisible(false);
        this.setLayout(new BorderLayout());
        final Vector<Vector> data = salaryDao.getSalaryItem(1, 2);
        final Vector<String> columnNames = new Vector<String>();
        columnNames.add(MessageUtils.getProperty("label.seq"));
        columnNames.add(MessageUtils.getProperty("label.item"));
        columnNames.add(MessageUtils.getProperty("label.salary"));
        TableModel model = new DefaultTableModel(data, columnNames);
        JPanel  tablePanel = new JPanel();
        tablePanel.setLayout(null);
        final JTable table = new JTable(model);
        table.getTableHeader().setFont(UIUtils.createFont(20));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);
        scrollPane.setBounds(10,10, 580, 330);
        tablePanel.add(scrollPane);
        JPanel pageToolBar = UIUtils.createPageToolBar(table, new TableModelRefresh() {
            @Override
            public void refresh(int page, int pageSize) {
                final Vector<Vector> data = salaryDao.getSalaryItem(page, pageSize);
                TableModel model = new DefaultTableModel(data, columnNames);
                table.setModel(model);
            }
        }, salaryDao.countSalaryItem(), 2);
        //tablePanel.add(pageToolBar, BorderLayout.SOUTH);
        JPanel tableCtrlPanel = UIUtils.createTableCtrlPanel(table,false,false,false);
        tablePanel.add(tableCtrlPanel, BorderLayout.SOUTH);
        
//        tablePanel.add(UIUtils.createPageToolBar(page, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                table.setModel(new DefaultTableModel(salaryDao.getSalaryItem(1, 2), columnNames));
//                page.setText("1");
//            }
//        }, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String pageStr = page.getText();
//                int pageInt = Integer.valueOf(pageStr);
//                table.setModel(new DefaultTableModel(salaryDao.getSalaryItem(pageInt - 1, 2), columnNames));
//                page.setText(pageInt - 1 + "");
//            }
//        }, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String pageStr = page.getText();
//                int pageInt = Integer.valueOf(pageStr);
//                table.setModel(new DefaultTableModel(salaryDao.getSalaryItem(pageInt + 1, 2), columnNames));
//                page.setText(pageInt + 1 + "");
//            }
//        }, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int taltal = salaryDao.countSalaryItem();
//                table.setModel(new DefaultTableModel(salaryDao.getSalaryItem((taltal-1)/2 + 1, 2), columnNames));
//                page.setText((taltal-1)/2 +1 + "");
//            }
//        }), BorderLayout.SOUTH);
        this.add(tablePanel, BorderLayout.CENTER);
        this.add(UIUtils.createSaveButtonPanel(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    salaryDao.saveSalaryItem(data);
                    UIUtils.showInfoDialog(Panel71.this, MessageUtils.getProperty("info.savedSuccessfully"));
                } catch (Exception e2) {
                    LOGGER.error("save emp error", e2);
                    UIUtils.showErrorDialog(Panel71.this, MessageUtils.getProperty("error.savedFail"));
                }
            }
        }, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Panel71.this.setVisible(false);
                mainFrame.p0.setVisible(true);
                mainFrame.current =  mainFrame.p0;
            }
        }), BorderLayout.SOUTH);
    }
}
