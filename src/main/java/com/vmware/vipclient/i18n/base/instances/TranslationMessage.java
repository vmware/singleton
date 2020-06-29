/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.CacheService;
import com.vmware.vipclient.i18n.messages.service.ComponentService;
import com.vmware.vipclient.i18n.messages.service.ComponentsService;
import com.vmware.vipclient.i18n.messages.service.StringService;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.JSONUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;

/**
 * This class provide some APIs to get translation from VIP service in
 * string-based, component-based level.
 */
public class TranslationMessage implements Message {
    Logger logger = LoggerFactory.getLogger(TranslationMessage.class);

    public VIPCfg getCfg() {
        return this.cfg;
    }

    public void setCfg(final VIPCfg cfg) {
        this.cfg = cfg;
    }

    private VIPCfg cfg;

    public TranslationMessage() {
        super();
    }
    
    /**
     * Retrieves the localized message from the cache
     * 
     * @param component The Singleton component in which the message belongs
     * @param key The key that represents the message
     * @param locale The locale in which the message is requested to be localized
     * @return One of the items in the following priority-ordered list: 
     * <ul>
     * 		<li>The message in the requested locale</li> 
     * 		<li>The message in the default locale</li>
     * 		<li>null</li>
     * </ul>
     */
    private String getMessageFromCache(String component, String key, Locale locale) {
    	MessagesDTO dto = new MessagesDTO(component, key, null, locale.toLanguageTag(), this.cfg);
    	ComponentService cs = new ComponentService(dto);
    	return cs.getMessages().getCachedData().get(key);
    }
    
    /**
     * Retrieves the localized message
     * 
     * @param locale The locale in which the message is requested to be localized
     * @param component The Singleton component in which the message belongs
     * @param key The key that represents the message
     * @param args Values to replace placeholders in the message with
     * @return One of the items in the following priority-ordered list:. 
     * @throws VIPJavaClientException If none from the list below is available 
     * <ul>
     * 		<li>The source message, if source message hasn't been collected and translated</li>
     * 		<li>The message in the requested locale</li>
     * 		<li>The message in the next available fallback locale</li>
     * 		<li>The source message</li>
     * </ul>
     */
    public String getMessage(final Locale locale, final String component, final String key, final Object... args) {
    	// Use source message if the message hasn't been collected/translated 
    	String source = getMessageFromCache(component, key, Locale.forLanguageTag(ConstantsKeys.SOURCE));
    	String collectedSourceMsg = getMessageFromCache(component, key, LocaleUtility.getSourceLocale());
    	if (source!=null && !source.isEmpty() && !source.equals(collectedSourceMsg)) {
			return FormatUtils.format(source, LocaleUtility.getSourceLocale(), args);
		}
    	
    	String message = FormatUtils.format(getMessages(locale, component).get(key), locale, args);
    	if (message == null || message.isEmpty()) {
    		throw new VIPJavaClientException(FormatUtils.format(ConstantsMsg.GET_MESSAGE_FAILED, key, component, locale));
    	}
    	
    	return message;
    	
    }
    
