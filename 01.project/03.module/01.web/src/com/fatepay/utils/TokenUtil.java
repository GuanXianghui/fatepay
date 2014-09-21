package com.fatepay.utils;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * token工具类
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 00:12
 */
public class TokenUtil {
    private static TokenUtil instance;

    public static TokenUtil getInstance() {
        if (null == instance) {
            instance = new TokenUtil();
        }
        return instance;
    }

    private static Map<String, Date> tokenMap;

    private TokenUtil() {
        tokenMap = new HashMap<String, Date>();
    }

    /**
     * 创建token串
     * @return
     */
    public String createToken(String merchantCode) {
        // 1 产生UUID
        String uuid = UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY);
        // 2 expireTime失效时间为当前时间戳yyyyMMddHHmmss+60秒（即60秒后失效）
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 60);
        // 3 将merchantCode_token=expireTime放入服务器application级别缓存中的Map tokenMap中
        tokenMap.put(merchantCode + "_" + uuid, calendar.getTime());
        return uuid;
    }

    /**
     * 根据merchantCode_token获取expireTime
     * @param merchantCode
     * @param token
     * @return
     */
    public Date getTokenExpireTime(String merchantCode, String token){
        return tokenMap.get(merchantCode + "_" + token);
    }

    /**
     * 校验token
     * @param merchantCode
     * @param token
     * @return
     */
    public boolean checkToken(String merchantCode, String token){
        Date expireTime = getTokenExpireTime(merchantCode, token);
        // 不存在该token
        if(null == expireTime){
            return false;
        }
        // 存在则删掉该token 只能用一次
        tokenMap.remove(merchantCode + "_" + token);
        // 已经失效
        if(expireTime.before(new Date())){
            return false;
        }
        // 暂未失效
        return true;
    }

    /**
     * 得到TokenMap
     * @return
     */
    public Map<String, Date> getTokenMap(){
        return tokenMap;
    }
}
