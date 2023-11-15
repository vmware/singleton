/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.constants;

import java.util.*;

/**
 * Constant strings.
 *
 */
public class ConstantsKeys {
   public static final String PRODUCTNAME = "productName";
   public static final String PRODUCT = "product";
   public static final String VERSION = "version";
   public static final String COMMENT_FOR_SOURCE = "commentForSource";
   public static final String SOURCE_FORMAT = "sourceFormat";
   public static final String lOCALE = "locale";
   public static final String lOCALES = "locales";
   public static final String DEFAULT = "default";
   public static final String ID = "id";
   public static final String COMPONENT = "component";
   public static final String COMPONENTS = "components";
   public static final String BUNDLES = "bundles";
   public static final String MESSAGES = "messages";
   public static final String COMMENTS = "comments";
   public static final String COMMENT = "comment";
   public static final String LATEST = "latest";
   public static final String COLLECT_SOURCE = "collectSource";
   public static final String PSEUDO = "pseudo";
   public static final String FALSE = "false";
   public static final String TRUE = "true";
   public static final String SOURCE = "source";
   public static final String TRANSLATION = "translation";
   public static final String CODE = "code";
   public static final String MESSAGE = "message";
   public static final String KEY = "key";
   public static final String EMPTY_STRING = "";
   public static final String EMPTY_JSON = "{}";
   public static final String TOKEN_CACHE_NAME = "tokenCache";
   public static final String TOKEN = "token";
   public static final String TOKEN_INTERCEP_PATH = "/**/api/**";
   public static final String AUTHENTICATION_PATH = "/**/security/authentication/**";
   public static final String LANGUAGES = "languages";
   public static final String REGIONS = "regions";
   public static final String SCRIPTS = "scripts";
   public static final String VARIANTS = "variants";
   public static final String LOCALE_DISPLAY_NAMES = "localeDisplayNames";
   public static final String LOCALE_DISPLAY_PATTERN = "localeDisplayPattern";
   public static final String LOCALE_PATTERN = "localePattern";
   public static final String LOCALE_SEPARATOR = "localeSeparator";

   public static final String DISPLAY_LANGUAGE = "displayLanguage";
   public static final String TERRITORIES = "territories";
   public static final String DEFAULT_REGION_CODE = "defaultRegionCode";
   public static final String LOCALEID = "localeID";
   public static final String CATEGORIES = "categories";
   public static final String PATTERN = "pattern";
   public static final String IS_EXIST_PATTERN = "isExistPattern";
   public static final String LANGUAGE = "language";
   public static final String REGION = "region";
   public static final String COMBINE = "combine";
   public static final String SCOPE = "scope";
   public static final String CITIES = "cities";
   public static final String CONTENT_TYPES = "content_types";
   // millisecond
   public static final Integer DELAY_TIME = 10000;
   public static final String VALIDATE_TOKEN_RESULT = "validateTokenResult";
   public static final String SIGNATURE = "signature";
   public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
   public static final String AES_ALGORITHM = "AES";
   public static final String SHA_512_ALGORITHM = "SHA-512";
   public static final String VIP = "vIP";
   public static final String AES_KEY =
         "W8p102YW9AZQ117g4t4z241pr6IM9oF49Q3L4pwsuWRE0E7Z04GM1819A217";
   public static final String RESOURCES = "resources";
   public static final String ZIP_SUFFIX = ".zip";
   public static final String MD5 = "MD5";
   public static final String POST = "POST";
   // requester
   public static final String GRM = "GRM";
   public static final String MT = "MT";
   public static final String DB = "DB";
   public static final String CACHE = "cache";
   public static final String BUNDLE = "bundle";
   public static final String UPDATEDTO = "updateTranslationDTO";
   public static final String VL10N = "SourceCollector";
   // pattern category
   public static final String DATES = "dates";
   public static final String NUMBERS = "numbers";
   public static final String PLURALS = "plurals";
   public static final String MEASUREMENTS = "measurements";
   public static final String CURRENCIES = "currencies";
   public static final String SUPPLEMENT = "supplemental";
   public static final String DATE_FIELDS = "dateFields";
   public static final String TIMEZONE_NAME = "TimeZoneName";
   public static final String[] ALL_CATEGORY =
         { DATES, NUMBERS, PLURALS, MEASUREMENTS, CURRENCIES };
   // fatal error message id
   public static final String FATA_ERROR = "[FATAL ERROR]";
   public static final String TOKEN_VALIDATION_ERROR = "Token validation failed";
   public static final String TOKEN_INVALIDATION_ERROR = "Invalid token";
   public static final String TOKEN_NOT_FOUND_ERROR = "Token not found";
   public static final String SOURCE_COLLECTION_ERROR = "Source collection forbidden";

   public static final String JSON_KEYSET = "jsonkeyset";
   public static final String DROP_ID = "drop_id";

   public static final String PLURAL_RULES = "pluralRules";

   public final static Set<String> SOURCE_FORMAT_LIST = new HashSet<String>(Arrays.asList("STRING", "SVG", "MD", "HTML"));
   public final static String SOURCE_FORMAT_BASE64 = "BASE64";
   public static final String CSP_AUTH_TOKEN = "csp-auth-token";
   public static final String IMAGE = "image";
   public static final String FLAGS = "flags";
   public static final String SVG = "svg";
   public static final String JSON = "json";
   public static final String ALL = "all";

   public final static Map<String,String> IMAGE_TYPE_MAP = Map.of("svg", "image/svg+xml", "json", "application/json;charset=utf-8");



}
