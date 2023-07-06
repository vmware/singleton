/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.formatting.number;

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

import com.vmware.i18n.l2.service.number.INumberFormatService;
import com.vmware.i18n.l2.service.number.NumberDTO;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.i18n.api.base.BaseAction;


/**
 * Provide RESTful API to manipulate the number by specific locale and scale.
 *
 */
@RestController("v2-FormattingNumberAPI")
public class FormattingNumberAPI extends BaseAction {

	@Autowired
	INumberFormatService numberFormatService;

	/**
	 * Get localized number by specific locale and scale
	 *
	 * @param locale
	 *            A string specified by the product to represent a specific
	 *            locale, in [language]_[country (region)] format. e.g. ja_JP,
	 *            zh_CN.
	 * @param number
	 *            The digits.
	 * @param scale
	 *            Digital precision, decimal digits
	 * @return APIResponseDTO The object which represents response status.
	 */
	@Operation(summary = APIOperation.FORMAT_NUMBER_GET_VALUE, description = APIOperation.FORMAT_NUMBER_GET_NOTES)
	@RequestMapping(value = APIV2.LOCALIZED_NUMBER, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO formatDate(
			@Parameter(name = APIParamName.LOCALE, required = true, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = true) String locale,
			@Parameter(name = APIParamName.NUMBER, required = true, description = APIParamValue.NUMBER) @RequestParam(value = APIParamName.NUMBER, required = true) String number,
			@Parameter(name = APIParamName.SCALE, required = false, description = APIParamValue.SCALE) @RequestParam(value = APIParamName.SCALE, required = false) Integer scale,
			HttpServletRequest request) {
		int s = scale == null ? 0 : scale.intValue();
		String localeNumber = numberFormatService.formatNumber(locale, number,
				s);
		NumberDTO numberDTO = new NumberDTO();
		numberDTO.setFormattedNumber(localeNumber);
		numberDTO.setLocale(locale);
		numberDTO.setScale(Integer.valueOf(s).toString());
		numberDTO.setNumber(number);
		return super.handleResponse(APIResponseStatus.OK, numberDTO);
	}

}
