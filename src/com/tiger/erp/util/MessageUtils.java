package com.tiger.erp.util;

import java.util.Locale;
import java.util.ResourceBundle;

import com.tiger.erp.common.SystemConfig;

public class MessageUtils {

    static  ResourceBundle resources = ResourceBundle.getBundle("message", new Locale(SystemConfig.getInstance().getLanguage()));
    
    public static String getProperty(String msgKey, String... paramName) {
        try {
            String msg = resources.getString(msgKey);
            if (msg != null && paramName != null) {
                for (int i = 0; i < paramName.length; i++) {
                    msg = msg.replace("{" + i + "}", paramName[i].trim());
                }
            }
            return msg == null ? "" : msg.trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgKey;
    }
}
