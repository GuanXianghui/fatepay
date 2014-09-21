package com.fatepay.bank.dinpay;

import com.fatepay.interfaces.BaseInterface;
import com.fatepay.interfaces.PayRecordInterface;
import com.fatepay.interfaces.SymbolInterface;
import com.fatepay.utils.MD5Util;
import com.fatepay.utils.PropertyUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
}