    /**
     * get a translation under the component of the configured product
     *
     * @param locale
     *            an object used to get the source's translation
     * @param component
     *            defined on VIP service, it will be created automatically if
     *            not exist
     * @param key
     *            identify the source
     * @param source
     *            it's English source which will be return if no translation
     *            available
     * @param comment
     *            used to describe the source to help understand the source for
     *            the translators.
     * @param args
     *            used to format the message with placeholder, it's not required
     *            if the message doesn't contain any placeholder
     * @return string
     * @deprecated Replaced by {@link #getMessage(Locale, String, String, Object...)} 
     * 		which fetches source messages from messages_source.json of the component.
     * 		This method only supports English as both the default and the source locale.
     */ 
     public String getString(final Locale locale, final String component,
            final String key, final String source, final String comment, final Object... args) {
        this.logger.trace("Start to execute TranslationMessage.getString");
        if (key == null || key.equalsIgnoreCase(""))
            return ""; 
        String translation = "";
        StringService s = new StringService();
        
        if (!LocaleUtility.isDefaultLocale(locale)) {  
        	MessagesDTO dto = new MessagesDTO(component, key, source, locale.toLanguageTag(), this.cfg);
            translation = s.getString(dto);
            // if the source is not equal to remote's source version, return the
            // source as latest, not return the old translation
            if (source != null && !"".equals(source) && !VIPCfg.getInstance().isPseudo()) {
                MessagesDTO remoteEnDTO = new MessagesDTO(component, key, source, 
                		LocaleUtility.getDefaultLocale().toLanguageTag(), this.cfg);
                String remoteEnMsg = s.getString(remoteEnDTO);
                if (!source.equals(remoteEnMsg)) {
                    translation = source;
                }
            }

            if (translation == null || translation.isEmpty()) {
                translation = source;
            }
        } else {
            translation = source;
        }

        if (VIPCfg.getInstance().isCollectSource() || VIPCfg.getInstance().isMachineTranslation()) {
        	MessagesDTO latestSourceDTO = new MessagesDTO(component, key, source, 
        			ConstantsKeys.LATEST, this.cfg);
            String latestStr = s.getString(latestSourceDTO);
            if (source != null && !source.equals(latestStr)) {
            	MessagesDTO dto2 = new MessagesDTO(component, key, source, 
            			locale.toLanguageTag(), this.cfg);
                String mt = s.postString(dto2);
                if (VIPCfg.getInstance().isMachineTranslation() && !"".equalsIgnoreCase(mt)) {
                    translation = mt;
                }
            }
        }

        if (!VIPCfg.getInstance().isMachineTranslation() && VIPCfg.getInstance().isPseudo() &&
                null != translation && translation.equals(source)) {
            // if source isn't collected by server, add PSEUDOCHAR2
            translation = ConstantsKeys.PSEUDOCHAR2 + translation + ConstantsKeys.PSEUDOCHAR2;
        }

        if (args != null && args.length > 0) {
            if ((null != translation && translation.equals(source)) || VIPCfg.getInstance().isPseudo()) {
                translation = FormatUtils.formatMsg(translation,
                        LocaleUtility.getDefaultLocale(), args);
            } else {
                translation = FormatUtils.formatMsg(translation, locale, args);
            }
        }
        return translation;
    }

    /**
     * post a set of sources to remote VIP server which is configured
     *
     * @param locale
     *            currently no matter which locale it is, all sources will be
     *            considered as English
     * @param component
     *            the component name used to categorize the sources and
     *            auto-created first time
     * @param sources
     *            the JSONObject should contain three attributes(key, source,
     *            commentForSource).
     * @return a boolean to indicate the post status
     * @deprecated Collection of source message is not supported at runtime.
     */
    public boolean postStrings(final Locale locale, final String component,
            final List<JSONObject> sources) {
        this.logger.trace("Start to execute TranslationMessage.postStrings");
        if (sources == null || sources.isEmpty())
            return false;
        MessagesDTO dto = new MessagesDTO();
        dto.setLocale(locale.toLanguageTag());
        dto.setComponent(component);
        if (this.cfg != null) {
            dto.setProductID(this.cfg.getProductName());
            dto.setVersion(this.cfg.getVersion());
        }
        List<JSONObject> sourcesList = new ArrayList<>();
        sourcesList.addAll(sources);
        List<JSONObject> removedList = new ArrayList<>();
        for (JSONObject jo : sourcesList) {
            String key = (String) jo.get(ConstantsKeys.KEY);
            String source = (String) jo.get(ConstantsKeys.SOURCE);
            dto.setKey(key);
            dto.setSource(source);
            dto.setLocale(ConstantsKeys.LATEST);
            String enStr = new StringService().getString(dto);
            if (source != null && source.equals(enStr)) {
                removedList.add(jo);
            }
        }
        sourcesList.removeAll(removedList);
        if (sourcesList.isEmpty())
            return true;
        else {
            dto.setLocale(locale.toLanguageTag());
            return new StringService().postStrings(sourcesList, dto);
        }
    }

