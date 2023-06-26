/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest;

public class APIParamValue {
	public final static String PRODUCT_NAME   = "product name";
	public final static String VERSION        = "translation version";
	public final static String COMPONENT      = "component name";
	public final static String COMPONENTS     = "a String contains multiple components, separated by commas. e.g. 'cim, common, cpa, cpu'";
	public final static String LOCALE         = "locale String. e.g. 'en-US'";
	public final static String LOCALES        = "a String contains multiple locales, separated by commas. e.g. 'zh-CN, ja-JP'";
	public final static String PSEUDO         = "a flag for returnning pseudo translation";
	public final static String MT             = "a flag to get machine translation";
	public final static String KEY            = "an id value to identify translation";
	public final static String KEYS           = "a String contains multiple keys, separated by commas. e.g. 'abc, common, time, cde'";
	public final static String SOURCE         = "a source String needs to be translated";
	public final static String COMMENT_SOURCE = "detailed comment to the source for better transaltion";
	public final static String SOURCE_FORMAT  = "the format of source(used to identify the source's secial chars like html tags)";
	public final static String COLLECT_SOURCE = "a flag to require backend collect the source for translation";
	public final static String SCOPE          = "pattern category string, separated by commas. e.g. 'dates, numbers, currencies, plurals, measurements, dateFields'";
	public final static String USERID         = "user id";
	public final static String NUMBER         = "number";
	public final static String SCALE          = "decimal digits";
	public final static String LONGDATE       = "long value of the date(e.g. 1472728030290)";
	public final static String PATTERN        = "pattern used to format the long date(the value could be one of this: YEAR = \"y\",QUARTER = \"QQQQ\",ABBR_QUARTER = \"QQQ\",QUARTER_YEAR = \"QQQQy\",QUARTER_ABBR_YEAR = \"QQQy\",MONTH = \"MMMM\",ABBR_MONTH = \"MMM\",NUM_MONTH = \"M\",MONTH_YEAR = \"MMMMy\",MONTH_ABBR_YEAR = \"MMMy\",MONTH_NUM_YEAR = \"My\",DAY = \"d\",MONTH_DAY_YEAR = \"MMMMdy\",ABBR_MONTH_DAY_YEAR = \"MMMdy\",NUM_MONTH_DAY_YEAR = \"Mdy\",WEEKDAY = \"EEEE\",ABBR_WEEKDAY = \"E\",WEEKDAY_MONTH_DAY_YEAR = \"EEEEMMMMdy\",ABBR_WEEKDAY_MONTH_DAY_YEAR = \"EMMMdy\",NUM_WEEKDAY_MONTH_DAY_YEAR = \"EMdy\",MONTH_DAY = \"MMMMd\",ABBR_MONTH_DAY = \"MMMd\",NUM_MONTH_DAY = \"Md\",WEEKDAY_MONTH_DAY = \"EEEEMMMMd\",ABBR_WEEKDAY_MONTH_DAY = \"EMMMd\",NUM_WEEKDAY_MONTH_DAY = \"EMd\")";
	public final static String SUPPORTED_LANGUAGES = "the supported language list, separated by commas. e.g. 'en, zh, ja'";
	public final static String DISPLAY_LANGUAGE   = "the display language. e.g. 'en'";
	public final static String LANGUAGE       = "a string which represents language, e.g. en, en-US, pt, pt-BR, zh-Hans";
	public final static String REGION         = "a string which represents region, e.g. US, PT, CN";
	public final static String COMBINE        = "an integer which represents combine type number 1 or 2";
	public final static String SCOPE_FILTER   = "a String for filtering the pattern data, separated by comma and underline. e.g. 'dates_eras,dates_dayPeriodsFormat'";
	public final static String DEFAULT_TERRITORY = "a boolean value to get the default territory timezone name or not, default is true";
	public final static String DISPLAY_CITY   = "a flag for displaying all the cities, e.g. true, false";
	public final static String REGIONS    	  = "a string which represents regions, separated by commas. e.g. US, PT, CN";
	public final static String USERNAME       = "ldap user name";
	public final static String PASSWORD       = "ldap user password";
	public final static String EXPIREDAYS     = "the authentication token's expired days";

	public final static String COUNTRY_FLAG_SCALE          = "The country flag scale：1 is 1x1, 2 is 3x2,default is 1x1";

}
