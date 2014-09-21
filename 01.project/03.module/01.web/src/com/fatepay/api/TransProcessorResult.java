package com.fatepay.api;

import java.util.Properties;

/**
 * 交易处理器返回结果
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 14:20
 */
public class TransProcessorResult {
    // 返回类型
    String resultType;
    // 参数集合
    Properties properties;
    // 跳转地址
    String jumpUrl;
    // 处理结果信息
    String message;

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
