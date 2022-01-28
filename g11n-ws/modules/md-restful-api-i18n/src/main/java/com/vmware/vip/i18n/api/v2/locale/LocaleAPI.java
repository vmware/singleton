/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.locale;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.i18n.l2.service.locale.ILocaleService;
import com.vmware.i18n.l2.service.locale.LocaleDTO;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.i18n.api.base.BaseAction;
import com.vmware.vip.i18n.api.base.utils.LocaleUtility;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Provide RESTful API to manipulate the locale.
 *
 */
@RestController("v2-LocaleAPI")
public class LocaleAPI extends BaseAction {

    @Autowired
    ILocaleService localeService;

    /**
     * Pick first locale from browser's language setting
     *
     * @param request
     *        Extends the ServletRequest interface to provide request information for HTTP servlets.
     * @return APIResponseDTO 
     *         The object which represents response status.
     */
    @ApiOperation(value = APIOperation.LOCALE_PICKUP_VALUE, notes = APIOperation.LOCALE_PICKUP_NOTES)
    @RequestMapping(value = APIV2.BROWSER_LOCALE, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getBrowserLocale(HttpServletRequest request) {
        return super.handleResponse(APIResponseStatus.OK, this.getLocaleDTO(request.getLocale().toString(), request.getLocale().getDisplayName()));
    }

    /**
     * Pick first locale from browser's language setting and normalize it.
     *
     * @param request
     *        Extends the ServletRequest interface to provide request information for HTTP servlets.s
     * @return APIResponseDTO 
     *         The object which represents response status.
     */
    @ApiOperation(value = APIOperation.LOCALE_NORMALIZATION_VALUE, notes = APIOperation.LOCALE_NORMALIZATION_NOTES)
    @RequestMapping(value = APIV2.NORM_BROWSER_LOCALE, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getNormalizedBrowserLocale(HttpServletRequest request) {
        String locale = LocaleUtility.normalizeLocaleFromString(request.getLocale().toString()).toString();
        return super.handleResponse(APIResponseStatus.OK, this.getLocaleDTO(locale, request.getLocale().getDisplayName()));
    }

    private LocaleDTO getLocaleDTO(String locale, String displayName) {
        if(locale.indexOf("__#")>=0){
        	locale=locale.replace("__#", "_");
        }
        LocaleDTO localeDTO = new LocaleDTO();
        localeDTO.setLocale(locale);
        localeDTO.setDisplayName(displayName);
    	return localeDTO;
    }

	/**
	 * Get the region list from CLDR
	 *
	 * @param supportedLanguageList
	 *            supported language, split by ','. e.g. 'zh,en,ja'
	 * @return APIResponseDTO
	 * @throws Exception
	 */
	@ApiOperation(value = APIOperation.LOCALE_REGION_LIST_VALUE, notes = APIOperation.LOCALE_REGION_LIST_NOTES)
	@RequestMapping(value = APIV2.REGION_LIST, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getRegionList(
			@ApiParam(name = APIParamName.SUPPORTED_LANGUAGE_LIST, required = true, value = APIParamValue.SUPPORTED_LANGUAGES) @RequestParam(value = APIParamName.SUPPORTED_LANGUAGE_LIST, required = true) String supportedLanguageList,
			@ApiParam(name = APIParamName.DISPLAY_CITY, required = false, value = APIParamValue.DISPLAY_CITY) @RequestParam(value = APIParamName.DISPLAY_CITY, required = false) String displayCity,
			@ApiParam(name = APIParamName.REGIONS, required = false, value = APIParamValue.REGIONS) @RequestParam(value = APIParamName.REGIONS, required = false) String regions)
			throws Exception {
		return super.handleResponse(APIResponseStatus.OK,
				this.localeService.getTerritoriesFromCLDR(supportedLanguageList.toLowerCase(), displayCity, regions));
	}

	/**
	 * Get the supported language list from CLDR by display language
	 *
	 * @param productName
	 *            product name
	 * @param version
	 *            product translation version
	 * @param displayLanguage
	 *            to be displayed language
	 * @return APIResponseDTO
	 * @throws Exception
	 */
	@ApiOperation(value = APIOperation.LOCALE_SUPPORTED_LANGUAGE_LIST_VALUE, notes = APIOperation.LOCALE_SUPPORTED_LANGUAGE_LIST_NOTES)
	@RequestMapping(value = APIV2.SUPPORTED_LANGUAGE_LIST, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getDisplayLanguagesList(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @RequestParam(value = APIParamName.PRODUCT_NAME, required = true) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@ApiParam(name = APIParamName.DISPLAY_LANGUAGE, required = false, value = APIParamValue.DISPLAY_LANGUAGE) @RequestParam(value = APIParamName.DISPLAY_LANGUAGE, required = false) String displayLanguage)
			throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		String newVersion = super.getAvailableVersion(productName, version);
		data.put(ConstantsKeys.LANGUAGES, this.localeService.getDisplayNamesFromCLDR(productName, newVersion, displayLanguage));
		data.put(ConstantsKeys.VERSION, newVersion);
		data.put(ConstantsKeys.PRODUCTNAME, productName);
		data.put(ConstantsKeys.DISPLAY_LANGUAGE, StringUtils.isEmpty(displayLanguage) ? "" : displayLanguage);
		return super.handleVersionFallbackResponse(version, newVersion, data);
	}
}