    /**
     * post a source to remote VIP server
     *
     * @param locale
     *            an object used to get the source's translation
     * @param component
     *            defined on VIP service, it will be created automatically if
     *            not exist
     * @param key
     *            identify the source
     * @param source
     *            it's English source which will be return if no translation
     *            available
     * @param comment
     *            used to describe the source to help understand the source for
     *            the translators.
     * @return a boolean to indicate post succeeded or failed
     * @deprecated Collection of source message is not supported at runtime.
     */
    public boolean postString(final Locale locale, final String component,
            final String key, final String source, final String comment) {
        this.logger.trace("Start to execute TranslationMessage.postString");
        MessagesDTO dto = new MessagesDTO();
        dto.setComponent(component);
        dto.setComment(comment);
        dto.setKey(key);
        dto.setSource(source);
        StringService s = new StringService();
        dto.setLocale(ConstantsKeys.LATEST);
        if (this.cfg != null) {
            dto.setProductID(this.cfg.getProductName());
            dto.setVersion(this.cfg.getVersion());
        }
        String enStr = s.getString(dto);
        if (source != null && !"".equalsIgnoreCase(source) && !source.equals(enStr)) {
            dto.setLocale(locale.toLanguageTag());
            String recievedStr = s.postString(dto);
            return !JSONUtils.isEmpty(recievedStr);
        } else
            return true;
    }

    /**
     * get one component's translations from VIP of the configured product
     *
     * @param locale
     *            a language tag to get the translations
     * @param component
     *            defined on VIP service, it will be created automatically if
     *            not exist
     * @return a map contains all translations of the component mapped by the
     *         source's key
     * @deprecated Replaced by {@link #getMessages(Locale, String)} 
     */
    @Deprecated public Map<String, String> getStrings(final Locale locale,
            final String component) {
        this.logger.trace("Start to execute TranslationMessage.getStrings");
        MessagesDTO dto = new MessagesDTO();
        dto.setLocale(locale.toLanguageTag());
        dto.setComponent(component);
        if (this.cfg != null) {
            dto.setProductID(this.cfg.getProductName());
            dto.setVersion(this.cfg.getVersion());
        }
        ComponentService cs = new ComponentService(dto);
        return cs.getMessages().getCachedData();
    }
    
     /**
     * Retrieves the set of localized messages from the cache. It applies locale fallback mechanism in case of failure.
     * 
     * @param component The Singleton component
     * @param locale The locale in which the messages are requested to be localized
     * @return One of the items in the following priority-ordered list: 
     * <ul>
     * 		<li>The messages in the requested locale</li> 
     * 		<li>The messages in the default locale</li>
     * 		<li>The source messages</li>
     * </ul>
     */
    public Map<String, String> getMessages(final Locale locale, final String component) {
        MessagesDTO dto = new MessagesDTO(component, null, null, locale.toLanguageTag(), this.cfg);
        MessageCacheItem cacheItem = new ComponentService(dto).getMessages();

        // While failed to get MessageCacheItem, use MessageCacheItem of the next fallback locale.
        Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
        while (cacheItem.getCachedData().isEmpty() && fallbackLocalesIter.hasNext()) {
            Locale fallback = fallbackLocalesIter.next();
            MessagesDTO fallbackLocaleDTO = new MessagesDTO(dto.getComponent(), dto.getKey(), dto.getSource(), fallback.toLanguageTag(), null);
            cacheItem = new ComponentService(fallbackLocaleDTO).getMessages();

            // Cache a reference to the MessageCacheItem of the fallback locale
            if (!cacheItem.getCachedData().isEmpty()) {
                CacheService cacheService = new CacheService(dto);
                cacheService.addCacheOfComponent(cacheItem);
            }
        }

        return cacheItem.getCachedData();
    }

