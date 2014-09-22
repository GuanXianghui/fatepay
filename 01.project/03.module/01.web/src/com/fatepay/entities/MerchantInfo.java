package com.fatepay.entities;

import javax.persistence.*;

/**
 * 商户信息表
 * 主键约束：code
 * 唯一性约束：email
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 10:35
 */
@Entity
public class MerchantInfo {
    int id;//自增id(主键)
    String code;//商户号 880001开始的6位逻辑唯一，逻辑自增
    String name;//商户名称
    int state;//状态
    String checkDesc;//审核失败描述
    String userId;//后台登陆（邮箱）
    String email;//邮箱 注册前必须验证通过
    String password;//密码
    String bankCode;//银行代码
    String bankCard;//银行账号
    String bankAccountName;//银行帐户名
    String userName;//联系人姓名
    String userPhone;//联系人电话
    String userQq;//联系人QQ
    String md5Key;//md5签名Key
    String returnUrl;//页面跳转返回URL
    String notifyUrl;//后台异步通知URL
    String whiteList;//域名白名单 多个用逗号隔开
    double fee;//手续费比例 2.5%则填0.025
    String createDate;//创建日期 格式yyyyMMdd
    String createTime;//创建时间 格式HHmmss
    String createIp;//请求IP地址
    String updateDate;//修改日期 格式yyyyMMdd
    String updateTime;//修改时间 格式HHmmss
    String updateIp;//修改IP地址
    int version;//Hibernate乐观锁 版本号

    @Override
    public String toString() {
        return "id:[" + id + "],code:[" + code + "],name:[" + name + "],state:[" + state + "],checkDesc:[" +
                checkDesc + "],email:[" + email + "],password:[" + password + "],bankCode:[" + bankCode +
                "],bankCard:[" + bankCard + "],bankAccountName:[" + bankAccountName + "],userName:[" + userName +
                "],userPhone:[" + userPhone + "],userQq:[" + userQq + "],md5Key:[" + md5Key + "],returnUrl:[" +
                returnUrl + "],notifyUrl:[" + notifyUrl + "],whiteList:[" + whiteList + "],fee:[" + fee +
                "],createDate:[" + createDate + "],createTime:[" + createTime + "],createIp:[" + createIp +
                "],updateDate:[" + updateDate + "],updateTime:[" + updateTime + "],updateIp:[" + updateIp +
                "],version:[" + version + "]";
    }

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 用内存中的单例来获取：880001开始的6位自增
     * 集群下这种方式不适合！
     * @return
     */
    @Column(nullable = false, unique = true, length = 6)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false)
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Column(length = 200)
    public String getCheckDesc() {
        return checkDesc;
    }

    public void setCheckDesc(String checkDesc) {
        this.checkDesc = checkDesc;
    }

    @Column(unique = true, nullable = false, length = 30)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(unique = true, nullable = false, length = 30)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(nullable = false, length = 32)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(nullable = false, length = 10)
    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @Column(nullable = false, length = 30)
    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    @Column(nullable = false, length = 50)
    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    @Column(nullable = false, length = 20)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(nullable = false, length = 20)
    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    @Column(length = 20)
    public String getUserQq() {
        return userQq;
    }

    public void setUserQq(String userQq) {
        this.userQq = userQq;
    }

    @Column(nullable = false, length = 20)
    public String getMd5Key() {
        return md5Key;
    }

    public void setMd5Key(String md5Key) {
        this.md5Key = md5Key;
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

    @Column(nullable = false, length = 200)
    public String getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String whiteList) {
        this.whiteList = whiteList;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
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
