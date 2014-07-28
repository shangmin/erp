package com.tiger.erp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtils {
    
    public static String getFileName(String path,String name) {
        return path + System.getProperty("file.separator")  + name + ".data";
    }
    
    public static boolean makeDir(String path) throws IOException {
        File file = new File(path);
        if(file.exists() && file.isDirectory()) {
            return true;
        }
        return file.mkdirs();
    }
    public static boolean renameTo(File src,String name) throws IOException {
        if(src == null || StringUtils.isEmptyString(name)) {
            return false;
        }
        if(name.contains(".")) {
            File dest = new File(name);
            return src.renameTo(dest);
        } else {
            File dest = new File(name + src.getName().substring(".".indexOf(src.getName())));
            return src.renameTo(dest);
        }
    }
    
    public static void writeToFile(String name,Object obj) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(name, false);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException("Error in write to file.",e);
        }finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static Object readFromFile(String name) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(name);
            ois = new ObjectInputStream(fis);
            return  ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Error in read from file.",e);
        }finally{
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
