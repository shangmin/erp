package com.tiger.erp.comp;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.tiger.erp.common.SystemConfig;
import com.tiger.erp.common.SystemConstant;
import com.tiger.erp.util.FileUtils;
import com.tiger.erp.util.MessageUtils;
import com.tiger.erp.util.StringUtils;
import com.tiger.erp.util.UIUtils;

public class Panel81 extends JPanel {
    
    public static final Logger LOGGER = Logger.getLogger(Panel81.class);
    
    MainFrame mainFrame;
    
    public  Panel81 (final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setLocation(0, 30);
        this.setVisible(true);
        this.setLayout(new GridLayout(8,2));
        this.setBorder(UIUtils.createTitledBorder(MessageUtils.getProperty("label.systemSetup")));
        final JTextField systemName = UIUtils.createTextField(MessageUtils.getProperty("label.systemName"), SystemConfig.getInstance().getSystemName(),this);
        final JComboBox skin = UIUtils.createComboBox(MessageUtils.getProperty("label.skin"), new String[]{MessageUtils.getProperty("label.office2007"),MessageUtils.getProperty("label.appleMac")}, this);
        final JRadioButton[]  languages = UIUtils.createRadioButton(MessageUtils.getProperty("label.language"), new String[]{MessageUtils.getProperty("label.english"),MessageUtils.getProperty("label.simplifiedChinese")}, this);
        final JFileChooser logoPath = UIUtils.createImageFileChooser(MessageUtils.getProperty("label.logo"), "", this);
        final JFileChooser homePhotoPath = UIUtils.createImageFileChooser(MessageUtils.getProperty("label.homePhoto"), "", this);
        final JTextField dataFilePath = UIUtils.createTextField(MessageUtils.getProperty("label.dataFileFolder"),SystemConfig.getInstance().getDataFilePath(),this);
       
        if(SystemConstant.SKIN_OFFICE2007.equals(SystemConfig.getInstance().getSkin())) {
            skin.setSelectedItem(MessageUtils.getProperty("label.office2007"));
        } else {
            skin.setSelectedItem(MessageUtils.getProperty("label.appleMac"));
        }
        if(SystemConstant.LANGUAGE_EN.equals(SystemConfig.getInstance().getLanguage())) {
            languages[0].setSelected(true);
        } else {
            languages[1].setSelected(true);
        }
        
        this.add(UIUtils.createSaveButtonPanel(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(StringUtils.isEmptyString(systemName.getText())) {
                        SystemConfig.getInstance().setSystemName("");
                    }else {
                        SystemConfig.getInstance().setSystemName(systemName.getText());
                    }
                    if(MessageUtils.getProperty("label.office2007").equals(skin.getSelectedItem())) {
                        SystemConfig.getInstance().setSkin(SystemConstant.SKIN_OFFICE2007);
                    } else {
                        SystemConfig.getInstance().setSkin(SystemConstant.SKIN_APPLEMAC);
                    }
                    SystemConfig.getInstance().setLanguage(languages[0].getSelectedObjects() == null ? SystemConstant.LANGUAGE_ZH : SystemConstant.LANGUAGE_EN);
                    if(logoPath.getSelectedFile() != null) {
                        SystemConfig.getInstance().setLogoPath(logoPath.getSelectedFile() .getAbsolutePath());
                    }else {
                        SystemConfig.getInstance().setLogoPath("");
                    }
                    if(homePhotoPath.getSelectedFile() != null) {
                        SystemConfig.getInstance().setHomePhotoPath(homePhotoPath.getSelectedFile() .getAbsolutePath());
                    }else {
                        SystemConfig.getInstance().setHomePhotoPath("");
                    }
                    if(!StringUtils.isEmptyString(dataFilePath.getText())) {
                        try {
                            boolean b = FileUtils.makeDir(dataFilePath.getText());
                            if(!b) {
                                UIUtils.showErrorDialog(Panel81.this, MessageUtils.getProperty("error.invalidFileDirectory"));
                            }
                            SystemConfig.getInstance().setDataFilePath(dataFilePath.getText());
                        } catch (IOException e1) {
                            LOGGER.error("invalid data file path", e1);
                            UIUtils.showErrorDialog(Panel81.this, MessageUtils.getProperty("error.invalidFileDirectory"));
                        }
                    }else {
                        SystemConfig.getInstance().setDataFilePath("");
                    }
                    SystemConfig.getInstance().saveProperty();
                    UIUtils.showInfoDialog(Panel81.this, MessageUtils.getProperty("info.setupSuccessfully"));
                } catch (Exception e2) {
                    LOGGER.error("save system config error!", e2);
                    UIUtils.showErrorDialog(Panel81.this, MessageUtils.getProperty("error.savedFail"));
                }
            }
        }, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Panel81.this.setVisible(false);
                mainFrame.p0.setVisible(true);
                mainFrame.current =  mainFrame.p0;
            }
        }));
    }
}
