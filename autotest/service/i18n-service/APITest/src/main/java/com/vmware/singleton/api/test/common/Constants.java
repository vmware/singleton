package com.vmware.singleton.api.test.common;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public interface Constants {
	public static final String RESOURCE_FOLDER = "resource";
//	public static final String APIINFO_FILE_NAME = "APIInfo.json";
//	public static final String APIINFO_FILE_PATH = Paths.get(RESOURCE_FOLDER, APIINFO_FILE_NAME).toString();
	public static final String CONFIG_FILE_NAME = "config.properties";
	public static final String CONFIG_FILE_PATH = Paths.get(RESOURCE_FOLDER, CONFIG_FILE_NAME).toString();
	public static final String TEST_SERVER= "server";
	public static final String URL_PARAM_TYPE_PATH="path";
	public static final String URL_PARAM_TYPE_BODY="body";
	public static final String URL_PARAM_TYPE_QUERY="query";
	public static final String URL_PARAM_PRODUCT="productName";
	public static final String URL_PARAM_COMPONENT="component";
	public static final String URL_PARAM_COMPONENTS="components";
	public static final String URL_PARAM_KEY="key";
	public static final String URL_PARAM_SOURCE="source";
	public static final String URL_PARAM_VERSION="version";
	public static final String URL_PARAM_LOCALE="locale";
	public static final String URL_PARAM_COMMENTFORSOURCE="commentForSource";
	public static final String URL_PARAM_SOURCEFORMAT="sourceFormat";
	public static final String URL_PARAM_COLLECTSOURCE="collectSource";
	public static final String URL_PARAM_PSEUDO="pseudo";

	public static final String RESPONSE_BODY_KEY_DATA="data";
	public static final String RESPONSE_BODY_KEY_STATUS="status";
	public static final String RESPONSE_BODY_KEY_SIGNATURE="signature";
	public static final String RESPONSE_BODY_KEY_CODE="code";
	public static final String RESPONSE_BODY_KEY_MESSAGE="message";

	public static final int RESPONSE_CODE_OK=200;







	public static final String REST_PREFIX =  "/i18n/api/v1";
	public static final String PRODUCTNAME = "productName";
	public static final String PRODUCT = "product";
	public static final String VERSION = "version";
	public static final String SOURCE = "source";
	public static final String TRANSLATION = "translation";
	public static final String LOCALE = "locale";
	public static final String LOCALES = "locales";
	public static final String LOCALE_DISPLAY_NAME = "displayName";
	public static final String KEY = "key";
	public static final String COMPONENTS = "components";
	public static final String COMPONENT = "component";
	public static final String STATUS = "status";
	public static final String LONGDATE = "longDate";
	public static final String PATTERN = "pattern";
	public static final String FORMATED_DATE = "formattedDate";
	public static final String REST_PREFIX_TRANSLATION_API =  REST_PREFIX+"/translation";
	public static final String REST_PREFIX_STRING = REST_PREFIX_TRANSLATION_API + "/string?";
	public static final String REST_PREFIX_COMPONENT = REST_PREFIX_TRANSLATION_API + "/component?";
	public static final String REST_PREFIX_COMPONENTS = REST_PREFIX_TRANSLATION_API + "/components?";
	public static final String REST_PREFIX_DATE_API = REST_PREFIX + "/date";
	public static final String REST_PREFIX_LOCALIZED_DATE = REST_PREFIX_DATE_API + "/localizedDate?";
	public static final String REST_PREFIX_LOCALE_API = REST_PREFIX + "/locale";
	public static final String REST_PREFIX_BROWSER_LOCALE = REST_PREFIX_LOCALE_API + "/browserLocale";
	public static final String REST_PREFIX_NORMALIZED_BROWSER_LOCALE = REST_PREFIX_LOCALE_API + "/normalizedBrowserLocale";

//	public static final String CONF_KEY_SUPPORTED_PRODUCTS = "supportedproducts";
	public static final String CONF_KEY_TRANSLATION_LOCALES = "translationlocales";
	public static final String CONF_KEY_RESOURCEROOTPATH = "resourcerootpath";
	public static final String CONF_LIST_SEPERATER = ",";//seperating character for multiple values of one key in properties file
	public static final String CONF_KEY_BU = "bu";
	public static final String CONF_KEY_BUILD_ID = "build.id";
	public static final String CONF_KEY_PRODUCT = "build.product";
	public static final String CONF_KEY_BRANCH = "build.branch";
	public static final String CONF_KEY_BUILD_TYPE = "build.type";
	public static final String CONF_KEY_RACETRACK_USER = "user";
	public static final String CONF_KEY_RACETRACK_ENABLE = "racetrack.enable";
	public static final String CONF_KEY_API_INFO_FILES = "api.info.files";
	public static final String CONF_KEY_TEST_DATA_FILTER_PRIORITY = "test.data.filter.priority";
	public static final String CONF_KEY_TEST_DATA_DEFAULT_BRANCH  = "test.data.branch.default";
	public static final String CONF_KEY_TEST_DATA_FILE  = "test.data.file";
	public static final String CONF_KEY_TEST_DATA_SHEETS = CONF_KEY_API_INFO_FILES.replace(".json", "");
	public static final String CONF_KEY_TEST_DATA_COLUMN_ID = "test.data.column.id";
	public static final String CONF_KEY_TEST_DATA_COLUMN_NAME = "test.data.column.name";
	public static final String CONF_KEY_TEST_DATA_COLUMN_AUTO = "test.data.column.auto";
	public static final String CONF_KEY_TEST_DATA_COLUMN_FEATURE  = "test.data.column.feature";
	public static final String CONF_KEY_TEST_DATA_COLUMN_PRIORITY  = "test.data.column.priority";
	public static final String CONF_KEY_TEST_DATA_COLUMN_DESC  = "test.data.column.desc";
	public static final String CONF_KEY_TEST_DATA_COLUMN_PARAM  = "test.data.column.request.param";
	public static final String CONF_KEY_TEST_DATA_COLUMN_BODY  = "test.data.column.request.body";
	public static final String CONF_KEY_TEST_DATA_COLUMN_HEADER  = "test.data.column.request.header";
	public static final String CONF_KEY_TEST_DATA_COLUMN_EXPECTED  = "test.data.column.expected";
	public static final String CONF_KEY_TEST_RESULT_FOLDER  = "test.result.folder";

	public static final String RESOURCE_KEY_MSG = "messages";
	public static final String RESOURCE_KEY_COMPONENT = "component";
	public static final String RESOURCE_KEY_LOCALE = "locale";

	//HTTP
	public static final String HTTP_REQU_LANGUAGE = "Accept-Language";

	//locale
	public static final List<String> LOCALE_LIST = Arrays.asList(new String[]{"ar_AE", "ar_JO", "ar_SY", "hr_HR", "fr_BE", "es_PA", "mt_MT", "es_VE", "bg", "zh_TW", "it", "ko", "uk", "lv", "da_DK", "es_PR", "vi_VN", "en_US", "sr_ME", "sv_SE", "es_BO", "en_SG", "ar_BH", "pt", "ar_SA", "sk", "ar_YE", "hi_IN", "ga", "en_MT", "fi_FI", "et", "sv", "cs", "sr_BA", "el", "uk_UA", "hu", "fr_CH", "in", "es_AR", "ar_EG", "ja_JP", "es_SV", "pt_BR", "be", "is_IS", "cs_CZ", "es", "pl_PL", "tr", "ca_ES", "sr_CS", "ms_MY", "hr", "lt", "es_ES", "es_CO", "bg_BG", "sq", "fr", "ja", "sr_BA", "is", "es_PY", "de", "es_EC", "es_US", "ar_SD", "en", "ro_RO", "en_PH", "ca", "ar_TN", "sr_ME", "es_GT", "sl", "ko_KR", "el_CY", "es_MX", "ru_RU", "es_HN", "zh_HK", "no_NO", "hu_HU", "th_TH", "ar_IQ", "es_CL", "fi", "ar_MA", "ga_IE", "mk", "tr_TR", "et_EE", "ar_QA", "sr", "pt_PT", "fr_LU", "ar_OM", "th", "sq_AL", "es_DO", "es_CU", "ar", "ru", "en_NZ", "sr_RS", "de_CH", "es_UY", "ms", "el_GR", "iw_IL", "en_ZA", "th_TH", "hi", "fr_FR", "de_AT", "nl", "no_NO", "en_AU", "vi", "nl_NL", "fr_CA", "lv_LV", "de_LU", "es_CR", "ar_KW", "sr", "ar_LY", "mt", "it_CH", "da", "de_DE", "ar_DZ", "sk_SK", "lt_LT", "it_IT", "en_IE", "zh_SG", "ro", "en_CA", "nl_BE", "no", "pl", "zh_CN", "ja_JP", "de_GR", "sr_RS", "iw", "en_IN", "ar_LB", "es_NI", "zh", "mk_MK", "be_BY", "sl_SI", "es_PE", "in_ID", "en_GB"});
	public static final List<String> DATE_PATTERN_LIST = Arrays.asList(new String[]{"QQQ" ,"yQQQQ", "yQQQ", "MMMM", "MMM", "M",
		"yMMMM", "yMMM", "yM", "d", "yMMMMd", "yMMMd", "yMd", "EEEE", "E", "yMMMMEEEEd", "yMMMEd",
		"yMEd", "yMEd", "MMMMd", "MMMd", "Md", "MMMMEEEEd", "MMMEd", "MEd" });

	public static final String DEFAULT_COMPONENT = "default";
}
