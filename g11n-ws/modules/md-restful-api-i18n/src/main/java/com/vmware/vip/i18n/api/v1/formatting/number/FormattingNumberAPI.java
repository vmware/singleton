/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.formatting.number;

import javax.servlet.http.HttpServletRequest;

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
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.i18n.api.base.BaseAction;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Provide RESTful API to manipulate the number by specific locale and scale.
 *
 */
@RestController
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
	@ApiOperation(value = APIOperation.FORMAT_NUMBER_GET_VALUE, notes = APIOperation.FORMAT_NUMBER_GET_NOTES)
	@RequestMapping(value = APIV1.LOCALIZED_NUMBER, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO formatDate(
			@ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = true) String locale,
			@ApiParam(name = APIParamName.NUMBER, required = true, value = APIParamValue.NUMBER) @RequestParam(value = APIParamName.NUMBER, required = true) String number,
			@ApiParam(name = APIParamName.SCALE, required = false, value = APIParamValue.SCALE) @RequestParam(value = APIParamName.SCALE, required = false) Integer scale,
			HttpServletRequest request) {
		int s = scale == null ? 0 : scale.intValue();
		String localeNumber = numberFormatService.formatNumber(locale, number,
				s);
		NumberDTO numberDTO = new NumberDTO();
		numberDTO.setFormattedNumber(localeNumber);
		numberDTO.setLocale(locale);
		numberDTO.setScale(new Integer(s).toString());
		numberDTO.setNumber(number);
		return super.handleResponse(APIResponseStatus.OK, numberDTO);
	}

}
