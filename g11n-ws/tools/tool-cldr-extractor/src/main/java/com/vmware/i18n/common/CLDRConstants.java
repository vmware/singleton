/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.common;

import java.io.File;

public class CLDRConstants {

	public static final String CLDR_CORE = "cldr-core-";
	public static final String CLDR_DATES_FULL =  "cldr-dates-full-";
	public static final String CLDR_VERSION = "32.0.0";
	public static final String FILE_NAME = CLDR_VERSION + ".zip";
	public static final String CLDR_DOWNLOAD_DIR = "src" + File.separator + "main" + File.separator + "resources"
			+ File.separator + "cldr" + File.separator + "data" + File.separator + CLDR_VERSION + File.separator;
	public static final String GEN_CLDR_PATTERN_DIR = "src" + File.separator + "main" + File.separator + "resources"
			+ File.separator + "cldr" + File.separator + "pattern" + File.separator + "common" + File.separator;
	public static final String GEN_CLDR_LOCALEDATA_DIR = "src" + File.separator + "main" + File.separator + "resources"
			+ File.separator + "cldr" + File.separator + "localedata" + File.separator;
	public static final String GEN_CLDR_SUPPLEMENT_DIR = "src" + File.separator + "main" + File.separator + "resources"
			+ File.separator + "cldr" + File.separator + "supplement" + File.separator;
	public static final String GEN_CLDR_REGION_DIR = "src" + File.separator + "main" + File.separator + "resources"
			+ File.separator + "cldr" + File.separator + "regionLanguage" + File.separator;
	public static final String GEN_CLDR_DEFAULT_CONTENT_DIR = "src" + File.separator + "main" + File.separator + "resources"
			+ File.separator + "cldr" + File.separator + "defaultContent" + File.separator;
	public static final String GEN_CLDR_ALIASES_DIR = "src" + File.separator + "main" + File.separator + "resources"
			+ File.separator + "cldr" + File.separator + "aliases" + File.separator;
	public static final String GEN_CLDR_PLURALS_DIR = "src" + File.separator + "main" + File.separator + "resources"
			+ File.separator + "cldr" + File.separator + "plurals" + File.separator;
    public static final String GEN_CLDR_LANGUAGE_DATA_DIR = "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "cldr" + File.separator + "supplement" + File.separator;

	// zip path
	public static final String CORE_ZIP_FILE_PATH = CLDR_DOWNLOAD_DIR + "cldr-core-" + FILE_NAME;
	public static final String DATE_ZIP_FILE_PATH = CLDR_DOWNLOAD_DIR + "cldr-dates-full-" + FILE_NAME;
	public static final String NUMBER_ZIP_FILE_PATH = CLDR_DOWNLOAD_DIR + "cldr-numbers-full-" + FILE_NAME;
	public static final String UNIT_ZIP_FILE_PATH = CLDR_DOWNLOAD_DIR + "cldr-units-full-" + FILE_NAME;
	public static final String LOCALE_ZIP_FILE_PATH = CLDR_DOWNLOAD_DIR + "cldr-localenames-full-" + FILE_NAME;

	// cldr
	public static final String JSON_PATH = CLDRConstants.class.getProtectionDomain().getCodeSource().getLocation()
			.getPath();
	public static final String PARSE_DATA = "cldr/pattern/common/parse.json";
	public static final String PATTERN_JSON_PATH = "cldr/pattern/common/{0}/pattern.json";
	public static final String SUPPLEMENTAL_PATH = "cldr/supplement/{0}.json";
	public static final String ALL_CATEGORIES = "dates,numbers,plurals,measurements,currencies,dateFields";
	public static final String LOCALE_TERRITORIES_PATH = "cldr/localedata/{0}/territories.json";
	public static final String LOCALE_LANGUAGES_PATH = "cldr/localedata/{0}/languages.json";
	public static final String DEFAULT_CONTENT_PATH = "cldr/defaultContent/defaultContent.json";
	public static final String REGION_LANGUAGES_PATH = "cldr/regionLanguage/regionLanguageMapping.json";
	public static final String LOCALE_ALIASES_PATH = "cldr/aliases/aliases.json";
	public static final String PLURALS_PATH = "cldr/plurals/plurals.json";
	public static final String LANGUAGE_DATA_PATH = "cldr/supplement/languageData.json";
	public static final String DATE_FIELDS_JSON_PATH = "cldr/pattern/common/{0}/dateFields.json";

	public static final String CLDR_CORE_LANGUAGE_DATA = CLDRConstants.CLDR_CORE + CLDR_VERSION + "/supplemental/languageData.json";
	public static final String CLDR_CORE_PLURALS = CLDRConstants.CLDR_CORE + CLDR_VERSION + "/supplemental/plurals.json";
	public static final String CLDR_CORE_ALIASES = CLDRConstants.CLDR_CORE + CLDR_VERSION + "/supplemental/aliases.json";
	public static final String CLDR_DATES_FULL_DATE_FIELDS = CLDR_DATES_FULL + "{0}/main/{1}/dateFields.json";
	public static final String CLDR_DATES_FULL_CA_GREGORIAN = CLDR_DATES_FULL + "{0}/main/{1}/ca-gregorian.json";

	public static final String LANGUAGE_DATA_KEY_PATH = "supplemental.languageData";
	public static final String PLURALS_KEY_PATH = "supplemental.plurals-type-cardinal";
	public static final String ALIAS_KEY_PATH = "supplemental.metadata.alias.languageAlias";

	public static final String DATE_FIELDS_KEY_PATH = "main.{0}.dates.fields";
}
