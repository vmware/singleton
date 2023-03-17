/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.common;

/**
 * Constant string.
 */
public class Constants {

	public static final String UTF8 = "UTF-8";

	//symbol
	public static final String DOT = ".";

	//format type
	public static final String FORMAT = "format";
	public static final String STANDALONE = "stand-alone";

	//format length
	public static final String NARROW = "narrow";
	public static final String ABBREVIATED = "abbreviated";
	public static final String WIDE = "wide";

	// locale
	public static final String TERRITORIES = "territories";
	public static final String LANGUAGE = "language";
	public static final String DEFAULT_REGION_CODE = "defaultRegionCode";
	public static final String LANGUAGES = "languages";
	public static final String SCRIPTS = "scripts";
	public static final String VARIANTS = "variants";
	public static final String LOCALEDISPLAYNAMES = "localeDisplayNames";
	public static final String DISPLAY_LANGUAGE = "displayLanguage";
	public static final String LOCALE_ID = "localeID";
	public static final String DATES = "dates";
	public static final String MEASUREMENTS = "measurements";
	public static final String CURRENCIES = "currencies";
	public static final String CATEGORIES = "categories";
	public static final String PLURALS = "plurals";
	public static final String CONTEXT_TRANSFORMS = "contextTransforms";

	// pattern
	public static final String LIKELY_SUBTAG = "likelySubtag";
	public static final String LOCALE_PATH = "localePath";
	public static final String NUMBERS = "numbers";
	public static final String DEFAULT_CONTENT = "defaultContent";
	public static final String REGION_INFO = "regionInfo";
	public static final String LANGUAGE_ALIASES = "languageAlias";
	public static final String PLURAL_INFO = "pluralInfo";
	public static final String LANGUAGE_DATA = "languageData";
	public static final String PLURAL_RULES = "pluralRules";
	public static final String LANGUAGE_POPULATION = "languagePopulation";
	public static final String OFFICIAL_STATUS = "_officialStatus";
	public static final String POPULATION_PERCENT = "_populationPercent";
	public static final String _SCRIPTS = "_scripts";
	public static final String REPLACEMENT = "_replacement";
	public static final String TERRITORY_001 = "001";

	// file named
	public static final String SUPPLEMENTAL = "supplemental";
	public static final String LANGUAGE_DATA_JSON = "languageData.json";
	public static final String PLURALS_JSON = "plurals.json";
	public static final String ALIASES_JSON = "aliases.json";
	public static final String DEFAULT_CONTENT_JSON = "defaultContent.json";
	public static final String REGION_LANGUAGE_MAPPING_JSON = "regionLanguageMapping.json";
	public static final String PATTERN_JSON = "pattern.json";
	public static final String DATE_FIELDS_JSON = "dateFields.json";
	public static final String CURRENCY_DATA = "currencyData.json";
	public static final String CURRENCIES_DATA = "currencies.json";
	public static final String NUMBERING_SYSTEMS_JSON = "numberingSystems.json";
	public static final String NUMBERS_JSON = "numbers.json";
	public static final String DAY_PERIODS_JSON = "dayPeriods.json";
	public static final String DATES_JSON = "dates.json";
	public static final String DATE_TIMEZONENAME = "timeZoneName.json";
	
	// key name
	public static final String YEAR = "year";
	public static final String MONTH = "month";
	public static final String DAY = "day";
	public static final String HOUR = "hour";
	public static final String MINUTE = "minute";
	public static final String SECOND = "second";

	public static final String DAY_PERIODS_FORMAT = "dayPeriodsFormat";
	public static final String DAY_PERIODS_STANDALONE = "dayPeriodsStandalone";
    public static final String MIDNIGHT = "midnight";
    public static final String AM = "am";
    public static final String AMALTVARIANT = "am-alt-variant";
    public static final String NOON = "noon";
    public static final String PM = "pm";
    public static final String PMALTVARIANT = "pm-alt-variant";
    public static final String MORNING1 = "morning1";
	public static final String MORNING2 = "morning2";
    public static final String AFTERNOON1 = "afternoon1";
	public static final String AFTERNOON2 = "afternoon2";
    public static final String EVENING1 = "evening1";
	public static final String EVENING2 = "evening2";
    public static final String NIGHT1 = "night1";
	public static final String NIGHT2 = "night2";

