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
            // 组织失败结果
            String message = "{" +
                    "\"isSuccess\":\"N\"," +
                    "\"tradeNo\":\"\"," +
                    "\"tradeDateTime\":\"\"," +
                    "\"tradeState\":\"\"," +
                    "\"tradeDesc\":\"" + e.getMessage() + "\"" +
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
        if(payRecord == null){
            // 组织失败结果
            String message = "{" +
                    "\"isSuccess\":\"N\"," +
                    "\"tradeNo\":\"\"," +
                    "\"tradeDateTime\":\"\"," +
                    "\"tradeState\":\"\"," +
                    "\"tradeDesc\":\"记录不存在！\"" +
                    "}";
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_STRING);
            transProcessorResult.setMessage(message);
            return transProcessorResult;
        } else {
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
                    "\"tradeDesc\":\"" + payRecord.getTradeDesc() + "\"" +
                    "}";
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_STRING);
            transProcessorResult.setMessage(message);
            return transProcessorResult;
        }
    }
}
