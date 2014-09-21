package com.fatepay;

import com.fatepay.utils.DateUtil;
import com.fatepay.utils.IPAddressUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

/**
 * 基础Action
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-17 22:45
 */
public class BaseAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {
    /**
     * 日志处理器
     */
    Logger logger = Logger.getLogger(BaseAction.class);

    /**
     * 当前时间
     */
    String date;
    String time;
    String defaultDateTime;

    /**
     * 消息
     */
    String message;

    /**
     * 跳转数据集合
     */
    Properties properties;

    /**
     * 跳转地址
     */
    String jumpUrl;

    /**
     * request，response
     */
    HttpServletRequest request;
    HttpServletResponse response;

    /**
     * 构造函数
     */
    public BaseAction() {
        this.date = DateUtil.getNowDate();
        this.time = DateUtil.getNowTime();
        this.defaultDateTime = DateUtil.getDefaultDateTime(new Date());
    }

    /**
     * 获取ip
     * @return
     */
    public String getIp() {
        return IPAddressUtil.getIPAddress(request);
    }

    /**
     * 获取session
     * @return
     */
    public HttpSession getSession() {
        return request.getSession();
    }

    /**
     * 获取application
     * @return
     */
    public ServletContext getApplication() {
        return request.getSession().getServletContext();
    }

    /**
     * ajax写出结果
     * @param resp
     */
    public void write(String resp) throws Exception{
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(resp);
        writer.flush();
        writer.close();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }
}