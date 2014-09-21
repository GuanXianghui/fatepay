package com.fatepay;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 测试Action
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-17 23:27
 */
@Component("testAction")
@Scope("prototype")
public class TestAction extends BaseAction {
    String userName;
    String password;

    /**
     * 空的构造方法
     */
    public TestAction(){}

    /**
     * 入口
     * @return
     */
    public String execute(){
        logger.info(userName + "," + password);
        if(StringUtils.isBlank(userName) || StringUtils.isBlank(password)){
            message = "用户名，密码不能为空！";
            return ERROR;
        }
        if(StringUtils.equals(userName, "admin") && StringUtils.equals(password, "123qwe")){
            message = "登陆成功！";
            return SUCCESS;
        }
        message = "用户名不存在，或者密码不正确！";
        return SUCCESS;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
