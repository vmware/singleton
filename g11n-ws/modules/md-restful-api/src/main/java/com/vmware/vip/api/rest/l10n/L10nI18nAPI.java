/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest.l10n;

public interface L10nI18nAPI {
	public static String BASE_COLLECT_SOURCE_PATH = "/i18n/l10n";
    
	//v1
	public static String TRANSLATION_KEY_APIV1 = BASE_COLLECT_SOURCE_PATH + "/api/v1/translation/string";
	//Translation Source API
	public static String TRANSLATION_SOURCE_APIV1 = BASE_COLLECT_SOURCE_PATH + "/api/v1/translation/product/{productName}/component/{component}/sources";
    //Translation Product Component Key API
	public static String  TRANSLATION_PRODUCT_COMOPONENT_KEY_APIV1 = BASE_COLLECT_SOURCE_PATH + "/api/v1/translation/product/{productName}/component/{component}/key/{key:.+}";
	public static String  TRANSLATION_PRODUCT_NOCOMOPONENT_KEY_APIV1 = BASE_COLLECT_SOURCE_PATH + "/api/v1/translation/product/{productName}/key/{key:.+}";
	
	//v2
	//Translation Source API
	public static String TRANSLATION_SOURCE_APIV2 = BASE_COLLECT_SOURCE_PATH + "/api/v2/translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}";
	//Translation Product Component Key API 
	public static String  KEYS_TRANSLATION_APIV2 = BASE_COLLECT_SOURCE_PATH + "/api/v2/translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}/keys";
	public static String  KEY_TRANSLATION_APIV2 = BASE_COLLECT_SOURCE_PATH + "/api/v2/translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}/keys/{key:.+}";

    //v1 l10n agent sync source API
	public static String SOURCE_SYNC_RECORDS_APIV1 = BASE_COLLECT_SOURCE_PATH + "/api/v1/source/records";
	public static String SOURCE_SYNC_RECORD_STATUS_APIV1 = BASE_COLLECT_SOURCE_PATH + "/api/v1/source/synchrecord";
	public static String SOURCE_SYNC_RECORD_SOURCE_APIV1 = BASE_COLLECT_SOURCE_PATH + "/api/v1/source/sourcecomponent/{product}/{version}/{component}/{locale}/";

	public static String SOURCE_SYNC_RECORDS_APIV2 = BASE_COLLECT_SOURCE_PATH + "/api/v2/source/records";

}
