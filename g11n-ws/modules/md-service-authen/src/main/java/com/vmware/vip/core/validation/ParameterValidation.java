/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.validation;


import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.utils.RegExpValidatorUtils;
import com.vmware.vip.common.utils.SourceFormatUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

public class ParameterValidation implements IVlidation {
	HttpServletRequest request = null;
	public final static String TAG_ALLOW_PRODUCT_LIST_MAP = "allowedProuductListMap";
	public ParameterValidation(HttpServletRequest request) {
		this.request = request;
	}

	public void validate() throws ValidationException {
		if (this.request == null) { 
			return;
		}
		validateQueryParams(request);
		validateAppId(request);
		validateProductname(request);
		validateVersion(request);
		validateComponent(request);
		validateComponents(request);
		validateKey(request);
		validateKeys(request);
		validateLocale(request);
		validateLanguage(request);
		validateLocales(request);
		validateSourceformat(request);
		validateCollectsource(request);
		validatePseudo(request);
		validatePattern(request);
		validateNumber(request);
		validateScale(request);
		validateRegion(request);
		validateCombine(request);
		validateScope(request);
	}

	private void validateQueryParams(HttpServletRequest request)
			throws ValidationException{
		Map<String, String[]> allMap = request.getParameterMap();
		for (String key : allMap.keySet()) {
			String[] strArr = allMap.get(key);
			for (String val : strArr) {
				if (RegExpValidatorUtils.containsHTML(val)) {
					throw new ValidationException(String.format(ValidationMsg.KEY_CONTAIN_HTML, key));
				}
			}
		}

	}
	@SuppressWarnings("unchecked")
	private void validateProductname(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String productName = pathVariables.get(APIParamName.PRODUCT_NAME) == null ? request
				.getParameter(APIParamName.PRODUCT_NAME) : pathVariables
				.get(APIParamName.PRODUCT_NAME);
		if (StringUtils.isEmpty(productName)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterOrNumber(productName)) {
			throw new ValidationException(ValidationMsg.PRODUCTNAME_NOT_VALIDE);
		}
		validateProductByAllowList(productName, (Map<String, Object>)request.getAttribute(ParameterValidation.TAG_ALLOW_PRODUCT_LIST_MAP));
	}
	
	private void validateAppId(HttpServletRequest request) 
	        throws ValidationException{
	   String appId = request.getHeader(APIParamName.APP_ID) == null ? request
               .getParameter(APIParamName.APP_ID) : request.getHeader(APIParamName.APP_ID);
       if (StringUtils.isEmpty(appId)) {
                   return;
       }
       if (!RegExpValidatorUtils.IsLetterOrNumber(appId)) {
                   throw new ValidationException(ValidationMsg.APPID_NOT_VALIDE);
       }         
       
	}

	@SuppressWarnings("unchecked")
	private void validateVersion(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String version = pathVariables.get(APIParamName.VERSION) == null ? request
				.getParameter(APIParamName.VERSION) : pathVariables
				.get(APIParamName.VERSION);
		if (StringUtils.isEmpty(version)) {
			return;
		}
		if (!RegExpValidatorUtils.IsNumberAndDot(version)) {
			throw new ValidationException(ValidationMsg.VERSION_NOT_VALIDE);
		}
	}

	@SuppressWarnings("unchecked")
	private void validateComponent(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String component = pathVariables.get(APIParamName.COMPONENT) == null ? request
				.getParameter(APIParamName.COMPONENT) : pathVariables
				.get(APIParamName.COMPONENT);
		if (StringUtils.isEmpty(component)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(component)) {
			throw new ValidationException(ValidationMsg.COMPONENT_NOT_VALIDE);
		}
	}

