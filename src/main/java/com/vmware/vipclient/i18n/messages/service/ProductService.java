/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.*;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.messages.api.opt.ComponentOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.ProductBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.messages.dto.LocaleDTO;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductService {
    private BaseDTO dto = null;
    Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(BaseDTO dto) {
        this.dto = dto;
    }

    /**
     * get supported components defined in vip service
     * @return JSONArray
     * @deprecated Replaced by {@link #getComponents(Iterator<>)}
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
     * @deprecated Replaced by {@link com.vmware.vipclient.i18n.messages.service.LocaleService#getSupportedLanguages(Iterator<>)}
     */
    @Deprecated
    public JSONArray getSupportedLocalesFromRemoteVIP() {
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setProductID(dto.getProductID());
        baseDTO.setVersion(dto.getVersion());
        ProductBasedOpt dao = new ProductBasedOpt(baseDTO);
        return dao.getSupportedLocalesFromRemoteVIP();
    }

    /**
     * Retrieves translated messages of all components of a product in the requested locale (See the dto object).
     *
     * @return translated messages of all components of a product locale specified in the dto object
     */
    public List<Map> getAllComponentTranslation() {
        List<Map> list = new ArrayList<Map>();
        LocaleDTO localeDTO = new LocaleDTO(dto.getProductID(), dto.getVersion());
        Map<String, String> locales = new LocaleService(localeDTO).getSupportedLanguages();
        List<String> components = this.getComponents(VIPCfg.getInstance().getMsgOriginsQueue().iterator());
        if (locales != null) {
            for (String languageTag : locales.keySet()) {
                for (Object component : components) {
                    MessagesDTO msgDTO = new MessagesDTO(((String) component).trim(), LocaleUtility.fmtToMappedLocale(Locale.forLanguageTag(languageTag)).toString().trim(),
                            dto.getProductID(), dto.getVersion());
                    Map<String, String> retMap = new ComponentService(msgDTO).getMessages(null).getCachedData();
                    if (retMap != null) {
                        list.add(retMap);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Retrieves the list of components of a product. It recursively applies data source fallback mechanism in case of failure.
     *
     * @param msgSourceQueueIter Iterator of DataSourceEnum sources
     * @return list of components of the product specified in the dto object
     */
    public List<String> getComponents (Iterator<DataSourceEnum> msgSourceQueueIter) {
        if (!msgSourceQueueIter.hasNext())
            return null;

        DataSourceEnum dataSource = msgSourceQueueIter.next();
        ComponentOpt opt = dataSource.createComponentOpt(dto);
        List<String> components = opt.getComponents();
        // If failed to get components from the data source
        if (components.isEmpty()) {
            // Try the next dataSource in the queue
            if (msgSourceQueueIter.hasNext()) {
                components = getComponents(msgSourceQueueIter);
                // If no more data source in queue, log the error. This means that neither online nor offline fetch succeeded.
            } else {
                logger.error(FormatUtils.format(ConstantsMsg.GET_COMPONENTS_FAILED, dataSource.toString()));
            }
        }
        return components;
    }

}
