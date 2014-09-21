package com.fatepay.api;

import com.fatepay.entities.MerchantInfo;
import com.fatepay.interfaces.MerchantInfoInterface;
import com.fatepay.interfaces.PayRecordInterface;
import com.fatepay.interfaces.SymbolInterface;
import com.fatepay.service.IPayRecordService;
import com.fatepay.utils.DateUtil;
import com.fatepay.utils.MD5Util;
import com.fatepay.utils.MerchantInfoUtil;
import com.fatepay.utils.TokenUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.http.HttpServletRequest;

/**
 * 基础交易处理器 抽象类
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 16:31
 */
public abstract class BaseTransProcessor implements ITransProcessor {
    /**
     * 日志处理器
     */
    Logger logger = Logger.getLogger(BaseTransProcessor.class);
    /**
     * 支付记录服务类
     */
    IPayRecordService payRecordService;
    /**
     * request对象
     */
    HttpServletRequest request;
    /**
     * 非空字段
     * 格式：字段1,字段2,字段3,...
     */
    String notEmptyColumns;
    /**
     * 字段长度上限
     * 格式：字段1=长度1,字段2=长度2,字段3=长度3,...
     */
    String maxLengthColumns;
    /**
     * 字段相等 分隔字符串
     */
    public static final String EQUALS_COLUMN_SPLIT_STR = "_SPLIT_STR_";
    /**
     * 字段相等
     * 格式：字段1=字符串1,字段2=字符串2,字段3=字符串3,...
     */
    String equalsColumns;
    /**
     * MD5参数顺序
     * 格式：字段1,字段2,字段3,...
     */
    String md5ParamOrder;

    /**
     * 无参构造方法
     */
    public BaseTransProcessor(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        payRecordService = (IPayRecordService)applicationContext.getBean("payRecordService");
    }

    /**
     * 校验非空字段
     */
    public void checkNotEmptyColumns() throws Exception {
        // 空则不校验
        if(StringUtils.isBlank(notEmptyColumns)){
            return;
        }
        String[] columns = notEmptyColumns.split(SymbolInterface.SYMBOL_COMMA);
        //逐个字段校验
        for(String column : columns){
            if(StringUtils.isEmpty(request.getParameter(column))){
                throw new RuntimeException(column + "不允许为空！");
            }
        }
    }

    /**
     * 校验字段长度上限
     */
    public void checkMaxLengthColumns() throws Exception {
        // 空则不校验
        if(StringUtils.isBlank(maxLengthColumns)){
            return;
        }
        String[] columns = maxLengthColumns.split(SymbolInterface.SYMBOL_COMMA);
        //逐个字段校验
        for(String column : columns){
            String columnName = column.substring(0, column.indexOf(SymbolInterface.SYMBOL_EQUAL));
            int length = Integer.parseInt(column.substring(column.indexOf(SymbolInterface.SYMBOL_EQUAL) + 1));
            if(StringUtils.trimToEmpty(request.getParameter(columnName)).length() > length){
                throw new RuntimeException(columnName + "长度超过上限！");
            }
        }
    }

    /**
     * 校验字段相等
     * @throws Exception
     */
    public void checkEqualsColumns() throws Exception {
        // 空则不校验
        if(StringUtils.isBlank(equalsColumns)){
            return;
        }
        String[] columns = equalsColumns.split(EQUALS_COLUMN_SPLIT_STR);
        //逐个字段校验
        for(String column : columns){
            String columnName = column.substring(0, column.indexOf(SymbolInterface.SYMBOL_EQUAL));
            String columnValue = column.substring(column.indexOf(SymbolInterface.SYMBOL_EQUAL) + 1);
            if(!StringUtils.equals(StringUtils.trimToEmpty(request.getParameter(columnName)), columnValue)){
                throw new RuntimeException(columnName + "字段不合法！");
            }
        }
    }

