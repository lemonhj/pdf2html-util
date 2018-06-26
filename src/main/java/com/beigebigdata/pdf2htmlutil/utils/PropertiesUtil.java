package com.beigebigdata.pdf2htmlutil.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;

/**
 * @author lin.tb lin.maple.leaf@gmail.com
 * @ClassName: PropertiesUtil
 * @Description:
 * @date 2018/6/14
 */
public class PropertiesUtil {
    /**
     * 写入properties信息
     * @param filePath
     * @param key
     * @param value
     */
    public static void writeProperties(String filePath,String key, String value) {
        OutputStream fos = null;
        try {
            Properties properties = new Properties();
            InputStream fis = org.apache.logging.log4j.util.PropertiesUtil.class.getClassLoader().getResourceAsStream(filePath);
            properties.load(fis);
            URI writeUrl = org.apache.logging.log4j.util.PropertiesUtil.class.getClassLoader().getResource(filePath).toURI();
            fos = new FileOutputStream(new File(writeUrl));
            properties.setProperty(key, value);
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            properties.store(fos, "『comments』Update key：" + key);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(fos != null){
                try {
                    fos.close();
                }catch (Exception err){}
            }
        }
    }

    /**
     * 读取properties 信息
     * @param filePath
     * @param key
     * @return
     */
    public static String getProperty(String filePath,String key){
        try {
            Properties properties = new Properties();
            InputStream fis = org.apache.logging.log4j.util.PropertiesUtil.class.getClassLoader().getResourceAsStream(filePath);
            properties.load(fis);
            return properties.getProperty(key);
        }catch (Exception err){
            err.printStackTrace();
        }
        return "";
    }


    /**
     * 删除properties 信息
     * @param filePath
     * @param key
     */
    public static void removeProperty(String filePath,String key){
        try {
            Properties properties = new Properties();
            InputStream fis = org.apache.logging.log4j.util.PropertiesUtil.class.getClassLoader().getResourceAsStream(filePath);
            properties.load(fis);
            properties.remove(key);
        }catch (Exception err){
            err.printStackTrace();
        }
    }
}
