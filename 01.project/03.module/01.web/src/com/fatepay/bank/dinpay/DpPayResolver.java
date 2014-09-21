package com.fatepay.bank.dinpay;

import com.fatepay.api.TransProcessorResult;
import com.fatepay.bank.IPayResolver;
import com.fatepay.entities.PayRecord;
import com.fatepay.interfaces.BaseInterface;
import com.fatepay.interfaces.PayRecordInterface;
import com.fatepay.interfaces.ProcessorInterface;
import com.fatepay.service.IPayRecordService;
import com.fatepay.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * 智付 支付处理器
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 14:20
 */
public class DpPayResolver implements IPayResolver {
    /**
     * 日志处理器
     */
    Logger logger = Logger.getLogger(DpPayResolver.class);

    /**
     * 准备数据
     * @param payRecord
     * @param request
     * @return
     */
    public TransProcessorResult prepareData(PayRecord payRecord, HttpServletRequest request) {
        // 1 组织参数
        Properties props = new Properties();

        // 2 放入签名请求参数
        props.put("service_type", "direct_pay");//业务类型
        props.put("merchant_code", PropertyUtil.getInstance().getProperty(BaseInterface.DINPAY_MERCHANT_CODE));//商家号
        props.put("input_charset", "UTF-8");//参数编码字符集
        props.put("notify_url", PropertyUtil.getInstance().getProperty(BaseInterface.NOTIFY_URL) + payRecord.getTradeNo());//服务器异步通知地址
        props.put("return_url", PropertyUtil.getInstance().getProperty(BaseInterface.RETURN_URL) + payRecord.getTradeNo());//页面跳转同步通知地址
        props.put("interface_version", "V3.0");//接口版本
        props.put("sign_type", "MD5");//签名方式
        props.put("order_no", payRecord.getTradeNo());//商户网站唯一订单号
        props.put("order_time", DateUtil.getLongDateTime(DateUtil.getDateTime(payRecord.getCreateDate(), payRecord.getCreateTime())));//商户订单时间 格式：yyyy-MM-dd HH:mm:ss
        props.put("order_amount", BaseUtil.translateFenToYuan(payRecord.getOrderAmount()));//该笔订单的总金额，以元为单位，精确到小数点后两位
        String productName = payRecord.getProductName();
        if(StringUtils.isBlank(productName)){
            productName = "网购商品";
        }
        props.put("product_name", productName);//商品名称

        // 3 生成签名
        props.put("sign", DinPayUtil.sign(props));

        // 4 组织结果
        TransProcessorResult transProcessorResult = new TransProcessorResult();
        transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_JUMP);
        transProcessorResult.setProperties(props);
        transProcessorResult.setJumpUrl(PropertyUtil.getInstance().getProperty(BaseInterface.DINPAY_PAY_URL));
        return transProcessorResult;
    }

    /**
     * 分析数据
     * @param payRecord 支付记录
     * @param request http请求
     * @param isPage 是否前台跳转
     * @return
     */
    public TransProcessorResult analiseData(PayRecord payRecord, HttpServletRequest request, boolean isPage) {
        // 1 组织参数
        Properties props = new Properties();
        String merchantCode = (String)BaseUtil.setNull2Empty(request.getParameter("merchant_code"));
        props.put("merchant_code", merchantCode);
        String notifyType = (String)BaseUtil.setNull2Empty(request.getParameter("notify_type"));
        props.put("notify_type", notifyType);
        /**
         * 这个作何用处？todo
         * 建议商家系统接收到此通知消息后，用此校验ID向智付支付平台校验此通知的合法性
         */
        String notifyId = (String)BaseUtil.setNull2Empty(request.getParameter("notify_id"));
        props.put("notify_id", notifyId);
        String interfaceVersion = (String)BaseUtil.setNull2Empty(request.getParameter("interface_version"));
        props.put("interface_version", interfaceVersion);
        String orderNo = (String)BaseUtil.setNull2Empty(request.getParameter("order_no"));
        props.put("order_no", orderNo);
        /**
         * 商户订单时间，格式：yyyy-MM-dd HH:mm:ss
         */
        String orderTime = (String)BaseUtil.setNull2Empty(request.getParameter("order_time"));
        props.put("order_time", orderTime);
        /**
         * 该笔订单的总金额，以元为单位，精确到小数点后两位
         */
        String orderAmount = (String)BaseUtil.setNull2Empty(request.getParameter("order_amount"));
        props.put("order_amount", orderAmount);
        String extraReturnParam = (String)BaseUtil.setNull2Empty(request.getParameter("extra_return_param"));
        props.put("extra_return_param", extraReturnParam);
        String tradeNo = (String)BaseUtil.setNull2Empty(request.getParameter("trade_no"));
        props.put("trade_no", tradeNo);
        /**
         * 智付交易订单时间，格式为：yyyy-MM-dd HH:mm:ss
         */
        String tradeTime = (String)BaseUtil.setNull2Empty(request.getParameter("trade_time"));
        props.put("trade_time", tradeTime);
        /**
         * 该笔订单交易状态 SUCCESS 交易成功 FAILED 交易失败
         */
        String tradeStatus = (String)BaseUtil.setNull2Empty(request.getParameter("trade_status"));
        props.put("trade_status", tradeStatus);
        String bankCode = (String)BaseUtil.setNull2Empty(request.getParameter("bank_code"));
        props.put("bank_code", bankCode);
        String bankSeqNo = (String)BaseUtil.setNull2Empty(request.getParameter("bank_seq_no"));
        props.put("bank_seq_no", bankSeqNo);
        String signType = (String)BaseUtil.setNull2Empty(request.getParameter("sign_type"));
        props.put("sign_type", signType);
        String sign = (String)BaseUtil.setNull2Empty(request.getParameter("sign"));
        props.put("sign", sign);
        // 打印出properties对象
        BaseUtil.loggerOut(props);

        // 2 生成签名
        String generateSign;
        try{
            generateSign = DinPayUtil.sign(props);
            logger.info("sign:[" + sign + "],generateSign:[" + generateSign + "]");
        } catch (Exception e){
            String message = "验签失败！";
            logger.error(message, e);
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            // 判是否前台跳转
            if(isPage){
                transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_PAGE_RESULT);
                transProcessorResult.setMessage(message);
            } else {
                transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_STRING);
                transProcessorResult.setMessage(message);
            }
            return transProcessorResult;
        }

        // 3 判签名一致
        if(!StringUtils.equals(sign, generateSign)){
            String message = "验签失败！";
            logger.error(message);
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            // 判是否前台跳转
            if(isPage){
                transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_PAGE_RESULT);
                transProcessorResult.setMessage(message);
            } else {
                transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_STRING);
                transProcessorResult.setMessage(message);
            }
            return transProcessorResult;
        }

        // 4 判金额一致
        if(Integer.parseInt(payRecord.getOrderAmount()) != Integer.parseInt(BaseUtil.translateYuanToFen(orderAmount))){
            String message = "记录已存在，金额不一致！";
            logger.error(message);
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            // 判是否前台跳转
            if(isPage){
                transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_PAGE_RESULT);
                transProcessorResult.setMessage(message);
            } else {
                transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_STRING);
                transProcessorResult.setMessage(message);
            }
            return transProcessorResult;
        }

        /**
         * 5 根据交易结果和记录结果比较是否修改支付结果
         * 优先级是：初始<未知<失败<成功
         * 这里重新查一遍记录，比较下优先级：记录结果如果小于交易结果则修改记录
         * 而且修改记录时候要带上WHERE条件tradeState!=1（交易成功），保证成功状态的记录不被修改
         */
        // 5.1 注入payRecordService
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        IPayRecordService payRecordService = (IPayRecordService)applicationContext.getBean("payRecordService");
        // 5.2 查询最新的支付记录
        payRecord = payRecordService.getPayRecordByTradeNo(payRecord.getTradeNo());
        // 5.3 把智付状态翻译成U支付状态
        int upStateFromDpState = DinPayUtil.translateDpState2UpState(tradeStatus);
        // 5.4 为支付状态判优先级 优先则需要修改
        boolean needUpdate = BaseUtil.comparePriorityForPayState(upStateFromDpState, payRecord.getTradeState());

        // 6 判修改记录
        if(needUpdate){
            payRecord.setTradeState(upStateFromDpState);
            payRecord.setTradeDateTime(DateUtil.getDateTime(DateUtil.getLongDateTime(tradeTime)));
            payRecord.setTradeBankSeq(tradeNo);
            payRecord.setTradeDesc(BaseUtil.translatePayRecordStateDesc(upStateFromDpState));
            payRecord.setUpdateDate(DateUtil.getNowDate());
            payRecord.setUpdateTime(DateUtil.getNowTime());
            payRecord.setUpdateIp(IPAddressUtil.getIPAddress(request));
            // 7 银行返回交易状态修改支付记录
            payRecordService.updateTradeState(payRecord);
        }
        /**
         * 8 返回给商户结果以库中记录为准，因为：
         * 8.1 需要修改记录 到这里已经修改记录了 则返回给商户结果以库中记录为准
         * 8.2 不需要修改 则表示库中记录优先级较高 则返回给商户结果以库中记录为准
         */
        // 9 判返回URL存在
        if(StringUtils.isBlank(payRecord.getReturnUrl())){
            // 10 不存在返回URL 组织结果
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            // 判是否前台跳转
            if(isPage){
                if(PayRecordInterface.STATE_SUCCESS == payRecord.getTradeState()){
                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_PAGE_RESULT);
                } else {
                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_PAGE_RESULT);
                }
                transProcessorResult.setMessage("交易结果：" + BaseUtil.translatePayRecordStateDesc(payRecord.getTradeState()));
            } else {
                if(PayRecordInterface.STATE_SUCCESS == payRecord.getTradeState()){
                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_STRING);
                    transProcessorResult.setMessage("SUCCESS");
                } else {
                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_STRING);
                    transProcessorResult.setMessage("交易未成功！");
                }
            }
            return transProcessorResult;
        } else {
            // 11 存在返回URL 组织结果
            TransProcessorResult transProcessorResult = new TransProcessorResult();
            // 判是否前台跳转
            if(isPage){
                if(PayRecordInterface.STATE_SUCCESS == payRecord.getTradeState()){
                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_JUMP);
                } else {
                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_JUMP);
                }
                transProcessorResult.setJumpUrl(payRecord.getReturnUrl());

                // 11.1 返回U支付订单时间
                String tradeDateTime = payRecord.getTradeDateTime();
                if(StringUtils.isBlank(tradeDateTime)){
                    tradeDateTime = payRecord.getCreateDate() + payRecord.getCreateTime();
                }

                // 11.2 组织返回数据集合
                String returnServiceCode = "payResultPage";
                String returnOrderNo = payRecord.getOrderNo();
                String returnOrderAmount = payRecord.getOrderAmount();
                String returnTradeNo = payRecord.getTradeNo();
                String returnTradeDateTime = tradeDateTime;
                String returnTradeState = BaseUtil.translatePayRecordState(payRecord.getTradeState());
                String returnTradeDesc = BaseUtil.translatePayRecordStateDesc(payRecord.getTradeState());
                String returnSignType = "MD5";
                String returnSign = new MD5Util().md5(returnServiceCode + returnOrderNo + returnOrderAmount +
                        returnTradeNo + returnTradeDateTime + returnTradeState + returnTradeDesc + returnSignType +
                        MerchantInfoUtil.getInstance().getMerchantInfo(payRecord.getMerchantCode()).getMd5Key());
                Properties properties = new Properties();
                properties.put("serviceCode", returnServiceCode);
                properties.put("orderNo", returnOrderNo);
                properties.put("orderAmount", returnOrderAmount);
                properties.put("tradeNo", returnTradeNo);
                properties.put("tradeDateTime", returnTradeDateTime);
                properties.put("tradeState", returnTradeState);
                properties.put("tradeDesc", returnTradeDesc);
                properties.put("signType", returnSignType);
                properties.put("sign", returnSign);
                transProcessorResult.setProperties(properties);
            } else {
                if(PayRecordInterface.STATE_SUCCESS == payRecord.getTradeState()){
                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_SUCCESS_STRING);
                    transProcessorResult.setMessage("SUCCESS");
                } else {
                    transProcessorResult.setResultType(ProcessorInterface.RESULT_TYPE_FAIL_STRING);
                    transProcessorResult.setMessage("交易未成功！");
                }
            }
            return transProcessorResult;
        }
    }
}
