/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.HttpRequester;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.StringUtil;

public class ComponentsBasedOpt extends BaseOpt implements Opt {
    private final Logger      logger = LoggerFactory.getLogger(ComponentsBasedOpt.class.getName());

    private final VIPCfg cfg;

    /**
     * @param cfg
     */
    public ComponentsBasedOpt(final VIPCfg cfg) {
        this.cfg = cfg;
    }

    public JSONObject queryFromServer(final Set<String> components, final Set<String> locales, final Map<String,Object> cacheProps) {
        String url = V2URL
                .getComponentsTranslationURL(VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL(),
                        this.cfg);

        HashMap<String, String> requestData = new HashMap<>();
        requestData.put(ConstantsKeys.LOCALES, String.join(",", locales));
        requestData.put(ConstantsKeys.COMPONENTS, String.join(",", components));
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(url, ConstantsKeys.GET,
                requestData);
        cacheProps.put(HttpRequester.HEADERS, response.get(HttpRequester.HEADERS));
        cacheProps.put(HttpRequester.RESPONSE_CODE, response.get(HttpRequester.RESPONSE_CODE));
        this.responseStr = (String) response.get(HttpRequester.BODY);
        if (StringUtil.isEmpty(this.responseStr))
            throw new VIPJavaClientException(ConstantsMsg.SERVER_RETURN_EMPTY);

        try {
            this.parseServerResponse();
        } catch (ParseException e) {
            throw new VIPJavaClientException(ConstantsMsg.SERVER_CONTENT_ERROR, e);
        }

        int statusCode = this.getResponseCode(this.responseJsonObj);
        if (!this.isSuccess(statusCode))
            throw new VIPJavaClientException(
                    String.format(ConstantsMsg.SERVER_RETURN_ERROR, statusCode, this.getResponseMessage(this.responseJsonObj)));

        return this.responseJsonObj;
    }
}
