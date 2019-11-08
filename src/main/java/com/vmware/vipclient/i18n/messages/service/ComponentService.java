/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
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
     * get messages from local bundle or from remote vip service(non-Javadoc)
     * 
     * @see
     * com.vmware.vipclient.i18n.messages.service.IComponentService#getMessages
     * (com.vmware.vipclient.i18n.base.DataSourceEnum)
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getMessages() {
        Map<String, String> transMap = new HashMap<String, String>();
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
            transMap = new ComponentBasedOpt(dto).getComponentMessages();
        } else if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.Bundle) {
            transMap = new LocalMessagesOpt(dto).getComponentMessages();
        }
        return transMap;
    }

    public Map<String, String> getComponentTranslation() {
        Map<String, String> retMap = new HashMap<String, String>();
        CacheService cs = new CacheService(dto);
        retMap = cs.getCacheOfComponent();
        if (retMap == null
                && VIPCfg.getInstance().getCacheMode() == CacheMode.DISK) {
            Loader loader = VIPCfg.getInstance().getCacheManager()
                    .getLoaderInstance(DiskCacheLoader.class);
            retMap = loader.load(dto.getCompositStrAsCacheKey());
        }
        if (retMap == null && !cs.isContainComponent()) {
            Object o = this.getMessages();
            Map<String, String> dataMap = (o == null ? null
                    : (Map<String, String>) o);
            cs.addCacheOfComponent(dataMap);
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
