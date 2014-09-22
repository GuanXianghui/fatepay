package com.fatepay.api;

import com.fatepay.interfaces.ProcessorInterface;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取token接口 处理器
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 14:26
 */
public class GetTokenProcessor extends BaseTransProcessor {
    /**
     * 无参构造函数
     */
    public GetTokenProcessor(){}

    /**
     * 构造函数
     */
    public GetTokenProcessor(HttpServletRequest request) {
        this.request = request;
        this.notEmptyColumns = "merchantCode,serviceCode,versionCode,charsetType,signType,sign";
        this.maxLengthColumns = "merchantCode=10,serviceCode=20,versionCode=10,charsetType=10,signType=10,sign=64";
        this.equalsColumns = "serviceCode=getToken" + EQUALS_COLUMN_SPLIT_STR + "versionCode=1.0" + EQUALS_COLUMN_SPLIT_STR + "signType=MD5";
        this.md5ParamOrder = "merchantCode,serviceCode,versionCode,charsetType,signType";
    }

    /**
     * 处理入口
     * @return
     */
    public TransProcessorResult process() {
        // 1 数据校验及验签名
        try {
            // 校验非空字段
            checkNotEmptyColumns();
            // 校验字段长度上限
            checkMaxLengthColumns();
            // 校验字段相等
            checkEqualsColumns();
            // 校验商户存在 而且 状态正常
            checkMerchantInfo();
            // MD5校验
            checkMd5();
            // 校验域名白名单 多个用逗号隔开
            checkWhiteList();
        } catch (Exception e) {
            logger.error("数据校验异常发生！", e);
            // 组织失败结果
            String message = "{" +
                    "\"isSuccess\":\"N\"," +
                    "\"token\":\"\"," +
                    "\"expireTime\":\"\"," +
                    "\"desc\":\"" + e.getMessage() + "\"," +
                    "\"returnUrl\":\"\"," +
                    "\"notifyUrl\":\"\"" +
                    "}";
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_STRING);
            transProcessorResult.setMessage(message);
            return transProcessorResult;
        }
        // 2 创建token串
        String message = createToken();
        // 3 组织成功结果
        TransProcessorResult transProcessorResult = new TransProcessorResult();
        transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_STRING);
        transProcessorResult.setMessage(message);
        return transProcessorResult;
    }
}
