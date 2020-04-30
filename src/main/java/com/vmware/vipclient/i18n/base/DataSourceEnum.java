/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base;

import com.vmware.vipclient.i18n.messages.api.opt.MessageOpt;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalMessagesOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;

public enum DataSourceEnum {
    Bundle {
    	@Override
        public MessageOpt createMessageOpt(MessagesDTO dto) {
            return new LocalMessagesOpt(dto);
        }
    }, 
    VIP {
    	@Override
        public MessageOpt createMessageOpt(MessagesDTO dto) {
            return new ComponentBasedOpt(dto);
        }
    };
    public abstract MessageOpt createMessageOpt(MessagesDTO dto);
}
