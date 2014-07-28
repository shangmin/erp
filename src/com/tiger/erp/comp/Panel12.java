package com.tiger.erp.comp;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.jdatepicker.impl.JDatePickerImpl;

import org.apache.log4j.Logger;

import com.tiger.erp.common.SystemConstant;
import com.tiger.erp.dao.DaoFactory;
import com.tiger.erp.dao.EmpDao;
import com.tiger.erp.po.Emp;
import com.tiger.erp.util.DateUtil;
import com.tiger.erp.util.MessageUtils;
import com.tiger.erp.util.StringUtils;
import com.tiger.erp.util.UIUtils;

public class Panel12 extends JPanel {
    
    public static final Logger LOGGER = Logger.getLogger(Panel81.class);

    MainFrame mainFrame;

    private EmpDao empDao;

    Emp emp;

    boolean  enabled = true;
    
    public Panel12(final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        empDao = DaoFactory.getInstance().getEmpDao();
        this.setVisible(false);
        this.setLayout(new BorderLayout());
    }
    public void formPanel() {
        //Detail
        if(emp == null) {
            emp = new Emp();
            emp.setStatus(SystemConstant.IN);
        }
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridLayout(9, 2, 10, 10));
        detailPanel.setBorder(UIUtils.createTitledBorder(MessageUtils.getProperty("label.detail")));
        final JTextField StaffNo = UIUtils.createTextField(MessageUtils.getProperty("label.staffNo"), emp.getStaffNo(), detailPanel);
        final JTextField StaffName = UIUtils.createTextField(MessageUtils.getProperty("label.staffName"), emp.getStaffName(), detailPanel);
        final JTextField phone = UIUtils.createTextField(MessageUtils.getProperty("label.phone"), emp.getPhone(), detailPanel);
        final JTextField emergencyContact = UIUtils.createTextField(MessageUtils.getProperty("label.emergencyContact"), emp.getEmergencyContact(),
                detailPanel);
        final JTextField emergencyContactPhone = UIUtils.createTextField(MessageUtils.getProperty("label.emergencyContactPhone"),
                emp.getEmergencyContactPhone(), detailPanel);
        final JTextField email = UIUtils.createTextField(MessageUtils.getProperty("label.email"), emp.getEmail(), detailPanel);
        final JComboBox dept = UIUtils.createComboBox(MessageUtils.getProperty("label.deptName"), empDao.getDept(), detailPanel);
        final JRadioButton[] status = UIUtils.createRadioButton(MessageUtils.getProperty("label.status"), new String[] {
                SystemConstant.IN, SystemConstant.OUT }, detailPanel);
        final JDatePickerImpl entryDate = UIUtils
                .createDatePicker(MessageUtils.getProperty("label.entryDate"), emp.getEntryDate(), detailPanel);
        final JDatePickerImpl outDate = UIUtils.createDatePicker(MessageUtils.getProperty("label.outDate"), emp.getOutDate(), detailPanel);
        final JTextArea address = UIUtils.createTextArea(MessageUtils.getProperty("label.address"), emp.getAddress(), detailPanel);
        dept.setSelectedIndex(emp.getDeptId() == null ? 0 : emp.getDeptId());
        status[0].setSelected(true);
        detailPanel.add(new JPanel());
        detailPanel.add(UIUtils.createSaveButtonPanel(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emp.setStaffNo(StaffNo.getText());
                emp.setStaffName(StaffName.getText());
                emp.setDeptId(dept.getSelectedIndex());
                emp.setPhone(phone.getText());
                emp.setEmail(email.getText());
                emp.setEmergencyContact(emergencyContact.getText());
                emp.setEmergencyContactPhone(emergencyContactPhone.getText());
                emp.setStatus(status[0].getSelectedObjects() == null ? SystemConstant.OUT : SystemConstant.IN);
                if (StringUtils.isNotEmptyString(entryDate.getJFormattedTextField().getText())) {
                    emp.setEntryDate(DateUtil.parseDate(entryDate.getJFormattedTextField().getText()));
                }
                if (StringUtils.isNotEmptyString(outDate.getJFormattedTextField().getText())) {
                    emp.setOutDate(DateUtil.parseDate(outDate.getJFormattedTextField().getText()));
                }
                emp.setAddress(address.getText());
                try {
                    empDao.save(emp);
                    UIUtils.showInfoDialog(Panel12.this, MessageUtils.getProperty("info.savedSuccessfully"));
                } catch (Exception e1) {
                    LOGGER.error("save emp error", e1);
                    UIUtils.showErrorDialog(Panel12.this, MessageUtils.getProperty("error.savedFail"));
                }
            }
        }, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.p11 = (Panel11) mainFrame.gotoDisplayPanel(new Panel11(mainFrame),mainFrame);
            }
        }));
        this.add(detailPanel, BorderLayout.CENTER);
        UIUtils.enabled(detailPanel, enabled);
    }
}
