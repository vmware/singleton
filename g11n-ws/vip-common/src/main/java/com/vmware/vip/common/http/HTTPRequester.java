/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

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
import com.vmware.vip.common.exceptions.VIPHttpException;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.l10n.source.util.IOUtil;

/**
 * A helper util for http request by post method, the http protocol can be
 * 'http', also can be 'https' if https, this util will not verify the hostname
 * and sslsession, you just income parameters and url
 */
public class HTTPRequester {

	private static Logger LOGGER = LoggerFactory.getLogger(HTTPRequester.class);
	public static final String HTTP_PROTOCOL = "http";
	public static final String HTTPS_PROTOCOL = "https";
	public static final String DEFAULT_PROTOCOL = HTTPS_PROTOCOL;
	public static final String METHOD_POST = "POST";
	/* 5 seconds */
	public static final Integer CONNECT_TIMEOUT = 50000;
	/* 5 seconds */
	public static final Integer READ_TIMEOUT = 50000;
	private static SSLSocketFactory sslSocketFactory;

	/**
	 * Convert input stream to string
	 *
	 * @param is
	 *            Input stream
	 * @return The converted string
	 */
	@SuppressWarnings("resource")
	public static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/**
	 * Create http connection for request
	 *
	 * @param url
	 *            The requested url
	 * @return
	 */
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
			LOGGER.error(e.getMessage(), e);
		}
		
		
		return result;
	}

	/**
	 * Verify the host legitimacy,it's a trusted host or not
	 *
	 * @return SSLSocketFactory object
	 */
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
				LOGGER.error(e.getMessage(), e);
			} catch (NoSuchAlgorithmException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return sslSocketFactory;
	}

	/**
	 * Post form data
	 *
	 * @param param
	 *            Request parameters
	 * @param urlStr
	 *            The target address
	 * @return
	 */
	public static void postFormData(Map<String, String> param, String urlStr)  throws VIPHttpException {
		String paramStr = "";
		for (String key : param.keySet()) {
			paramStr += "&" + key + "=" + param.get(key);
		}
		String response =  HTTPRequester.postData(paramStr, urlStr, "application/x-www-form-urlencoded", "POST", null);
		if(response == null || response.equalsIgnoreCase("")) {
			throw new VIPHttpException("postFormData error.");
		}
	}

	/*
	 * Post JSON data
	 */
	public static void postJSONStr(String jsonStr, String urlStr) throws VIPHttpException {
		String response =  HTTPRequester.postData(jsonStr, urlStr, "application/json", "POST", null);
		if(response == null || response.equalsIgnoreCase("")) {
			throw new VIPHttpException("postJSONStr error.");
		}
	}

	/*
	 * Post JSON data
	 */
	public static void postJSONStrWithHeaders(String jsonStr, String urlStr, Map headers) throws VIPHttpException {
		String response =  HTTPRequester.postData(jsonStr, urlStr, "application/json", "POST", headers);
		if(response == null || response.equalsIgnoreCase("")) {
			throw new VIPHttpException("postJSONStr error.");
		}
	}

	/*
	 * Put JSON data
	 */
	public static void putJSONStr(String jsonStr, String urlStr, Map<String, String> headers) throws VIPHttpException{
		String response =  HTTPRequester.postData(jsonStr, urlStr, "application/json", "PUT", headers);
		if(response == null || response.equalsIgnoreCase("")) {
			throw new VIPHttpException("putJSONStr error.");
		}
	}

	/*
	 * Post data by content type.
	 */

	public static String postData(String data, String urlStr,
			String contentType, String requestMethod, Map<String, String> headers) {
		LOGGER.info("<<< Start to post data : \n" + data);
		LOGGER.info("The remote url is : " + urlStr);
		HttpURLConnection http = null;
		OutputStreamWriter osw = null;
		OutputStream out = null;
		InputStream input = null;
		BufferedReader in = null;
		InputStreamReader reader = null;
		String postResult = "";
		try {
			URL url = new URL(urlStr);
			http = createConnection(url);
			if (http == null) {
				throw new NullPointerException("create http connection is null");
			}
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setUseCaches(false);
			http.setConnectTimeout(CONNECT_TIMEOUT);
			http.setReadTimeout(READ_TIMEOUT);
			http.setRequestMethod(requestMethod);
			http.setRequestProperty("Content-Type", contentType);
			if (headers != null && !headers.isEmpty()) {
				Iterator<String> it = headers.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					http.setRequestProperty(key, headers.get(key));
				}
			}
			http.connect();
			out = http.getOutputStream();
			osw = new OutputStreamWriter(out, ConstantsUnicode.UTF8);
			osw.write(data);
			osw.flush();
			if (http.getResponseCode() == APIResponseStatus.OK.getCode()) {
				input = http.getInputStream();
				reader = new InputStreamReader(input, ConstantsUnicode.UTF8);
				in = new BufferedReader(reader);
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					postResult += inputLine;
				}
				LOGGER.info("End to post data[" + data + "] with this result: " + postResult + ">>>");
			} else {
				LOGGER.error("Failed to post data, get the response code: {} >>>",http.getResponseCode());
			}
		} catch (Exception e) {
			LOGGER.error("Failed to create http connection.");

		} finally {
			IOUtil.closeWriter(osw);
			IOUtil.closeOutputStream(out);
			IOUtil.closeReader(in);
			IOUtil.closeReader(reader);
			IOUtil.closeInputStream(input);
			if (http != null) {
				http.disconnect();
			}
		}
		return postResult;
	}

	/**
	 * get data
	 * @param urlStr
	 * @param requestMethod
	 * @param headers
	 * @return
	 */
	public static String getData(String urlStr, String requestMethod, Map<String, String> headers) {
		LOGGER.trace("The remote url is : {}", urlStr);
		HttpURLConnection http = null;
		InputStream input = null;
		BufferedReader in = null;
		InputStreamReader reader = null;
		StringBuffer getResult = new StringBuffer();
		try {
			URL url = new URL(urlStr);
			http = createConnection(url);
			if (http == null) {
				throw new NullPointerException("create http connection is null");
			}
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setUseCaches(false);
			http.setConnectTimeout(CONNECT_TIMEOUT);
			http.setReadTimeout(READ_TIMEOUT);
			http.setRequestMethod(requestMethod);
			if (headers != null && !headers.isEmpty()) {
				Iterator<String> it = headers.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					http.setRequestProperty(key, headers.get(key));
				}
			}
			http.connect();
			if (http.getResponseCode() == APIResponseStatus.OK.getCode()) {
				input = http.getInputStream();
				reader = new InputStreamReader(input, ConstantsUnicode.UTF8);
				in = new BufferedReader(reader);
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					getResult.append(inputLine);
				}
			} else {
				LOGGER.error("Failed to get data, get the response code: {} >>>", http.getResponseCode());
			}
		} catch (Exception e) {
			LOGGER.error("Failed to get data.", e);

		} finally {
			IOUtil.closeReader(in);
			IOUtil.closeReader(reader);
			IOUtil.closeInputStream(input);
			if (http != null) {
				http.disconnect();
			}
		}
		return getResult.toString();
	}
}