    /**
     * 校验商户存在 而且 状态正常
     * @throws Exception
     */
    public void checkMerchantInfo() throws Exception {
        String merchantCode = StringUtils.trimToEmpty(request.getParameter("merchantCode"));
        MerchantInfo merchantInfo = MerchantInfoUtil.getInstance().getMerchantInfo(merchantCode);
        if(null == merchantInfo){
            throw new RuntimeException("该商户[" + merchantCode + "]不存在！");
        }
        if(MerchantInfoInterface.STATE_NORMAL != merchantInfo.getState()){
            throw new RuntimeException("该商户[" + merchantCode + "]状态不正常！");
        }
    }

    /**
     * 校验域名白名单 多个用逗号隔开
     * @throws Exception
     */
    public void checkWhiteList() throws Exception {
        String merchantCode = StringUtils.trimToEmpty(request.getParameter("merchantCode"));
        MerchantInfo merchantInfo = MerchantInfoUtil.getInstance().getMerchantInfo(merchantCode);
        String[] whiteUrlArray = merchantInfo.getWhiteList().split(SymbolInterface.SYMBOL_COMMA);
        String referer = request.getHeader("referer");
        logger.info("访问该接口的请求来自域名为：" + referer);
        if(StringUtils.isNotBlank(referer)){
            for(String whiteUrl : whiteUrlArray){
                if(referer.indexOf(whiteUrl) > -1){
                    return;
                }
            }
        }

        throw new RuntimeException("该请求来源不合法！");
    }

    /**
     * MD5校验
     * @throws Exception
     */
    public void checkMd5() throws Exception {
        String merchantCode = StringUtils.trimToEmpty(request.getParameter("merchantCode"));
        MerchantInfo merchantInfo = MerchantInfoUtil.getInstance().getMerchantInfo(merchantCode);
        String md5Key = merchantInfo.getMd5Key();
        String[] columns = md5ParamOrder.split(SymbolInterface.SYMBOL_COMMA);
        String preMd5Str = StringUtils.EMPTY;
        for(String column : columns){
            String value = request.getParameter(column);
            if(null != value){
                preMd5Str += value;
            }
        }
        preMd5Str += md5Key;
        String sign = new MD5Util().md5(preMd5Str);
        String requestSign = request.getParameter("sign");
        if(!StringUtils.equals(sign, requestSign)){
            logger.info("验签前的字符串：[" + preMd5Str + "],md5Key：[" + md5Key + "]," +
                    "接受签名：[" + requestSign + "],U支付产生签名：[" + sign + "]");
            throw new RuntimeException("签名验证失败！");
        }
    }

    /**
     * 创建token串
     */
    public String createToken(){
        String merchantCode = StringUtils.trimToEmpty(request.getParameter("merchantCode"));
        MerchantInfo merchantInfo = MerchantInfoUtil.getInstance().getMerchantInfo(merchantCode);
        String token = TokenUtil.getInstance().createToken(merchantCode);
        String expireTime = DateUtil.getDateTime(TokenUtil.getInstance().getTokenExpireTime(merchantCode, token));
        String message = "{" +
                "\"isSuccess\":\"Y\"," +
                "\"token\":\"" + token + "\"," +
                "\"expireTime\":\"" + expireTime + "\"," +
                "\"desc\":\"获取token成功！\"," +
                "\"returnUrl\":\"" + merchantInfo.getReturnUrl() + "\"," +
                "\"notifyUrl\":\"" + merchantInfo.getNotifyUrl() + "\"" +
                "}";
        return message;
    }

    /**
     * 校验token
     */
    public void checkToken(){
        String merchantCode = StringUtils.trimToEmpty(request.getParameter("merchantCode"));
        String token = StringUtils.trimToEmpty(request.getParameter("token"));
        if(!TokenUtil.getInstance().checkToken(merchantCode, token)){
            throw new RuntimeException("token不存在或者已失效！");
        }
    }
}
