/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.dto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.vmware.vipclient.i18n.base.DataSourceEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.LocaleUtility;

/**
 * DTO objects for cache data encapsulation
 *
 */
public class MessagesDTO extends BaseDTO {
    Logger         logger = LoggerFactory.getLogger(MessagesDTO.class);

    private String comment;
    private String source;
    private String key;

    private String component;
    private String locale;

    public MessagesDTO() {
        super();
    }

    public MessagesDTO(BaseDTO dto) {
        super(dto.getProductID(), dto.getVersion());
    }

    public MessagesDTO(String component, String localeLanguageTag, String productName, String version) {
        this.setComponent(component);
        this.setLocale(localeLanguageTag);
        this.setProductID(productName);
        this.setVersion(version);
    }

    public MessagesDTO(String productName, String version, String component, String localeLanguageTag, String key) {
        this.setProductID(productName);
        this.setVersion(version);
        this.setComponent(component);
        this.setLocale(localeLanguageTag);
        this.setKey(key);
    }

    public MessagesDTO(String component, String key, String source, String localeLanguageTag, VIPCfg cfg) {
    	this.setComponent(component);
    	
    	this.setKey(key);
    	this.setSource(source);
    	this.setLocale(localeLanguageTag);
    	if (cfg != null) {
    		this.setProductID(cfg.getProductName());
            this.setVersion(cfg.getVersion());
    	} else {
    		super.setProductID(VIPCfg.getInstance().getProductName());
            super.setVersion(VIPCfg.getInstance().getVersion());
    	}
    }

    /**
     * assembly the key of cache by productID, version, component and locale.
     * 
     * @return The key of cache.
     */
    public String getCompositStrAsCacheKey() {
        StringBuilder key = new StringBuilder(super.getProductID());
        key.append(ConstantsKeys.UNDERLINE);
        key.append(super.getVersion());
        key.append(ConstantsKeys.UNDERLINE);
        key.append(component);
        key.append(ConstantsKeys.UNDERLINE);
        key.append(VIPCfg.getInstance().isPseudo());
        key.append(ConstantsKeys.UNDERLINE_POUND);
        key.append(locale == null ? ConstantsKeys.EN
                : LocaleUtility
                        .fmtToMappedLocale(locale).toLanguageTag());
        return key.toString();
    }

    public String encryption(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }
        return re_md5;
    }

    public String getTransStatusAsCacheKey() {
        StringBuilder key = new StringBuilder(super.getProductID());
        key.append(ConstantsKeys.UNDERLINE);
        key.append(super.getVersion());
        key.append(ConstantsKeys.UNDERLINE);
        key.append(component == null ? ConstantsKeys.DEFAULT_COMPONENT
                : component);
        key.append(ConstantsKeys.UNDERLINE);
        key.append(ConstantsKeys.TRANSLATION_STATUS);
        key.append(ConstantsKeys.UNDERLINE);
        key.append(locale == null ? ConstantsKeys.EN
                : LocaleUtility
                        .fmtToMappedLocale(locale).toLanguageTag());
        return key.toString();
    }

    public String getLocalesCacheKey(DataSourceEnum dataSource) {
        StringBuilder key = new StringBuilder(super.getProductID());
        key.append(ConstantsKeys.UNDERLINE);
        key.append(super.getVersion());
        key.append(ConstantsKeys.UNDERLINE);
        key.append(ConstantsKeys.LOCALES);
        key.append(ConstantsKeys.UNDERLINE);
        key.append(dataSource.name());
        return key.toString();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = LocaleUtility.normalizeToLanguageTag(locale);
    }

}
