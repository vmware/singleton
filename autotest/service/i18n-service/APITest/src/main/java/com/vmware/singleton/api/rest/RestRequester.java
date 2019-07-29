package com.vmware.singleton.api.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//import com.vmware.g11n.log.GLogger;
import com.vmware.singleton.api.test.common.Config;
import com.vmware.singleton.api.test.common.Constants;
import com.vmware.singleton.api.test.common.Util;

public class RestRequester {
	public String thumbprint = null;
	public String adapterId = null;
	public String adapterName = null;
	public String credentialInstanceId = null;
	public String superstr=null;
	public static String serverUrl;
	public static int lastCallResponseCode;
	public Config config= Config.getInstance();
//	public static GLogger logger = GLogger.getInstance(RestRequester.class.getSimpleName());


	public static void httpSSLExceptionHandler() throws NoSuchAlgorithmException, KeyManagementException
	{
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
		{
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}
			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType)
			{
			}
			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType)
			{
			}
		}};
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier(){
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}};


			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

	}

	public static String getResponse(String requestUrl, String requestMethod,
			String requestBody) throws KeyManagementException, NoSuchAlgorithmException {
		return getResponse(requestUrl, requestMethod, requestBody, null);
	}

	public static String getResponse(String requestUrl, String requestMethod,
			String requestBody, HashMap<String, String> requestProperties) {
		try {
			httpSSLExceptionHandler();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			System.out.println("http ssl exception handler: " + e.getStackTrace());
//			logger.debug("http ssl exception handler: " + e.getStackTrace());
			return null;
		}
		HttpsURLConnection con = null;
		URL url = null;
		InputStream responseStream;
		lastCallResponseCode = -1;
		try {
			url = new URL(requestUrl);
			con = (HttpsURLConnection)url.openConnection();
			con.setRequestMethod(requestMethod);
			con.setRequestProperty("Accept","application/json");
			con.setRequestProperty("Accept-Charset", "UTF-8");
			con.setRequestProperty("Content-Type","application/json");
			if (requestProperties!=null && !requestProperties.isEmpty()) {
				for (String key : requestProperties.keySet()) {
					con.setRequestProperty(key, requestProperties.get(key));
				}
			}
			if (requestBody!=null && requestBody!="") {
				con.setDoOutput(true);
				byte[] bodyInByte = requestBody.getBytes(StandardCharsets.UTF_8);
				con.setRequestProperty("Content-Length", Integer.toString(bodyInByte.length));
				con.getOutputStream().write(bodyInByte);
			}
			lastCallResponseCode=con.getResponseCode();
			responseStream=con.getInputStream();
		} catch (MalformedURLException e) {
			responseStream = con.getErrorStream();
			e.printStackTrace();
		} catch (IOException e) {
			responseStream = con.getErrorStream();
		}
		return responseStream==null?Integer.toString(lastCallResponseCode):Util.streamToString(responseStream);
	}

	public static String [] getResponseStrList(String requestUrl, String requestMethod, String requestBody)
			throws KeyManagementException, NoSuchAlgorithmException {
		return getResponseStrList(requestUrl, requestMethod, requestBody, null);
	}

	public static String [] getResponseStrList(String requestUrl, String requestMethod,
			String requestBody, HashMap<String, String> requestProperties)
			throws KeyManagementException, NoSuchAlgorithmException {
		String []responseStrList = null;
		String response = getResponse(requestUrl, requestMethod, requestBody, requestProperties);
		if(lastCallResponseCode==200) {
			response = response.replace("[", "").replace("]", "").replace("\"", "");
			if (response!=null && !response.trim().equals("")) {
				responseStrList = response.split(Constants.CONF_LIST_SEPERATER);
			}
		}
		return responseStrList;
	}


}
