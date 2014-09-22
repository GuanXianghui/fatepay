package com.fatepay.bank.dinpay;

import com.fatepay.bank.SingleTradeQueryResult;
import com.fatepay.interfaces.BaseInterface;
import com.fatepay.interfaces.PayRecordInterface;
import com.fatepay.interfaces.SymbolInterface;
import com.fatepay.utils.DateUtil;
import com.fatepay.utils.MD5Util;
import com.fatepay.utils.PropertyUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 智付工具类
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 14:36
 */
public class DinPayUtil implements DinPayInterface {
    /**
     * 日志处理器
     */
    static Logger logger = Logger.getLogger(DinPayUtil.class);

    /**
     * 生成签名
     * 规则:
     * 1. sign、sign_type 两个参数 不加入签名 需要过滤
     * 2. a 到 z 的顺序排序 以“&”字符连接起来 例如 charset=UTF-8&merchant=SNJJ&orderAmount=6860
     * 3. 没有值的参数无需传递，也无需包含到待签名数据中
     * 4. MD5私钥 直接拼接到待签名字符串后面 做MD5算法
     * @param props
     * @return
     */
    public static String sign(Properties props)
    {
        // 1 组装参数到List
        List keyValueList = new ArrayList();
        Enumeration enums = props.keys();
        while (enums.hasMoreElements())
        {
            String key = (String)enums.nextElement();
            String value = props.getProperty(key);
            // 1.1 过滤指定条件参数
            if(IGNORE_SIGN.equals(key) || IGNORE_SIGN_TYPE.equals(key) || null == value
                    || StringUtils.EMPTY.equals(value))
            {
                continue;
            }
            keyValueList.add(key + SymbolInterface.SYMBOL_EQUAL + value);
        }

        // 2 排序List
        Collections.sort(keyValueList);

        // 3 组织签名前串
        String signPre = getSignPreFromParamList(keyValueList);
        logger.info("组织前面前串=[" + signPre + "]");

        // 4 生成md5签名 暂不带编码
        String md5Str = new MD5Util().md5(signPre);
        logger.info("生成签名=[" + md5Str + "]");
        return md5Str;
    }

    /**
     * 组织签名前串
     * @param paramList
     * @return
     */
    public static String getSignPreFromParamList(List paramList)
    {
        if(null == paramList || 0 == paramList.size())
        {
            throw new RuntimeException("签名请求参数为空");
        }
        String signPre = StringUtils.EMPTY;
        for(int i=0;i<paramList.size();i++)
        {
            if(StringUtils.isNotBlank(signPre))
            {
                signPre += SymbolInterface.SYMBOL_BIT_AND;
            }
            signPre += paramList.get(i);
        }
        // 带上md5私钥
        signPre += SymbolInterface.SYMBOL_BIT_AND + "key=" + PropertyUtil.getInstance().getProperty(BaseInterface.DINPAY_MD5KEY);
        return signPre;
    }

    /**
     * 把智付状态翻译成U支付状态
     * @param dpState
     * @return
     */
    public static int translateDpState2UpState(String dpState){
        if("SUCCESS".equalsIgnoreCase(dpState)){
            return PayRecordInterface.STATE_SUCCESS;
        }
        if("FAILED".equalsIgnoreCase(dpState)){
            return PayRecordInterface.STATE_FAILED;
        }
        return PayRecordInterface.STATE_UNKNOWN;
    }

