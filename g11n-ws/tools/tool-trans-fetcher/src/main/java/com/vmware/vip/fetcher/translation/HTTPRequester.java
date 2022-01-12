/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.fetcher.translation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Git Translation Fetcher Main
 * 
 * @author <a href="mailto:linr@vmware.com">Colin Lin</a>
 */
public class HTTPRequester {

    public static final String HTTP_PROTOCOL = "http";
    public static final String HTTPS_PROTOCOL = "https";
    public static final String DEFAULT_PROTOCOL = HTTPS_PROTOCOL;
    private static SSLSocketFactory sslSocketFactory;

    
    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static HttpURLConnection createConnection(URL url) {
        assert (null != url);
        assert (HTTP_PROTOCOL.equals(url.getProtocol()) || HTTPS_PROTOCOL
                .equals(url.getProtocol()));
        HttpURLConnection result = null;
        try {
            result = (HttpURLConnection) url.openConnection();
            if (result instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) result;
                httpsConn.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                httpsConn.setSSLSocketFactory(getSocketFactory());
            }
        } catch (IOException e) {
        }

        return result;
    }

    private static final SSLSocketFactory getSocketFactory() {
        if (sslSocketFactory == null) {
            try {
                TrustManager[] tm = new TrustManager[] { new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] cert,
                            String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] cert,
                            String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                } };

                SSLContext context = SSLContext.getInstance("SSL");
                context.init(new KeyManager[0], tm, new SecureRandom());
                sslSocketFactory = context.getSocketFactory();
            } catch (KeyManagementException e) {
            } catch (NoSuchAlgorithmException e) {
            }
        }
        return sslSocketFactory;
    }

}
