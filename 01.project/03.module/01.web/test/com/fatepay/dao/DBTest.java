package com.fatepay.dao;

import com.fatepay.entities.MerchantInfo;
import com.fatepay.entities.PayRecord;
import com.fatepay.interfaces.MerchantInfoInterface;
import com.fatepay.utils.DateUtil;
import junit.framework.TestCase;
import org.hibernate.SessionFactory;
import org.hibernate.impl.SessionFactoryImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据库链接测试
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 09:46
 */
@Component
public class DBTest extends TestCase {
    /**
     * 数据库连接 session工厂
     */
    SessionFactory sessionFactory;

    /**
     * Hibernate模板
     */
    private static HibernateTemplate hibernateTemplate;

    /**
     * 无参构造函数
     */
    public DBTest(){}

    /**
     * 新增商户信息
     */
    public void testSaveMerchantInfo(){
        MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setCode("880001");
        merchantInfo.setName("第一个商户");
        merchantInfo.setState(MerchantInfoInterface.STATE_CHECKING);
        merchantInfo.setCheckDesc("待审核状态！");
        merchantInfo.setUserId("419066357@163.com");
        merchantInfo.setEmail(merchantInfo.getUserId());
        merchantInfo.setPassword("MD5(123qwe)");
        merchantInfo.setBankCode("ABC");
        merchantInfo.setBankCard("955990030002165514");
        merchantInfo.setBankAccountName("关向辉");
        merchantInfo.setUserName("关向辉");
        merchantInfo.setUserPhone("13764603603");
        merchantInfo.setUserQq("419066357");
        merchantInfo.setMd5Key("293801203741092834");
        merchantInfo.setReturnUrl("http://www.jd.com/pg.do");
        merchantInfo.setNotifyUrl("http://www.jd.com/bg.do");
        merchantInfo.setWhiteList("http://www.baidu.com");
        merchantInfo.setFee(0.025f);
        merchantInfo.setCreateDate(DateUtil.getNowDate());
        merchantInfo.setCreateTime(DateUtil.getNowTime());
        merchantInfo.setCreateIp("127.0.0.1");
        hibernateTemplate.save(merchantInfo);
    }

    /**
     * 查询所有商户信息
     */
    public void testQueryAllMerchantInfo(){
        List<MerchantInfo> merchantInfoList = hibernateTemplate.find("FROM MerchantInfo");
        System.out.println(merchantInfoList.size());
        for(MerchantInfo merchantInfo : merchantInfoList){
            System.out.println(merchantInfo);
        }
    }

    /**
     * 根据商户号查商户信息
     */
    public MerchantInfo testGetMerchantInfoByMerchantCode(){
        String merchantCode = "880001";
        List<MerchantInfo> merchantInfoList = hibernateTemplate.find("from MerchantInfo m where m.code='" + merchantCode + "'");
        if(merchantInfoList.size() > 0){
            MerchantInfo merchantInfo = merchantInfoList.get(0);
            System.out.println(merchantInfo);
            return merchantInfo;
        } else {
            System.out.println("不存在该商户[" + merchantCode + "]");
            return null;
        }
    }

    /**
     * 修改商户状态
     */
    public void testUpdateMerchantInfoState(){
        MerchantInfo merchantInfo = testGetMerchantInfoByMerchantCode();
        merchantInfo.setState(MerchantInfoInterface.STATE_NORMAL);
        merchantInfo.setCheckDesc("审核通过！");
        merchantInfo.setUpdateDate(DateUtil.getNowDate());
        merchantInfo.setUpdateTime(DateUtil.getNowTime());
        merchantInfo.setUpdateIp("127.0.0.1");
        hibernateTemplate.update(merchantInfo);
    }

    /**
     * 修改商户白名单
     */
    public void testUpdateMerchantInfoWhiteList(){
        MerchantInfo merchantInfo = testGetMerchantInfoByMerchantCode();
        merchantInfo.setWhiteList("http://localhost,http://127.0.0.1,http://www.suncare-sys.com," +
                "http://192.168.1.182,http://58.247.137.210,http://test.rungroup.com.cn");
        hibernateTemplate.update(merchantInfo);
    }

    /**
     * 查询支付记录
     * @return
     */
    public static void testGetPayRecord(){
        String merchantCode = "880001";
        String orderNo = "1370001";
        List<PayRecord> payRecordList = hibernateTemplate.find(
                "from PayRecord p where p.merchantCode='" + merchantCode + "' and p.orderNo='" + orderNo +
                        "' and p.tradeDesc='" + "初始状态" + "'");
        if(payRecordList == null || payRecordList.size() == 0){
            System.out.println("查不到记录");
            return;
        }
        System.out.println(payRecordList.get(0).getProductName());
    }

    @Override
    protected void setUp() throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        sessionFactory = (SessionFactoryImpl) ctx.getBean("sessionFactory");
    }

    @Override
    protected void tearDown() throws Exception {
        sessionFactory.close();
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    @Resource
    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
