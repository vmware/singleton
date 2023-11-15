/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.locale;

import java.util.HashMap;
import java.util.Map;

import com.vmware.i18n.PatternUtil;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.utils.JSONUtils;
import org.apache.commons.lang3.StringUtils;

public class LanguagesFileParser {

	public Map<String, Object> getDisplayNames(String displayLanguage){
		Map<String, Object> displayNamesMap = new HashMap<>();
		if(!StringUtils.isEmpty(displayLanguage)) {
			String languageJson = PatternUtil.getLanguageFromLib(displayLanguage);
			Map<String, Object> languageMap = JSONUtils.getMapFromJson(languageJson);
			String regionJson = PatternUtil.getRegionFromLib(displayLanguage);
			Map<String, Object> regionMap = JSONUtils.getMapFromJson(regionJson);
			String scriptsJson = PatternUtil.getScriptsFromLib(displayLanguage);
			Map<String, Object> scriptsMap = JSONUtils.getMapFromJson(scriptsJson);
			String variantsJson = PatternUtil.getVariantsFromLib(displayLanguage);
			Map<String, Object> variantsMap = JSONUtils.getMapFromJson(variantsJson);
			String localeDisplayNamesJson = PatternUtil.getLocaleDisplayNamesFromLib(displayLanguage);
			Map<String, Object> localeDisplayNamesMap = JSONUtils.getMapFromJson(localeDisplayNamesJson);
			displayNamesMap.put(ConstantsKeys.LANGUAGES, languageMap);
			displayNamesMap.put(ConstantsKeys.REGIONS, regionMap);
			displayNamesMap.put(ConstantsKeys.SCRIPTS, scriptsMap);
			displayNamesMap.put(ConstantsKeys.VARIANTS, variantsMap);
			displayNamesMap.put(ConstantsKeys.LOCALE_DISPLAY_NAMES, localeDisplayNamesMap);
		}
		return displayNamesMap;
	}

	public Map<String, Object> getContextTransforms(String displayLanguage){
		if(StringUtils.isEmpty(displayLanguage)) {
			return null;
		}
		String json = PatternUtil.getContextTransformFromLib(displayLanguage);
		return JSONUtils.getMapFromJson(json);
	}
}
