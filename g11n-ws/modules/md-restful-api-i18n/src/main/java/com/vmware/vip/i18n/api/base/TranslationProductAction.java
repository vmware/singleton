/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.DropVersionDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.multcomponent.IMultComponentService;
import com.vmware.vip.core.messages.service.multcomponent.TranslationDTO;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.i18n.api.base.BaseAction;


public class TranslationProductAction  extends BaseAction {
	private final static String PRODUCT_NAME = "productName";
	private final static String VERSION = "version";
	private static Logger logger = LoggerFactory.getLogger(TranslationProductAction.class);
	@Autowired
	IProductService productService;
	
	@Autowired
	IMultComponentService multipleComponentsService;

	public String getProductTrans(String productName, String version,String locale,String pseudo,
			HttpServletRequest request, HttpServletResponse response)  throws Exception {
		locale = locale == null ? ConstantsUnicode.EN : locale;
		List<String> componentList = productService
				.getComponentNameList(productName, version);
		String newURI = "";
		if (componentList.size() > 0) {
			StringBuffer componentBuffer = new StringBuffer();
			for (String component : componentList) {
				componentBuffer.append(component);
				componentBuffer.append(ConstantsChar.COMMA);
			}
			String components = componentBuffer.toString().substring(0,
					componentBuffer.toString().length() - 1);
			newURI = "/i18n/api/v1/translation/components?components="
					+ components + "&locales=" + locale;
		} else {
			newURI = "/i18n/api/v1/translation/components?locales=" + locale;
		}
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

    public APIResponseDTO getMultTrans(String productName, String version,String pseudo,
            HttpServletRequest req)  throws Exception {
        TranslationDTO translationDTO = new TranslationDTO();
        translationDTO.setProductName(productName);
		List<String> components = productService
				.getComponentNameList(productName, version);
		List<String> localeList = productService.getSupportedLocaleList(productName, version);
        translationDTO.setComponents(components);
        translationDTO.setVersion(version);
        if(new Boolean(pseudo)) {
        	localeList = new ArrayList<String>();
        	localeList.add(ConstantsKeys.LATEST);
            translationDTO.setLocales(localeList);
            translationDTO.setPseudo(new Boolean(pseudo));
        } else {
            translationDTO.setLocales(localeList);
            translationDTO.setPseudo(new Boolean(pseudo));
        }
        translationDTO = multipleComponentsService.getMultiComponentsTranslation(translationDTO);
        return super.handleResponse(APIResponseStatus.OK, translationDTO);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public APIResponseDTO getSLocales(String productName,String version,
			HttpServletRequest request) throws Exception{
		List<String> localeList = productService.getSupportedLocaleList(
				productName, version);
		Map data = new HashMap();
		data.put(PRODUCT_NAME, productName);
		data.put(VERSION, version);
		data.put("locales", localeList);
		return super.handleResponse(APIResponseStatus.OK, data);
	}

    public APIResponseDTO getCNameList(String productName,String version,
            HttpServletRequest request)  throws Exception {
        List<String> componentList= productService.getComponentNameList(productName, version);
        Map<String,Object> data = new HashMap<String,Object>();
        data.put(PRODUCT_NAME, productName);
        data.put(VERSION, version);
        data.put("components", componentList);
        return super.handleResponse(APIResponseStatus.OK, data);
    }

    public APIResponseDTO getVersionInfo(String productName,String version) throws L3APIException{
        DropVersionDTO versioninfo = productService.getVersionInfo(productName, version);
        Map<String,Object> data = new HashMap<String,Object>();
        data.put(PRODUCT_NAME, productName);
        data.put(VERSION, version);
        data.put("versioninfo", versioninfo);
        return super.handleResponse(APIResponseStatus.OK, data);
    }
}
