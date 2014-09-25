package com.fatepay.api;

import com.fatepay.entities.MerchantInfo;
import com.fatepay.interfaces.MerchantInfoInterface;
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
                logger.error(column + "不允许为空！");
                throw new RuntimeException("PARAM_ERROR");
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
                logger.error(columnName + "长度超过上限！");
                throw new RuntimeException("PARAM_ERROR");
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
                logger.error(columnName + "不等于固定值" + columnValue);
                throw new RuntimeException("PARAM_ERROR");
            }
        }
    }

    /**
     * 校验金额
     * 以元为单位，精确到小数点后两位，举例：10.01
     * @param moneyParamName 金额字段名称
     * @throws Exception
     */
    public void checkMoney(String moneyParamName) throws Exception {
        try{
            String money = StringUtils.trimToEmpty(request.getParameter(moneyParamName));
            if(StringUtils.isBlank(money)){
                throw new RuntimeException("金额不能为空");
            }
            int dotIndex = money.indexOf(SymbolInterface.SYMBOL_DOT);
            if(dotIndex > -1 && money.substring(dotIndex+1).length()>2){
                throw new RuntimeException("金额不合法");
            }
            double doubleMoney = Double.parseDouble(money);
            if(doubleMoney <= 0){
                throw new RuntimeException("金额必须大于0");
            }
        } catch (Exception e){
            logger.error("金额校验失败，" + moneyParamName + "=" +
                    StringUtils.trimToEmpty(request.getParameter(moneyParamName)), e);
            throw new RuntimeException("金额校验失败");
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
            logger.error("该商户[" + merchantCode + "]不存在！");
            throw new RuntimeException("MERCHANT_NOT_EXIST");
        }
        if(MerchantInfoInterface.STATE_NORMAL != merchantInfo.getState()){
            logger.error("该商户[" + merchantCode + "]状态不正常！");
            throw new RuntimeException("MERCHANT_NOT_NORMAL");
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
        logger.error("该请求来源不合法！");
        throw new RuntimeException("REQUEST_DENIED");
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
            logger.error("token不存在或者已失效！");
            throw new RuntimeException("REQUEST_NOT_EFFECTIVE");
        }
    }

    /**
     * 翻译错误代码
     * @param errorCode
     * @return
     */
    public String translateErrorCode(String errorCode){
        if(StringUtils.equalsIgnoreCase(errorCode, "PARAM_ERROR")){
            return "字段有误";
        } else if(StringUtils.equalsIgnoreCase(errorCode, "MERCHANT_NOT_EXIST")){
            return "商户不存在";
        } else if(StringUtils.equalsIgnoreCase(errorCode, "MERCHANT_NOT_NORMAL")){
            return "商户不合法";
        } else if(StringUtils.equalsIgnoreCase(errorCode, "REQUEST_NOT_EFFECTIVE")){
            return "请求失效";
        } else if(StringUtils.equalsIgnoreCase(errorCode, "REQUEST_DENIED")){
            return "请求受限";
        } else if(StringUtils.equalsIgnoreCase(errorCode, "RECORD_NOT_EXIST")){
            return "记录不存在";
        } else {
            return "其他";
        }
    }
}
