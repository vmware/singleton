/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

	public String getTranslationBySource(String productName, String component,
			String version, String locale, String source, String sourceFormat,
			String collectSource, String pseudo, HttpServletRequest request,
			HttpServletResponse response) {
		String key = KeyUtils.generateKey(component, null, source);
		request.setAttribute("version", version);
		request.setAttribute("source", source);
		request.setAttribute("locale", locale);
		request.setAttribute("sourceFormat", sourceFormat);
		request.setAttribute("pseudo", pseudo);
		String newURI = APIV1.KEY2_GET
				.replace("{" + APIParamName.PRODUCT_NAME + "}", productName)
				.replace("{" + APIParamName.COMPONENT + "}", component)
				.replace("{" + APIParamName.KEY2 + "}", key);
		try {
			request.getRequestDispatcher(newURI).forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		return null;
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
