/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.ComponentOpt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.simple.JSONArray;

public class RemoteComponentOpt extends BaseOpt implements ComponentOpt {
    private BaseDTO dto;
    public RemoteComponentOpt(BaseDTO dto) {
        this.dto = dto;
    }

    @Override
    public List<String> getComponents() {
        List<String> components = new LinkedList<String>();
        BaseDTO dto = new BaseDTO();
        dto.setProductID(this.dto.getProductID());
        dto.setVersion(this.dto.getVersion());

        JSONArray componentsObj = new JSONArray();
        String responseStr = "";
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                V2URL.getComponentListURL(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, null);
        responseStr = (String) response.get(URLUtils.BODY);
        if (null != responseStr && !responseStr.equals("")) {
            Object dataObj = this.getMessagesFromResponse(responseStr,
                    ConstantsKeys.COMPONENTS);
            if (dataObj != null) {
                componentsObj = (JSONArray) dataObj;
            }
        }
        for (Object obj : componentsObj) {
            components.add(obj.toString());
        }
        return components;
    }
}
