package com.fatepay.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 成功支付记录扫描监听器
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-22 11:48
 */
public class SuccessPayRecordScanListener implements ServletContextListener {
    /**
     * 成功支付记录扫描线程
     */
    private SuccessPayRecordScanThread successPayRecordScanThread;

    /**
     * 初始化
     * @param servletContextEvent
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if(successPayRecordScanThread == null){
            successPayRecordScanThread = new SuccessPayRecordScanThread();
            successPayRecordScanThread.start();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if(successPayRecordScanThread != null && !successPayRecordScanThread.isInterrupted()){
            successPayRecordScanThread.interrupt();
        }
    }
}
