/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.utils;

import com.vmware.l10n.source.dto.SourceAPIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;

public class SourceUtils {
	private SourceUtils() {}
	
	/*
	 * create a StringSourceDTO object.
	 */
	public static StringSourceDTO createSourceDTO(String productName, String version, String component, String locale, String key, String source, String commentForSource,  String sourceFormat){
		StringSourceDTO stringSourceDTO = new StringSourceDTO();
		stringSourceDTO.setProductName(productName);
		stringSourceDTO.setComponent(component);
		stringSourceDTO.setVersion(version);
		stringSourceDTO.setLocale(locale);
		stringSourceDTO.setKey(key);
		stringSourceDTO.setSource(source);
		stringSourceDTO.setComment(commentForSource);
		stringSourceDTO.setSourceFormat(sourceFormat);
		return stringSourceDTO;
	}

	public static SourceAPIResponseDTO handleSourceResponse(boolean isSourceCached){
		SourceAPIResponseDTO sourceAPIResponseDTO = new SourceAPIResponseDTO();
		if (isSourceCached) {
			sourceAPIResponseDTO.setStatus(APIResponseStatus.TRANSLATION_COLLECT_SUCCESS);
		} else {
			sourceAPIResponseDTO.setStatus(APIResponseStatus.TRANSLATION_COLLECT_FAILURE);
		}
		return sourceAPIResponseDTO;
	}
}