    /**
     * get multiple components' translations from VIP server
     *
     * @param locales
     *            locales to get the translations of them
     * @param components
     *            names of the components to get translation
     * @return
     *         a map contains all translations of the components of specified locales.
     *         Key is loale; value is also a map whose key is component and value is the messages belong to
     *         this component.
     */
    public Map<Locale, Map<String, Map<String, String>>> getStrings(final Set<Locale> locales,
            final Set<String> components) {
        this.logger.trace("Start to execute TranslationMessage.getStrings of multiple components of multiple locales.");

        Map<Locale, Map<String, Map<String, String>>> retMap = new HashMap<>();
        if (null == locales || locales.isEmpty() || null == components || components.isEmpty()) {
            this.logger.error(ConstantsMsg.WRONG_PARAMETER + "locales: {}, components: {}.", locales, components);
            return retMap;
        }

        try {
            VIPCfg config = this.cfg;
            if (null == config) {
                config = VIPCfg.getInstance();
            }
            final ComponentsService cs = new ComponentsService(config);
            retMap = cs.getTranslation(components, locales);
        } catch (final Exception e) {
            this.logger.error(ConstantsMsg.EXCEPTION_OCCUR, e);
        }

        return retMap;
    }

    /**
     * get one translation of the configured product from VIP, if message not
     * found will get the English message from specified bundle.
     * get a translation under the component of the configured product
     *
     * @param component
     *            defined on VIP service, it will be created automatically if
     *            not exist
     * @param bundle
     *            properties file name, normally it should be put under the root
     *            'src' path
     * @param locale
     *            an object used to get the source's translation
     * @param key
     *            identify the source
     * @param args
     *            used to format the message with placeholder, it's not required
     *            if the message doesn't contain any placeholder
     * @return a message of translation, if the translation is not found from
     *         VIP service, it will return the value defined in the bundle
     *         searching by the key
     * @deprecated Replaced by {@link #getMessage(Locale, String, String, Object...)} 
     * 		which fetches source messages from messages_source.json of the component.
     * 		This method only supports English as both the default and the source locale.
     */
     public String getString2(final String component,
            final String bundle, final Locale locale, final String key, final Object... args) {
        this.logger.trace("Start to execute TranslationMessage.getString2");
        if (key == null || key.equalsIgnoreCase(""))
            return "";
        String message = "";
        String source;
        try {
            ResourceBundle rb = ResourceBundle.getBundle(bundle,
                    LocaleUtility.getDefaultLocale());
            source = rb.getString(key);
        } catch (Exception e) {
            this.logger.error(e.getMessage());
            source = key;
        }
        // get translation from VIP service
        message = this.getString(locale, component, key, source, "", args);
        return message;
    }

    /**
     * check if the translations of specified component is available
     *
     * @param component
     * @param locale
     * @return
     */
    public boolean isAvailable(final String component, final Locale locale) {
        this.logger.trace("Start to execute component-based TranslationMessage.isAvailable");
        boolean available = false;
        if (!LocaleUtility.isDefaultLocale(locale)) {
            MessagesDTO dto = new MessagesDTO();
            dto.setComponent(component);
            dto.setLocale(locale.toLanguageTag());
            if (this.cfg != null) {
                dto.setProductID(this.cfg.getProductName());
                dto.setVersion(this.cfg.getVersion());
            }
            ComponentService cs = new ComponentService(dto);
            available = cs.isComponentAvailable();
        }
        return available;
    }

    /**
     * check if one translation of specified key is available
     *
     * @param component
     * @param locale
     * @return
     */
    public boolean isAvailable(final String component, final String key, final Locale locale) {
        this.logger.trace("Start to execute string-based TranslationMessage.isAvailable");
        boolean available = false;
        if (!LocaleUtility.isDefaultLocale(locale)) {
            MessagesDTO dto = new MessagesDTO();
            dto.setComponent(component);
            dto.setKey(key);
            dto.setLocale(locale.toLanguageTag());
            if (this.cfg != null) {
                dto.setProductID(this.cfg.getProductName());
                dto.setVersion(this.cfg.getVersion());
            }
            available = new StringService().isStringAvailable(dto);
        }
        return available;
    }
}
