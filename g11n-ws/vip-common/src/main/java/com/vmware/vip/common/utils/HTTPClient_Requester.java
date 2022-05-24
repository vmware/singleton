package com.vmware.vip.common.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
**/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.exceptions.VIPHttpException;

public class HTTPClient_Requester {

	private static Logger LOGGER = LoggerFactory.getLogger(HTTPClient_Requester.class);
	public static final String HTTP_PROTOCOL = "http";
	public static final String HTTPS_PROTOCOL = "https";
	/* 5 seconds */
	public static final Integer CONNECT_TIMEOUT = 5000;

	/* 5 seconds */
	public static final Integer READ_TIMEOUT = 5000;
/**
	private static RequestConfig REQUEST_CONF = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
			.setSocketTimeout(READ_TIMEOUT).build();

	public static String postFormData(Map<String, String> params, String urlStr) throws VIPHttpException {
		return postFormData(params, urlStr);
	}

	public static String postFormData(Map<String, String> params, String urlStr, Map<String, String> headers)
			throws VIPHttpException {

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if (params != null) {
			for (Entry<String, String> entry : params.entrySet()) {
				formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

		HttpPost post = new HttpPost(urlStr);
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				post.setHeader(entry.getKey(), entry.getValue());
			}
		}
		post.setConfig(REQUEST_CONF);
		post.setEntity(entity);

		String result = executeRequest(post);
		LOGGER.info("End to pust data[" + formparams.toArray().toString() + "] with this result: " + result + ">>>");
		return result;
	}


	public static String postJSONStr(String jsonStr, String urlStr) throws VIPHttpException {
		return postJSONStrWithHeaders(jsonStr, urlStr, null);
	}


	public static String postJSONStrWithHeaders(String jsonStr, String urlStr, Map<String, String> headers)
			throws VIPHttpException {
		StringEntity entity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
		entity.setChunked(true);
		HttpPost post = new HttpPost(urlStr);
		post.setConfig(REQUEST_CONF);
		post.setEntity(entity);
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				post.setHeader(entry.getKey(), entry.getValue());
			}
		}

		String result = executeRequest(post);
		LOGGER.info("End to pust data[" + jsonStr + "] with this result: " + result + ">>>");
		return result;

	}

	
	public static String putJSONStr(String jsonStr, String urlStr, Map<String, String> headers) throws VIPHttpException {

		StringEntity entity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
		entity.setChunked(true);
		HttpPut put = new HttpPut(urlStr);
		put.setConfig(REQUEST_CONF);
		put.setEntity(entity);
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				put.setHeader(entry.getKey(), entry.getValue());
			}
		}

		String result = executeRequest(put);
		LOGGER.info("End to pust data[" + jsonStr + "] with this result: " + result + ">>>");
		return result;
	}

	public static String getReqJsonStr(String urlStr, Map<String, String> headers) {

		HttpGet get = new HttpGet(urlStr);
		get.setConfig(REQUEST_CONF);
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				get.setHeader(entry.getKey(), entry.getValue());
			}
		}

		try {
			return executeRequest(get);
		} catch (VIPHttpException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	
	
	
	
	
	public static <T> T postReqJSON2Obj(String jsonStr, String urlStr, Map<String, String> headers, Class<T> clazz) throws VIPHttpException {

		StringEntity entity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
		entity.setChunked(true);
		HttpPut put = new HttpPut(urlStr);
		put.setConfig(REQUEST_CONF);
		put.setEntity(entity);
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				put.setHeader(entry.getKey(), entry.getValue());
			}
		}

		return requestObj(put, clazz);
	
	}
	
	public static <T> T getReqJSON2Obj(String urlStr, Map<String, String> headers, Class<T> clazz) throws VIPHttpException {

		HttpGet get = new HttpGet(urlStr);
		get.setConfig(REQUEST_CONF);
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				get.setHeader(entry.getKey(), entry.getValue());
			}
		}
		
		return requestObj(get, clazz);
	
	}
	

	
	private static String executeRequest(HttpUriRequest request) throws VIPHttpException {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		try (CloseableHttpResponse response = httpClient.execute(request)) {
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, Charset.forName(ConstantsUnicode.UTF8));
		} catch (Exception e) {
			throw new VIPHttpException(e.getMessage(), e);
		}

	}
	

	public static <T> T requestObj(HttpUriRequest request, Class<T> clazz) throws VIPHttpException {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		ResponseHandler<T> rh = new ResponseHandler<T>() {
			@Override
			public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() >= 300) {
					throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
				}

				HttpEntity entity = response.getEntity();
				if (entity == null) {
					throw new ClientProtocolException("Response contains no content");
				}

				ContentType contentType = ContentType.getOrDefault(entity);
				Charset charset = contentType.getCharset();
				Reader reader = new InputStreamReader(entity.getContent(), charset);
				ObjectMapper mapper = new ObjectMapper();
				return mapper.readValue(reader, clazz);
			}

		};

		try {
			return httpclient.execute(request, rh);
		} catch (Exception e) {
			throw new VIPHttpException(e.getMessage(), e);
		}

	}
  **/
}