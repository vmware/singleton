/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.url;

import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.l2.common.PatternCategory;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 * Encapsulates some methods related to vIP Server.
 *
 */
public class V2URL {
    private static Logger logger = LoggerFactory.getLogger(V2URL.class);

    private V2URL() {

    }

    /*
     * get component list URL
     */
    public static String getComponentListURL(final BaseDTO dto, final String baseURL) {
        StringBuilder url = new StringBuilder(baseURL);
        String gurl = APIV2.PRODUCT_COMPONENT_LIST_GET
                .replace("{" + APIParamName.PRODUCT_NAME + "}", dto.getProductID())
                .replace("{" + APIParamName.VERSION2 + "}", dto.getVersion());
        url.append(gurl);
        return url.toString();
    }

    /*
     * get locale list URL
     */
    public static String getSupportedLocaleListURL(final BaseDTO dto, final String baseURL) {
        StringBuilder url = new StringBuilder(baseURL);
        String gurl = APIV2.PRODUCT_LOCALE_LIST_GET.replace("{" + APIParamName.PRODUCT_NAME + "}", dto.getProductID())
                .replace("{" + APIParamName.VERSION2 + "}", dto.getVersion());
        url.append(gurl);
        return url.toString();
    }

    /**
     * assembly the request URL for component Translation.
     *
     * @param params
     *            The parameter of the request.
     * @param baseURL
     *            The root path of the URL.
     * @return
     */
    public static String getComponentTranslationURL(final MessagesDTO dto, final String baseURL) {
        StringBuilder url = new StringBuilder(baseURL);
        String gurl = APIV2.COMPONENT_TRANSLATION_GET.replace("{" + APIParamName.PRODUCT_NAME + "}", dto.getProductID())
                .replace("{" + APIParamName.VERSION2 + "}", dto.getVersion())
                .replace("{" + APIParamName.COMPONENT + "}", dto.getComponent())
                .replace("{" + APIParamName.LOCALE + "}", dto.getLocale());
        url.append(gurl);

        if (VIPCfg.getInstance().isPseudo()) {
            URLUtils.appendParamToURL(url, ConstantsKeys.PSEUDO, Boolean.toString(VIPCfg.getInstance().isPseudo()));
        } else {
            URLUtils.appendParamToURL(url, ConstantsKeys.PSEUDO, Boolean.FALSE.toString());
        }

        if (VIPCfg.getInstance().isMachineTranslation()) {
            URLUtils.appendParamToURL(url, ConstantsKeys.MACHINE_TRANSLATION,
                    Boolean.toString(VIPCfg.getInstance().isMachineTranslation()));
        }
        return url.toString();
    }

    /**
     * assembly the request URL for multiple components Translation.
     *
     * @param components
     *
     * @param locales
     *
     * @param baseURL
     *            The root path of the URL.
     * @return
     */
    public static String getComponentsTranslationURL(final String baseURL, final VIPCfg cfg) {
        final String url_path = APIV2.PRODUCT_TRANSLATION_GET
                .replace("{" + APIParamName.PRODUCT_NAME + "}", cfg.getProductName())
                .replace("{" + APIParamName.VERSION2 + "}", cfg.getVersion());
        StringBuilder url = new StringBuilder(baseURL).append(url_path);
        if (VIPCfg.getInstance().isPseudo()) {
            URLUtils.appendParamToURL(url, ConstantsKeys.PSEUDO, Boolean.toString(true));
        }

        return url.toString();
    }

    /**
     * assembly the request URL for key Translation.
     *
     * @param params
     *            The parameter of the request.
     * @param baseURL
     *            The root path of the URL.
     * @return
     */
    public static String getKeyTranslationURL(final MessagesDTO dto, final String baseURL) {
        StringBuilder url = new StringBuilder(baseURL);
        String url2 = "";
        try {
            url2 = APIV2.KEY_TRANSLATION_POST.replace("{" + APIParamName.PRODUCT_NAME + "}", dto.getProductID())
                    .replace("{" + APIParamName.VERSION2 + "}", dto.getVersion())
                    .replace("{" + APIParamName.COMPONENT + "}", dto.getComponent())
                    .replace("{" + APIParamName.LOCALE + "}", dto.getLocale())
                    .replace("{" + APIParamName.KEY2 + "}", URLEncoder.encode(dto.getKey(), ConstantsKeys.UTF8));
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage());
        }
        url.append(url2);
        if (VIPCfg.getInstance().isPseudo()) {
            URLUtils.appendParamToURL(url, ConstantsKeys.PSEUDO, Boolean.toString(VIPCfg.getInstance().isPseudo()));
        }

        if (VIPCfg.getInstance().isCollectSource()) {
            URLUtils.appendParamToURL(url, ConstantsKeys.COLLECT_SOURCE,
                    Boolean.toString(VIPCfg.getInstance().isCollectSource()));
        }

        if (VIPCfg.getInstance().isMachineTranslation()) {
            URLUtils.appendParamToURL(url, ConstantsKeys.MACHINE_TRANSLATION,
                    Boolean.toString(VIPCfg.getInstance().isMachineTranslation()));
        }

