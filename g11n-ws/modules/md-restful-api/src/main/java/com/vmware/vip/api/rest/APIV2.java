/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest;

public class APIV2 {
	public static final String V = "v2";
    public static final String API_FORMATTING = API.I18N_API_ROOT + V + "/" + API.TYPE_FORMATTING;
	public static final String API_TRANSLATOIN = API.I18N_API_ROOT + V + "/" + API.TYPE_TRANSLATION;
	public static final String API_ABOUT = API.I18N_API_ROOT + V + "/" + API.TYPE_ABOUT;

    public static final String VERSION = API_ABOUT  + "/version";

	// product-based
	public static final String PRODUCT_TRANSLATION_GET    = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}";
	public static final String PRODUCT_TRANSLATION_PUT    = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}";
	public static final String PRODUCT_VERSIONINFO_GET       = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/versioninfo";
	public static final String PRODUCT_LOCALE_LIST_GET    = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/localelist";
	public static final String PRODUCT_COMPONENT_LIST_GET = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/componentlist";
	public static final String PRODUCT_VERSION_LIST_GET   = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versionlist";
	public static final String PRODUCT_TRANSLATION_SYNC_PUT = API_TRANSLATOIN +"/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/synch";
	// key-based
	public static final String KEY_MULTI_VERSION_TRANSLATION_GET   = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/multiVersionKey";
	public static final String KEY_TRANSLATION_GET        = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/locales/{"+APIParamName.LOCALE+"}/components/{"+APIParamName.COMPONENT+"}/keys/{"+APIParamName.KEY2+"}";
	public static final String KEY_TRANSLATION_POST       = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/locales/{"+APIParamName.LOCALE+"}/components/{"+APIParamName.COMPONENT+"}/keys/{"+APIParamName.KEY2+"}";
	public static final String KEY_SET_POST               = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/locales/{"+APIParamName.LOCALE+"}/components/{"+APIParamName.COMPONENT+"}/keys";
	public static final String KEY_SET_GET               = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/locales/{"+APIParamName.LOCALE+"}/components/{"+APIParamName.COMPONENT+"}/keys";
	// source-based
	public static final String SOURCE_TRANSLATION_GET     = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/locales/{"+APIParamName.LOCALE+"}/components/{"+APIParamName.COMPONENT+"}/sources/{"+APIParamName.SOURCE+"}";
	public static final String SOURCE_TRANSLATION_POST    = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/locales/{"+APIParamName.LOCALE+"}/components/{"+APIParamName.COMPONENT+"}";
	// component-based	
	public static final String COMPONENT_TRANSLATION_GET  = API_TRANSLATOIN + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{"+APIParamName.VERSION2+"}/locales/{"+APIParamName.LOCALE+"}/components/{"+APIParamName.COMPONENT+"}";
	// L2
	public static final String LOCALIZED_DATE             = API_FORMATTING  + "/date/localizedDate";
	public static final String LOCALIZED_TIMEZONE_NAME    = API_FORMATTING  + "/date/timezoneNameList";
	public static final String LOCALIZED_NUMBER           = API_FORMATTING  + "/number/localizedNumber";
	public static final String FORMAT_PATTERN_GET         = API_FORMATTING  + "/patterns/locales/{"+APIParamName.LOCALE+"}";
	public static final String BROWSER_LOCALE             = API.I18N_API_ROOT + V + "/locale/browserLocale";
	public static final String NORM_BROWSER_LOCALE        = API.I18N_API_ROOT + V + "/locale/normalizedBrowserLocale";
	public static final String REGION_LIST                = API.I18N_API_ROOT + V + "/locale/regionList";
	public static final String SUPPORTED_LANGUAGE_LIST    = API.I18N_API_ROOT + V + "/locale/supportedLanguageList";
	public static final String FORMAT_PATTERN_WITH_LANGUAGE     = API_FORMATTING  + "/patterns";
	public static final String TRANSLATION_WITH_PATTERN = API.I18N_API_ROOT + V+"/combination/translationsAndPattern";
	public static final String IMAGE_COUNTRY_FLAG_GET = API.I18N_API_ROOT + V +"/image/countryFlag";

}
