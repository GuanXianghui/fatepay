package com.fatepay.dao;

import com.fatepay.entities.MerchantInfo;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商户信息表DAO实现类
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 18:22
 */
@Component("merchantInfoDao")
public class MerchantInfoDaoImpl implements IMerchantInfoDao {
    /**
     * Hibernate模板
     */
    private HibernateTemplate hibernateTemplate;

    /**
     * 无参构造函数
     */
    public MerchantInfoDaoImpl(){}

    /**
     * 查询所有的商户信息
     * @return
     */
    public List<MerchantInfo> queryAllMerchantInfos(){
        return hibernateTemplate.find("from MerchantInfo");
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    @Resource
    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
