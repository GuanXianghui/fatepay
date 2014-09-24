package com.fatepay.dao;

import com.fatepay.entities.PayRecord;
import com.fatepay.interfaces.BaseInterface;
import com.fatepay.interfaces.PayRecordInterface;
import com.fatepay.utils.PropertyUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 支付记录表DAO实现类
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 20:34
 */
@Component("payRecordDao")
public class PayRecordDaoImpl implements IPayRecordDao {
    /**
     * Hibernate模板
     */
    private HibernateTemplate hibernateTemplate;

    /**
     * 无参构造函数
     */
    public PayRecordDaoImpl(){}

    /**
     * 根据交易网关流水查询支付记录
     * @param tradeNo
     * @return
     */
    public PayRecord getPayRecordByTradeNo(String tradeNo){
        List<PayRecord> payRecordList = hibernateTemplate.find(
                "from PayRecord p where p.tradeNo='" + tradeNo + "'");
        if(payRecordList == null || payRecordList.size() == 0){
            return null;
        }
        return payRecordList.get(0);
    }

    /**
     * 查询支付记录
     * @param merchantCode
     * @param orderNo
     * @return
     */
    public PayRecord getPayRecord(String merchantCode, String orderNo){
        List<PayRecord> payRecordList = hibernateTemplate.find(
                "from PayRecord p where p.merchantCode='" + merchantCode + "' and p.orderNo='" + orderNo + "'");
        if(payRecordList == null || payRecordList.size() == 0){
            return null;
        }
        return payRecordList.get(0);
    }

    /**
     * 新增支付记录
     * @param payRecord
     */
    public void save(PayRecord payRecord){
        hibernateTemplate.save(payRecord);
    }

    /**
     * 查询最大的tradeNo
     */
    public String getMaxTradeNo(){
        List result = hibernateTemplate.find("select max(p.tradeNo) FROM PayRecord p");
        if(result == null || result.size() == 0){
            return StringUtils.EMPTY;
        }
        return StringUtils.trimToEmpty((String)result.get(0));
    }

    /**
     * 银行返回交易状态修改支付记录
     * 修改以下字段，带上WHERE条件tradeState!=1（交易成功），保证成功状态的记录不被修改
     * 字段名	值
     * tradeDateTime	银行返回交易时间
     * tradeState	    银行返回交易状态
     * tradeBankSeq	    银行返回交易流水
     * tradeDesc	    交易状态翻译交易描述
     * updateDate	    当前日期，yyyyMMdd
     * updateTime	    当前时间，HHmmss
     * updateIp	        用户IP
     * version	        +1
     * @param payRecord
     */
    public void updateTradeState(PayRecord payRecord){
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        String hql = "update PayRecord p set p.tradeDateTime='" + payRecord.getTradeDateTime() +
                "',p.tradeState=" + payRecord.getTradeState() + ",p.tradeBankSeq='" + payRecord.getTradeBankSeq() +
                "',p.tradeDesc='" + payRecord.getTradeDesc() + "',p.updateDate='" + payRecord.getUpdateDate() +
                "',p.updateTime='" + payRecord.getUpdateTime() + "',p.updateIp='" + payRecord.getUpdateIp() +
                "' where p.id=" + payRecord.getId() + " and  p.tradeState!=" + PayRecordInterface.STATE_SUCCESS;
        session.createQuery(hql).executeUpdate();
        session.getTransaction().commit();
    }

    /**
     * 银行返回交易状态【强制】修改支付记录
     * 修改以下字段，【不】带上WHERE条件tradeState!=1（交易成功），【不】保证成功状态的记录不被修改
     * 字段名	值
     * tradeDateTime	银行返回交易时间
     * tradeState	    银行返回交易状态
     * tradeBankSeq	    银行返回交易流水
     * tradeDesc	    交易状态翻译交易描述
     * updateDate	    当前日期，yyyyMMdd
     * updateTime	    当前时间，HHmmss
     * updateIp	        用户IP
     * version	        +1
     * @param payRecord
     */
    public void forceUpdateTradeState(PayRecord payRecord){
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        String hql = "update PayRecord p set p.tradeDateTime='" + payRecord.getTradeDateTime() +
                "',p.tradeState=" + payRecord.getTradeState() + ",p.tradeBankSeq='" + payRecord.getTradeBankSeq() +
                "',p.tradeDesc='" + payRecord.getTradeDesc() + "',p.updateDate='" + payRecord.getUpdateDate() +
                "',p.updateTime='" + payRecord.getUpdateTime() + "',p.updateIp='" + payRecord.getUpdateIp() +
                "' where p.id=" + payRecord.getId();
        session.createQuery(hql).executeUpdate();
        session.getTransaction().commit();
    }

    /**
     * 查询需要异步通知的记录
     * 条件：【支付成功+未完成通知+通知次数小于5】
     * @return
     */
    public List<PayRecord> queryNeedNotifyRecords(){
        // 最多异步通知次数
        int maxNotifyTimes = Integer.parseInt(PropertyUtil.getInstance().getProperty(BaseInterface.MAX_NOTIFY_TIMES));
        // 查询记录集合
        List result = hibernateTemplate.find("from PayRecord p where p.tradeState=" +
                PayRecordInterface.STATE_SUCCESS + " and p.alreadyNotify=" + PayRecordInterface.ALREADY_NOTIFY_NO +
                " and p.notifyTimes<" + maxNotifyTimes);
        return result;
    }

    /**
     * 异步通知商户系统返回结果修改支付记录
     * 修改记录时候要带上WHERE条件alreadyNotify!=1（已通知），保证已通知状态的记录不被修改
     * @param payRecord
     */
    public void updateNotify(PayRecord payRecord){
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        String hql = "update PayRecord p set p.alreadyNotify=" + payRecord.getAlreadyNotify() +
                ",p.notifyTimes=" + payRecord.getNotifyTimes() + ",p.updateDate='" + payRecord.getUpdateDate() +
                "',p.updateTime='" + payRecord.getUpdateTime() +  "' where p.id=" + payRecord.getId() +
                " and p.alreadyNotify!=" + PayRecordInterface.ALREADY_NOTIFY_YES;
        session.createQuery(hql).executeUpdate();
        session.getTransaction().commit();
    }


    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    @Resource
    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
