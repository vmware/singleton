/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.multcomponent.IMultComponentService;
import com.vmware.vip.core.messages.service.multcomponent.TranslationDTO;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.singlecomponent.IOneComponentService;
import com.vmware.vip.i18n.api.base.BaseAction;


/**
 * Provide API for product to get translation by component base.
 *
 */
@RestController
public class TranslationComponentAPI  extends BaseAction {
    @Autowired
    IOneComponentService singleComponentService;
    
    @Autowired
    IMultComponentService multipleComponentsService;
    
    @Autowired
    IProductService productService;

    /**
     * Get translation based on single component.
     *
     */
    //@ApiIgnore
    @Operation(summary = APIOperation.COMPONENT_TRANSLATION_VALUE, description = APIOperation.COMPONENT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV1.TRANS_COMPONENT, method = RequestMethod.GET, produces = {
            API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getSingleComponentTranslation(
            @Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @RequestParam(value = APIParamName.PRODUCT_NAME, required = true) String productName,
            @Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
            @Parameter(name = APIParamName.COMPONENT, required = true, description = APIParamValue.COMPONENT) @RequestParam(value = APIParamName.COMPONENT, required = true) String component,
            @Parameter(name = APIParamName.LOCALE, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
            @Parameter(name = APIParamName.PSEUDO, description = APIParamValue.PSEUDO)
            @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
            HttpServletRequest request)  throws Exception {
        ComponentMessagesDTO componentMessagesDTO = new ComponentMessagesDTO();
        componentMessagesDTO.setProductName(productName);
        componentMessagesDTO.setComponent(component == null ? ConstantsKeys.DEFAULT : component);
        componentMessagesDTO.setVersion(version);
        componentMessagesDTO.setLocale(locale == null ? ConstantsUnicode.EN : locale);
        componentMessagesDTO.setPseudo(Boolean.parseBoolean(pseudo));
        ComponentMessagesDTO dto = singleComponentService
                .getComponentTranslation(componentMessagesDTO);
        return super.handleResponse(APIResponseStatus.OK, dto);
    }

    /**
     * Get multiple translation basing on single component.
     *
     */
    //@ApiIgnore
    @Operation(summary = APIOperation.MULT_COMPONENT_TRANSLATION_VALUE, description = APIOperation.MULT_COMPONENT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV1.TRANS_COMPONENTS, method = RequestMethod.GET, produces = {
            API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getMultipleComponentsTranslation(
            @Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @RequestParam(value = APIParamName.PRODUCT_NAME, required = true) String productName,
            @Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
            @Parameter(name = APIParamName.COMPONENTS, required = true, description = APIParamValue.COMPONENTS) @RequestParam(value = APIParamName.COMPONENTS, required = true) String components,
            @Parameter(name = APIParamName.LOCALES, required = true, description = APIParamValue.LOCALES) @RequestParam(value =  APIParamName.LOCALES, required = true) String locales,
            @Parameter(name = APIParamName.PSEUDO, description = APIParamValue.PSEUDO)
            @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
            HttpServletRequest request) throws Exception {
        TranslationDTO translationDTO = new TranslationDTO();
        translationDTO.setProductName(productName);
        List<String> componentList = new ArrayList<String>();
        if(components != null) {
            for(String component: components.split(",")) {
            	componentList.add(component.trim());
            }
        }
        translationDTO.setComponents(componentList);
        translationDTO.setVersion(version);
        List<String> localeList = new ArrayList<String>();
        if(Boolean.parseBoolean(pseudo)) {
        	localeList.add(ConstantsKeys.LATEST);
        } else if(locales != null) {
            List<String> supportedLocaleList = productService.getSupportedLocaleList(productName, version);
  			for (String locale : locales.split(",")) {
  				localeList.add(getFormatLocale(productName, version, locale.trim(), supportedLocaleList));
  			}
        }
        translationDTO.setLocales(localeList);
        translationDTO.setPseudo(Boolean.parseBoolean(pseudo));
        translationDTO =multipleComponentsService.getMultiComponentsTranslation(translationDTO);
        if(translationDTO.getBundles() == null || translationDTO.getBundles().size() == 0) {
        	throw new L3APIException(String.format(ConstantsMsg.TRANS_GET_FAILD,  productName + ConstantsChar.BACKSLASH + version));
        }
        return super.handleResponse(APIResponseStatus.OK, translationDTO);
    }
}
