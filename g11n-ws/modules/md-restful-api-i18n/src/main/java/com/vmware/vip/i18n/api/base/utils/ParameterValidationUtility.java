/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base.utils;


import org.apache.commons.lang3.StringUtils;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.VIPAPIException;
import com.vmware.vip.common.i18n.dto.TranslationWithPatternDTO;
import com.vmware.vip.common.utils.RegExpValidatorUtils;

public class ParameterValidationUtility{
	private ParameterValidationUtility() {}

	public static void validateTranslationWithPatternAPI(TranslationWithPatternDTO requestBody) throws VIPAPIException {
		validateProductname(requestBody.getProductName());
		validateVersion( requestBody.getVersion());
		validateLanguage(requestBody.getLanguage());
		validatePseudo(requestBody.getPseudo());
	}


	private static void validateProductname(String productName)
			throws VIPAPIException {
		if (StringUtils.isEmpty(productName)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterOrNumber(productName)) {
			throw new VIPAPIException(ValidationMsg.PRODUCTNAME_NOT_VALIDE);
		}
	}

	private static void validateVersion(String version)
			throws VIPAPIException {
		if (StringUtils.isEmpty(version)) {
			return;
		}
		if (!RegExpValidatorUtils.IsNumberAndDot(version)) {
			throw new VIPAPIException(ValidationMsg.VERSION_NOT_VALIDE);
		}
	}

	private static void validateLanguage(String language)
			throws VIPAPIException {
		if (StringUtils.isEmpty(language)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(language)) {
			throw new VIPAPIException(ValidationMsg.LOCALE_NOT_VALIDE);
		}
	}

	private static void validatePseudo(String pseudo)
			throws VIPAPIException {
		if (StringUtils.isEmpty(pseudo)) {
			return;
		}
		if (!RegExpValidatorUtils.IsTrueOrFalse(pseudo)) {
			throw new VIPAPIException(ValidationMsg.PSEUDO_NOT_VALIDE);
		}
	}


}