    /**
     * 分析单笔查询返回xml结果
     * (1)当查询成功时，返回XML数据如下：
     * <?xml version="1.0" encoding="UTF-8" ?>
     * <dinpay>
     * <response>
     * <is_success>T</is_success>
     * <sign_type>MD5</sign_type>
     * <sign>56ae9c3286886f76e57e0993625c71fe</sign>
     * <trade>
     * <merchant_code>2181230245</merchant_id>
     * <order_no>210023569</order_no>
     * <order_time>2013-05-10 11:18:00</order_time>
     * <order_amount>100.00</order_amount>
     * <trade_no>128600</trade_no>
     * <trade_time>2013-05-10 11:20:01</trade_time>
     * <trade_status>SUCCESS</trade_status>
     * </trade>
     * </response>
     * </dinpay>
     * (2)当查询失败时，返回XML数据如下：
     * <?xml version="1.0" encoding="UTF-8" ?>
     * <dinpay>
     * <response>
     * <is_success>F</is_success>
     * <error_code>TRADE_IS_NOT_EXIST</error_code>
     * </response>
     * </dinpay>
     * @param responseXml
     * @return
     * @throws Exception
     */
    public static SingleTradeQueryResult analyseSingleTradeQueryReturn(String responseXml) throws Exception
    {
        StringReader sr = new StringReader(responseXml);
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(sr);
        Element root = document.getRootElement();

        // isSuccess标签
        Element isSuccessElement = root.getChild("response").getChild("is_success");
        boolean queryResult = "T".equalsIgnoreCase(isSuccessElement.getTextTrim());// T代表成功,F代表失败
        if(!queryResult)//查询失败
        {
            String errorCode = root.getChild("response").getChild("error_code").getTextTrim();
            logger.info("智付查询单笔交易返回错误码：" + errorCode);
            if(StringUtils.equalsIgnoreCase("TRADE_NOT_EXIST", errorCode) ||
                    StringUtils.equalsIgnoreCase("csp.orderService.order-not-exist", errorCode)){
                logger.info("该笔交易不存在");
                SingleTradeQueryResult singleTradeQueryResult = new SingleTradeQueryResult();
                singleTradeQueryResult.setQuerySuccess(true);
                singleTradeQueryResult.setExist(false);
                return singleTradeQueryResult;
            } else {
                logger.error("智付查询单笔交易返回异常！");
                SingleTradeQueryResult singleTradeQueryResult = new SingleTradeQueryResult();
                singleTradeQueryResult.setQuerySuccess(false);
                return singleTradeQueryResult;
            }
        } else {//查询成功
            /**
             * <trade_no>128600</trade_no>
             * <trade_time>2013-05-10 11:20:01</trade_time>
             * <trade_status>SUCCESS</trade_status>
             */
            String tradeNo = root.getChild("response").getChild("trade").getChild("trade_no").getTextTrim();
            String tradeTime = root.getChild("response").getChild("trade").getChild("trade_time").getTextTrim();
            String tradeStatus = root.getChild("response").getChild("trade").getChild("trade_status").getTextTrim();
            logger.info("智付查询单笔交易成功，查询结果：tradeNo=[" + tradeNo + "],tradeTime=[" + tradeTime +
                    "],tradeStatus=[" + tradeStatus + "]");
            SingleTradeQueryResult singleTradeQueryResult = new SingleTradeQueryResult();
            singleTradeQueryResult.setQuerySuccess(true);
            singleTradeQueryResult.setExist(true);
            /**
             * trade_status 该笔订单交易状态
             * SUCCESS 交易成功
             * FAILED 交易失败
             * UNPAY 未支付
             */
            if(StringUtils.equalsIgnoreCase("SUCCESS", tradeStatus)){
                singleTradeQueryResult.setTradeState(PayRecordInterface.STATE_SUCCESS);
            } else if(StringUtils.equalsIgnoreCase("FAILED", tradeStatus)){
                singleTradeQueryResult.setTradeState(PayRecordInterface.STATE_FAILED);
            } else {
                /**
                 * 比如返回UNPAY，没有支付成功，但是有可能待会儿就成功了
                 * 但是所有返回UNPAY的交易，在T+1早上2点跑T日交易对账的时候，再查一次所有交易
                 * 1.如果返回明确结果，成功或者失败，则修改记录
                 * 2.如果库中成功，查询结果不成功报警！
                 * 3.如果还是返回UNPAY，视为交易失败，如果银行返回成功，会再修改成成功，不过到2点时候应该不大可能有这种事情
                 * 4.返回UNPAY以外的其他不明确结果，报警！
                 * 当然以上这些修改数据库，要比较状态优先级
                 */
                singleTradeQueryResult.setTradeState(PayRecordInterface.STATE_UNKNOWN);
            }
            try{
                if(StringUtils.isNotBlank(tradeTime)){
                    singleTradeQueryResult.setTradeDateTime(DateUtil.getDateTime(DateUtil.getLongDateTime(tradeTime)));
                }
            } catch (Exception e){
                logger.error("智付查询单笔交易返回时间格式有误：" + tradeTime);
            }
            singleTradeQueryResult.setTradeBankSeq(tradeNo);
            return singleTradeQueryResult;
        }
    }
}