	@SuppressWarnings("unchecked")
	private void validateRegion(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String region = pathVariables.get(APIParamName.REGION) == null ? request
				.getParameter(APIParamName.REGION) : pathVariables
				.get(APIParamName.REGION);
		if (StringUtils.isEmpty(region)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(region)) {
			throw new ValidationException(ValidationMsg.REGION_NOT_VALIDE);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validateScope(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String scope = pathVariables.get(APIParamName.SCOPE) == null ? request
				.getParameter(APIParamName.SCOPE) : pathVariables
				.get(APIParamName.SCOPE);
		if (StringUtils.isEmpty(scope)) {
			return;
		}
		if (!RegExpValidatorUtils.isLetterArray(scope)) {
			throw new ValidationException(ValidationMsg.SCOPE_NOT_VALIDE);
		}
	}
	@SuppressWarnings("unchecked")
	private void validateCombine(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String combine = pathVariables.get(APIParamName.COMBINE) == null ? request
				.getParameter(APIParamName.COMBINE) : pathVariables
				.get(APIParamName.COMBINE);
		if (StringUtils.isEmpty(combine)) {
			return;
		}
		if (!RegExpValidatorUtils.isOneOrTwo(combine)) {
			throw new ValidationException(ValidationMsg.COMBINE_NOT_VALIDE);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validateComponents(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String components = pathVariables.get(APIParamName.COMPONENTS) == null ? request
				.getParameter(APIParamName.COMPONENTS) : pathVariables
				.get(APIParamName.COMPONENTS);
		if (StringUtils.isEmpty(components)) {
			return;
		}
		String[] compArr = components.split(ConstantsChar.COMMA);
		for(int i=0; i<compArr.length; i++) {
			if(StringUtils.isEmpty(compArr[i]) || !RegExpValidatorUtils.IsLetterAndNumberAndValidchar(compArr[i])) {
				throw new ValidationException(ValidationMsg.COMPONENTS_NOT_VALIDE);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validateLocales(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String locales = pathVariables.get(APIParamName.LOCALES) == null ? request
				.getParameter(APIParamName.LOCALES) : pathVariables
				.get(APIParamName.LOCALES);
		if (StringUtils.isEmpty(locales)) {
			return;
		}
		if (!RegExpValidatorUtils.isLetterNumbCommaAndValidchar(locales)) {
			throw new ValidationException(ValidationMsg.LOCALES_NOT_VALIDE);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validateKey(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String key = pathVariables.get(APIParamName.KEY) == null ? request
				.getParameter(APIParamName.KEY) : pathVariables
				.get(APIParamName.KEY);
		if (StringUtils.isEmpty(key)) {
			return;
		}
		if (!RegExpValidatorUtils.isAscii(key)) {
			throw new ValidationException(String.format(ValidationMsg.KEY_NOT_VALIDE_FORMAT, key));
		}

		if (RegExpValidatorUtils.containsHTML(key)) {
			throw new ValidationException(String.format(ValidationMsg.KEY_CONTAIN_HTML, key));
		}
	}

	@SuppressWarnings("unchecked")
	private void validateKeys(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String keys = pathVariables.get(APIParamName.KEYS) == null ? request
				.getParameter(APIParamName.KEYS) : pathVariables
				.get(APIParamName.KEYS);
		if (StringUtils.isEmpty(keys)) {
			return;
		}
		if (!RegExpValidatorUtils.isAscii(keys)) {
			throw new ValidationException(ValidationMsg.KEYS_NOT_VALIDE);
		}
		if (RegExpValidatorUtils.containsHTML(keys)) {
			throw new ValidationException(String.format(ValidationMsg.KEY_CONTAIN_HTML, keys));
		}
	}

	@SuppressWarnings("unchecked")
	private void validateLocale(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String locale = pathVariables.get(APIParamName.LOCALE) == null ? request
				.getParameter(APIParamName.LOCALE) : pathVariables
				.get(APIParamName.LOCALE);
		if (StringUtils.isEmpty(locale)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(locale)) {
			throw new ValidationException(ValidationMsg.LOCALE_NOT_VALIDE);
		}
	}

	@SuppressWarnings("unchecked")
	private void validateLanguage(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String language = pathVariables.get(APIParamName.LANGUAGE) == null ? request
				.getParameter(APIParamName.LANGUAGE) : pathVariables
				.get(APIParamName.LANGUAGE);
		if (StringUtils.isEmpty(language)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(language)) {
			throw new ValidationException(ValidationMsg.LANGUAGE_NOT_VALIDE);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void validateNumber(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String number = pathVariables.get(APIParamName.NUMBER) == null ? request
				.getParameter(APIParamName.NUMBER) : pathVariables
				.get(APIParamName.NUMBER);
		if (StringUtils.isEmpty(number)) {
			return;
		}
		if (!RegExpValidatorUtils.isNumeric(number)) {
			throw new ValidationException(ValidationMsg.NUMBER_NOT_VALIDE);
		}
	}

	private void validateScale(HttpServletRequest request)
			throws ValidationException {
		String scale = request.getParameter(APIParamName.SCALE) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.SCALE);
		try {
			if (!StringUtils.isEmpty(scale)
					&& Integer.parseInt(scale) < 0) {
				throw new ValidationException(ValidationMsg.SCALE_NOT_VALIDE);
			}
		} catch (NumberFormatException e) {
			throw new ValidationException("NumberFormatException: " + e.getMessage());
		}
	}

	public static void validateSourceformat(HttpServletRequest request)
			throws ValidationException {
		String sourceFormat = request.getParameter(APIParamName.SOURCE_FORMAT) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.SOURCE_FORMAT);
		if (!StringUtils.isEmpty(sourceFormat)) {
			sourceFormat = sourceFormat.toUpperCase();
			if (SourceFormatUtils.isBase64Encode(sourceFormat)){
				sourceFormat = SourceFormatUtils.formatSourceFormatStr(sourceFormat);
				if (!StringUtils.isEmpty(sourceFormat) && !ConstantsKeys.SOURCE_FORMAT_LIST.contains(sourceFormat)) {
					throw new ValidationException(ValidationMsg.SOURCEFORMAT_NOT_VALIDE);
				}
			}else{
				if (!ConstantsKeys.SOURCE_FORMAT_LIST.contains(sourceFormat)) {
					throw new ValidationException(ValidationMsg.SOURCEFORMAT_NOT_VALIDE);
				}
			}
		}
	}

	private void validateCollectsource(HttpServletRequest request)
			throws ValidationException {
		String collectsource = request
				.getParameter(APIParamName.COLLECT_SOURCE) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.COLLECT_SOURCE);
		if (StringUtils.isEmpty(collectsource)) {
			return;
		}
		if (!RegExpValidatorUtils.IsTrueOrFalse(collectsource)) {
			throw new ValidationException(
					ValidationMsg.COLLECTSOURCE_NOT_VALIDE);
		}
	}

	private void validatePseudo(HttpServletRequest request)
			throws ValidationException {
		String pseudo = request.getParameter(APIParamName.PSEUDO) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.PSEUDO);
		if (StringUtils.isEmpty(pseudo)) {
			return;
		}
		if (!RegExpValidatorUtils.IsTrueOrFalse(pseudo)) {
			throw new ValidationException(ValidationMsg.PSEUDO_NOT_VALIDE);
		}
	}

	@SuppressWarnings("unchecked")
	private void validatePattern(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String pattern = pathVariables.get(APIParamName.PATTERN) == null ? request
				.getParameter(APIParamName.PATTERN) : pathVariables
				.get(APIParamName.PATTERN);
		if (StringUtils.isEmpty(pattern)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetter(pattern)) {
			//throw new ValidationException(ValidationMsg.PATTERN_NOT_VALIDE);
		}
	}

    /**
     * validate the product name by the allow list
     *
     * @param productName
     * @param allowListMap
     * @throws ValidationException
     */
    private void validateProductByAllowList(String productName, Map<String, Object> allowListMap) throws ValidationException {
        if (allowListMap != null && !allowListMap.isEmpty() && !allowListMap.containsKey(productName)) {
        	throw new ValidationException(String.format(ValidationMsg.PRODUCTNAME_NOT_SUPPORTED, productName));
        }
    }
}
