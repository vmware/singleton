/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.utils.KeyUtils;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.i18n.api.base.BaseAction;
import com.vmware.vip.i18n.api.v1.translation.TranslationProductComponentKeyAPI;

/**
 * Provide RESTful API for product to get translation by String base.
 *
 */
@RestController
public class TranslationSourceAction extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(TranslationSourceAction.class);
	@Autowired
	private ApplicationContext context;

	public APIResponseDTO getTranslationBySource(String productName, String component,
			String version, String locale, String source, String sourceFormat,
			String pseudo, HttpServletRequest request) throws L3APIException{
		String key = KeyUtils.generateKey(component, null, source.toString());

		return context.getBean(TranslationProductComponentKeyAPI.class)
				.getStringBasedTranslation(productName, version, component,
						locale, key, source, pseudo, "false", sourceFormat, "false");
	}

	public APIResponseDTO createSource(String productName, String component,
			String version, String locale, String source, String sourceFormat,
			String collectSource, String pseudo, HttpServletRequest request)
			throws L3APIException {
		String key = KeyUtils.generateKey(component, null, source.toString());
		request.setAttribute(ConstantsKeys.KEY, key);
		// put source to attribute for source collection
		if (!StringUtils.isEmpty(source)) {
			request.setAttribute(ConstantsKeys.SOURCE, source);
		}
		return context.getBean(TranslationProductComponentKeyAPI.class)
				.getStringBasedTranslation(productName, version, component,
						locale, key, source, pseudo, "false", sourceFormat, "false");
	}
}