        if (null != dto.getComment()) {
            try {
                URLUtils.appendParamToURL(url, "commentForSource",
                        URLEncoder.encode(dto.getComment(), ConstantsKeys.UTF8));
            } catch (UnsupportedEncodingException e) {
                logger.info(e.getMessage());
            }
        }
        return url.toString();
    }

    public static String getMultiVersionKeyTranslationURL(final MessagesDTO dto, final String baseURL) {
        StringBuilder url = new StringBuilder(baseURL);
        String url2 = "";
        try {
            url2 = APIV2.PRODUCT_MULTI_VERSION_KEY_GET.replace("{" + APIParamName.PRODUCT_NAME + "}", dto.getProductID());
            url.append(url2);

            URLUtils.appendParamToURL(url, ConstantsKeys.VERSIONS, dto.getVersion().replaceAll(" ", ""));

            URLUtils.appendParamToURL(url, ConstantsKeys.LOCALE, dto.getLocale());

            URLUtils.appendParamToURL(url, ConstantsKeys.COMPONENT, dto.getComponent());

            URLUtils.appendParamToURL(url, ConstantsKeys.KEY,
                    URLEncoder.encode(dto.getKey(), ConstantsKeys.UTF8));
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage());
        }

        return url.toString();
    }

    /**
     * get the url of key-set-post API
     *
     * @param dto
     * @param baseURL
     * @return
     */
    public static String getPostKeys(final MessagesDTO dto, final String baseURL) {
        StringBuilder url = new StringBuilder(baseURL);
        String url2 = APIV2.KEY_SET_POST.replace("{" + APIParamName.PRODUCT_NAME + "}", dto.getProductID())
                .replace("{" + APIParamName.VERSION2 + "}", dto.getVersion())
                .replace("{" + APIParamName.COMPONENT + "}", dto.getComponent())
                .replace("{" + APIParamName.LOCALE + "}", dto.getLocale());
        url.append(url2);
        if (VIPCfg.getInstance().isCollectSource()) {
            URLUtils.appendParamToURL(url, ConstantsKeys.COLLECT_SOURCE,
                    Boolean.toString(VIPCfg.getInstance().isCollectSource()));
        }
        return url.toString();
    }

    public static String getPatternURL(final String locale, final String baseURL) {// PatternsDTO
        StringBuilder url = new StringBuilder(baseURL);
        String subUrl = APIV2.FORMAT_PATTERN_GET.replace("{" + APIParamName.LOCALE + "}", locale);
        url.append(subUrl);
        String i18nScope = VIPCfg.getInstance().getI18nScope();
        if (i18nScope != null && !"".equalsIgnoreCase(i18nScope)) {
            String[] scopeArray = i18nScope.split(ConstantsKeys.COMMA);
            PatternCategory[] categories = { PatternCategory.NUMBERS, PatternCategory.CURRENCIES, PatternCategory.DATES,
                    PatternCategory.PLURALS, PatternCategory.MEASUREMENTS };
            for (String category : scopeArray) {
                boolean flag = false;
                for (PatternCategory categoryConstant : categories) {
                    if (categoryConstant.toString().equals(category.trim())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag)
                    throw new IllegalArgumentException(
                            "The value of initialize parameter 'i18nScope' defined in 'vipconfig.properties' contains wrong value!");
            }
            URLUtils.appendParamToURL(url, "scope", i18nScope);
        }
        return url.toString();
    }

    // add by shihu
    /**
     *
     * @param language
     * @param region
     * @param baseURL
     * @return
     */
    public static String getPatternURL(final String language, final String region, final String baseURL) {// PatternsDTO
        // //
        // dto,
        StringBuilder url = new StringBuilder(baseURL);

        String subUrl = APIV2.FORMAT_PATTERN_WITH_LANGUAGE;
        url.append(subUrl);
        String i18nScope = VIPCfg.getInstance().getI18nScope();
        if (i18nScope != null && !"".equalsIgnoreCase(i18nScope)) {
            String[] scopeArray = i18nScope.split(ConstantsKeys.COMMA);
            PatternCategory[] categories = { PatternCategory.NUMBERS, PatternCategory.CURRENCIES, PatternCategory.DATES,
                    PatternCategory.PLURALS, PatternCategory.MEASUREMENTS };
            for (String category : scopeArray) {
                boolean flag = false;
                for (PatternCategory categoryConstant : categories) {
                    if (categoryConstant.toString().equals(category.trim())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag)
                    throw new IllegalArgumentException(
                            "The value of initialize parameter 'i18nScope' defined in 'vipconfig.properties' contains wrong value!");
            }

            URLUtils.appendParamToURL(url, "language", language);
            URLUtils.appendParamToURL(url, "region", region);
            URLUtils.appendParamToURL(url, "scope", i18nScope);
        }

        return url.toString();
    }

    /**
     * @param supportedLanguages
     *            languages string, split by comma. e.g. 'de,fr'
     * @param baseUrl
     * @return
     */
    public static String getRegionListURL(final String supportedLanguages, final String baseUrl) {
        StringBuilder url = new StringBuilder(baseUrl);
        url.append(APIV2.REGION_LIST);
        return URLUtils.appendParamToURL(url, ConstantsKeys.SUPPORTED_LANGUAGE_LIST, supportedLanguages);
    }

    /**
     * @param supportedLanguages
     *            languages string, split by comma. e.g. 'de,fr'
     * @param baseUrl
     * @return
     */
    public static String getSupportedLanguageListURL( final String baseUrl, BaseDTO dto, final String displayLanguage) {
        StringBuilder url = new StringBuilder(baseUrl);
        url.append(APIV2.SUPPORTED_LANGUAGE_LIST);
        URLUtils.appendParamToURL(url, ConstantsKeys.PRODUCT_NAME, dto.getProductID());
        URLUtils.appendParamToURL(url, ConstantsKeys.PRODUCT_VERSION, dto.getVersion());
        return URLUtils.appendParamToURL(url, ConstantsKeys.DISPLAY_LANGUAGE, displayLanguage);
    }
}
