package com.tiger.erp.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SystemConfig {

    private static SystemConfig systemConfig;
    private static Properties prop;
    static {
        prop = new Properties();
        try {
            prop.load(SystemConfig.class.getClassLoader().getResourceAsStream("systemconfig.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SystemConfig() {

    }

    public static SystemConfig getInstance() {
        if (systemConfig == null) {
            systemConfig = new SystemConfig();
        }
        return systemConfig;
    }

    private String iconPath = "";

    private String systemName;

    private String language;

    private String dataFilePath;

    private String logoPath;

    private String homePhotoPath;

    private String skin;

    public String getIconPath() {
        return getProperty("iconPath", SystemConstant.ICONPATH);
    }

    public void setIconPath(String iconPath) {
        prop.setProperty("iconPath", iconPath);
    }

    public String getSystemName() {
        return getProperty("systemName", SystemConstant.SYSTEMNAME);
    }

    public void setSystemName(String systemName) {
        prop.setProperty("systemName", systemName);
    }

    public String getLanguage() {
        return getProperty("language", SystemConstant.LANGUAGE);
    }

    public void setLanguage(String language) {
        prop.setProperty("language", language);
    }

    public String getDataFilePath() {
        return getProperty("dataFilePath", SystemConstant.DATAFILEPATH);
    }

    public void setDataFilePath(String dataFilePath) {
        prop.setProperty("dataFilePath", dataFilePath);
    }

    public String getLogoPath() {
        return getProperty("logoPath", "");
    }

    public void setLogoPath(String logoPath) {
        prop.setProperty("logoPath", logoPath);
    }

    public String getHomePhotoPath() {
        return getProperty("homePhotoPath", "");
    }

    public void setHomePhotoPath(String homePhotoPath) {
        prop.setProperty("homePhotoPath", homePhotoPath);
    }

    public String getSkin() {
        return getProperty("skin", SystemConstant.SKIN_OFFICE2007);
    }

    public void setSkin(String skin) {
        prop.setProperty("skin", skin);
    }

    private String getProperty(String key, String defalut) {
        Object obj = prop.get(key);
        if (obj == null || "".equals(obj.toString().trim())) {
            return defalut;
        } else {
            return obj.toString().trim();
        }
    }

    public void saveProperty() throws Exception {

        File file = new File(SystemConfig.class.getClassLoader().getResource("systemconfig.properties").toURI());
        FileOutputStream fos = new FileOutputStream(file);
        prop.store(fos, "System Config");

    }
}
