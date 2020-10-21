/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base;

import java.net.MalformedURLException;
import java.util.Map;

import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;

/**
 *
 * Singleton class, used for creating connection with vIP server
 *
 */
public class VIPService {
    private HttpRequester     httpRequester;

    public VIPService(String vipServer)throws MalformedURLException {
        createHttpRequester(vipServer);
    }

    /**
     * @Deprecated Constructor is used instead
     */
    @Deprecated
    public void initializeVIPService(String vipServer) throws MalformedURLException {
    }

    private void createHttpRequester(String vIPServer) throws MalformedURLException {
        if (httpRequester == null) {
            httpRequester = new HttpRequester(vIPServer);
        }
    }

    public HttpRequester getHttpRequester() {
        if (httpRequester == null) {
            throw new VIPJavaClientException(
                    "Please create HttpRequester, call createHttpRequest API first! ");
        }
        return httpRequester;
    }

    public void setHeaderParams(Map<String, String> params) {
        if (httpRequester == null) {
            throw new VIPJavaClientException(
                    "Please create HttpRequester, call createHttpRequest API first! ");
        }
        httpRequester.setCustomizedHeaderParams(params);
    }
}
