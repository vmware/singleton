package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.KeyBasedOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalKeyBasedOpt extends BaseOpt implements Opt, KeyBasedOpt {
    private Logger logger = LoggerFactory.getLogger(LocalKeyBasedOpt.class);

    private MessagesDTO dto = null;

    public LocalKeyBasedOpt(MessagesDTO dto) {
        this.dto = dto;
    }

    @Override
    public void fetchMultiVersionKeyMessages(MessageCacheItem cacheItem) {
        //TODO
        logger.info(dto.getVersion());
    }
}
