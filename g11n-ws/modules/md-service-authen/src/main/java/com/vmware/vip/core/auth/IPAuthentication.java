/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.auth;



import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPAuthentication implements IAuthen{

	private static Logger LOGGER = LoggerFactory.getLogger(IPAuthentication.class);
	HttpServletRequest request = null;

	public IPAuthentication(HttpServletRequest request) {
		this.request = request;
	}
	public void authen() throws AuthenException {
		if (this.request == null) {
			return;
		}
		String remoteAddr = request.getRemoteAddr();
		String host = request.getRemoteHost();
		int port = request.getRemotePort();
		LOGGER.info("===========Remote Requester==========");
		LOGGER.info("Remote Addr: " + remoteAddr);
		LOGGER.info("Remote Host: " + host);
		LOGGER.info("Remote Port: " + port);
	}
}
