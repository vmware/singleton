/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.StringBasedDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.mt.IMTService;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.string.IStringService;
import com.vmware.vip.i18n.api.base.BaseAction;

/**
 * Provide RESTful API for product to get translation by String base.
 *
 */
public class TranslationProductComponentKeyAction extends BaseAction {
	@Autowired
	IStringService stringBasedService;

	@Autowired
	IMTService mtService;

	public APIResponseDTO getTransByGet(String productName, String version,
			String locale, String component, String key, String source,
			String commentForSource, String sourceFormat, String collectSource,
			String pseudo, HttpServletRequest request) throws L3APIException {
		ComponentMessagesDTO c = new ComponentMessagesDTO();
		c.setProductName(productName);
		c.setComponent(StringUtils.isEmpty(component) ? ConstantsKeys.DEFAULT
				: component);
		c.setVersion(version);
		c.setLocale(locale == null ? ConstantsUnicode.EN : locale);
		if (ConstantsKeys.TRUE.equalsIgnoreCase(pseudo)) {
			c.setPseudo(new Boolean(pseudo));
		}
		String keycomp = StringUtils.isEmpty(sourceFormat) ? key : (key
				+ ConstantsChar.DOT + ConstantsChar.POUND + sourceFormat
				.toUpperCase());
		StringBasedDTO stringBasedDTO = stringBasedService
				.getStringTranslation(c, keycomp, source);
		return super.handleResponse(APIResponseStatus.OK, stringBasedDTO);
	}

	/*
	 * Get the translation by string-based
	 */
	public APIResponseDTO getStringBasedTranslation(String productName,
			String version, String component, String locale, String key,
			String source, String pseudo, String machineTranslation,
			String sourceFormat, String checkTranslationStatus) throws L3APIException {
		ComponentMessagesDTO c = new ComponentMessagesDTO();
		c.setProductName(productName);
		c.setComponent(component);
		c.setVersion(version);
		c.setPseudo(new Boolean(pseudo));
		c.setLocale(locale == null ? ConstantsUnicode.EN : locale);
		String ckey = StringUtils.isEmpty(sourceFormat) ? key : (key
				+ ConstantsChar.DOT + ConstantsChar.POUND + sourceFormat
				.toUpperCase());
		StringBasedDTO stringBasedDTO = null;
		if (new Boolean(machineTranslation)) {
			stringBasedDTO = mtService.getStringMTTranslation(c, ckey, source);
			stringBasedDTO
					.setMachineTranslation(new Boolean(machineTranslation));
		} else {
			stringBasedDTO = stringBasedService.getStringTranslation(c, ckey,
					source);
		}
		if(new Boolean(checkTranslationStatus)) {
			if(stringBasedDTO.getStatus().indexOf(ConstantsMsg.TRANS_IS_NOT_FOUND) != -1 || stringBasedDTO.getStatus().equals(String.format(ConstantsMsg.EN_NOT_SOURCE, ConstantsMsg.TRANS_FOUND_RETURN))) {
				return super.handleResponse(APIResponseStatus.TRANSLATION_NOT_READY, stringBasedDTO);
			} else {
				return super.handleResponse(APIResponseStatus.TRANSLATION_READY, stringBasedDTO);
			}
		}
		return super.handleResponse(APIResponseStatus.OK, stringBasedDTO);
	}

	public APIResponseDTO getTransByPost(String productName, String version,
			String locale, String component, String key, String source,
			String commentForSource, String sourceFormat, String collectSource,
			String pseudo, String machineTranslation, String checkTranslationStatus, 
			HttpServletRequest request, HttpServletResponse response)
			throws L3APIException, IOException {
		// find the source by this order: parameter-attribute-body
		if (StringUtils.isEmpty(source)) {
			source = request.getAttribute(ConstantsKeys.SOURCE) == null ? source
					: (String) request.getAttribute(ConstantsKeys.SOURCE);
		}
		if (StringUtils.isEmpty(source)) {
			source = IOUtils.toString(request.getInputStream(),
					ConstantsUnicode.UTF8);
			source = source.equalsIgnoreCase(ConstantsKeys.EMPTY_JSON) ? ConstantsChar.EMPTY
					: source;
		}
		if (!StringUtils.isEmpty(source)) {
			request.setAttribute(ConstantsKeys.SOURCE, source);
		}
		return this.getStringBasedTranslation(productName, version, component,
				locale, key, source, pseudo, machineTranslation, sourceFormat, checkTranslationStatus);
	}
}
