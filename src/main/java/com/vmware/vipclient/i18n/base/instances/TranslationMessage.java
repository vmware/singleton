/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
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

    public TranslationMessage() {
        super();
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
     */
	public String getString(final Locale locale, final String component,
			final String key, final String source, final String comment, final Object... args) {
		logger.debug("Start to execute TranslationMessage.getString");
		if (key == null || key.equalsIgnoreCase("")) {
			return "";
		}
		MessagesDTO dto = new MessagesDTO();
		dto.setComponent(component);
		dto.setComment(comment);
		dto.setKey(key);
		dto.setSource(source);
		dto.setLocale(locale.toLanguageTag());
		StringService s = new StringService(dto);
		String translation = "";
		if (!LocaleUtility.isDefaultLocale(locale)) {
			translation = s.getString();
			// if the source is not equal to remote's source version, return the
			// source as latest, not return the old translation
			if (source != null && !"".equals(source) && !VIPCfg.getInstance().isPseudo()) {
				dto.setLocale(LocaleUtility.defaultLocale.toLanguageTag());
				String remoteEnMsg = s.getString();
				if(!source.equals(remoteEnMsg)) {
					translation = source;
				}
			}

			if("".equals(translation)) {
				translation = source;
			}
		} else {
			translation = source;
		}

		if (VIPCfg.getInstance().isCollectSource() || VIPCfg.getInstance().isMachineTranslation()){
			dto.setLocale(ConstantsKeys.LATEST);
			String latestStr = s.getString();
			if(source != null && !source.equals(latestStr)) {
				dto.setLocale(locale.toLanguageTag());
				String mt = s.postString();
				if(VIPCfg.getInstance().isMachineTranslation() && !"".equalsIgnoreCase(mt)) {
					translation = mt;
				}
			}
		}

		if(!VIPCfg.getInstance().isMachineTranslation() && VIPCfg.getInstance().isPseudo() &&
				null!=translation && translation.equals(source) ) {
			//if source isn't collected by server, add PSEUDOCHAR2
			translation = ConstantsKeys.PSEUDOCHAR2 + translation + ConstantsKeys.PSEUDOCHAR2;
		}

		if (args != null && args.length > 0) {
			if ( (null != translation && translation.equals(source)) || VIPCfg.getInstance().isPseudo()) {
				translation = FormatUtils.format(translation,
						LocaleUtility.defaultLocale, args);
			} else {
				translation = FormatUtils.format(translation, locale, args);
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
	 */
	public boolean postStrings(final Locale locale, final String component,
			final List<JSONObject> sources) {
		logger.info("Start to execute TranslationMessage.postStrings");
		if (sources == null || sources.isEmpty()) {
			return false;
		}
		MessagesDTO dto = new MessagesDTO();
		dto.setLocale(locale.toLanguageTag());
		dto.setComponent(component);
		List<JSONObject> sourcesList = new ArrayList<>();
		sourcesList.addAll(sources);
		List<JSONObject> removedList = new ArrayList<>();
		for(JSONObject jo : sourcesList) {
			String key = (String)jo.get(ConstantsKeys.KEY);
			String source = (String)jo.get(ConstantsKeys.SOURCE);
			dto.setKey(key);
			dto.setSource(source);
			dto.setLocale(ConstantsKeys.LATEST);
			String enStr = new StringService(dto).getString();
			if(source != null && source.equals(enStr)) {
				removedList.add(jo);
			}
		}
		sourcesList.removeAll(removedList);
		if(sourcesList.isEmpty()) {
			return true;
		} else {
			dto.setLocale(locale.toLanguageTag());
			StringService s = new StringService(dto);
			return s.postStrings(sourcesList);
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
	 */
	public boolean postString(final Locale locale, final String component,
			final String key, final String source, final String comment) {
		logger.info("Start to execute TranslationMessage.postString");
		MessagesDTO dto = new MessagesDTO();
		dto.setComponent(component);
		dto.setComment(comment);
		dto.setKey(key);
		dto.setSource(source);
		StringService s = new StringService(dto);
		dto.setLocale(ConstantsKeys.LATEST);
		String enStr = s.getString();
		if(source != null && !"".equalsIgnoreCase(source) && !source.equals(enStr)) {
			dto.setLocale(locale.toLanguageTag());
			String recievedStr = s.postString();
			return !JSONUtils.isEmpty(recievedStr);			
		}
		else return true;
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
	 */
	public Map<String, String> getStrings(final Locale locale,
			final String component) {
		logger.info("Start to execute TranslationMessage.getStrings");
		MessagesDTO dto = new MessagesDTO();
		dto.setLocale(locale.toLanguageTag());
		dto.setComponent(component);
		ComponentService cs = new ComponentService(dto);
		return cs.getComponentTranslation();
	}


    /**
     * get multiple component's translations from VIP server
     *
     * @param locales
     *            language tags to get the translations of them
     * @param components
     *            names of the components
     * @return a map contains all translations of the components of specified locales
     */
    public Map<Locale, Map<String, Map<String, String>>> getStrings(final Set<Locale> locales,
            final Set<String> components) {
        logger.info("Start to execute TranslationMessage.getStrings of multiple components of multiple locales.");

        Map<Locale, Map<String, Map<String, String>>> retMap = new HashMap<>();
        if (null == locales || locales.isEmpty() || null == components || components.isEmpty()) {
            logger.error(ConstantsMsg.WRONG_PARAMETER + "locales: {}, components: {}.", locales, components);
            return retMap;
        }


        try {
            final ComponentsService cs = new ComponentsService(components, locales);
            retMap = cs.getTranslation();
        } catch (final Exception e) {
            logger.error(ConstantsMsg.EXCEPTION_OCCUR, e);
        }

        return retMap;
    }

	/**
	 * get one translation of the configured product from VIP, if message not
	 * found will get the English message from specified bundle.
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
	 */
	public String getString2(final String component,
			final String bundle, final Locale locale, final String key, final Object... args) {
		logger.debug("Start to execute TranslationMessage.getString2");
		if (key == null || key.equalsIgnoreCase("")) {
			return "";
		}
		String message = "";
		String source;
		try {
			ResourceBundle rb = ResourceBundle.getBundle(bundle,
					LocaleUtility.defaultLocale);
			source = rb.getString(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			source = key;
		}
		// get translation from VIP service
		message = getString(locale, component, key, source, "", args);
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
		logger.info("Start to execute component-based TranslationMessage.isAvailable");
		boolean available = false;
		if (!LocaleUtility.isDefaultLocale(locale)) {
			MessagesDTO dto = new MessagesDTO();
			dto.setComponent(component);
			dto.setLocale(locale.toLanguageTag());
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
		logger.info("Start to execute string-based TranslationMessage.isAvailable");
		boolean available = false;
		if (!LocaleUtility.isDefaultLocale(locale)) {
			MessagesDTO dto = new MessagesDTO();
			dto.setComponent(component);
			dto.setKey(key);
			dto.setLocale(locale.toLanguageTag());
			StringService s = new StringService(dto);
			available = s.isStringAvailable();
		}
		return available;
	}
}
