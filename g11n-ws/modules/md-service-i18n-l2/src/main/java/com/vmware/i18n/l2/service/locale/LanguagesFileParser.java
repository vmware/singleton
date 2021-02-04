/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.locale;

import java.util.Map;

import com.vmware.i18n.PatternUtil;
import com.vmware.vip.common.utils.JSONUtils;

public class LanguagesFileParser {

	public Map<String, Object> getDisplayNames(String displayLanguage){
		String json = PatternUtil.getLanguageFromLib(displayLanguage);
		return JSONUtils.getMapFromJson(json);
	}

	public Map<String, Object> getContextTransforms(String displayLanguage){
		String json = PatternUtil.getContextTransformFromLib(displayLanguage);
		return JSONUtils.getMapFromJson(json);
	}
}
