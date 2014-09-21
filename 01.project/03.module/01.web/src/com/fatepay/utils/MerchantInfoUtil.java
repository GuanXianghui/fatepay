package com.fatepay.utils;

import com.fatepay.entities.MerchantInfo;
import com.fatepay.service.IMerchantInfoService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * 商户信息工具类
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 18:18
 */
public class MerchantInfoUtil {
    /**
     * 日志处理器
     */
    static Logger logger = Logger.getLogger(MerchantInfoUtil.class);
    /**
     * 商户信息服务类
     */
    private static IMerchantInfoService merchantInfoService;
    /**
     * 单例实例
     */
    private static MerchantInfoUtil instance;
    /**
     * 所有商户信息集合
     */
    private static List<MerchantInfo> merchantInfoList;

    public static MerchantInfoUtil getInstance() {
        if (null == instance) {
            instance = new MerchantInfoUtil();
        }
        return instance;
    }

    private MerchantInfoUtil() {
        refresh();
    }

    /**
     * 读取所有商户信息
     */
    public static void refresh() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        merchantInfoService = (IMerchantInfoService)applicationContext.getBean("merchantInfoService");
        merchantInfoList = merchantInfoService.queryAllMerchantInfos();
    }

    /**
     * 获取商户信息
     *
     * @param merchantCode
     * @return
     */
    public MerchantInfo getMerchantInfo(String merchantCode) {
        for(MerchantInfo merchantInfo : merchantInfoList){
            if(StringUtils.equals(merchantCode, merchantInfo.getCode())){
                return merchantInfo;
            }
        }
        return null;
    }
}
