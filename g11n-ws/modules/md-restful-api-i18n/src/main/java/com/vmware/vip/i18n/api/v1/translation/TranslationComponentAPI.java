/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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
    @ApiOperation(value = APIOperation.COMPONENT_TRANSLATION_VALUE, notes = APIOperation.COMPONENT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV1.TRANS_COMPONENT, method = RequestMethod.GET, produces = {
            API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getSingleComponentTranslation(
            @ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @RequestParam(value = APIParamName.PRODUCT_NAME, required = true) String productName,
            @ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
            @ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @RequestParam(value = APIParamName.COMPONENT, required = true) String component,
            @ApiParam(name = APIParamName.LOCALE, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
            @ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO)
            @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
            HttpServletRequest request)  throws Exception {
        ComponentMessagesDTO componentMessagesDTO = new ComponentMessagesDTO();
        componentMessagesDTO.setProductName(productName);
        componentMessagesDTO.setComponent(component == null ? ConstantsKeys.DEFAULT : component);
        componentMessagesDTO.setVersion(version);
        componentMessagesDTO.setLocale(locale == null ? ConstantsUnicode.EN : locale);
        componentMessagesDTO.setPseudo(new Boolean(pseudo));
        ComponentMessagesDTO dto = singleComponentService
                .getComponentTranslation(componentMessagesDTO);
        return super.handleResponse(APIResponseStatus.OK, dto);
    }

    /**
     * Get multiple translation basing on single component.
     *
     */
    //@ApiIgnore
    @ApiOperation(value = APIOperation.MULT_COMPONENT_TRANSLATION_VALUE, notes = APIOperation.MULT_COMPONENT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV1.TRANS_COMPONENTS, method = RequestMethod.GET, produces = {
            API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getMultipleComponentsTranslation(
            @ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @RequestParam(value = APIParamName.PRODUCT_NAME, required = true) String productName,
            @ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
            @ApiParam(name = APIParamName.COMPONENTS, required = true, value = APIParamValue.COMPONENTS) @RequestParam(value = APIParamName.COMPONENTS, required = true) String components,
            @ApiParam(name = APIParamName.LOCALES, required = true, value = APIParamValue.LOCALES) @RequestParam(value =  APIParamName.LOCALES, required = true) String locales,
            @ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO)
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
        if(new Boolean(pseudo)) {
        	localeList.add(ConstantsKeys.LATEST);
        } else if(locales != null) {
            List<String> supportedLocaleList = productService.getSupportedLocaleList(productName, version);
  			for (String locale : locales.split(",")) {
  				localeList.add(getFormatLocale(productName, version, locale.trim(), supportedLocaleList));
  			}
        }
        translationDTO.setLocales(localeList);
        translationDTO.setPseudo(new Boolean(pseudo));
        translationDTO =multipleComponentsService.getMultiComponentsTranslation(translationDTO);
        if(translationDTO.getBundles() == null || translationDTO.getBundles().size() == 0) {
        	throw new L3APIException(String.format(ConstantsMsg.TRANS_GET_FAILD,  productName + ConstantsChar.BACKSLASH + version));
        }
        return super.handleResponse(APIResponseStatus.OK, translationDTO);
    }
}
