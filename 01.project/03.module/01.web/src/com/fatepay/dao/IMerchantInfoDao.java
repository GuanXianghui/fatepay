package com.fatepay.dao;

import com.fatepay.entities.MerchantInfo;

import java.util.List;

/**
 * 商户信息表DAO
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 15:49
 */
public interface IMerchantInfoDao {
    /**
     * 查询所有的商户信息
     * @return
     */
    public List<MerchantInfo> queryAllMerchantInfos();
}
