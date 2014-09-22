package com.fatepay.bank.dinpay;

import com.fatepay.bank.ISingleTradeQueryResolver;
import com.fatepay.bank.SingleTradeQueryResult;
import com.fatepay.entities.PayRecord;
import com.fatepay.interfaces.BaseInterface;
import com.fatepay.utils.*;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * 智付 单笔交易查询处理器
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 14:20
 */
public class DpSingleTradeQueryResolver implements ISingleTradeQueryResolver {
    /**
     * 日志处理器
     */
    Logger logger = Logger.getLogger(DpSingleTradeQueryResolver.class);

    /**
     * 查询交易
     * @param payRecord
     * @return 单笔交易查询结果
     */
    public SingleTradeQueryResult query(PayRecord payRecord) {
        // 1 组织参数
        Properties props = new Properties();

        // 2 放入签名请求参数
        props.put("service_type", "single_trade_query");//业务类型
        props.put("merchant_code", PropertyUtil.getInstance().getProperty(BaseInterface.DINPAY_MERCHANT_CODE));//商家号
        props.put("interface_version", "V3.0");//接口版本
        props.put("sign_type", "MD5");//签名方式
        props.put("order_no", payRecord.getTradeNo());//商户网站唯一订单号

        // 3 生成签名
        props.put("sign", DinPayUtil.sign(props));

        // 4 向智付发起查询
        String responseXml;
        try {
            responseXml = HttpClientUtils.postProps(PropertyUtil.getInstance().
                    getProperty(BaseInterface.DINPAY_QUERY_URL), props, "UTF-8", "UTF-8");
            System.out.println(responseXml);
            logger.info("查询交易返回信息：[" + responseXml + "]");
        } catch (Exception e) {
            logger.error("查询交易异常发生！", e);
            SingleTradeQueryResult singleTradeQueryResult = new SingleTradeQueryResult();
            singleTradeQueryResult.setQuerySuccess(false);
            return singleTradeQueryResult;
        }

        // 5 分析单笔查询返回xml结果
        try {
            return DinPayUtil.analyseSingleTradeQueryReturn(responseXml);
        } catch (Exception e) {
            logger.error("分析单笔查询返回xml结果，异常发生！", e);
            SingleTradeQueryResult singleTradeQueryResult = new SingleTradeQueryResult();
            singleTradeQueryResult.setQuerySuccess(false);
            return singleTradeQueryResult;
        }
    }

    public static void main(String[] params) throws Exception {
        String tradeNo = "你好";
        tradeNo = new String(tradeNo.getBytes("UTF-16"));
        System.out.println(tradeNo);
        PayRecord payRecord = new PayRecord();
        payRecord.setTradeNo(tradeNo);
        new DpSingleTradeQueryResolver().query(payRecord);
    }
}
