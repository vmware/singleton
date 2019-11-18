/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class StringBasedOpt extends BaseOpt implements Opt {
    private MessagesDTO dto = null;

    public StringBasedOpt(MessagesDTO dto) {
        this.dto = dto;
    }

    public JSONObject getComponentMessages() {
        String responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(V2URL
                .getComponentTranslationURL(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, null);
        if (null == responseStr || responseStr.equals("")) {
            return null;
        } else {
            Object dataObj = this.getMessagesFromResponse(responseStr,
                    ConstantsKeys.MESSAGES);
            JSONObject msgObject = null;
            if (dataObj != null && !("").equalsIgnoreCase(dataObj.toString())) {
                msgObject = (JSONObject) dataObj;
            }
            return msgObject;
        }
    }

    public String getString() {
        JSONObject jo = this.getComponentMessages();
        String k = dto.getKey();
        Object v = jo.get(k);
        return (v == null ? "" : (String) v);
    }

    public String postString() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("source", this.dto.getSource());
        String responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(V2URL
                .getKeyTranslationURL(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.POST, params);
        Object o = this.getMessagesFromResponse(responseStr,
                ConstantsKeys.TRANSLATION);
        if (o != null)
            return (String) o;
        else
            return "";
    }

    /**
     * post a set of sources to remote
     * 
     * @param sourceSet
     * @return
     */
    public String postSourceSet(String sourceSet) {
        String status = "";
        if (sourceSet == null || "".equalsIgnoreCase(sourceSet)) {
            return status;
        }
        String responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                V2URL.getPostKeys(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.POST, sourceSet);
        Object o = this.getStatusFromResponse(responseStr, ConstantsKeys.CODE);
        if (o != null)
            status = o.toString();
        return status;
    }

    public String getTranslationStatus() {
        String status = "";
        Map<String, String> params = new HashMap<String, String>();
        params.put("checkTranslationStatus", "true");
        String getURL = V2URL.getComponentTranslationURL(dto,
                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());
        String responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(getURL, ConstantsKeys.GET,
                params);
        if (null == responseStr || responseStr.equals("")) {
            return status;
        } else {
            Object o = this.getMessagesFromResponse(responseStr, ConstantsKeys.STATUS);
            if (o != null) {
                status = o.toString();
            }
        }
        return status;
    }
}
