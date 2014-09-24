package com.fatepay.utils;

import com.fatepay.bank.dinpay.DinPayUtil;
import junit.framework.TestCase;

import java.util.Properties;

/**
 * MD5测试
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 10:13
 */
public class MD5UtilTest extends TestCase {
    /**
     * 测试md5
     */
    public void testMd5(){
        String preMd5Str = "880001getToken1.0UTF-8MD5";
        String md5Key = "293801203741092834";
        preMd5Str += md5Key;
        String sign = new MD5Util().md5(preMd5Str);
        System.out.println(sign);
    }

    /**
     * 测试智付md5
     */
    public void testDinpayMd5(){
        Properties props = new Properties();
        props.put("merchant_code", "000001");
        props.put("notify_type", "page_notify");
        props.put("notify_id", "asdkofahsidh0f1");
        props.put("interface_version", "V3.0");
        props.put("order_no", "2014092000000001");//商户网站唯一订单号
        props.put("order_time", "2014-09-20 12:12:12");
        props.put("order_amount", "1.00");//商户订单总金额
        props.put("extra_return_param", "");
        props.put("trade_no", "20140920098621");//智付交易订单号
        props.put("trade_time", "2014-09-20 13:13:13");//智付交易订单时间
        props.put("trade_status", "SUCCESS");//交易状态
        props.put("bank_code", "");
        props.put("bank_seq_no", "");
        props.put("sign_type", "MD5");
        props.put("sign", "");
        String sign = DinPayUtil.sign(props);
        System.out.println(sign);
    }

    /**
     * 测试md5
     */
    public void testForceSingleQueryMd5(){
        String merchantCode = "880001";
        String orderNo = "M_201409230000000014";
        String md5Key = "asdf1234#$%^";
        String preMd5Str = merchantCode + orderNo + md5Key;
        String sign = new MD5Util().md5(preMd5Str);
        System.out.println(sign);
    }
}
