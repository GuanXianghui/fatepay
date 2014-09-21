package com.fatepay;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * 测试SchemaExport
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 09:36
 */
public class SchemaExportTest {
    /**
     * main方法
     * @param params
     */
    public static void main(String[] params) {
        // 1 获取Configuration
        ClassPathResource ac = new ClassPathResource("applicationContext.xml");
        XmlBeanFactory xbf = new XmlBeanFactory(ac);
        // 1.1 注意： &sessionFactory ，一定要包含 & ，不加Spring返回的是Hibernate下的SessionFactoryImpl类
        LocalSessionFactoryBean localSessionFactoryBean = (LocalSessionFactoryBean) xbf.getBean("&sessionFactory");
        Configuration cfg = localSessionFactoryBean.getConfiguration();
        // 2 获取SchemaExport
        SchemaExport export = new SchemaExport(cfg);
        // 3 执行SchemaExport
        export.create(true, false);
    }
}
