/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.locale;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.vmware.i18n.PatternUtil;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.utils.JSONUtils;

public class TerritoriesFileParser {

	@SuppressWarnings("unchecked")
	public TerritoryDTO getRegionsByLanguage(String language) {
		TerritoryDTO dto = new TerritoryDTO();
		dto.setLanguage(language);
		String regionJson = PatternUtil.getRegionFromLib(language.replace("_", "-"));
		if (StringUtils.isEmpty(regionJson)) {
			dto.setTerritories(null);
			dto.setDefaultRegionCode("");
			return dto;
		}
		Map<String, String> terrMap = (Map<String, String>) JSONUtils.getMapFromJson(regionJson)
				.get(ConstantsKeys.TERRITORIES);
		Object defaultRegionCode = JSONUtils.getMapFromJson(regionJson).get(ConstantsKeys.DEFAULT_REGION_CODE);
		dto.setTerritories(terrMap);
		dto.setDefaultRegionCode(defaultRegionCode.toString());
		dto.setCities(null);
		return dto;
	}

	@SuppressWarnings("unchecked")
	public TerritoryDTO getCitiesByLanguage(String language) {
		TerritoryDTO dto = new TerritoryDTO();
		dto.setLanguage(language);
		String citiesJson = PatternUtil.getCitiesFromLib(language.replace("_", "-"));
		if (!StringUtils.isEmpty(citiesJson)) {
			Map<String, Object> citiesMap = (Map<String, Object>) JSONUtils.getMapFromJson(citiesJson)
					.get(ConstantsKeys.CITIES);
			dto.setCities(citiesMap);
		}
		return dto;
	}

}
