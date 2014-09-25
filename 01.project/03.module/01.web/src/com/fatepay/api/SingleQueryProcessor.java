package com.fatepay.api;

import com.fatepay.entities.PayRecord;
import com.fatepay.interfaces.ProcessorInterface;
import com.fatepay.utils.BaseUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 单笔交易查询接口 处理器
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 14:26
 */
public class SingleQueryProcessor extends BaseTransProcessor {
    /**
     * 无参构造函数
     */
    public SingleQueryProcessor(){}

    /**
     * 构造函数
     * @param request
     */
    public SingleQueryProcessor(HttpServletRequest request) {
        this.request = request;
        this.notEmptyColumns = "serviceCode,merchantCode,token,orderNo";
        this.maxLengthColumns = "serviceCode=20,merchantCode=10,token=32,orderNo=32";
        this.equalsColumns = "serviceCode=singleQuery";
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
            // 校验商户存在 而且 状态正常
            checkMerchantInfo();
            // 校验域名白名单 多个用逗号隔开
            checkWhiteList();
            // 校验token
            checkToken();
        } catch (Exception e) {
            logger.error("数据校验异常发生！", e);
            // 组织失败结果
            String message = "{" +
                    "\"isSuccess\":\"N\"," +
                    "\"tradeNo\":\"\"," +
                    "\"tradeDateTime\":\"\"," +
                    "\"tradeState\":\"\"," +
                    "\"tradeDesc\":\"" + translateErrorCode(e.getMessage()) + "\"," +
                    "\"errorCode\":\"" + e.getMessage() + "\"" +
                    "}";
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_STRING);
            transProcessorResult.setMessage(message);
            return transProcessorResult;
        }

        // 2 查支付记录
        String merchantCode = StringUtils.trimToEmpty(request.getParameter("merchantCode"));
        String orderNo = StringUtils.trimToEmpty(request.getParameter("orderNo"));
        PayRecord payRecord = payRecordService.getPayRecord(merchantCode, orderNo);
        if(payRecord == null){//记录不存在
            // 组织失败结果
            String message = "{" +
                    "\"isSuccess\":\"N\"," +
                    "\"tradeNo\":\"\"," +
                    "\"tradeDateTime\":\"\"," +
                    "\"tradeState\":\"\"," +
                    "\"tradeDesc\":\"记录不存在！\"," +
                    "\"errorCode\":\"" + "RECORD_NOT_EXIST" + "\"" +
                    "}";
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_STRING);
            transProcessorResult.setMessage(message);
            return transProcessorResult;
        } else {//记录存在
//            if(PayRecordInterface.STATE_SUCCESS == payRecord.getTradeState() ||
//                    PayRecordInterface.STATE_FAILED == payRecord.getTradeState()){//有明确结果
            /**
             * 不管是否有明确结果都直接返回给商户，不调用银行查询交易，将商户与银行之间的联系断掉，只能依赖我们日终对账去查银行系统
             */
                String tradeDateTime = payRecord.getTradeDateTime();
                if(StringUtils.isBlank(tradeDateTime)){
                    tradeDateTime = payRecord.getCreateDate() + payRecord.getCreateTime();
                }
                // 组织成功结果
                String message = "{" +
                        "\"isSuccess\":\"Y\"," +
                        "\"tradeNo\":\"" + payRecord.getTradeNo() + "\"," +
                        "\"tradeDateTime\":\"" + tradeDateTime + "\"," +
                        "\"tradeState\":\"" + BaseUtil.translatePayRecordState(payRecord.getTradeState()) + "\"," +
                        "\"tradeDesc\":\"" + StringUtils.trimToEmpty(payRecord.getTradeDesc()) + "\"," +
                        "\"errorCode\":\"\"" +
                        "}";
                TransProcessorResult transProcessorResult = new TransProcessorResult();
                transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_STRING);
                transProcessorResult.setMessage(message);
                return transProcessorResult;
