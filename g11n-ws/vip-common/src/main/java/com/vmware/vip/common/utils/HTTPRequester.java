/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.l10n.source.util.IOUtil;

/**
 * This class is used for HTTP request
 * 
 */
public class HTTPRequester {

    private static Logger LOGGER = LoggerFactory.getLogger(HTTPRequester.class);
    public static final String HTTP_PROTOCOL = "http";
    public static final String HTTPS_PROTOCOL = "https";
    public static final String DEFAULT_PROTOCOL = HTTPS_PROTOCOL;
    private static SSLSocketFactory sslSocketFactory;

    /* 5 seconds */
    public static final Integer CONNECT_TIMEOUT = 5000;

    /* 5 seconds */
    public static final Integer READ_TIMEOUT = 5000;

    /**
     * Convert a stream to string
     *
     * @param is instance of InputStream
     * @return one string same from the input stream
     */
    @SuppressWarnings("resource")
    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Create connect with specific URL
     *
     * @param url the location to connect
     * @return a HTTP URL connection
     */
    public static HttpURLConnection createConnection(URL url) {
        assert (null != url);
        assert (HTTP_PROTOCOL.equals(url.getProtocol()) || HTTPS_PROTOCOL.equals(url.getProtocol()));
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
        	LOGGER.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * Create a socket factory
     *
     * @return the SSL socketFactory
     */
    private static final SSLSocketFactory getSocketFactory() {
        if (sslSocketFactory == null) {
            try {
                TrustManager[] tm = new TrustManager[] { new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] cert, String authType)
                            throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] cert, String authType)
                            throws CertificateException {
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
            	LOGGER.error(e.getMessage(), e);
            } catch (NoSuchAlgorithmException e) {
            	LOGGER.error(e.getMessage(), e);
            }
        }
        return sslSocketFactory;
    }

    /**
     * Send a JSON data
     *
     * @param urlStr the location of remote server
     * @param jsonContent JSON data to be sent
     * @param method request method
     * @return true if send successfully
     */
    public static boolean send(String urlStr, String jsonContent, String method) {
        HttpURLConnection http = null;
        OutputStreamWriter osw = null;
        InputStream input = null;
        BufferedReader in = null;
        InputStreamReader reader = null;
        try {
            URL url = new URL(urlStr);
            http = createConnection(url);
            
            if(http == null) {
            	throw new NullPointerException("fail create connetion error");
            }
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setUseCaches(false);
            http.setConnectTimeout(CONNECT_TIMEOUT);
            http.setReadTimeout(READ_TIMEOUT);
            http.setRequestMethod(method);
            http.setRequestProperty("Content-Type", "application/json");// x-www-form-urlencoded
            http.connect();
            osw = new OutputStreamWriter(http.getOutputStream(), ConstantsUnicode.UTF8);
            osw.write(jsonContent);
            osw.flush();
            String result = "";
            if (http.getResponseCode() == APIResponseStatus.OK.getCode()) {
                input = http.getInputStream();
                reader = new InputStreamReader(input, ConstantsUnicode.UTF8);
                in = new BufferedReader(reader);
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    result += inputLine;
                }
                LOGGER.info("The request has successed, the result: " + result);
                return true;
            } else {
                LOGGER.info("The request has failed, the response code: " + http.getResponseCode());
                return false;
            }
        } catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
            return false;
        } finally {
            IOUtil.closeWriter(osw);
            IOUtil.closeReader(in);
            IOUtil.closeReader(reader);
            IOUtil.closeInputStream(input);
            if (http != null) {
                http.disconnect();
            }
        }
    }

}
