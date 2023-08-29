/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest;

public class APIOperation {
	public final static String ABOUT_VERSION_VALUE = "Get the build's version information";
	public final static String ABOUT_VERSION_NOTES = "Get the build's version information, including service's version info and product's translation's version info.";
	public final static String PRODUCT_TRANSLATION_VALUE = "Get the product's translation";
	public final static String PRODUCT_TRANSLATION_NOTES = "Get the product's translations by the specific version.";
	public final static String PRODUCT_LOCALE_LIST_VALUE = "Get the supported locale list";
	public final static String PRODUCT_LOCALE_LIST_NOTES = "Get the supported locale list which contains all suppported locale by the specific version.";
	public final static String PRODUCT_COMPONENT_LIST_VALUE = "Get the component list";
	public final static String PRODUCT_COMPONENT_LIST_NOTES = "Get the component list by the specific version.";
	public final static String PRODUCT_VERSIONINFO_VALUE = "Get drop version info";
	public final static String PRODUCT_VERSIONINFO_NOTES = "Get drop version info by the specific version, including drop create date(long id), compnent changed date(long id), etc.";
	public final static String PRODUCT_VERSION_LIST_VALUE = "Get the version list";
	public final static String PRODUCT_VERSION_LIST_NOTES = "Get the version list by the specific product name.";

	public final static String MULT_COMPONENT_TRANSLATION_VALUE = "Get multiple components' translation";
	public final static String MULT_COMPONENT_TRANSLATION_NOTES = "Get multiple components' by the specific version";
	public final static String COMPONENT_TRANSLATION_VALUE = "Get a component's translation";
	public final static String COMPONENT_TRANSLATION_NOTES = "Get a component's translation by the specific version";

	public final static String KEY_TRANSLATION_GET_VALUE = "Get a key's translation";
	public final static String KEY_TRANSLATION_GET_NOTES = "Get a key's translation in specific component";
	public final static String KEY_TRANSLATION_POST_VALUE = "Post a source";
	public final static String KEY_TRANSLATION_POST_NOTES = "Post a source under the component";
	public final static String KEY_SET_POST_VALUE = "Post a set of sources";
	public final static String KEY_SET_POST_NOTES = "Post a set of sources under the component";
	public final static String KEY_SET_GET_VALUE = "Get mult-keys' translation";
	public final static String KEY_SET_GET_NOTES = "Get mult-keys' translation in specific component";

	public final static String SOURCE_TRANSLATION_GET_VALUE = "Get translation by source";
	public final static String SOURCE_TRANSLATION_GET_NOTES = "Get translation by source";
	public final static String SOURCE_TRANSLATION_POST_VALUE = "Post the source";
	public final static String SOURCE_TRANSLATION_POST_NOTES = "Post the source";

	public static final String FORMAT_PATTERN_GET_VALUE = "Get pattern from CLDR";
	public static final String FORMAT_PATTERN_GET_NOTES = "Get pattern from CLDR by locale and scope";
	public static final String FORMAT_NUMBER_GET_VALUE = "Get localized number";
	public static final String FORMAT_NUMBER_GET_NOTES = "Get localized number by locale and scale";
	public static final String FORMAT_DATE_GET_VALUE = "Get localized date";
	public static final String FORMAT_DATE_GET_NOTES = "Get localized date by locale and pattern";


	public static final String TRANSLATION_UPDATE_VALUE = "Update translation";
	public static final String TRANSLATION_UPDATE_NOTES = "Update translation";

	public static final String TRANSLATION_SYNC_VALUE = "synchronization  translation";
	public static final String TRANSLATION_SYNC_NOTES = "synchronization  translation";

	public static final String LOCALE_PICKUP_VALUE = "Get the locale from browser";
	public static final String LOCALE_PICKUP_NOTES = "Get the first locale from browser's language setting";
	public static final String LOCALE_NORMALIZATION_VALUE = "Normalize the locale from browser";
	public static final String LOCALE_NORMALIZATION_NOTES = "Get the first locale from the browser's language setting and normalize it";
	public static final String LOCALE_REGION_LIST_VALUE = "Get the region list from CLDR";
	public static final String LOCALE_REGION_LIST_NOTES = "Get the region list from CLDR by the language";
	public static final String LOCALE_SUPPORTED_LANGUAGE_LIST_VALUE = "Get the supported language list";
	public static final String LOCALE_SUPPORTED_LANGUAGE_LIST_NOTES = "Get the supported language list from CLDR by the display language";

	public static final String KEY_GET_VALUE = "Generate a key";
	public static final String KEY_GET_NOTES = "Generate a key by productName,version,and userID for validation";
	public static final String Authenticate_VALUE = "Authenticate token";
	public static final String Authenticate_NOTES = "Authenticate token";

	public static final String FORMAT_PATTERN_VALUE = "Get pattern from CLDR";
	public static final String FORMAT_PATTERN_NOTES = "Get pattern from CLDR with language, region and scope";


	public static final String TRANSLATION_WITH_PATTERN_VALUE = "Get translations and patterns";
	public static final String TRANSLATION_WITH_PATTERN_NOTES = "Get translations and patterns by customized type";
	
	public final static String KEY_SOURCE_POST_VALUE = "Post a key's source";
	public final static String KEY_SOURCE_POST_NOTES = "Post a key's source under the component";

	public static final String IMAGE_COUNTRY_FLAG_VALUE = "Get the country flag svg image";
	public static final String IMAGE_COUNTRY_FLAG_NOTES = "Get the country flag svg image with region and scale";
}
