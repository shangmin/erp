package com.tiger.test;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.tiger.erp.common.SystemConfig;
import com.tiger.erp.comp.MainFrame;
import com.tiger.erp.util.H2Utils;
import com.tiger.erp.util.StringUtils;

public class Test extends JFrame{
    
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);  
        JDialog.setDefaultLookAndFeelDecorated(true);  
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {  
                try {  
                    if(StringUtils.isEmptyString(SystemConfig.getInstance().getSkin())) {
                        UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel");  
                    }else {
                        UIManager.setLookAndFeel(SystemConfig.getInstance().getSkin());  
                    }
                } catch (Exception e) {  
                    System.err.println("error!" + e.getMessage());
                }  
              H2Utils.getConnection();
              new MainFrame();
            }
        });
    }
}
