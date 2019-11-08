/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

/**
 * This class provides methods of requesting a remote server.
 *
 */
public class HttpRequester {
    static Logger                   logger = LoggerFactory.getLogger(HttpRequester.class);
    private static SSLSocketFactory sslSocketFactory;
    private String                  vipHostName;

    /**
     * Basic path of the request URL.
     */
    private String                  baseURL;

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * The extra parameters to add to http header
     */
    private Map<String, String> customizedHeaderParams = null;

    public void setCustomizedHeaderParams(Map<String, String> params) {
        customizedHeaderParams = params;
    }

    /**
     * get the baseURL
     *
     * @param vIPServer
     *            The host address of the vIP Server
     * @throws MalformedURLException
     */
    protected HttpRequester(String vIPServer) throws MalformedURLException {
        if (null != vIPServer && vIPServer.length() > 0) {
            if (!vIPServer.trim().startsWith(ConstantsKeys.HTTP_PROTOCOL_PREFIX)
                    && !vIPServer.trim().startsWith(ConstantsKeys.HTTPS_PROTOCOL_PREFIX)) {
                vIPServer = ConstantsKeys.HTTPS_PROTOCOL_PREFIX.concat(vIPServer);
            }

            URL url = new URL(vIPServer);
            this.baseURL = url.toString();
            this.vipHostName = url.getHost();
        }
    }

    /**
     * Check that the server of the vIP is available.
     *
     * @param ipAddress
     *            the ip address or domain.
     * @return
     */
    protected static boolean ping(String ipAddress) {
        boolean status = false;
        try {
            status = InetAddress.getByName(ipAddress).isReachable(ConstantsKeys.HTTP_CONNECT_TIMEOUT);
        } catch (IOException e) {
            return false;
        }
        return status;
    }

    /**
     * The get method of requesting a remote server.
     *
     * @param url
     *            The remote server url.
     * @return
     */
    public String request(final String url, final String method, final Object requestData) {
        String r = "";
        HttpURLConnection conn = null;
        try {
            StringBuilder urlStr = new StringBuilder();
            if (ConstantsKeys.GET.equalsIgnoreCase(method) && requestData != null) {
                if (requestData instanceof Map) {
                    urlStr.append(
                            URLUtils.appendParamToURL(new StringBuilder(url), "", this.getFormStr((Map) requestData)));
                }
            } else {
                urlStr.append(url);
            }
            logger.info("[" + method + "]" + urlStr.toString());
            conn = createConnection(urlStr.toString());
            if (conn != null) {
                conn.setRequestMethod(method);
                if (ConstantsKeys.POST.equalsIgnoreCase(method)) {
                    this.writeData(requestData, conn);
                } else {
                    conn.connect();
                }
                if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                    r = this.handleResult(conn);
                    // logger.debug("The response from server is:\n"+r);
                }
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return r;
    }

    public String getBaseURL() {
        return this.baseURL;
    }

    private String handleResult(HttpURLConnection conn) throws IOException {
        InputStream is = conn.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = is.read(buffer))) {
            baos.write(buffer, 0, len);
            baos.flush();
        }
        baos.close();
        is.close();
        return baos.toString(ConstantsKeys.UTF8);

    }

    private void writeData(Object requestData, HttpURLConnection conn) throws IOException {
        if (requestData != null) {
            String outStr = "";
            if (requestData instanceof Map) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                outStr = getFormStr((Map) requestData);
                logger.info("[Content-Type][form]" + outStr + "");
            } else if (requestData instanceof String) {
                outStr = requestData.toString();
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                logger.info("[Content-Type[json]" + outStr + "");
            }
            conn.connect();
            OutputStream p = conn.getOutputStream();
            p.write(outStr.getBytes("utf-8"));
            p.flush();
            p.close();
        }
    }

    private String getFormStr(Map<String, String> params) {
        StringBuilder p = new StringBuilder();
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            Iterator<Map.Entry<String, String>> it = entrySet.iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> me = it.next();
                String key = me.getKey();
                String value = me.getValue();
                p.append("&").append(key).append("=").append(value);
            }
        }
        if (p.length() > 0) {
            return p.substring(1, p.length());
        } else {
            return p.toString();
        }
    }

    /**
     * Get the request connection.
     *
     * @param path
     *            The remote server url.
     * @return
     */
    private HttpURLConnection createConnection(String path) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(path.trim());
            assert (null != url);
            assert (ConstantsKeys.HTTP_PROTOCOL.equals(url.getProtocol())
                    || ConstantsKeys.HTTPS_PROTOCOL.equals(url.getProtocol()));
            connection = (HttpURLConnection) url.openConnection();
            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) connection;
                httpsConn.setHostnameVerifier(new HostnameVerifier() {
                    // verify the client identity, here does't do any
                    // validation.
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                httpsConn.setSSLSocketFactory(getSocketFactory());
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        if (connection != null) {
            connection.setConnectTimeout(ConstantsKeys.HTTP_CONNECT_TIMEOUT);
            connection.setReadTimeout(ConstantsKeys.HTTP_READ_TIMEOUT);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("accept", "*/*");

            addHeaderParams(connection);
        }
        return connection;
    }

    /**
     * Get socket factory.
     *
     * @return
     */
    private static final SSLSocketFactory getSocketFactory() {
        if (sslSocketFactory == null) {
            try {
                TrustManager[] tm = new TrustManager[] { new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] cert, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] cert, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        logger.info("no client accept check");
                        return null;
                    }
                } };
                String ssl = "SSL";
                SSLContext context = SSLContext.getInstance(ssl);
                context.init(new KeyManager[0], tm, new SecureRandom());
                sslSocketFactory = context.getSocketFactory();
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                logger.error(e.getMessage());
            }
        }
        return sslSocketFactory;
    }

    public boolean isConnected() {
        String ipAddress = (vipHostName.contains(":")) ? vipHostName.split(":")[0] : vipHostName;
        return HttpRequester.ping(ipAddress);
    }

    private void addHeaderParams(HttpURLConnection connection) {
        if (null == customizedHeaderParams || null == connection) {
            return;
        }

        for (final Entry<String, String> entry : customizedHeaderParams.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}
