/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.login;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class LTSTrustmanager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}