/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.utils.LocaleUtility;
import com.vmware.vip.i18n.api.base.utils.VersionMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BaseAction {
	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	@Autowired
	protected IProductService baseProductService;
	
	/**
	 *The method use to get the available version string by matching support versions. 
     *if not matching return the request version string
	 */
	protected String getAvailableVersion(String productName, String version) {
        try {
          return VersionMatcher.getMatchedVersion(version, baseProductService.getSupportVersionList(productName));
        }catch(Exception e) {
          return version;
        }
    }
   
	/**
	 * This method use to handle and package the version fallback response content 
	 * 
	 */
	protected APIResponseDTO handleVersionFallbackResponse(String requestedVersion, String availableVersion, Object data) {
		if(requestedVersion != null && availableVersion != null) {
			if (requestedVersion.equals(availableVersion)) {
				return handleResponse(APIResponseStatus.OK, data);
			} else {
				return handleResponse(APIResponseStatus.VERSION_FALLBACK_TRANSLATION, data);
			}
		}else{
			return handleResponse(APIResponseStatus.INTERNAL_NO_RESOURCE_ERROR, data);
		}
	}
	
	protected APIResponseDTO handleResponse(Response response, Object data) {
		APIResponseDTO d = new APIResponseDTO();
		d.setData(data);
		response.setServerTime(LocalDateTime.now().toString());
		d.setResponse(response);
		if (logger.isDebugEnabled()) {
			String logOfResData = "The response data: "
					+ d.getData().toString();
			logger.debug(logOfResData);
		}
		String rstr = "[response] " + response.toJSONString();
		logger.info(rstr);
		String endHandle = "[thread-" + Thread.currentThread().getId() + "] End to handle request.";
		logger.info(endHandle);
		return d;
	}

	protected APIResponseDTO handleResponse(int code, String message,
			Object data) {
		APIResponseDTO d = new APIResponseDTO();
		d.setData(data);
		Response r = new Response();
		r.setCode(code);
		r.setMessage(message);
		r.setServerTime(LocalDateTime.now().toString());
		d.setResponse(r);
		if (logger.isDebugEnabled() && d.getData() != null) {
			String logOfResData = "The response data: "
					+ d.getData().toString();
			logger.debug(logOfResData);
		}
		String rstr = "[response] " + r.toJSONString();
		logger.info(rstr);
		String endHandle = "[thread-" + Thread.currentThread().getId() + "] End to handle request.";
		logger.info(endHandle);
		return d;
	}
	
	
	/**
     *this function use to locale fallback 
     *
     */
    protected String getFormatLocale(String productName, String version,
            String inputLocale,  List<String> supportedLocaleList) throws L3APIException {
        List<Locale> supportedLocales = new ArrayList<Locale>();
        for (String supportedLocale : supportedLocaleList) {
            supportedLocale = supportedLocale.replace("_", "-");
            supportedLocales.add(Locale.forLanguageTag(supportedLocale));
        }
        String requestLocale = inputLocale.replace("_", "-");
        Locale fallbackLocale = LocaleUtility.pickupLocaleFromListNoDefault(
                supportedLocales, Locale.forLanguageTag(requestLocale));
        return fallbackLocale.toLanguageTag();
    }
}
