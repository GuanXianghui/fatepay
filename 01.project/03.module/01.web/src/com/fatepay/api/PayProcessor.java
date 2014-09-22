package com.fatepay.api;

import com.fatepay.bank.ISingleTradeQueryResolver;
import com.fatepay.bank.PayResolverDispatcher;
import com.fatepay.bank.SingleTradeQueryResolverDispatcher;
import com.fatepay.bank.SingleTradeQueryResult;
import com.fatepay.entities.MerchantInfo;
import com.fatepay.entities.PayRecord;
import com.fatepay.interfaces.PayRecordInterface;
import com.fatepay.interfaces.ProcessorInterface;
import com.fatepay.utils.*;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * 支付接口 处理器
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 14:26
 */
public class PayProcessor extends BaseTransProcessor {
    /**
     * 无参构造函数
     */
    public PayProcessor(){}

    /**
     * 构造函数
     * @param request
     */
    public PayProcessor(HttpServletRequest request) {
        this.request = request;
        this.notEmptyColumns = "serviceCode,merchantCode,token,orderNo,orderAmount,notifyUrl";
        this.maxLengthColumns = "serviceCode=20,merchantCode=10,token=32,orderNo=32,orderAmount=16,productName=100,extraReturnParam=100,returnUrl=255,notifyUrl=255";
        this.equalsColumns = "serviceCode=pay";
    }

    /**
     * 处理入口
     * @return
     */
    public TransProcessorResult process() {
        // 1 数据校验
        try {
            // 校验非空字段
            checkNotEmptyColumns();
            // 校验字段长度上限
            checkMaxLengthColumns();
            // 校验字段相等
            checkEqualsColumns();
            // 校验金额
            checkMoney("orderAmount");
            // 校验商户存在 而且 状态正常
            checkMerchantInfo();
            // 校验域名白名单 多个用逗号隔开
            checkWhiteList();
            // 校验token
            checkToken();
        } catch (Exception e) {
            logger.error("数据校验异常发生！", e);
            // 组织失败结果
            String message = e.getMessage();
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_PAGE_RESULT);
            transProcessorResult.setMessage(message);
            return transProcessorResult;
        }

        // 2 查询支付记录
        String merchantCode = StringUtils.trimToEmpty(request.getParameter("merchantCode"));
        MerchantInfo merchantInfo = MerchantInfoUtil.getInstance().getMerchantInfo(merchantCode);
        String orderNo = StringUtils.trimToEmpty(request.getParameter("orderNo"));
        String returnUrl = StringUtils.trimToEmpty(request.getParameter("returnUrl"));
        PayRecord payRecord = payRecordService.getPayRecord(merchantCode, orderNo);

