/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class ComponentBasedOpt extends BaseOpt implements Opt {
    private Logger      logger = LoggerFactory.getLogger(ComponentBasedOpt.class.getName());
    private MessagesDTO dto    = null;

    public ComponentBasedOpt(MessagesDTO dto) {
        this.dto = dto;
    }

    public JSONObject getComponentMessages() {
        String url = V2URL.getComponentTranslationURL(dto,
                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());
        if (ConstantsKeys.LATEST.equals(dto.getLocale())) {
            url = url.replace("pseudo=false", "pseudo=true");
        }
        String responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(url, ConstantsKeys.GET,
                null);
        if (null == responseStr || responseStr.equals("")) {
            return null;
        } else {
            if (ConstantsKeys.LATEST.equals(dto.getLocale())) {
                responseStr = responseStr.replace(ConstantsKeys.PSEUDOCHAR, "");
            }

            JSONObject msgObject = (JSONObject) this.getMessagesFromResponse(responseStr,
                    ConstantsKeys.MESSAGES);

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
        else {
            Object m = this.getStatusFromResponse(responseStr, ConstantsKeys.MESSAGE);
            if (m != null) {
                logger.warn((String) m);
            }
            return "";
        }
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
        String responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(V2URL
                .getComponentTranslationURL(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, params);
        if (null == responseStr || responseStr.equals("")) {
            return status;
        } else {
            Object o = this.getStatusFromResponse(responseStr, ConstantsKeys.CODE);
            if (o != null)
                status = o.toString();
        }
        return status;
    }
}
