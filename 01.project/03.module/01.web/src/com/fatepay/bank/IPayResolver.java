package com.fatepay.bank;

import com.fatepay.api.TransProcessorResult;
import com.fatepay.entities.PayRecord;

import javax.servlet.http.HttpServletRequest;

/**
 * 支付接口
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 14:22
 */
public interface IPayResolver {
    /**
     * 准备数据
     * @param payRecord
     * @param request
     * @return
     */
    public TransProcessorResult prepareData(PayRecord payRecord, HttpServletRequest request);

    /**
     * 分析数据
     * @param payRecord 支付记录
     * @param request http请求
     * @param isPage 是否前台跳转
     * @return
     */
    public TransProcessorResult analiseData(PayRecord payRecord, HttpServletRequest request, boolean isPage);
}
