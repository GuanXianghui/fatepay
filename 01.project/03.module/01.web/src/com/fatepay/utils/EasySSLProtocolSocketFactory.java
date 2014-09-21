package com.fatepay.utils;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * SSL协议socket
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 00:12
 */
public class EasySSLProtocolSocketFactory implements SecureProtocolSocketFactory
{

    private static final Log _logger = LogFactory.getLog(EasySSLProtocolSocketFactory.class);

    private SSLContext sslcontext = null;

    public EasySSLProtocolSocketFactory() {
        super();
        _logger.info("using easy https connection...");
    }

    private static SSLContext createEasySSLContext() {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[] { new EasyX509TrustManager() }, null);
            return context;
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private SSLContext getSSLContext() {
        if (this.sslcontext == null) {
            this.sslcontext = createEasySSLContext();
        }
        return this.sslcontext;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * org.apache.commons.httpclient.protocol.ProtocolSocketFactory#createSocket
      * (java.lang.String, int, java.net.InetAddress, int)
      */
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException,
            UnknownHostException {

        return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * org.apache.commons.httpclient.protocol.ProtocolSocketFactory#createSocket
      * (java.lang.String, int)
      */
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory#
      * createSocket(java.net.Socket, java.lang.String, int, boolean)
      */
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
            UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * org.apache.commons.httpclient.protocol.ProtocolSocketFactory#createSocket
      * (java.lang.String, int, java.net.InetAddress, int,
      * org.apache.commons.httpclient.params.HttpConnectionParams)
      */
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
                               HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException
    {
        if (params == null) {
            throw new IllegalArgumentException("HttpConnectionParams empty!");
        }
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory = getSSLContext().getSocketFactory();
        if (timeout == 0) {
            return socketfactory.createSocket(host, port, localAddress, localPort);
        } else {
            Socket socket = socketfactory.createSocket();
            SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
    }

    private static class EasyX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // 不校验服务器端证书
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // 不校验客户端证书
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

}
