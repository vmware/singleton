/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.StringUtil;

public class ComponentsBasedOpt extends BaseOpt implements Opt {
    private final Logger      logger = LoggerFactory.getLogger(ComponentsBasedOpt.class.getName());
    private final Set<String> components;
    private final Set<String> locales;

    /**
     * @param components
     * @param locales
     */
    public ComponentsBasedOpt(Set<String> components, Set<String> locales) {
        this.components = components;
        this.locales = locales;
    }

    public JSONObject queryFromServer() {
        String url = V2URL
                .getComponentsTranslationURL(VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());

        HashMap<String, String> requestData = new HashMap<>();
        requestData.put(ConstantsKeys.LOCALES, String.join(",", locales));
        requestData.put(ConstantsKeys.COMPONENTS, String.join(",", components));
        responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(url, ConstantsKeys.GET,
                requestData);
        if (StringUtil.isEmpty(responseStr)) {
            throw new VIPJavaClientException(ConstantsMsg.SERVER_RETURN_EMPTY);
        }

        try {
            parseServerResponse();
        } catch (ParseException e) {
            throw new VIPJavaClientException(ConstantsMsg.SERVER_CONTENT_ERROR, e);
        }

        int statusCode = getResponseCode(responseJsonObj);
        if (!isSuccess(statusCode)) {
            throw new VIPJavaClientException(
                    String.format(ConstantsMsg.SERVER_RETURN_ERROR, statusCode, getResponseMessage(responseJsonObj)));
        }

        return responseJsonObj;
    }
}
