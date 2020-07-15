/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.*;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.messages.api.opt.ComponentOpt;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.ProductBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONArray;

public class ProductService {
    private MessagesDTO dto = null;

    public ProductService(MessagesDTO dto) {
        this.dto = dto;
    }

    /**
     * get supported components defined in vip service
     * @deprecated Replaced by {@link #getComponents(Iterator<DataSourceEnum>)}
     */
    @Deprecated
    public JSONArray getComponentsFromRemoteVIP() {
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setProductID(dto.getProductID());
        baseDTO.setVersion(dto.getVersion());
        ProductBasedOpt dao = new ProductBasedOpt(baseDTO);
        return dao.getComponentsFromRemoteVIP();
    }

    /**
     * get supported locales defined in vip service
     * @deprecated Replaced by {@link #getLanguages(Iterator<DataSourceEnum>)}
     */
    @Deprecated
    public JSONArray getSupportedLocalesFromRemoteVIP() {
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setProductID(dto.getProductID());
        baseDTO.setVersion(dto.getVersion());
        ProductBasedOpt dao = new ProductBasedOpt(baseDTO);
        return dao.getSupportedLocalesFromRemoteVIP();
    }

    public List<Map> getAllComponentTranslation() {
        List<Map> list = new ArrayList<Map>();
        Map<String, String> locales = this.getLanguages(VIPCfg.getInstance().getMsgOriginsQueue().iterator());
        List<String> components = this.getComponents(VIPCfg.getInstance().getMsgOriginsQueue().iterator());
        if (locales != null) {
            for (String languageTag : locales.keySet()) {
                for (Object component : components) {
                    dto.setComponent(((String) component).trim());
                    dto.setLocale(LocaleUtility.fmtToMappedLocale(Locale.forLanguageTag(languageTag)).toString().trim());
                    Map<String, String> retMap = new ComponentService(dto).getMessages().getCachedData();
                    if (retMap != null) {
                        list.add(retMap);
                    }
                }
            }
        }
        return list;
    }

    private Map<String, String> getLanguages(Iterator<DataSourceEnum> msgSourceQueueIter) {
        if (!msgSourceQueueIter.hasNext())
            return null;

        DataSourceEnum dataSource = msgSourceQueueIter.next();
        LocaleOpt opt = dataSource.createLocaleOpt();
        return opt.getLanguages(LocaleUtility.getDefaultLocale().toLanguageTag());
    }

    private List<String> getComponents (Iterator<DataSourceEnum> msgSourceQueueIter) {
        if (!msgSourceQueueIter.hasNext())
            return null;

        DataSourceEnum dataSource = msgSourceQueueIter.next();
        ComponentOpt opt = dataSource.createComponentOpt(dto);
        return opt.getComponents();
    }

}
