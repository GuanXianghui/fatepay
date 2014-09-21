package com.fatepay.api;

import com.fatepay.interfaces.TransInterface;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 交易处理器分发
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 14:21
 */
public class TransProcessorDispatcher {
    /**
     * 分发处理器
     * @param serviceCode
     * @return
     */
    public static ITransProcessor dispatch(String serviceCode, HttpServletRequest request){
        // 获取token接口
        if(StringUtils.equals(TransInterface.SERVICE_CODE_GET_TOKEN, serviceCode)){
            return new GetTokenProcessor(request);
        }
        // 支付接口
        if(StringUtils.equals(TransInterface.SERVICE_CODE_PAY, serviceCode)){
            return new PayProcessor(request);
        }
        // 单笔交易查询接口
        if(StringUtils.equals(TransInterface.SERVICE_CODE_SINGLE_QUERY, serviceCode)){
            return new SingleQueryProcessor(request);
        }
        // 不支持该服务名称
        return null;
    }
}