	public static final String DAYS_FORMAT = "daysFormat";
	public static final String DAYS_STANDALONE = "daysStandalone";
	public static final String MONTH_FORMAT = "monthsFormat";
	public static final String MONTHS_STANDALONE = "monthsStandalone";
	public static final String ERAS = "eras";
	public static final String FIRST_DAY_OF_WEEK = "firstDayOfWeek";
	public static final String WEEKEND_RANGE = "weekendRange";
	public static final String DATE_FORMATS = "dateFormats";
	public static final String TIME_FORMATS = "timeFormats";
	public static final String DATE_TIME_FORMATS = "dateTimeFormats";
	public static final String DATE_FIELDS = "dateFields";

	// number data
	public static final String DECIMAL = "decimal";
	public static final String GROUP = "group";
	public static final String LIST = "list";
	public static final String PERCENT_SIGN = "percentSign";
	public static final String PLUS_SIGN = "plusSign";
	public static final String MINUS_SIGN = "minusSign";
	public static final String EXPONENTIAL = "exponential";
	public static final String SUPER_SCRIPT_EXPONENT = "superscriptingExponent";
	public static final String PER_MILLE = "perMille";
	public static final String INFINITY = "infinity";
	public static final String NAN = "nan";
	public static final String TIME_SEPARATOR = "timeSeparator";
	public static final String STANDARD = "standard";
	public static final String LONG = "long";
	public static final String SHORT = "short";
	public static final String LONG_DECIMAL_FORMATS = "long.decimalFormat";
	public static final String SHORT_DECIMAL_FORMATS = "short.decimalFormat";
	public static final String SHORT_STANDARD = "short.standard";
	public static final String DECIMAL_FORMAT = "decimalFormat";
	public static final String DECIMAL_FORMATS = "decimalFormats";
	public static final String PERCENT_FORMATS = "percentFormats";
	public static final String CURRENCY_FORMATS = "currencyFormats";
	public static final String SCIENTIFIC_FORMATS = "scientificFormats";
	public static final String NUMBER_SYMBOLS = "numberSymbols";
	public static final String NUMBER_FORMATS = "numberFormats";
	public static final String DEFAULT_NUMBER_SYSTEM = "defaultNumberingSystem";
	public static final String DECIMAL_FORMATS_LONG = "decimalFormats-long";
	public static final String DECIMAL_FORMATS_SHORT = "decimalFormats-short";
	public static final String CURRENCY_FORMATS_SHORT = "currencyFormats-short";
	
	//timezonename 
	public static final String TIMEZONENAME_HOUR_FORMAT ="hourFormat";
	public static final String TIMEZONENAME_GMT_ZERO_FORMAT ="gmtZeroFormat";
	public static final String TIMEZONENAME_GMT_FORMAT ="gmtFormat";
	public static final String TIMEZONENAME_REGION_FORMAT ="regionFormat";
	public static final String TIMEZONENAME_REGION_FORMAT_TYPE_DAYLIGHT ="regionFormat-type-daylight";
	public static final String TIMEZONENAME_REGION_FORMAT_TYPE_STANDARD ="regionFormat-type-standard";
	public static final String TIMEZONENAME_FALLBACK_FORMAT ="fallbackFormat";
	public static final String TIMEZONENAME_METAZONES ="metaZones";
	public static final String TIMEZONENAME_USES_METAZONE = "usesMetazone";
	public static final String TIMEZONENAME_METAZONE_TIMEZONEKEY ="timezoneKey";
	public static final String TIMEZONENAME_METAZONE_EXEMPLARCITY="exemplarCity";
	public static final String TIMEZONENAME_METAZONE_TIMEZONE ="timeZone";
	public static final String TIMEZONENAME_METAZONE_USESMETAZONES ="usesMetazones";
	public static final String TIMEZONENAME_METAZONE_MAPZONES ="mapZones";
	
	// currency supplemental data
	public static final String SUPPLEMENTAL_CURRENCY_DATA = "supplemental.currencyData";
	public static final String FRACTIONS = "fractions";
	public static final String REGION = "region";
	public static final String FROM = "_from";
	public static final String TO = "_to";
	public static final String SUPPLEMENTAL_NUMBERING_SYSTEMS = "supplemental.numberingSystems";
	public static final String NUMBERING_SYSTEMS = "numberingSystems";
	public static final String SUPPLEMENTAL_DAY_PERIODS_RULESET = "supplemental.dayPeriodRuleSet";
	public static final String DAY_PERIODS_RULESET = "dayPeriodRuleSet";

}
