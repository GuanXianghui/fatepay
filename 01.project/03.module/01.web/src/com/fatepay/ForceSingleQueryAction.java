package com.fatepay;

import com.fatepay.bank.ISingleTradeQueryResolver;
import com.fatepay.bank.SingleTradeQueryResolverDispatcher;
import com.fatepay.bank.SingleTradeQueryResult;
import com.fatepay.entities.PayRecord;
import com.fatepay.interfaces.BaseInterface;
import com.fatepay.interfaces.PayRecordInterface;
import com.fatepay.service.IPayRecordService;
import com.fatepay.utils.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 强制单笔交易查询Action
 * 该接口调用放为后台对账跑批程序，调用时间为凌晨跑批的时候
 * 不管该笔记录是成功，还是失败，还是未知，都向银行发起查询，修改最新交易状态(不比较状态优先级，强制修改记录，以银行返回状态为准)，将结果返回给调用方
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-24 12:20
 */
@Component("forceSingleQueryAction")
@Scope("prototype")
public class ForceSingleQueryAction extends BaseAction {
    /**
     * 商户号
     */
    String merchantCode;

    /**
     * 订单号
     */
    String orderNo;

    /**
     * 签名串
     */
    String sign;

    /**
     * 支付记录服务类
     */
    private IPayRecordService payRecordService;

    /**
     * 空的构造方法
     */
    public ForceSingleQueryAction(){}

    /**
     * 入口
     * @return
     */
    public String execute() throws Exception {
        // 判服务名称不为空
        if(StringUtils.isBlank(merchantCode) || StringUtils.isBlank(orderNo)){
            // 组织失败结果
            write(createFailResponse("商户号或者订单号不能为空！"));
            return null;
        }

        // 验证签名
        String preMd5Str = merchantCode + orderNo + PropertyUtil.getInstance().getProperty(BaseInterface.MD5_KEY_WITH_BACK);
        String generateSign = new MD5Util().md5(preMd5Str);
        if(!StringUtils.equalsIgnoreCase(sign, generateSign)){
            // 组织失败结果
            write(createFailResponse("签名验证失败！"));
            return null;
        }

        PayRecord payRecord = payRecordService.getPayRecord(merchantCode, orderNo);
        if(payRecord == null){//记录不存在
            // 组织失败结果
            write(createFailResponse("记录不存在！"));
            return null;
        } else {//记录存在 不判状态，强制查询
            // 分发处理器 向银行发起查询
            ISingleTradeQueryResolver singleTradeQueryResolver = SingleTradeQueryResolverDispatcher.dispatch();
            // 查询交易
            SingleTradeQueryResult singleTradeQueryResult = singleTradeQueryResolver.query(payRecord);
            // 判断查询结果
            if(!singleTradeQueryResult.isQuerySuccess()){//查询失败
                write(createFailResponse("查询银行返回异常发生！"));
                return null;
            } else {//查询成功
                //定义查询返回结果
                int queryPayState;
                String tradeDateTime;
                String tradeBankSeq;
                // 判交易存在
                if(!singleTradeQueryResult.isExist()){//不存在
                    queryPayState = PayRecordInterface.STATE_FAILED;
                    tradeDateTime = StringUtils.EMPTY;
                    tradeBankSeq = StringUtils.EMPTY;
                } else {//存在
                    queryPayState = singleTradeQueryResult.getTradeState();
                    tradeDateTime = singleTradeQueryResult.getTradeDateTime();
                    tradeBankSeq = singleTradeQueryResult.getTradeBankSeq();
                }
                // 由于查询交易消耗时间 重新查询最新的支付记录
                payRecord = payRecordService.getPayRecordByTradeNo(payRecord.getTradeNo());
                // 查询前老的状态
                int oldTradeState = payRecord.getTradeState();
                // 为支付状态判优先级 优先则需要修改
                boolean needUpdate = BaseUtil.comparePriorityForPayState(queryPayState, payRecord.getTradeState());
                if(!needUpdate && (queryPayState != payRecord.getTradeState())){
                    //这里比较只为记录下，不需要修改但是又强制修改了的数据，而且前后状态不一致的，log记录异常：
                    logger.error("_FORCE_UPDATE_NOT_NEED_UPDATE_PAY_RECORD_->" + payRecord.toString());
                }
                /**
                 * 修改最新交易状态(不比较状态优先级，强制修改记录，以银行返回状态为准)
                 */
                payRecord.setTradeState(queryPayState);
                payRecord.setTradeDateTime(tradeDateTime);
                payRecord.setTradeBankSeq(tradeBankSeq);
                payRecord.setTradeDesc(BaseUtil.translatePayRecordStateDesc(queryPayState));
                payRecord.setUpdateDate(DateUtil.getNowDate());
                payRecord.setUpdateTime(DateUtil.getNowTime());
                payRecord.setUpdateIp(IPAddressUtil.getIPAddress(request));
                // 银行返回交易状态【强制】修改支付记录
                payRecordService.forceUpdateTradeState(payRecord);
                tradeDateTime = payRecord.getTradeDateTime();
                if(StringUtils.isBlank(tradeDateTime)){
                    tradeDateTime = payRecord.getCreateDate() + payRecord.getCreateTime();
                }
                // 组织成功结果
                write(createSuccessResponse(oldTradeState, payRecord.getTradeState(), StringUtils.trimToEmpty(payRecord.getTradeDesc())));
                return null;
            }
        }
    }

    /**
     * 创建失败返回结果
     * @param errorMessage
     * @return
     */
    private String createFailResponse(String errorMessage) {
        return "{" +
                "\"isQuerySuccess\":\"N\"," +
                "\"oldTradeState\":\"\"," +
                "\"newTradeState\":\"\"," +
                "\"message\":\"" + errorMessage + "\"" +
                "}";
    }

    /**
     * 创建成功返回结果
     * @param oldTradeState
     * @param newTradeState
     * @param message
     * @return
     */
    private String createSuccessResponse(int oldTradeState, int newTradeState, String message) {
        return "{" +
                "\"isQuerySuccess\":\"Y\"," +
                "\"oldTradeState\":\"" + oldTradeState + "\"," +
                "\"newTradeState\":\"" + newTradeState + "\"," +
                "\"message\":\"" + message + "\"" +
                "}";
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public IPayRecordService getPayRecordService() {
        return payRecordService;
    }

    @Resource
    public void setPayRecordService(IPayRecordService payRecordService) {
        this.payRecordService = payRecordService;
    }
}
