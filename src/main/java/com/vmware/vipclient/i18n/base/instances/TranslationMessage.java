/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.instances;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.ComponentService;
import com.vmware.vipclient.i18n.messages.service.ComponentsService;
import com.vmware.vipclient.i18n.messages.service.StringService;
import com.vmware.vipclient.i18n.util.*;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class provide some APIs to get translation from VIP service in
 * string-based, component-based level.
 * <pre>
 * <p>For string-based level APIs, some need the source string as parameter, some don't, instead the source string is put to the resource bundle.
 * Currently we support source string of the following form:
 *
 *      message = messageText (argument messageText)*
 *      argument = noneArg | simpleArg | complexArg
 *      complexArg = pluralArg
 *
 *      noneArg = '{' argNameOrNumber '}'
 *      simpleArg = '{' argNameOrNumber ',' argType [',' argStyle] '}'
 *      pluralArg = '{' argNameOrNumber ',' "plural" ',' pluralStyle '}'
 *
 *      pluralStyle: see PluralFormat
 *
 *      argNameOrNumber = argName | argNumber
 *      argName = [^[[:Pattern_Syntax:][:Pattern_White_Space:]]]+
 *      argNumber = '0' | ('1'..'9' ('0'..'9')*)
 *
 *      argType = "number" | "date"
 *      argStyle for "number" type = "currency" | "percent" (argStyle is optional, when it's not specified, the number will be formatted in decimal format)
 *      argStyle for "date" type = "shortDate" | "mediumDate" | "longDate" | "fullDate" | "shortTime" | "mediumTime" | "longTime" | "fullTime" | "short" | "medium" | "long" | "full"
 *      (The format results of "short" | "medium" | "long" | "full" style are the combination of date and time.)
 *      (When argStyle for "date" is not specified, the date will be formatted in "medium" style)
 *
 *      Below is an example for the format of source:
 *      "At {1,date,shortTime} on {1,date,longDate}, there was {2} on planet {0,number}."
 *</pre>
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
     * Retrieves the localized message
     * 
     * @param locale The locale in which the message is requested to be localized
     * @param component The Singleton component in which the message belongs
     * @param key The key that represents the message
     * @param args Values to replace placeholders in the message with
     * @return One of the items in the following priority-ordered list:
     * @throws VIPJavaClientException If none from the list below is available 
     * <ul>
     * 		<li>The source message, if source message hasn't been collected and translated</li>
     * 		<li>The message in the requested locale</li>
     * 		<li>The message in the next available fallback locale</li>
     * 		<li>The source message</li>
     * </ul>
     */
    public String getMessage(final Locale locale, final String component, final String key, final Object... args) {
    	return getMessageWithArgs(null, locale, component, key, args);
    }

    /**
     * Retrieves the localized message
     *
     * @param locale The locale in which the message is requested to be localized
     * @param component The Singleton component in which the message belongs
     * @param key The key that represents the message
     * @param args Named arguments to replace placeholders in the message with
     * @return One of the items in the following priority-ordered list:
     * @throws VIPJavaClientException If none from the list below is available
     * <ul>
     * 		<li>The source message, if source message hasn't been collected and translated</li>
     * 		<li>The message in the requested locale</li>
     * 		<li>The message in the next available fallback locale</li>
     * 		<li>The source message</li>
     * </ul>
     */
    public String getMessage(final Locale locale, final String component, final String key, final Map<String, Object> args) {
        return getMessageWithArgs(null, locale, component, key, args);
    }

    public String getMessage(String resourceBundle, final Locale locale, final String component, final String key, final Map<String, Object> args) {
        return getMessageWithArgs(resourceBundle, locale, component, key, args);
    }

    public String getMessage(String resourceBundle, final Locale locale, final String component, final String key, final Object... args) {
        return getMessageWithArgs(resourceBundle, locale, component, key, args);
    }

    private String getMessageWithArgs(String resourceBundle, Locale locale, final String component, final String key, final Object args) {
        Locale messageLocale = locale;
        if(messageLocale == null){
            messageLocale = LocaleUtility.getDefaultLocale();
            logger.warn(String.format(ConstantsMsg.LOCALE_IS_NULL, messageLocale.toLanguageTag()));
        }
        if(StringUtil.isEmpty(component)){
            throw new VIPJavaClientException(ConstantsMsg.COMPONENT_CANNOT_EMPTY);
        }

        // Use source message if the message hasn't been collected/translated
        String source = null;
        try {
            source = resourceBundle != null ? ResourceBundle.getBundle(resourceBundle).getString(key) :
                    getMessages(Locale.forLanguageTag(ConstantsKeys.SOURCE), component, false).getMessages().get(key);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        if (source!=null && !source.isEmpty()) {
            String collectedSource = getMessages(LocaleUtility.getSourceLocale(), component, false).getMessages().get(key);
            if (!source.equals(collectedSource)) {
                return FormatUtils.formatMsg(source, LocaleUtility.getSourceLocale(), args);
            }
        }

        ComponentService.TranslationsDTO msgsItemDTO = getMessages(messageLocale, component, true);
        String message = msgsItemDTO.getMessages().get(key);
        if (message == null || message.isEmpty()) {
            if (source != null)
                return source;
            throw new VIPJavaClientException(FormatUtils.format(ConstantsMsg.GET_MESSAGE_FAILED, key, component, locale));
        }

        return FormatUtils.formatMsg(message, Locale.forLanguageTag(msgsItemDTO.getLocale()), args);
    }

    /**
     * Get one version's key based message if there is multiple versions for one product, the message's arguments are put in a variable object
     *
     * @param locale
     * @param version
     * @param component
     * @param key
     * @param args
     * @return
     */
    public String getMultiVersionMessage(final Locale locale, final String version, final String component, final String key, final Object... args) {
        return getMultiVersionMessageWithArgs(null, locale, version, component, key, args);
    }

    /**
     * Get one version's key based message if there is multiple versions for one product, the message's arguments are put in a Map
     *
     * @param locale
     * @param version
     * @param component
     * @param key
     * @param args
     * @return
     */
    public String getMultiVersionMessage(final Locale locale, final String version, final String component, final String key, final Map<String, Object> args) {
        return getMultiVersionMessageWithArgs(null, locale, version, component, key, args);
    }

    private String getMultiVersionMessageWithArgs(String resourceBundle, Locale locale, final String version, final String component, final String key, final Object args) {
        Locale messageLocale = locale;
        if(messageLocale == null){
            messageLocale = LocaleUtility.getDefaultLocale();
            logger.warn(String.format(ConstantsMsg.LOCALE_IS_NULL, messageLocale.toLanguageTag()));
        }
        if(StringUtil.isEmpty(component)){
            throw new VIPJavaClientException(ConstantsMsg.COMPONENT_CANNOT_EMPTY);
        }

        // Use source message if the message hasn't been collected/translated
        String source = null;
        try {
            source = resourceBundle != null ? ResourceBundle.getBundle(resourceBundle).getString(key) :
                    getMultiVersionMessagesOfKey(Locale.forLanguageTag(ConstantsKeys.SOURCE), version, component, key,false).getMessages().get(version);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        if (source!=null && !source.isEmpty()) {
            String collectedSource = getMultiVersionMessagesOfKey(LocaleUtility.getSourceLocale(), version, component, key,false).getMessages().get(version);
            if (!source.equals(collectedSource)) {
                return FormatUtils.formatMsg(source, LocaleUtility.getSourceLocale(), args);
            }
        }

        ComponentService.TranslationsDTO msgsItemDTO = getMultiVersionMessagesOfKey(messageLocale, version, component, key,true);
        String message = msgsItemDTO.getMessages().get(version);
        if (message == null || message.isEmpty()) {
            if (source != null)
                return source;
            throw new VIPJavaClientException(FormatUtils.format(ConstantsMsg.GET_VERSION_KEY_MESSAGE_FAILED, key, version, component, locale));
        }

        return FormatUtils.formatMsg(message, Locale.forLanguageTag(msgsItemDTO.getLocale()), args);
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
     *            available. For the format of source, please refer to the class annotation.
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
        return getStringWithArgs(locale, component, key, source, comment, args);
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
     *            available. For the format of source, please refer to the class annotation.
     * @param comment
     *            used to describe the source to help understand the source for
     *            the translators.
     * @param args
     *            named arguments used to format the message with placeholder
     * @return string
     */
    public String getString(final Locale locale, final String component,
            final String key, final String source, final String comment,
            final Map<String, Object> args) {
        return getStringWithArgs(locale, component, key, source, comment, args);
    }

    private String getStringWithArgs(final Locale locale,
            final String component, final String key, final String source,
            final String comment, final Object args) {
        logger.trace("Start to execute TranslationMessage.getStringWithArgs");
        if (key == null || key.equalsIgnoreCase(""))
            return "";

        MessagesDTO dto = new MessagesDTO();
        dto.setComponent(component);
        dto.setComment(comment);
        dto.setKey(key);
        dto.setSource(source);
        dto.setLocale(locale.toLanguageTag());
        if (this.getCfg() != null) {
            dto.setProductID(getCfg().getProductName());
            dto.setVersion(getCfg().getVersion());
        }

        if (StringUtil.isEmpty(source)) {
            return getStringWithoutSource(dto, args);
        }

        return getStringWithSource(dto, args);
    }

    private String getStringWithSource(MessagesDTO dto, final Object args) {
        Locale locale = Locale.forLanguageTag(dto.getLocale());
        String source = dto.getSource();
        StringService s = new StringService();

        String translation = "";
        if (!LocaleUtility.isDefaultLocale(locale)) {
            translation = s.getString(dto);
            // if the source is not equal to remote's source version, return the
            // source as latest, not return the old translation
            if (source != null && !"".equals(source) && !this.getCfg().isPseudo()) {
                dto.setLocale(LocaleUtility.getDefaultLocale().toLanguageTag());
                String remoteEnMsg = s.getString(dto);
                if (!source.equals(remoteEnMsg)) {
                    translation = source;
                }
            }

            if (StringUtil.isEmpty(translation)) {
                translation = source;
            }
        } else {
            translation = source;
        }

        if (this.getCfg().isCollectSource() || this.getCfg().isMachineTranslation()) {
            dto.setLocale(ConstantsKeys.LATEST);
            String latestStr = s.getString(dto);
            if (source != null && !source.equals(latestStr)) {
                dto.setLocale(locale.toLanguageTag());
                String mt = s.postString(dto);
                if (this.getCfg().isMachineTranslation() && !"".equalsIgnoreCase(mt)) {
                    translation = mt;
                }
            }
        }

        if (!this.getCfg().isMachineTranslation() && this.getCfg().isPseudo()
                && source != null && source.equals(translation)) {
            // if source isn't collected by server, add PSEUDOCHAR2
            translation = ConstantsKeys.PSEUDOCHAR2 + translation + ConstantsKeys.PSEUDOCHAR2;
        }

        if (args != null) {
        	Locale formatLocale = locale;
        	if (source != null && source.equals(translation) || this.getCfg().isPseudo()) {
        		formatLocale = LocaleUtility.getDefaultLocale();
        	}

            translation = FormatUtils.formatMsg(translation, formatLocale, args);
        }

        return translation;
    }

    private String getStringWithoutSource(MessagesDTO dto, final Object args) {
        String translation = new StringService().getString(dto);
        if (StringUtil.isEmpty(translation)) {
            return "";
        }

        if (args != null) {
            Locale locale = Locale.forLanguageTag(dto.getLocale());
            translation = FormatUtils.formatMsg(translation, locale, args);
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
     *            commentForSource). For the format of source, please refer to the class annotation.
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
        if (this.getCfg() != null) {
            dto.setProductID(this.getCfg().getProductName());
            dto.setVersion(this.getCfg().getVersion());
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
     *            available. For the format of source, please refer to the class annotation.
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
        if (this.getCfg() != null) {
            dto.setProductID(this.getCfg().getProductName());
            dto.setVersion(this.getCfg().getVersion());
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
        if (this.getCfg() != null) {
            dto.setProductID(this.getCfg().getProductName());
            dto.setVersion(this.getCfg().getVersion());
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
        return getMessages(null, locale, component);
    }

    public Map<String, String> getMessages(String resourceBundle, final Locale locale, final String component) {
        Locale messageLocale = locale;
        if (messageLocale == null) {
            messageLocale = LocaleUtility.getDefaultLocale();
            logger.warn(String.format(ConstantsMsg.LOCALE_IS_NULL, messageLocale.toLanguageTag()));
        }
        if (StringUtil.isEmpty(component)) {
            throw new VIPJavaClientException(ConstantsMsg.COMPONENT_CANNOT_EMPTY);
        }

        Map<String, String> messages = new LinkedHashMap<>();

        Map<String,String> sources = resourceBundle != null ? getSourcesFromBundle(resourceBundle):
                    getMessages(Locale.forLanguageTag(ConstantsKeys.SOURCE), component, false).getMessages();

        if(!sources.isEmpty()) {
            Map<String, String> collectedSources = getMessages(LocaleUtility.getSourceLocale(), component, false).getMessages();
            if(!collectedSources.isEmpty()){
                Map<String, String> translations = getMessages(messageLocale, component, true).getMessages();
                for (String key : sources.keySet()) {
                    String source = sources.get(key);
                    String collectedSource = collectedSources.get(key);
                    if (!source.equals(collectedSource)) {
                        messages.put(key, source);
                    } else {
                        messages.put(key, translations.get(key));
                    }
                }
            }else{
                messages = sources;
            }
        }else{
            Map<String, String> translations = getMessages(messageLocale, component, true).getMessages();
            messages = translations;
        }
        return messages;
    }

    private Map<String, String> getSourcesFromBundle(String resourceBundle){
        Map<String, String> sources = new LinkedHashMap<>();
        ResourceBundle bundle = ResourceBundle.getBundle(resourceBundle);
        for(String key: bundle.keySet()){
            sources.put(key, bundle.getString(key));
        }
        return sources;
    }

    private ComponentService.TranslationsDTO getMessages(final Locale locale, final String component, boolean useLocaleFallback) {
        MessagesDTO dto = new MessagesDTO(component, null, null, locale.toLanguageTag(), this.cfg);
        if (useLocaleFallback)
            return new ComponentService(dto).getTranslations();
        else
            return new ComponentService(dto).getTranslations(null);
    }

    private ComponentService.TranslationsDTO getMultiVersionMessagesOfKey(final Locale locale, final String version, final String component, final String key, boolean useLocaleFallback) {
        MessagesDTO dto = new MessagesDTO(component, key, null, locale.toLanguageTag(), this.cfg);
        StringService stringService = new StringService(dto);
        if (useLocaleFallback) {
            Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
            return stringService.getMultiVersionKeyCacheItem(version, fallbackLocalesIter);
        }else {
            return stringService.getMultiVersionKeyCacheItem(version, null);
        }
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
            final ComponentsService cs = new ComponentsService(this.getCfg());
            retMap = cs.getTranslation(components, locales);
        } catch (final Exception e) {
            this.logger.error(ConstantsMsg.EXCEPTION_OCCUR, e);
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
     * @deprecated Replaced by {@link #getMessage(Locale, String, String, Object...)} 
     * 		which fetches source messages from messages_source.json of the component.
     * 		This method only supports English as both the default and the source locale.
     */
    public String getString2(final String component, final String bundle, final Locale locale, final String key,
            final Object... args) {
        return getString2WithArgs(component, bundle, locale, key, args);
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
     *            named arguments used to format the message with placeholder
     * @return a message of translation, if the translation is not found from
     *         VIP service, it will return the value defined in the bundle
     *         searching by the key
     */
    public String getString2(final String component, final String bundle,
            final Locale locale, final String key, final Map<String, Object> args) {
        return getString2WithArgs(component, bundle, locale, key, args);
    }

    private String getString2WithArgs(final String component, final String bundle,
            final Locale locale, final String key, Object args) {
        logger.trace("Start to execute TranslationMessage.getString2WithArgs");
        if (key == null || key.equalsIgnoreCase(""))
            return "";

        String source = null;
        try {
            ResourceBundle rb = ResourceBundle.getBundle(bundle, LocaleUtility.getDefaultLocale());
            source = rb.getString(key);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        // get translation from VIP service
        String msg = getStringWithArgs(locale, component, key, source, "", args);

        // return translation -> source -> key
        if (StringUtil.isEmpty(msg))
            msg = StringUtil.isEmpty(source) ? key : source;

        return msg;
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
            if (this.getCfg() != null) {
                dto.setProductID(this.getCfg().getProductName());
                dto.setVersion(this.getCfg().getVersion());
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
            if (this.getCfg() != null) {
                dto.setProductID(this.getCfg().getProductName());
                dto.setVersion(this.getCfg().getVersion());
            }
            available = new StringService().isStringAvailable(dto);
        }
        return available;
    }
}
