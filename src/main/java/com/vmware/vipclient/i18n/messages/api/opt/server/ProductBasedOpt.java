/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import org.json.simple.JSONArray;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class ProductBasedOpt extends BaseOpt implements Opt {
    private BaseDTO dto = null;

    public ProductBasedOpt(BaseDTO dto) {
        this.dto = dto;
    }

    /*
     * get supported components from vip(non-Javadoc)
     * 
     * @see com.vmware.vipclient.i18n.messages.dao.IComponentDao#getComponents()
     */
    public JSONArray getComponentsFromRemoteVIP() {
        JSONArray msgObject = new JSONArray();
        String responseStr = "";
        responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                V2URL.getComponentListURL(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, null);
        if (null != responseStr && !responseStr.equals("")) {
            Object dataObj = this.getMessagesFromResponse(responseStr,
                    ConstantsKeys.COMPONENTS);
            if (dataObj != null) {
                msgObject = (JSONArray) dataObj;
            }
        }
        return msgObject;
    }

    /*
     * get supported locales from vip(non-Javadoc)
     * 
     * @see com.vmware.vipclient.i18n.messages.dao.ILocaleDao#getSupportedLocales()
     */
    public JSONArray getSupportedLocalesFromRemoteVIP() {
        JSONArray msgObject = new JSONArray();
        String responseStr = "";
        responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(V2URL.getSupportedLocaleListURL(
                dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()), ConstantsKeys.GET, null);
        if (null != responseStr && !responseStr.equals("")) {
            Object dataObj = this.getMessagesFromResponse(responseStr,
                    ConstantsKeys.LOCALES);
            if (dataObj != null) {
                msgObject = (JSONArray) dataObj;
            }
        }
        return msgObject;
    }
}
