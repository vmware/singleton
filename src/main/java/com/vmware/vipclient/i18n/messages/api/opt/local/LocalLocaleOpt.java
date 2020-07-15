/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;
import com.vmware.vipclient.i18n.messages.service.PatternCacheService;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import com.vmware.vipclient.i18n.util.PatternBundleUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

public class LocalLocaleOpt implements LocaleOpt{

    private Logger logger = LoggerFactory.getLogger(LocalLocaleOpt.class);
	private static final String JSON_LANGUAGES = "level2/localeData/{0}/languages.json";
	private static final String JSON_TERRITORIES = "level2/localeData/{0}/territories.json";
	private static final String LANGUAGES_PREFIX = "languages_";
    private static final String BUNDLE_PREFIX = "messages_";
	private static final String BUNDLE_SUFFIX = ".json";
    
    @Override
    public Map<String, String> getSupportedLanguages(String displayLanguage) {
    	JSONObject languagesNames = getLanguagesNames(displayLanguage);

    	Map<String, String> supportedLocales = new HashMap<String, String>();
		try {
			
			Path path = Paths.get(VIPCfg.getInstance().getOfflineResourcesBaseUrl());
			
			URI uri = Thread.currentThread().getContextClassLoader().
					getResource(path.toString()).toURI();

	    	if (uri.getScheme().equals("jar")) {
	    		try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
	    			path = fileSystem.getPath(path.toString());
	    			getSupportedLocales(path, supportedLocales, languagesNames);
	    		}
			} else {
				path = Paths.get(uri);
				getSupportedLocales(path, supportedLocales, languagesNames);
			}
	    	
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
    	return supportedLocales;
    }

    private void getSupportedLocales(Path path, Map<String, String> supportedLocales, JSONObject languagesNames) throws IOException {
    	try (Stream<Path> listOfFiles = Files.walk(path).filter(p -> Files.isRegularFile(p))) {
        	listOfFiles.map(file -> {
				String fileName = file.getFileName().toString();
				if(fileName.startsWith(BUNDLE_PREFIX) && fileName.endsWith(BUNDLE_SUFFIX)) {
					return fileName.substring(BUNDLE_PREFIX.length(), fileName.indexOf('.'));
				}
				return "";
			}).forEach(language -> {
				if(language != null && !"".equalsIgnoreCase(language) && !ConstantsKeys.SOURCE.equalsIgnoreCase(language)) {
					if (languagesNames != null) {
						supportedLocales.put(language, (String) languagesNames.get(language));
					} else {
						supportedLocales.put(language, "");
					}
				}
			});
        }
    }

    private JSONObject getLanguagesNames(String displayLanguage){
		JSONObject languagesNames = null;
		logger.debug("Look for languages from cache for locale [{}]!", displayLanguage);
		String cacheKey = LANGUAGES_PREFIX + displayLanguage;
		languagesNames = new PatternCacheService().lookForPatternsFromCache(cacheKey);// key
		if (languagesNames != null) {
			logger.debug("Find languages from cache for locale [{}]!", displayLanguage);
			return languagesNames;
		}
		languagesNames = getLanguagesNamesFromBundle(displayLanguage);
		if (languagesNames != null) {
			logger.debug("Find the languages from local bundle for locale [{}].\n", displayLanguage);// [datetime] and
			new PatternCacheService().addPatterns(cacheKey, languagesNames);
			logger.debug("Languages is cached for locale [{}]!\n\n", displayLanguage);
			return languagesNames;
		}
		if (!LocaleUtility.isDefaultLocale(displayLanguage)) {
			logger.info("Can't find languages for locale [{}], look for English languages as fallback!", displayLanguage);
			languagesNames = getLanguagesNames(ConstantsKeys.EN);
		}
		return languagesNames;

	}

	private JSONObject getLanguagesNamesFromBundle(String displayLanguage) {
		JSONObject languagesNames = null;
		if (LocaleUtility.isDefaultLocale(displayLanguage)) {
			logger.debug("Look for languages from local en bundle for locale [{}]!", displayLanguage);
			languagesNames = getEnLanguagesNames(ConstantsKeys.EN);
		} else {
			logger.debug("Look for languages from local package bundle for locale [{}]!", displayLanguage);
			languagesNames = getOtherLanguagesNames(displayLanguage);
		}
		return languagesNames;
	}

	private JSONObject getEnLanguagesNames(String displayLanguage) {
		Map<String, Object> languagesData = PatternBundleUtil.readJSONFile(JSON_LANGUAGES, displayLanguage);
		if (languagesData == null) {
			return null;
		} else {
			return (JSONObject) languagesData.get(PatternKeys.LANGUAGES);
		}
	}

	private JSONObject getOtherLanguagesNames(String displayLanguage) {
		String normalizedLocale = CommonUtil.getCLDRLocale(displayLanguage, localePathMap, localeAliasesMap);
		logger.info("Normalized locale for locale [{}] is [{}]", displayLanguage, normalizedLocale);
		if(normalizedLocale == null || "".equalsIgnoreCase(normalizedLocale))
			return null;
		String languagesJsonStr = PatternUtil.getLanguageFromLib(normalizedLocale);
		JSONObject languagesData = null;
		try {
			languagesData = (JSONObject) new JSONParser().parse(languagesJsonStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (languagesData == null) {
			return null;
		}
		return (JSONObject) languagesData.get(PatternKeys.LANGUAGES);
	}

	public JSONObject getEnRegions(String displayLanguage) {
		Map<String, Object> regionsData = PatternBundleUtil.readJSONFile(JSON_TERRITORIES, displayLanguage);
		if (regionsData == null) {
			return null;
		} else {
			return (JSONObject) regionsData.get(PatternKeys.TERRITORIES);
		}
	}

	public JSONObject getOtherRegions(String displayLanguage) {
		JSONObject regionsData = null;
		String normalizedLocale = CommonUtil.getCLDRLocale(displayLanguage, localePathMap, localeAliasesMap);
		logger.info("Normalized locale for locale [{}] is [{}]", displayLanguage, normalizedLocale);
		if(normalizedLocale == null || "".equalsIgnoreCase(normalizedLocale))
			return null;
		String regionsJsonStr = PatternUtil.getRegionFromLib(normalizedLocale);
		try {
			regionsData = (JSONObject) new JSONParser().parse(regionsJsonStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (regionsData == null) {
			return null;
		}
		return (JSONObject) regionsData.get(PatternKeys.TERRITORIES);
	}
}
