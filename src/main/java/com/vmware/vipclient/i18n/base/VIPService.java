/*
 * Copyright 2019 VMware, Inc.
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
    private static VIPService vIPServiceInstance;
    private HttpRequester     httpRequester;
    private String            productID;
    private String            version;

    private VIPService() {
    }

    /**
     * get the instance of the VIPService.
     *
     * @return The object of the VIPService.
     */
    public static VIPService getVIPServiceInstance() {
        if (vIPServiceInstance == null) {
            vIPServiceInstance = new VIPService();
        }
        return vIPServiceInstance;
    }

    /**
     * Initialize vIP Service with productID, version and vIPHostName.
     *
     * @param productID
     *            The name of product.
     * @param version
     *            The release version of product.
     * @param vIPHostName
     *            The info of vIP Server(ip:port).
     * @throws MalformedURLException
     */
    public void initializeVIPService(String productID, String version,
            String vIPServer) throws MalformedURLException {
        if (vIPServiceInstance == null) {
            throw new VIPJavaClientException(
                    "Please create VIPServiceInstance first!");
        }
        this.productID = productID;
        this.version = version;
        createHttpRequester(vIPServer);
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

    public String getProductID() {
        return productID;
    }

    public String getVersion() {
        return version;
    }

    public void setHeaderParams(Map<String, String> params) {
        if (httpRequester == null) {
            throw new VIPJavaClientException(
                    "Please create HttpRequester, call createHttpRequest API first! ");
        }
        httpRequester.setCustomizedHeaderParams(params);
    }
}