//            } else {//暂无明确结果
//                // 分发处理器 向银行发起查询
//                ISingleTradeQueryResolver singleTradeQueryResolver = SingleTradeQueryResolverDispatcher.dispatch();
//                // 查询交易
//                SingleTradeQueryResult singleTradeQueryResult = singleTradeQueryResolver.query(payRecord);
//                // 判断查询结果
//                if(!singleTradeQueryResult.isQuerySuccess()){//查询失败
//                    // 组织失败结果
//                    String message = "{" +
//                            "\"isSuccess\":\"N\"," +
//                            "\"tradeNo\":\"\"," +
//                            "\"tradeDateTime\":\"\"," +
//                            "\"tradeState\":\"\"," +
//                            "\"tradeDesc\":\"交易结果未知，查询银行返回异常发生！\"," +
//                            "\"errorCode\":\"BANK_EXCEPTION_HAPPEN\"" +
//                            "}";
//                    TransProcessorResult transProcessorResult = new TransProcessorResult();
//                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_STRING);
//                    transProcessorResult.setMessage(message);
//                    return transProcessorResult;
//                } else {//查询成功
//                    //定义查询返回结果
//                    int queryPayState;
//                    String tradeDateTime;
//                    String tradeBankSeq;
//                    // 判交易存在
//                    if(!singleTradeQueryResult.isExist()){//不存在
//                        queryPayState = PayRecordInterface.STATE_FAILED;
//                        tradeDateTime = StringUtils.EMPTY;
//                        tradeBankSeq = StringUtils.EMPTY;
//                    } else {//存在
//                        queryPayState = singleTradeQueryResult.getTradeState();
//                        tradeDateTime = singleTradeQueryResult.getTradeDateTime();
//                        tradeBankSeq = singleTradeQueryResult.getTradeBankSeq();
//                    }
//                    // 由于查询交易消耗时间 重新查询最新的支付记录
//                    payRecord = payRecordService.getPayRecordByTradeNo(payRecord.getTradeNo());
//                    // 为支付状态判优先级 优先则需要修改
//                    boolean needUpdate = BaseUtil.comparePriorityForPayState(queryPayState, payRecord.getTradeState());
//                    if(needUpdate){
//                        payRecord.setTradeState(queryPayState);
//                        payRecord.setTradeDateTime(tradeDateTime);
//                        payRecord.setTradeBankSeq(tradeBankSeq);
//                        payRecord.setTradeDesc(BaseUtil.translatePayRecordStateDesc(queryPayState));
//                        payRecord.setUpdateDate(DateUtil.getNowDate());
//                        payRecord.setUpdateTime(DateUtil.getNowTime());
//                        payRecord.setUpdateIp(IPAddressUtil.getIPAddress(request));
//                        // 银行返回交易状态修改支付记录
//                        payRecordService.updateTradeState(payRecord);
//                    }
//                    tradeDateTime = payRecord.getTradeDateTime();
//                    if(StringUtils.isBlank(tradeDateTime)){
//                        tradeDateTime = payRecord.getCreateDate() + payRecord.getCreateTime();
//                    }
//                    // 组织成功结果
//                    String message = "{" +
//                            "\"isSuccess\":\"Y\"," +
//                            "\"tradeNo\":\"" + payRecord.getTradeNo() + "\"," +
//                            "\"tradeDateTime\":\"" + tradeDateTime + "\"," +
//                            "\"tradeState\":\"" + BaseUtil.translatePayRecordState(payRecord.getTradeState()) + "\"," +
//                            "\"tradeDesc\":\"" + StringUtils.trimToEmpty(payRecord.getTradeDesc()) + "\"," +
//                            "\"errorCode\":\"\"" +
//                            "}";
//                    TransProcessorResult transProcessorResult = new TransProcessorResult();
//                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_STRING);
//                    transProcessorResult.setMessage(message);
//                    return transProcessorResult;
//                }
//            }
        }
    }
}
