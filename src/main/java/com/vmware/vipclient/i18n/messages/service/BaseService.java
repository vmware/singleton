package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Set;

public class BaseService {
    Logger logger = LoggerFactory.getLogger(BaseService.class);

    public void doLocaleMatching(MessagesDTO dto) {
        dto.setLocale(LocaleUtility.fmtToMappedLocale(dto.getLocale()).toLanguageTag());

        //Match against list of supported locales that is already in the cache
        Set<Locale> supportedLocales = LocaleUtility.langTagtoLocaleSet(new ProductService(dto).getCachedSupportedLocales());
        Locale matchedLocale = LocaleUtility.pickupLocaleFromList(supportedLocales, Locale.forLanguageTag(dto.getLocale()));
        if (matchedLocale != null) { // Requested locale matches a supported locale (eg. requested locale "fr_CA matches supported locale "fr")
            dto.setLocale(matchedLocale.toLanguageTag());
        }
    }

    /**
     * @return 'true' for either of the following cases. Otherwise, false (locale not supported in data source).
     * <ul>
     * 	<li>the dataSource's set of supported locales is not in cache. If the list is not in cache, it should not block refreshCacheItem</li>
     * 	<li>the requested locale is found in the data source's cached set of supported locales.</li>
     * </ul>
     */
    public boolean proceed(MessagesDTO dto, DataSourceEnum dataSource) {
        ProductService ps = new ProductService(dto);
        Set<String> supportedLocales = ps.getCachedSupportedLocales(dataSource);
        logger.debug("supported languages: [{}]", supportedLocales);

        /*
         * Do not block refreshCacheItem if set of supported locales is not in cache (i.e. supportedLocales.isEmpty()).
         * This happens either when cache is not initialized, OR previous attempts to fetch the set had failed.
         */
        return (supportedLocales.isEmpty() || supportedLocales.contains(dto.getLocale()) || ConstantsKeys.SOURCE.equals(dto.getLocale())|| VIPCfg.getInstance().isPseudo());
    }
}
