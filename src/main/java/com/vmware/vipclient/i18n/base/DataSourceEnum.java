/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base;

import com.vmware.vipclient.i18n.messages.api.opt.KeyBasedOpt;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;
import com.vmware.vipclient.i18n.messages.api.opt.PatternOpt;
import com.vmware.vipclient.i18n.messages.api.opt.MessageOpt;
import com.vmware.vipclient.i18n.messages.api.opt.ProductOpt;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalKeyBasedOpt;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalLocaleOpt;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalPatternOpt;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalMessagesOpt;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalProductOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.RemoteProductOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.StringBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.messages.dto.LocaleDTO;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.api.opt.server.RemoteLocaleOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.RemotePatternOpt;

public enum DataSourceEnum {
    Bundle {
        @Override
        public ProductOpt createProductOpt(BaseDTO dto) {
            return new LocalProductOpt(dto);
        }

    	@Override
        public MessageOpt createMessageOpt(MessagesDTO dto) {
            return new LocalMessagesOpt(dto);
        }

        @Override
        public KeyBasedOpt createKeyBasedOpt(MessagesDTO dto) {
            return new LocalKeyBasedOpt(dto);
        }

        @Override
        public PatternOpt createPatternOpt() {
            return new LocalPatternOpt();
        }

		@Override
		public LocaleOpt createLocaleOpt(LocaleDTO dto) {
			return new LocalLocaleOpt(dto);
		}
    }, 
    VIP {
        @Override
        public ProductOpt createProductOpt(BaseDTO dto) {
            return new RemoteProductOpt(dto);
        }

    	@Override
        public MessageOpt createMessageOpt(MessagesDTO dto) {
            return new ComponentBasedOpt(dto);
        }

        @Override
        public KeyBasedOpt createKeyBasedOpt(MessagesDTO dto) {
            return new StringBasedOpt(dto);
        }

        @Override
        public PatternOpt createPatternOpt() {
            return new RemotePatternOpt();
        }

		@Override
		public LocaleOpt createLocaleOpt(LocaleDTO dto) {
			return new RemoteLocaleOpt(dto);
		}
    };
    public abstract ProductOpt createProductOpt(BaseDTO dto);
    public abstract MessageOpt createMessageOpt(MessagesDTO dto);
    public abstract KeyBasedOpt createKeyBasedOpt(MessagesDTO dto);
    public abstract PatternOpt createPatternOpt();
    public abstract LocaleOpt createLocaleOpt(LocaleDTO dto);
}
