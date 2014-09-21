package com.fatepay.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 取客户IP地址帮助类
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-17 23:56
 */
public class IPAddressUtil {
    /**
     * 从HTTP请求中获取客户IP地址
     *
     * @param request http请求
     * @return 客户IP地址
     */
    public static String getIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
