package com.fatepay.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

/**
 * HttpClient处理类
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 00:12
 */
public class HttpClientUtils
{
    /**
     * 日志记录器
     */
    static Logger logger = Logger.getLogger(HttpClientUtils.class);

    /**
     * UTF-8
     */
    public static final String ENCODE_UTF8 = "UTF-8";

    /**
     * UTF-8
     */
    public static final String ENCODE_UTF16 = "UTF-16";

    /**
     * UTF-8
     */
    public static final String ENCODE_GB2312 = "GB2312";

    /**
     * GBK
     */
    public static final String ENCODE_GBK = "GBK";

    /**
     * GBK
     */
    public static final String ENCODE_ISO = "ISO-8859-1";

    /**
     * httpclient post 请求
     *
     * @param url            请求地址
     * @param xml            请求xml参数
     * @param requestEncode  请求编码
     * @param responseEncode 请求编码
     * @return
     */
    public static String post(String url, String xml, String requestEncode, String responseEncode)
    {
        logger.info("httpclient post 请求 开始==============");
        logger.info("url=" + url);
        //PropertyUtil.getInstance().refresh();
        logger.info("requestXml=" + xml);
        logger.debug("没有使用证书，协议注册443端口信任所有证书，开始=================================");
        Protocol easyHttps1 = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", easyHttps1);
        logger.debug("没有使用证书，协议注册443端口信任所有证书，结束=================================");

        String resultStr = null;
        PostMethod postMethod = new PostMethod(url);//创建POST方法的实例
        postMethod.setRequestBody(xml);//将表单的值放入postMethod中
        postMethod.addRequestHeader("Content", "text/xml,charset=" + requestEncode + "");
        HttpClient httpClient = new HttpClient();//构造HttpClient的实例
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, requestEncode);

        try
        {
            int code = httpClient.executeMethod(postMethod);
            logger.info("Response status code: " + code);//返回200为成功
            resultStr = new String(postMethod.getResponseBodyAsString().getBytes(), responseEncode);
            logger.info("resultStr=" + resultStr);
        } catch (Exception e)
        {
            logger.error("HttpClient异常发生~", e);
        } finally
        {
            postMethod.releaseConnection();
        }

        logger.info("httpclient post 请求 结束==============");
        return resultStr;
    }

    /**
     * httpclient post url 请求
     * @param url            请求地址
     * @param requestEncode  请求编码
     * @param responseEncode 接受编码
     * @return
     */
    public static String postUrl(String url, String requestEncode, String responseEncode)
    {
        logger.info("httpclient post url 请求 开始==============");
        logger.info("url=" + url);
        logger.debug("没有使用证书，协议注册443端口信任所有证书，开始=================================");
        Protocol easyHttps1 = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", easyHttps1);
        logger.debug("没有使用证书，协议注册443端口信任所有证书，结束=================================");

        String resultStr = null;
        PostMethod postMethod = new PostMethod(url);//创建POST方法的实例
        HttpClient httpClient = new HttpClient();//构造HttpClient的实例
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, requestEncode);

        try
        {
            int code = httpClient.executeMethod(postMethod);
            logger.info("Response status code: " + code);//返回200为成功
            resultStr = new String(postMethod.getResponseBodyAsString().getBytes(), responseEncode);
            logger.info("resultStr=" + resultStr);
        } catch (Exception e)
        {
            logger.error("HttpClient异常发生~", e);
        } finally
        {
            postMethod.releaseConnection();
        }

        logger.info("httpclient post url 请求 结束==============");
        return resultStr;
    }

    /**
     * httpclient post props 请求
     * @param url
     * @param props
     * @param requestEncode
     * @param responseEncode
     * @return
     */
    public static String postProps(String url, Properties props, String requestEncode, String responseEncode) throws Exception
    {
        PostMethod method = new PostMethod(url);
        Enumeration enums = props.keys();
        while (enums.hasMoreElements())
        {
            String key = (String)enums.nextElement();
            String value = props.getProperty(key);
            if(StringUtils.isNotBlank(value))
            {
                method.addParameter(key, value);
            }
        }
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,requestEncode);
        return connect(method, responseEncode);
    }

    /**
     * 与服务器通讯
     * @param method
     * @param responseEncode
     * @return
     * @throws Exception
     */
    public static String connect(PostMethod method, String responseEncode) throws Exception
    {
        InputStream is = null;
        BufferedReader br = null;
        try
        {
            HttpClient client = new HttpClient();
            int returnCode = client.executeMethod(method);

            if (HttpURLConnection.HTTP_OK != returnCode)
            {
                logger.error("与服务器交互发生异常,returnCode=" + returnCode);
            }
            is = method.getResponseBodyAsStream();
            br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), responseEncode));
            StringBuffer response = new StringBuffer();
            String readLine;
            while (((readLine = br.readLine()) != null))
            {
                response.append(readLine);
            }
            //logger.info("response.toString=====>" + response.toString());
            return response.toString();
        } catch (Exception e)
        {
            logger.error("与服务器交互发生异常~", e);
            throw new RuntimeException("与服务器交互发生异常~", e);
        } finally
        {
            try
            {
                if (null != method)
                {
                    method.releaseConnection();
                }
                if (null != is)
                {
                    is.close();
                }
                if (null != br)
                {
                    br.close();
                }
            } catch (Exception e)
            {
                logger.error("与服务器交互发生异常~", e);
            }
        }
    }
}