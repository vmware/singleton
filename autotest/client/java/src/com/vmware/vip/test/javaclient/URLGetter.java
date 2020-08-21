package com.vmware.vip.test.javaclient;

import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIV2;

public class URLGetter {
	public static String productTranslationGet(String product, String version) {
		return APIV2.PRODUCT_TRANSLATION_GET
				.replace("{" + APIParamName.PRODUCT_NAME + "}", product)
				.replace("{" + APIParamName.VERSION2 + "}", version);
	}

	public static String productTranslationPut(String product, String version) {
		return APIV2.PRODUCT_TRANSLATION_PUT
				.replace("{" + APIParamName.PRODUCT_NAME + "}", product)
				.replace("{" + APIParamName.VERSION2 + "}", version);
	}

	public static String productLocaleList(String product, String version) {
		return APIV2.PRODUCT_LOCALE_LIST_GET
				.replace("{" + APIParamName.PRODUCT_NAME + "}", product)
				.replace("{" + APIParamName.VERSION2 + "}", version);
	}

	public static String productComponentList(String product, String version) {
		return APIV2.PRODUCT_COMPONENT_LIST_GET
				.replace("{" + APIParamName.PRODUCT_NAME + "}", product)
				.replace("{" + APIParamName.VERSION2 + "}", version);
	}

	public static String keyTranslationGet(String product, String version, String locale, String component, String key) {
		return APIV2.KEY_TRANSLATION_GET
				.replace("{" + APIParamName.PRODUCT_NAME + "}", product)
				.replace("{" + APIParamName.VERSION2 + "}", version)
				.replace("{" + APIParamName.LOCALE + "}", locale)
				.replace("{" + APIParamName.COMPONENT + "}", component)
				.replace("{" + APIParamName.KEY2 + "}", key);
	}

	public static String keyTranslationPost(String product, String version, String locale, String component, String key) {
		return APIV2.KEY_TRANSLATION_POST
				.replace("{" + APIParamName.PRODUCT_NAME + "}", product)
				.replace("{" + APIParamName.VERSION2 + "}", version)
				.replace("{" + APIParamName.LOCALE + "}", locale)
				.replace("{" + APIParamName.COMPONENT + "}", component)
				.replace("{" + APIParamName.KEY2 + "}", key);
	}

	public static String sourceTranslationGet(String product, String version, String locale, String component, String source) {
		return APIV2.SOURCE_TRANSLATION_GET
				.replace("{" + APIParamName.PRODUCT_NAME + "}", product)
				.replace("{" + APIParamName.VERSION2 + "}", version)
				.replace("{" + APIParamName.LOCALE + "}", locale)
				.replace("{" + APIParamName.COMPONENT + "}", component)
				.replace("{" + APIParamName.SOURCE + "}", source);
	}

	public static String sourceTranslationPost(String product, String version, String locale, String component) {
		return APIV2.SOURCE_TRANSLATION_POST
				.replace("{" + APIParamName.PRODUCT_NAME + "}", product)
				.replace("{" + APIParamName.VERSION2 + "}", version)
				.replace("{" + APIParamName.LOCALE + "}", locale)
				.replace("{" + APIParamName.COMPONENT + "}", component);
	}

	public static String componentTranslationGet(String product, String version, String component, String locale) {
		return APIV2.COMPONENT_TRANSLATION_GET
				.replace("{" + APIParamName.PRODUCT_NAME + "}", product)
				.replace("{" + APIParamName.VERSION2 + "}", version)
				.replace("{" + APIParamName.COMPONENT + "}", component)
				.replace("{" + APIParamName.LOCALE + "}", locale);
	}

	public static String localizedDate(String product, String version, String component, String locale) {
		return APIV2.LOCALIZED_DATE;
	}

	public static String localizedNumber(String product, String version, String component, String locale) {
		return APIV2.LOCALIZED_NUMBER;
	}

	public static String formatPatternGet(String locale) {
		return APIV2.FORMAT_PATTERN_GET.replace("{" + APIParamName.LOCALE + "}", locale);
	}
}
