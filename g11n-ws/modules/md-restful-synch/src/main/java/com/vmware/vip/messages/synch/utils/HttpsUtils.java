/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.synch.utils;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
/**
 * 
 *
 * @author shihu
 *
 */
public class HttpsUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpsUtils.class);
	 
    private static CloseableHttpClient httpsClientExe;
 
    static {
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            httpClientBuilder.setMaxConnTotal(1000);
            httpClientBuilder.setMaxConnPerRoute(100);
            httpClientBuilder.evictIdleConnections(15,TimeUnit.SECONDS);
            SocketConfig.Builder socketConfigBuilder = SocketConfig.custom();
            socketConfigBuilder.setTcpNoDelay(true);
            httpClientBuilder.setDefaultSocketConfig(socketConfigBuilder.build());
            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
            requestConfigBuilder.setConnectTimeout(3000);
            requestConfigBuilder.setSocketTimeout(3000);
            httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}
               
          };
            
            
            ctx.init(null, new TrustManager[]{tm}, null);
            httpClientBuilder.setSSLContext(ctx);
            httpClientBuilder.setSSLHostnameVerifier(new HostnameVerifier() {

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					// TODO Auto-generated method stub
					return true;
				}
            	
            });
            httpsClientExe = httpClientBuilder.build();
        } catch (Exception e) {
            logger.error("", e);
        }
    }
 
    public static String doGet(String url) throws Exception {
        return execute(new HttpGet(url));
    }
 
    public static <T> T doGet2Obj(String url, Class<T> clazz) throws Exception {
    	 String resultStr =execute(new HttpGet(url));
         logger.debug("result json:{}", resultStr);
        return JSON.parseObject(resultStr, clazz);
    }
    public static String doGet(String url, String hostname, int port, String scheme) throws Exception {
        HttpGet getMethod = new HttpGet(url);
        HttpHost proxy = new HttpHost(hostname, port, scheme);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        getMethod.setConfig(config);
        return execute(getMethod);
    }
 
    public static String doPost(String url, Map<String, String> params) throws Exception {
    	 HttpPost post = new HttpPost(url);
         List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
         BasicNameValuePair basicNameValuePair = null;
         for (Map.Entry<String,String> kvEntry : params.entrySet()) {
             basicNameValuePair = new BasicNameValuePair(kvEntry.getKey(), kvEntry.getValue());
             urlParameters.add(basicNameValuePair);
         }
         HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
         post.setEntity(postParams);
        return execute(post);
    }
 
    public static String doPost(String url, Map<String, String> params, String hostname, int port, String scheme) throws Exception {
  
        logger.debug("request url = {}, params = {}",url,params);
        HttpPost post = new HttpPost(url);
        List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
        BasicNameValuePair basicNameValuePair = null;
        for (Map.Entry<String,String> kvEntry : params.entrySet()) {
            basicNameValuePair = new BasicNameValuePair(kvEntry.getKey(), kvEntry.getValue());
            urlParameters.add(basicNameValuePair);
        }
        HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
        post.setEntity(postParams);
        HttpHost proxy = new HttpHost(hostname, port, scheme);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        post.setConfig(config);
        return execute(post);
    }
 

    
    public static String doPostWithJson(String url, String jsontext) throws Exception {

        logger.debug("request url = {}, content = {}",url,jsontext);
        HttpPost post = new HttpPost(url);
        HttpEntity requestEntity = new StringEntity(jsontext, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);
        return execute(post);
    }
    
    
    public static String doPostWithJson(String url, Object jsonObj) throws Exception {
    	
    	String jsontext = JSON.toJSONString(jsonObj, SerializerFeature.WriteSlashAsSpecial);
        logger.debug("request url = {}, content = {}",url,jsontext);
        HttpPost post = new HttpPost(url);
        HttpEntity requestEntity = new StringEntity(jsontext, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);
        return execute(post);
    }
    
    public static <T> T doPostWithObj2Obj(String url, Object jsonObj, Class<T> clazz) throws Exception {
    	String jsontext = JSON.toJSONString(jsonObj, SerializerFeature.WriteSlashAsSpecial);
        logger.info("request url = {}, content = {}",url,jsontext);
        HttpPost post = new HttpPost(url);
        HttpEntity requestEntity = new StringEntity(jsontext, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);
        String resultStr = execute(post);
        logger.debug("result json:{}", resultStr);
       return JSON.parseObject(resultStr, clazz);
       
    }
 
    private static String execute(HttpUriRequest request) throws Exception {
        CloseableHttpResponse response = httpsClientExe.execute(request);
        if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
            throw new Exception("Invalid http status code:" + response.getStatusLine().getStatusCode());
        }
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

  
    
    
    
    
}
