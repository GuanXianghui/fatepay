package com.fatepay.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * 配置文件读取工具类
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 00:12
 */
public class PropertyUtil {
    private static PropertyUtil instance;

    public static PropertyUtil getInstance() {
        if (null == instance) {
            instance = new PropertyUtil();
        }
        return instance;
    }

    private static String propertyRoute = "config.properties";

    static Properties prop;

    private PropertyUtil() {
        refresh();
    }

    /**
     * 配置缓存刷新
     */
    public static void refresh() {
        // 1 读取properties文件
        URL configUrl = Thread.currentThread().getContextClassLoader().getResource(propertyRoute);
        if (null == configUrl) {
            throw new RuntimeException("找不到配置文件:" + propertyRoute);
        }
        // 2 将文件URL装载为properties类
        prop = new Properties();
        InputStream configIs = null;
        try {
            configIs = configUrl.openStream();
            prop.load(configIs);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (configIs != null) {
                try {
                    configIs.close();
                } catch (IOException e) {
                    throw new RuntimeException("配置文件流关闭异常!");
                }
            }
        }
    }

    /**
     * 获取属性
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}
