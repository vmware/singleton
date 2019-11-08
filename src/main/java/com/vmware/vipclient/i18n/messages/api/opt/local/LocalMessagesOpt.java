/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import org.json.simple.JSONObject;

import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.JSONBundleUtil;

public class LocalMessagesOpt implements Opt {
    private MessagesDTO dto;

    public LocalMessagesOpt(MessagesDTO dto) {
        this.dto = dto;
    }

    public JSONObject getComponentMessages() {
        return JSONBundleUtil.getMessages(dto.getLocale(), dto.getProductID(),
                dto.getVersion(), dto.getComponent());
    }

    public String getString() {
        JSONObject jo = this.getComponentMessages();
        String k = dto.getKey();
        String v = "";
        if (jo != null) {
            v = jo.get(k) == null ? "" : v;
        }
        return v;
    }
}
