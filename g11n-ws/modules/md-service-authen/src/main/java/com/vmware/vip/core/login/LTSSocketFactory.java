/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.login;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LTSSocketFactory extends SSLSocketFactory {
	private static Logger LOGGER = LoggerFactory.getLogger(LTSSocketFactory.class);
	private SSLSocketFactory socketFactory;

	public LTSSocketFactory() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");

			ctx.init(null, new TrustManager[] { new LTSTrustmanager() }, new SecureRandom());

			socketFactory = ctx.getSocketFactory();

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);

		}

	}

	
	 public static SocketFactory getDefault() { 
		 return new LTSSocketFactory();
	  
	  }
	 

	@Override

	public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException {
		return socketFactory.createSocket(arg0, arg1, arg2, arg3);

	}

	@Override

	public String[] getDefaultCipherSuites() {
		return socketFactory.getDefaultCipherSuites();

	}

	@Override

	public String[] getSupportedCipherSuites() {
		return socketFactory.getSupportedCipherSuites();

	}

	@Override

	public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
		return socketFactory.createSocket(arg0, arg1);

	}

	@Override

	public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
		return socketFactory.createSocket(arg0, arg1);

	}

	@Override

	public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
			throws IOException, UnknownHostException {
		return socketFactory.createSocket(arg0, arg1, arg2, arg3);

	}

	@Override

	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
		return socketFactory.createSocket(arg0, arg1, arg2, arg3);

	}

}


