/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.login;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LTSTrustmanager implements X509TrustManager {
	private static Logger LOGGER = LoggerFactory.getLogger(LTSTrustmanager.class);
    public void checkClientTrusted(X509Certificate[] xcs, String client) throws CertificateException {
    	LOGGER.info("ClientTrusted:{}", client);
    }

    public void checkServerTrusted(X509Certificate[] xcs, String server) throws CertificateException {
    	LOGGER.info("ServerTrusted:{}", server);
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}