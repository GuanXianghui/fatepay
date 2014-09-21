package com.fatepay.service;

import com.fatepay.entities.MerchantInfo;

import java.util.List;

/**
 * 商户信息服务类
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 15:57
 */
public interface IMerchantInfoService {
    /**
     * 查询所有的商户信息
     * @return
     */
    public List<MerchantInfo> queryAllMerchantInfos();
}