        // 3 存在支付记录
        if(payRecord != null){//存在支付记录
            String orderAmount = StringUtils.trimToEmpty(request.getParameter("orderAmount"));
            // 4 判金额一致
            if(Double.parseDouble(payRecord.getOrderAmount()) != Double.parseDouble(orderAmount)){//金额不一致
                String message = "交易已存在，金额不一致";
                // 5 判返回URL存在
                if(StringUtils.isBlank(returnUrl)){
                    // 6 不存在返回URL 组织失败结果
                    TransProcessorResult transProcessorResult = new TransProcessorResult();
                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_PAGE_RESULT);
                    transProcessorResult.setMessage(message);
                    return transProcessorResult;
                } else {
                    // 7 存在返回URL  组织失败结果
                    TransProcessorResult transProcessorResult = new TransProcessorResult();
                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_JUMP);
                    transProcessorResult.setJumpUrl(returnUrl);

                    // 7.1 返回U支付订单时间
                    String tradeDateTime = payRecord.getTradeDateTime();
                    if(StringUtils.isBlank(tradeDateTime)){
                        tradeDateTime = payRecord.getCreateDate() + payRecord.getCreateTime();
                    }

                    // 7.2 组织返回数据集合
                    String returnServiceCode = "payResultPage";
                    String returnOrderNo = orderNo;
                    String returnOrderAmount = orderAmount;
                    String returnTradeNo = payRecord.getTradeNo();
                    String returnTradeDateTime = tradeDateTime;
                    String returnTradeState = "UNKNOWN";
                    String returnTradeDesc = message;
                    String returnExtraReturnParam = payRecord.getExtraReturnParam();
                    String returnSignType = "MD5";
                    String returnSign = new MD5Util().md5(returnServiceCode + returnOrderNo + returnOrderAmount +
                            returnTradeNo + returnTradeDateTime + returnTradeState + returnTradeDesc + returnExtraReturnParam +
                            returnSignType + merchantInfo.getMd5Key());
                    Properties properties = new Properties();
                    properties.put("serviceCode", returnServiceCode);
                    properties.put("orderNo", returnOrderNo);
                    properties.put("orderAmount", returnOrderAmount);
                    properties.put("tradeNo", returnTradeNo);
                    properties.put("tradeDateTime", returnTradeDateTime);
                    properties.put("tradeState", returnTradeState);
                    properties.put("tradeDesc", returnTradeDesc);
                    properties.put("extraReturnParam", returnExtraReturnParam);
                    properties.put("signType", returnSignType);
                    properties.put("sign", returnSign);
                    transProcessorResult.setProperties(properties);
                    return transProcessorResult;
                }
            } else {//金额一致
                // 8 判没有明确结果 做查询
                if(PayRecordInterface.STATE_SUCCESS != payRecord.getTradeState() &&
                        PayRecordInterface.STATE_FAILED != payRecord.getTradeState()){
                    // 分发处理器 向银行发起查询
                    ISingleTradeQueryResolver singleTradeQueryResolver = SingleTradeQueryResolverDispatcher.dispatch();
                    // 查询交易
                    SingleTradeQueryResult singleTradeQueryResult = singleTradeQueryResolver.query(payRecord);
                    // 判查询成功
                    if(singleTradeQueryResult.isQuerySuccess()){
                        //定义查询返回结果
                        int queryPayState;
                        String tradeDateTime;
                        String tradeBankSeq;
                        // 判交易存在
                        if(!singleTradeQueryResult.isExist()){//不存在
                            queryPayState = PayRecordInterface.STATE_FAILED;
                            tradeDateTime = StringUtils.EMPTY;
                            tradeBankSeq = StringUtils.EMPTY;
                        } else {//存在
                            queryPayState = singleTradeQueryResult.getTradeState();
                            tradeDateTime = singleTradeQueryResult.getTradeDateTime();
                            tradeBankSeq = singleTradeQueryResult.getTradeBankSeq();
                        }
                        // 由于查询交易消耗时间 重新查询最新的支付记录
                        payRecord = payRecordService.getPayRecordByTradeNo(payRecord.getTradeNo());
                        // 为支付状态判优先级 优先则需要修改
                        boolean needUpdate = BaseUtil.comparePriorityForPayState(queryPayState, payRecord.getTradeState());
                        if(needUpdate){
                            payRecord.setTradeState(queryPayState);
                            payRecord.setTradeDateTime(tradeDateTime);
                            payRecord.setTradeBankSeq(tradeBankSeq);
                            payRecord.setTradeDesc(BaseUtil.translatePayRecordStateDesc(queryPayState));
                            payRecord.setUpdateDate(DateUtil.getNowDate());
                            payRecord.setUpdateTime(DateUtil.getNowTime());
                            payRecord.setUpdateIp(IPAddressUtil.getIPAddress(request));
                            // 银行返回交易状态修改支付记录
                            payRecordService.updateTradeState(payRecord);
                        }
                    } // 查询失败不做处理
                }

                // 9 判返回URL存在
                if(StringUtils.isBlank(returnUrl)){
                    // 10 不存在返回URL 组织结果
                    TransProcessorResult transProcessorResult = new TransProcessorResult();
                    if(PayRecordInterface.STATE_SUCCESS == payRecord.getTradeState()){
                        transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_PAGE_RESULT);
                    } else {
                        transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_PAGE_RESULT);
                    }
                    transProcessorResult.setMessage("交易已存在，结果：" + BaseUtil.translatePayRecordStateDesc(payRecord.getTradeState()));
                    return transProcessorResult;
                } else {
                    // 11 存在返回URL 组织结果
                    TransProcessorResult transProcessorResult = new TransProcessorResult();
                    if(PayRecordInterface.STATE_SUCCESS == payRecord.getTradeState()){
                        transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_JUMP);
                    } else {
                        transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_JUMP);
                    }
                    transProcessorResult.setJumpUrl(returnUrl);

                    // 11.1 返回U支付订单时间
                    String tradeDateTime = payRecord.getTradeDateTime();
                    if(StringUtils.isBlank(tradeDateTime)){
                        tradeDateTime = payRecord.getCreateDate() + payRecord.getCreateTime();
                    }

                    // 11.2 组织返回数据集合
                    String returnServiceCode = "payResultPage";
                    String returnOrderNo = orderNo;
                    String returnOrderAmount = orderAmount;
                    String returnTradeNo = payRecord.getTradeNo();
                    String returnTradeDateTime = tradeDateTime;
                    String returnTradeState = BaseUtil.translatePayRecordState(payRecord.getTradeState());
                    String returnTradeDesc = BaseUtil.translatePayRecordStateDesc(payRecord.getTradeState());
                    String returnExtraReturnParam = payRecord.getExtraReturnParam();
                    String returnSignType = "MD5";
                    String returnSign = new MD5Util().md5(returnServiceCode + returnOrderNo + returnOrderAmount +
                            returnTradeNo + returnTradeDateTime + returnTradeState + returnTradeDesc +returnExtraReturnParam +
                            returnSignType +merchantInfo.getMd5Key());
                    Properties properties = new Properties();
                    properties.put("serviceCode", returnServiceCode);
                    properties.put("orderNo", returnOrderNo);
                    properties.put("orderAmount", returnOrderAmount);
                    properties.put("tradeNo", returnTradeNo);
                    properties.put("tradeDateTime", returnTradeDateTime);
                    properties.put("tradeState", returnTradeState);
                    properties.put("tradeDesc", returnTradeDesc);
                    properties.put("extraReturnParam", returnExtraReturnParam);
                    properties.put("signType", returnSignType);
                    properties.put("sign", returnSign);
                    transProcessorResult.setProperties(properties);
                    return transProcessorResult;
                }
            }
        } else {//不存在支付记录
            // 12 新增支付记录
            payRecord = new PayRecord();
            payRecord.setTradeNo(PayRecordTradeNoUtil.getInstance().nextTradeNo());
            payRecord.setMerchantCode(merchantCode);
            payRecord.setOrderNo(orderNo);
            payRecord.setToken(StringUtils.trimToEmpty(request.getParameter("token")));
            payRecord.setProductName(StringUtils.trimToEmpty(request.getParameter("productName")));
            payRecord.setOrderAmount(StringUtils.trimToEmpty(request.getParameter("orderAmount")));
            payRecord.setReturnUrl(StringUtils.trimToEmpty(request.getParameter("returnUrl")));
            payRecord.setNotifyUrl(StringUtils.trimToEmpty(request.getParameter("notifyUrl")));
            payRecord.setTradeState(PayRecordInterface.STATE_INIT);
            payRecord.setTradeDesc(PayRecordInterface.STATE_DESC_INIT);
            payRecord.setExtraReturnParam(StringUtils.trimToEmpty(request.getParameter("extraReturnParam")));
            payRecord.setAlreadyNotify(0);
            payRecord.setNotifyTimes(0);
            payRecord.setCreateDate(DateUtil.getNowDate());
            payRecord.setCreateTime(DateUtil.getNowTime());
            payRecord.setCreateIp(IPAddressUtil.getIPAddress(request));
            payRecordService.save(payRecord);

            // 13 分发处理器 准备数据
            TransProcessorResult transProcessorResult = PayResolverDispatcher.dispatch().prepareData(payRecord, request);
            return transProcessorResult;
        }
    }
}
