package com.fatepay;

import com.fatepay.api.TransProcessorResult;
import com.fatepay.bank.PayResolverDispatcher;
import com.fatepay.entities.PayRecord;
import com.fatepay.interfaces.ProcessorInterface;
import com.fatepay.service.IPayRecordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 支付返回前台Action
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 21:36
 */
@Component("payReturnPgAction")
@Scope("prototype")
public class PayReturnPgAction extends BaseAction {
    /**
     * 交易网关流水
     */
    String uPayTradeNo;

    /**
     * 支付记录服务类
     */
    private IPayRecordService payRecordService;

    /**
     * 空的构造方法
     */
    public PayReturnPgAction(){}

    /**
     * 入口
     * @return
     */
    public String execute(){
        logger.info("uPayTradeNo=" + uPayTradeNo);
        // 判交易网关流水不为空
        if(StringUtils.isEmpty(uPayTradeNo)){
            message = "交易网关流水不能为空！";
            return "result";
        }
        // 根据交易网关流水查询支付记录
        PayRecord payRecord = payRecordService.getPayRecordByTradeNo(uPayTradeNo);
        // 判不存在
        if(payRecord == null){
            message = "不存在该笔交易网关流水：" + uPayTradeNo;
            return "result";
        }

        // 分发处理器 分析数据
        TransProcessorResult transProcessorResult = PayResolverDispatcher.dispatch().analiseData(payRecord, request, true);

        // 页面跳转到其他系统
        if(StringUtils.equals(ProcessorInterface.RESULT_TYPE_SUCCESS_JUMP, transProcessorResult.getResultType())
                || StringUtils.equals(ProcessorInterface.RESULT_TYPE_FAIL_JUMP, transProcessorResult.getResultType())){
            jumpUrl = transProcessorResult.getJumpUrl();
            properties = transProcessorResult.getProperties();
            return "jump";
        }

        // U支付页面显示结果
        if(StringUtils.equals(ProcessorInterface.RESULT_TYPE_SUCCESS_PAGE_RESULT, transProcessorResult.getResultType())
                || StringUtils.equals(ProcessorInterface.RESULT_TYPE_FAIL_PAGE_RESULT, transProcessorResult.getResultType())){
            message = transProcessorResult.getMessage();
            return "result";
        }

        return null;
    }

    public String getUPayTradeNo() {
        return uPayTradeNo;
    }

    public void setUPayTradeNo(String uPayTradeNo) {
        this.uPayTradeNo = uPayTradeNo;
    }

    public IPayRecordService getPayRecordService() {
        return payRecordService;
    }

    @Resource
    public void setPayRecordService(IPayRecordService payRecordService) {
        this.payRecordService = payRecordService;
    }
}
