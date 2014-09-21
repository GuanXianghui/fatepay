package com.fatepay.entities;

import javax.persistence.*;

/**
 * 支付记录表
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 11:06
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"merchantCode", "orderNo"})})
public class PayRecord {
    int id;//自增id(主键)
    String tradeNo;//交易网关流水 yyyyMMdd+00000001开始的8位自增 逻辑唯一，逻辑自增
    String merchantCode;//商户号
    String orderNo;//商户订单号
    String token;//token串
    String productName;//商品名称
    String orderAmount;//订单金额，正整数，单位为分
    String returnUrl;//页面跳转返回URL
    String notifyUrl;//后台异步通知URL
    String tradeDateTime;//返回结果时间 格式：yyyyMMddHHmmss
    int tradeState;//交易状态
    String tradeBankSeq;//银行流水
    String tradeDesc;//交易结果描述
    int alreadyNotify;//是否已异步通知
    int notifyTimes;//异步通知次数
    String createDate;//创建日期 格式yyyyMMdd
    String createTime;//创建时间 格式HHmmss
    String createIp;//请求IP地址
    String updateDate;//修改日期 格式yyyyMMdd
    String updateTime;//修改时间 格式HHmmss
    String updateIp;//修改IP地址
    int version;//Hibernate乐观锁 版本号

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 用内存中的单例来获取：yyyyMMdd+00000001开始的8位自增
     * 每新的一天都从00000001开始
     * 集群下这种方式不适合！
     * @return
     */
    @Column(nullable = false, unique = true, length = 16)
    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    @Column(nullable = false, length = 6)
    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    @Column(nullable = false, length = 32)
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Column(nullable = false, length = 32)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(length = 100)
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Column(nullable = false, length = 16)
    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    @Column(length = 255)
    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    @Column(nullable = false, length = 255)
    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    @Column(length = 14)
    public String getTradeDateTime() {
        return tradeDateTime;
    }

    public void setTradeDateTime(String tradeDateTime) {
        this.tradeDateTime = tradeDateTime;
    }

    @Column(nullable = false)
    public int getTradeState() {
        return tradeState;
    }

    public void setTradeState(int tradeState) {
        this.tradeState = tradeState;
    }

    @Column(length = 32)
    public String getTradeBankSeq() {
        return tradeBankSeq;
    }

    public void setTradeBankSeq(String tradeBankSeq) {
        this.tradeBankSeq = tradeBankSeq;
    }

    @Column(length = 64)
    public String getTradeDesc() {
        return tradeDesc;
    }

    public void setTradeDesc(String tradeDesc) {
        this.tradeDesc = tradeDesc;
    }

    @Column(nullable = false)
    public int getAlreadyNotify() {
        return alreadyNotify;
    }

    public void setAlreadyNotify(int alreadyNotify) {
        this.alreadyNotify = alreadyNotify;
    }

    @Column(nullable = false)
    public int getNotifyTimes() {
        return notifyTimes;
    }

    public void setNotifyTimes(int notifyTimes) {
        this.notifyTimes = notifyTimes;
    }

    @Column(nullable = false, length = 8)
    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Column(nullable = false, length = 6)
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Column(nullable = false, length = 50)
    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }

    @Column(length = 8)
    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    @Column(length = 6)
    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Column(length = 50)
    public String getUpdateIp() {
        return updateIp;
    }

    public void setUpdateIp(String updateIp) {
        this.updateIp = updateIp;
    }

    @Version
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
