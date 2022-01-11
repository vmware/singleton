/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.alibaba.fastjson.JSONObject;
import com.vmware.l10agent.base.PropertyContantKeys;


/**
 * This class provides methods of requesting a remote server.
 *
 */
public class HttpRequester {
	private static SSLSocketFactory sslSocketFactory;
	@SuppressWarnings("unused")
	private static String vIPHostName;

	/**
	 * Basic path of the request URL.
	 */
	private String baseURL;

	/**
	 * get the baseURL
	 *
	 * @param vIPServer
	 *            The host address of the vIP Server
	 */
	protected HttpRequester(String vIPServer) {
		if (null != vIPServer && vIPServer.length() > 0) {
			this.baseURL = PropertyContantKeys.HTTPS_PROTOCOL  + vIPServer;
			HttpRequester.vIPHostName = (vIPServer.contains(":")) ? vIPServer.split(":")[0] : vIPServer;
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
			status = InetAddress.getByName(ipAddress).isReachable(PropertyContantKeys.HTTP_CONNECT_TIMEOUT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return status;
	}
	
	
	
	public static String sendGet(String url, String accessToken, Map<String, String> params,Map<String, String> headers) {
		params.put("AccessToken", accessToken);
		return sendGet(url, params, headers);
	}

	/**
	 * The get method of requesting a remote server.
	 *
	 * @param url
	 *            The remote server url.
	 * @return
	 */
	public static String sendGet(String url, Map<String, String> params,Map<String, String> headers) {
		if(params != null) {
			String paramsStr = "";
			for (Entry<String, String> entry : params.entrySet()) {
				paramsStr += entry.getKey() + "=" + entry.getValue()+ "&";
			}
			
			url = url + "?" + paramsStr;
		}

		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
	
			HttpURLConnection conn = HttpRequester.createConnection(url);
			conn.setConnectTimeout(PropertyContantKeys.HTTP_CONNECT_TIMEOUT);
			conn.setReadTimeout(PropertyContantKeys.HTTP_READ_TIMEOUT);
			conn.setRequestMethod(PropertyContantKeys.HTTP_MOTHED_GET);

			if(headers != null) {
				for(Entry<String, String> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while (-1 != (len = is.read(buffer))) {
					baos.write(buffer, 0, len);
					baos.flush();
				}
				return baos.toString("UTF-8");
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getBaseURL() {
		return this.baseURL;
	}

	/**
	 * The post method of requesting a remote server.
	 * 
	 * @param url
	 *            The remote server url.
	 * @param params
	 *            The request parameter.
	 * @return
	 */
	public static String post(String url, Map<String, String> params,Map<String, String> headers) {
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
		    HttpURLConnection conn = HttpRequester.createConnection(url);
			conn.setRequestMethod(PropertyContantKeys.HTTP_MOTHED_POST);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(PropertyContantKeys.HTTP_CONNECT_TIMEOUT);
			conn.setReadTimeout(PropertyContantKeys.HTTP_READ_TIMEOUT);
			if(headers != null) {
				for(Entry<String, String> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			PrintWriter pw = new PrintWriter(conn.getOutputStream());
			String paramsStr = "";
			for (String key : params.keySet()) {
				paramsStr += key + "=" + params.get(key) + "&";
			}
			paramsStr = paramsStr.substring(0, paramsStr.length() - 1);
			pw.write(paramsStr);
			pw.flush();
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				bis = new BufferedInputStream(conn.getInputStream());
				bos = new ByteArrayOutputStream();
				int len;
				byte[] arr = new byte[1024];
				while ((len = bis.read(arr)) != -1) {
					bos.write(arr, 0, len);
					bos.flush();
				}
				return bos.toString("UTF-8");
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String sendPostJson(String url, Map<String, String> params,Map<String, String> headers) {
		
		
	    HttpURLConnection conn =null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try{
		conn= HttpRequester.createConnection(url);
		conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod(PropertyContantKeys.HTTP_MOTHED_POST);
		
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Content-Type","application/json");
        
		if(headers != null) {
			for(Entry<String, String> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		conn.connect();
		PrintWriter pw = new PrintWriter(conn.getOutputStream());
		String paramsStr = JSONObject.toJSONString(params);
		pw.write(paramsStr);
		pw.flush();
		if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
			bis = new BufferedInputStream(conn.getInputStream());
			bos = new ByteArrayOutputStream();
			int len;
			byte[] arr = new byte[1024];
			while ((len = bis.read(arr)) != -1) {
				bos.write(arr, 0, len);
				bos.flush();
			}
			return bos.toString("UTF-8");
		}
	} catch (SocketTimeoutException e) {
		e.printStackTrace();
	} catch (ProtocolException e) {
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		try {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return null;
	}
	
	
	
public static String sendJsonWithToken(String url, String method, String jsonStr,String tokenStr) {
		
		
      
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
		HttpURLConnection conn = HttpRequester.createConnection(url);
		conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod(method);
		
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Content-Type","application/json");
        
        if(tokenStr != null) {
        	conn.setRequestProperty("token", tokenStr);
        }
	
		conn.connect();
		if(jsonStr != null) {
			PrintWriter pw = new PrintWriter(conn.getOutputStream());
			pw.write(jsonStr);
			pw.flush();
			
		}
		if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
			bis = new BufferedInputStream(conn.getInputStream());
			bos = new ByteArrayOutputStream();
			int len;
			byte[] arr = new byte[1024];
			while ((len = bis.read(arr)) != -1) {
				bos.write(arr, 0, len);
				bos.flush();
			}
			return bos.toString("UTF-8");
		}else {
		  System.out.println(conn.getResponseCode());
		}
	} catch (SocketTimeoutException e) {
		e.printStackTrace();
	
	} catch (ProtocolException e) {
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		try {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return null;
	}
	
	
	
	
	/**
	 * Get the request connection.
	 *
	 * @param path
	 *            The remote server url.
	 * @return
	 * @throws IOException 
	 */
	private static HttpURLConnection createConnection(String path) throws IOException {
		HttpURLConnection result = null;

			URL url = new URL(path.trim());
			
			 /* assert (null != url); assert
			  (ConstantsKeys.HTTP_PROTOCOL.equals(url.getProtocol()) ||
			  ConstantsKeys.HTTPS_PROTOCOL .equals(url.getProtocol()));
			 */
			result = (HttpURLConnection) url.openConnection();
			if (result instanceof HttpsURLConnection) {
				HttpsURLConnection httpsConn = (HttpsURLConnection) result;
				httpsConn.setHostnameVerifier(new HostnameVerifier() {
					// verify the client identity, here does't do any
					// validation.
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				httpsConn.setSSLSocketFactory(getSocketFactory());
			}
	
		return result;
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
					public void checkClientTrusted(X509Certificate[] cert, String authType)
							throws CertificateException {
					}

					public void checkServerTrusted(X509Certificate[] cert, String authType)
							throws CertificateException {
					}

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
