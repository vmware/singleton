/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.messages.api.opt.server.RemoteProductOpt;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.LocaleUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductService {
    private MessagesDTO dto = null;

    public ProductService(MessagesDTO dto) {
        this.dto = dto;
    }

    // get supported components defined in vip service
    public List<String> getComponents() {
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setProductID(dto.getProductID());
        baseDTO.setVersion(dto.getVersion());
        RemoteProductOpt dao = new RemoteProductOpt(baseDTO);
        return dao.getComponents();
    }

    // get supported locales defined in vip service
    public List<String> getSupportedLocales() {
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setProductID(dto.getProductID());
        baseDTO.setVersion(dto.getVersion());
        RemoteProductOpt dao = new RemoteProductOpt(baseDTO);
        return dao.getSupportedLocales();
    }

    public List<Map> getAllComponentTranslation() {
        List<Map> list = new ArrayList<Map>();
        Object[] locales = {};
        Object[] components = {};
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
            locales = this.getSupportedLocales().toArray();
            components = this.getComponents()
                    .toArray();
        }
        for (Object locale : locales) {
            for (Object component : components) {
                dto.setComponent(((String) component).trim());
                dto.setLocale(LocaleUtility.fmtToMappedLocale((String) locale).toString().trim());
                Map<String, String> retMap = new ComponentService(dto).getMessages().getCachedData();
                if (retMap != null) {
                    list.add(retMap);
                }
            }
        }
        return list;
    }
}
