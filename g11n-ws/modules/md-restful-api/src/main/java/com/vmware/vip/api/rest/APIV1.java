/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest;

public class APIV1 {
    public static final String V = "v1";
    public static final String API_TRANSLATOIN = API.I18N_API_ROOT + V + "/" + API.TYPE_TRANSLATION;
    public static final String API_FORMATTING = API.I18N_API_ROOT + V + "/" + API.TYPE_FORMATTING;
    
    public static final String AUTHENTICATION          = API.I18N_API_ROOT + V + "/security/authentication";
    public static final String AUTHENTICATION_KEY      = API.I18N_API_ROOT + V + "/security/authentication/key";
    public static final String LOCALIZED_DATE          = API.I18N_API_ROOT + V + "/date/localizedDate";
    public static final String LOCALIZED_NUMBER        = API.I18N_API_ROOT + V + "/number/localizedNumber";
    public static final String PATTERN                 = API.I18N_API_ROOT + V + "/i18nPattern";
    public static final String BROWSER_LOCALE          = API.I18N_API_ROOT + V + "/locale/browserLocale";
    public static final String NORM_BROWSER_LOCALE     = API.I18N_API_ROOT + V + "/locale/normalizedBrowserLocale";
    public static final String TRANS_COMPONENT         = API.I18N_API_ROOT + V + "/translation/component";
    public static final String TRANS_COMPONENTS        = API.I18N_API_ROOT + V + "/translation/components";
    public static final String TRANS_STRING            = API.I18N_API_ROOT + V + "/translation/string";
    public static final String TRANSLATION             = API.I18N_API_ROOT + V + "/translation";
    public static final String SUPPORTED_LOCALES       = API.I18N_API_ROOT + V + "/translation/product/{" + APIParamName.PRODUCT_NAME + "}/version/{" + APIParamName.VERSION + "}/supportedLocales";
    public static final String COMPONENTS              = API.L10N_API_ROOT + V + "/bundles/components";
    public static final String COMPONENTS2             = API.I18N_API_ROOT + V + "/translation/product/{" + APIParamName.PRODUCT_NAME + "}/components/{" + APIParamName.COMPONENTS + "}";
    public static final String COMPONENT               = API.I18N_API_ROOT + V + "/translation/product/{" + APIParamName.PRODUCT_NAME + "}/component/{" + APIParamName.COMPONENT + "}";
    public static final String KEY2_GET                = API.I18N_API_ROOT + V + "/translation/product/{" + APIParamName.PRODUCT_NAME + "}/component/{" + APIParamName.COMPONENT + "}/key/{" + APIParamName.KEY2 + "}";
    public static final String PRODUCT_KEY2            = API.I18N_API_ROOT + V + "/translation/product/{" + APIParamName.PRODUCT_NAME + "}/key/{" + APIParamName.KEY2 + "}";
    public static final String KEY2_POST               = API.I18N_API_ROOT + V + "/translation/product/{" + APIParamName.PRODUCT_NAME + "}/component/{" + APIParamName.COMPONENT + "}/key/{" + APIParamName.KEY2 + "}";
    public static final String SOURCES_GET             = API.I18N_API_ROOT + V + "/translation/product/{" + APIParamName.PRODUCT_NAME + "}/component/{" + APIParamName.COMPONENT + "}/sources";
    public static final String SOURCES_POST            = API.I18N_API_ROOT + V + "/translation/product/{" + APIParamName.PRODUCT_NAME + "}/component/{" + APIParamName.COMPONENT + "}/sources";
    public static final String PRODUCT_VERSION         = API.I18N_API_ROOT + V + "/translation/product/{" + APIParamName.PRODUCT_NAME + "}/version/{" + APIParamName.VERSION2 + "}";
    
}
