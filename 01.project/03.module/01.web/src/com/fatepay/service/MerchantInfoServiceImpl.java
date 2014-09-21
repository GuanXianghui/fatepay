package com.fatepay.service;

import com.fatepay.dao.IMerchantInfoDao;
import com.fatepay.entities.MerchantInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商户信息服务实现类
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 15:36
 */
@Component("merchantInfoService")
public class MerchantInfoServiceImpl implements IMerchantInfoService {
    /**
     * 商户信息表DAO
     */
    private IMerchantInfoDao merchantInfoDao;

    /**
     * 无参构造函数
     */
    public MerchantInfoServiceImpl(){}

    /**
     * 查询所有的商户信息
     * @return
     */
    public List<MerchantInfo> queryAllMerchantInfos(){
        return merchantInfoDao.queryAllMerchantInfos();
    }

    public IMerchantInfoDao getMerchantInfoDao() {
        return merchantInfoDao;
    }

    @Resource
    public void setMerchantInfoDao(IMerchantInfoDao merchantInfoDao) {
        this.merchantInfoDao = merchantInfoDao;
    }

}
