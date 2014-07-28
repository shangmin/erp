package com.tiger.erp.comp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.tiger.erp.common.SystemConfig;
import com.tiger.erp.util.MessageUtils;
import com.tiger.erp.util.StringUtils;
import com.tiger.erp.util.UIUtils;

public class MainFrame extends JFrame {

    public static final Logger LOGGER = Logger.getLogger(MainFrame.class);
    
    Panel0 p0 = new Panel0();
    Panel11 p11;
    Panel12 p12;
    Panel71 p71;
    Panel81 p81;
    JPanel current = p0;
    
    public MainFrame() {
        this.setSize(850, 650);
        this.setVisible(true);
        this.setTitle(SystemConfig.getInstance().getSystemName());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        ImageIcon icon = new ImageIcon();
        if(StringUtils.isEmptyString(SystemConfig.getInstance().getLogoPath())) {
            icon = UIUtils.createIcon("logo.png");
        }else {
            icon = new ImageIcon(SystemConfig.getInstance().getLogoPath());
        }
        UIUtils.addSystemTray(icon.getImage(), SystemConfig.getInstance().getSystemName(), this);
        this.setIconImage(icon.getImage());
        this.setLayout(new BorderLayout());
        JMenuBar menubar = createMenuBar();
        this.add(menubar,BorderLayout.NORTH);
        this.add(p0,BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu menu1 = UIUtils.createMenu(MessageUtils.getProperty("label.employeeManagement"), "emp.jpg");
        JMenuItem menuitem11 = UIUtils.createMenuItem(MessageUtils.getProperty("label.employeeList"), "emp.jpg");
        JMenuItem menuitem12 = UIUtils.createMenuItem(MessageUtils.getProperty("label.employeeAdd"), "add_emp.jpg");
        //JMenuItem menuitem13 = UIUtils.createMenuItem(MessageUtils.getProperty("label.queryExport"), "export.jpg");
        JMenu menu7 = UIUtils.createMenu(MessageUtils.getProperty("label.salaryManagement"), "rmb.jpg");
        JMenuItem menuitem71 = UIUtils.createMenuItem(MessageUtils.getProperty("label.salaryItem"), "rmb.jpg");
        JMenuItem menuitem72 = UIUtils.createMenuItem(MessageUtils.getProperty("label.salaryCalculate"), "count.jpg");
        JMenuItem menuitem73 = UIUtils.createMenuItem(MessageUtils.getProperty("label.queryExport"), "export.jpg");
        JMenu menu8 = UIUtils.createMenu(MessageUtils.getProperty("label.systemManagement"), "setup.jpg");
        JMenuItem menuitem81 = UIUtils.createMenuItem(MessageUtils.getProperty("label.systemSetup"), "setup.jpg");
        JMenuItem menuitem82 = UIUtils.createMenuItem(MessageUtils.getProperty("label.about"), "about.jpg");
        menubar.add(menu1);
        menubar.add(menu7);
        menubar.add(menu8);
        menu1.add(menuitem11);
        menu1.add(menuitem12);
       //menu1.add(menuitem13);
        menu7.add(menuitem71);
        menu7.add(menuitem72);
        menu7.add(menuitem73);
        menu8.add(menuitem81);
        menu8.add(menuitem82);
        menuitem11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addContent(MainFrame.this, e);
            }
        });
        menuitem12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addContent(MainFrame.this, e);
            }
        });
        menuitem71.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addContent(MainFrame.this, e);
            }
        });
        menuitem81.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addContent(MainFrame.this, e);
            }
        });
        menuitem82.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addContent(MainFrame.this, e);
            }
        });
        return menubar;
    }

    protected void addContent(MainFrame mainFrame, ActionEvent e) {
        if (MessageUtils.getProperty("label.employeeList").equals(e.getActionCommand())) {
            mainFrame.p11 = (Panel11) gotoDisplayPanel(new Panel11(mainFrame),mainFrame);
        }else if (MessageUtils.getProperty("label.employeeAdd").equals(e.getActionCommand())) {
            mainFrame.p12 = (Panel12) gotoDisplayPanel(new Panel12(mainFrame),mainFrame);
            mainFrame.p12.formPanel();
        }else if (MessageUtils.getProperty("label.salaryItem").equals(e.getActionCommand())) {
            mainFrame.p71 = (Panel71) gotoDisplayPanel(new Panel71(mainFrame),mainFrame);
        }else if(MessageUtils.getProperty("label.systemSetup").equals(e.getActionCommand())) {
            mainFrame.p81 = (Panel81) gotoDisplayPanel(new Panel81(mainFrame),mainFrame);
        }else if(MessageUtils.getProperty("label.about").equals(e.getActionCommand())) {
            aboutDialog();
        }
    }

    private void aboutDialog() {
        JDialog about = new JDialog(this);
        about.setVisible(true);
        about.setSize(300, 250);
        about.setTitle(MessageUtils.getProperty("label.aboutERP"));
        about.setLocationRelativeTo(null);
        about.setLayout(null);
        JLabel  l0 = new JLabel(UIUtils.createIcon("water-logo.jpg"));
        l0.setBounds(0, 0, 300, 100);
        about.add(l0);
        JLabel  l1 = new JLabel(MessageUtils.getProperty("label.erp"));
        l1.setBounds(10, 120, 300, 20);
        about.add(l1);
        JLabel  l2 = new JLabel(MessageUtils.getProperty("label.version"));
        l2.setBounds(10, 140, 300, 20);
        about.add(l2);
        JLabel  l3 = new JLabel(MessageUtils.getProperty("label.copyright"));
        l3.setBounds(10, 160, 300, 20);
        about.add(l3);
        JLabel  l4 = new JLabel(MessageUtils.getProperty("label.conect"));
        l4.setBounds(10, 180, 300, 20);
        about.add(l4);
    }

    public JPanel gotoDisplayPanel(JPanel p,MainFrame mainFrame) {
        current.setVisible(false);
        current = null;
        mainFrame.add(p,BorderLayout.CENTER);
        current = p;
        current.setVisible(true);
        return p ;
    }
}
