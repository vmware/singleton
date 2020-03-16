/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base.utils;


import org.apache.commons.lang3.StringUtils;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.i18n.dto.TranslationWithPatternDTO;
import com.vmware.vip.common.utils.RegExpValidatorUtils;

public class ParameterValidationUtility{
	private ParameterValidationUtility() {}

	public static void validateTranslationWithPatternAPI(TranslationWithPatternDTO requestBody) throws RuntimeException {
		validateProductname(requestBody.getProductName());
		validateVersion( requestBody.getVersion());
		validateLanguage(requestBody.getLanguage());
		validatePseudo(requestBody.getPseudo());
	}


	private static void validateProductname(String productName)
			throws RuntimeException {
		if (StringUtils.isEmpty(productName)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterOrNumber(productName)) {
			throw new RuntimeException(ValidationMsg.PRODUCTNAME_NOT_VALIDE);
		}
	}

	private static void validateVersion(String version)
			throws RuntimeException {
		if (StringUtils.isEmpty(version)) {
			return;
		}
		if (!RegExpValidatorUtils.IsNumberAndDot(version)) {
			throw new RuntimeException(ValidationMsg.VERSION_NOT_VALIDE);
		}
	}

	private static void validateLanguage(String language)
			throws RuntimeException {
		if (StringUtils.isEmpty(language)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(language)) {
			throw new RuntimeException(ValidationMsg.LOCALE_NOT_VALIDE);
		}
	}

	private static void validatePseudo(String pseudo)
			throws RuntimeException {
		if (StringUtils.isEmpty(pseudo)) {
			return;
		}
		if (!RegExpValidatorUtils.IsTrueOrFalse(pseudo)) {
			throw new RuntimeException(ValidationMsg.PSEUDO_NOT_VALIDE);
		}
	}


}
