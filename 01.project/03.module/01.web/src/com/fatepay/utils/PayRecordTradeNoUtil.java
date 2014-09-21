package com.fatepay.utils;

import com.fatepay.service.IPayRecordService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 支付记录 交易网关流水 工具类
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 00:12
 */
public class PayRecordTradeNoUtil {
    /**
     * 日志处理器
     */
    Logger logger = Logger.getLogger(PayRecordTradeNoUtil.class);
    private static PayRecordTradeNoUtil instance;
    public static PayRecordTradeNoUtil getInstance() {
        if (null == instance) {
            instance = new PayRecordTradeNoUtil();
        }
        return instance;
    }

    /**
     * 空的构造函数
     */
    private PayRecordTradeNoUtil(){
        nowDate = DateUtil.getNowDate();
        index = 0;
        // 启动应用 初始化：查询库中最大的index
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        IPayRecordService payRecordService = (IPayRecordService)applicationContext.getBean("payRecordService");
        if(StringUtils.isBlank(payRecordService.getMaxTradeNo())){//库中一条记录都没有
            index = 0;
        } else {
            index = Integer.parseInt(payRecordService.getMaxTradeNo().substring(8));
        }
        logger.info("查询库中最大的index:[" + index + "]");
    }

    /**
     * 当前日期，如果日期更新则计数index从1开始
     */
    private String nowDate;

    /**
     * 计数索引
     */
    private int index;

    /**
     * 获取下一个交易代码
     * @return
     */
    public String nextTradeNo(){
        // 只能一个线程进入以下代码块
        synchronized(this){
            // 如果日期更新则计数index从1开始
            if(!StringUtils.equals(DateUtil.getNowDate(), nowDate)){
                this.nowDate = DateUtil.getNowDate();
                this.index = 0;
            }

            // index自增
            ++index;

            // yyyyMMdd+00000001开始的8位自增
            return this.nowDate + StringUtils.leftPad(StringUtils.EMPTY + index, 8, "0");
        }
    }
}
