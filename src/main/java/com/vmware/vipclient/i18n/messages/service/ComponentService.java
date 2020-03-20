/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.HttpRequester;
import com.vmware.vipclient.i18n.base.cache.CacheMode;
import com.vmware.vipclient.i18n.base.cache.persist.DiskCacheLoader;
import com.vmware.vipclient.i18n.base.cache.persist.Loader;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalMessagesOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.JSONUtils;

public class ComponentService {
    private MessagesDTO dto    = null;
    Logger              logger = LoggerFactory.getLogger(ComponentService.class);

    public ComponentService(MessagesDTO dto) {
        this.dto = dto;
    }

    /*
     * Get messages from local bundle or from remote vip service(non-Javadoc)
     * 
     * @see
     * com.vmware.vipclient.i18n.messages.service.IComponentService#getMessages
     * (com.vmware.vipclient.i18n.base.DataSourceEnum)
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getMessages(final Map<String, Object> cacheProps) {
        Map<String, String> transMap = new HashMap<String, String>();
        ComponentBasedOpt cbo = new ComponentBasedOpt(dto);
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
        	Map<String, Object> response = cbo.getComponentMessages();
	    	transMap = cbo.getMsgsJson(response);
	    	cacheProps.put(HttpRequester.HEADERS, response.get(HttpRequester.HEADERS));
	    	cacheProps.put(HttpRequester.RESPONSE_CODE, response.get(HttpRequester.RESPONSE_CODE));
			
        } else if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.Bundle) {
            transMap = new LocalMessagesOpt(dto).getComponentMessages();
        }
        return transMap;
    }

    public Map<String, String> getComponentTranslation() {
        Map<String, String> retMap = new HashMap<String, String>();
        Map<String, Object> cacheProps = new HashMap<String, Object>();
        
        CacheService cs = new CacheService(dto);
        retMap = cs.getCacheOfComponent();
        if (retMap == null
                && VIPCfg.getInstance().getCacheMode() == CacheMode.DISK) {
            Loader loader = VIPCfg.getInstance().getCacheManager()
                    .getLoaderInstance(DiskCacheLoader.class);
            retMap = loader.load(dto.getCompositStrAsCacheKey());
        }
        if (retMap == null && !cs.isContainComponent()) {
            Object o = this.getMessages(cacheProps);
            Map<String, String> dataMap = (o == null ? null
                    : (Map<String, String>) o);
            
            cs.addCacheOfComponent(dataMap, cacheProps);
            retMap = dataMap;
        }
        return retMap;
    }

    public boolean isComponentAvailable() {
        boolean r = false;
        Long s = null;
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
            ComponentBasedOpt dao = new ComponentBasedOpt(dto);
            String json = dao.getTranslationStatus();
            if (!JSONUtils.isEmpty(json)) {
                try {
                    s = (Long) JSONValue.parseWithException(json);
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                }
            }
            r = (s != null) && (s.longValue() == 206);
        }
        return r;
    }
}
