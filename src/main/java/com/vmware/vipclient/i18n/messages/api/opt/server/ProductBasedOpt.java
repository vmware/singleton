/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.ProductOpt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Map;

public class ProductBasedOpt extends BaseOpt implements ProductOpt {
    private BaseDTO dto = null;

    public ProductBasedOpt(BaseDTO dto) {
        this.dto = dto;
    }

    /*
     * get supported components from vip(non-Javadoc)
     * 
     * @see com.vmware.vipclient.i18n.messages.dao.IComponentDao#getComponents()
     */
    public List<String> getComponents() {
        JSONArray msgObject = new JSONArray();
        String responseStr = "";
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                V2URL.getComponentListURL(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, null);
        responseStr = (String) response.get(URLUtils.BODY);
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
    public List<String> getSupportedLocales() {
        JSONArray msgObject = new JSONArray();
        String responseStr = "";
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(V2URL.getSupportedLocaleListURL(
                dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()), ConstantsKeys.GET, null);
        responseStr = (String) response.get(URLUtils.BODY);
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
