package com.fatepay;

import com.fatepay.api.ITransProcessor;
import com.fatepay.api.TransProcessorDispatcher;
import com.fatepay.api.TransProcessorResult;
import com.fatepay.interfaces.ProcessorInterface;
import com.fatepay.interfaces.TransInterface;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 商户调用交易Action
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 13:16
 */
@Component("transAction")
@Scope("prototype")
public class TransAction extends BaseAction {

    String serviceCode;//服务名称

    /**
     * 空的构造方法
     */
    public TransAction(){}

    /**
     * 入口
     * @return
     */
    public String execute() throws Exception {
        // 判服务名称不为空
        if(StringUtils.isBlank(serviceCode)){
            String resp = "服务名称不能为空！";
            write(resp);
            return null;
        }

        // 判是否支持该服务
        if(!StringUtils.equals(serviceCode, TransInterface.SERVICE_CODE_GET_TOKEN) &&
                !StringUtils.equals(serviceCode, TransInterface.SERVICE_CODE_PAY) &&
                !StringUtils.equals(serviceCode, TransInterface.SERVICE_CODE_SINGLE_QUERY)){
            String resp = "不支持该服务：" + serviceCode + "！";
            write(resp);
            return null;
        }

        // 分发处理器
        ITransProcessor transProcessor = TransProcessorDispatcher.dispatch(serviceCode, request);
        TransProcessorResult transProcessorResult = transProcessor.process();

        /**
         * 判结果类型，字符串输出 或者 页面跳转到其他系统 或者 U支付页面显示结果
         * 1 字符串输出
         */
        if(StringUtils.equals(ProcessorInterface.RESULT_TYPE_SUCCESS_STRING, transProcessorResult.getResultType())
                || StringUtils.equals(ProcessorInterface.RESULT_TYPE_FAIL_STRING, transProcessorResult.getResultType())){
            write(transProcessorResult.getMessage());
            return null;
        }

        // 2 页面跳转到其他系统
        if(StringUtils.equals(ProcessorInterface.RESULT_TYPE_SUCCESS_JUMP, transProcessorResult.getResultType())
                || StringUtils.equals(ProcessorInterface.RESULT_TYPE_FAIL_JUMP, transProcessorResult.getResultType())){
            jumpUrl = transProcessorResult.getJumpUrl();
            properties = transProcessorResult.getProperties();
            return "jump";
        }

        // 3 U支付页面显示结果
        if(StringUtils.equals(ProcessorInterface.RESULT_TYPE_SUCCESS_PAGE_RESULT, transProcessorResult.getResultType())
                || StringUtils.equals(ProcessorInterface.RESULT_TYPE_FAIL_PAGE_RESULT, transProcessorResult.getResultType())){
            message = transProcessorResult.getMessage();
            return "result";
        }

        return null;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }
}
