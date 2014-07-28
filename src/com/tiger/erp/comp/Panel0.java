package com.tiger.erp.comp;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.tiger.erp.common.SystemConfig;
import com.tiger.erp.util.StringUtils;
import com.tiger.erp.util.UIUtils;

public class Panel0 extends JPanel {
    
    public static final Logger LOGGER = Logger.getLogger(Panel0.class);
    
    public  Panel0 () {
        this.setLocation(0, 30);
        this.setBackground(Color.BLUE);
        this.setVisible(true);
        if(StringUtils.isEmptyString(SystemConfig.getInstance().getHomePhotoPath())) {
            this.add(new JLabel(UIUtils.createIcon("home.png")));
        }else {
            this.add(new JLabel(new ImageIcon(SystemConfig.getInstance().getHomePhotoPath())));
        }
    }
}
