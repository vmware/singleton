/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.messages.api.opt.ProductOpt;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductService {
    private BaseDTO dto = null;
    Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(BaseDTO dto) {
        this.dto = dto;
    }

    /**
     * Retrieves translated messages of all components of a product in the requested locale (See the dto object).
     *
     * @return translated messages of all components of a product locale specified in the dto object
     */
    public List<Map> getAllComponentTranslation() {
        List<Map> list = new ArrayList<Map>();
        List<String> locales = this.getSupportedLocales(VIPCfg.getInstance().getMsgOriginsQueue().iterator());
        List<String> components = this.getComponents(VIPCfg.getInstance().getMsgOriginsQueue().iterator());
        if (locales != null) {
            for (String languageTag : locales) {
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
        ProductOpt opt = dataSource.createProductOpt(dto);
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

    /**
     * Retrieves the list of locales of a product. It recursively applies data source fallback mechanism in case of failure.
     *
     * @param msgSourceQueueIter Iterator of DataSourceEnum sources
     * @return list of locales of the product specified in the dto object
     */
    public List<String> getSupportedLocales(Iterator<DataSourceEnum> msgSourceQueueIter) {
        if (!msgSourceQueueIter.hasNext()) { return null; }

        DataSourceEnum dataSource = msgSourceQueueIter.next();
        ProductOpt opt = dataSource.createProductOpt(dto);
        List<String> locales = opt.getSupportedLocales();
        // If failed to get locales from the data source
        if (locales.isEmpty()) {
            // Try the next dataSource in the queue
            if (msgSourceQueueIter.hasNext()) {
                locales = getSupportedLocales(msgSourceQueueIter);
                // If no more data source in queue, log the error. This means that neither online nor offline fetch succeeded.
            } else {
                logger.error(FormatUtils.format(ConstantsMsg.GET_LOCALES_FAILED, dataSource.toString()));
            }
        }
        return locales;
    }
}
