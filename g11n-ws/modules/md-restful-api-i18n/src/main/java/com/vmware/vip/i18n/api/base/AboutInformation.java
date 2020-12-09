/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.i18n.api.base.BaseAction;
import com.vmware.vip.api.rest.APIOperation;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import java.util.Properties;
import com.vmware.vip.common.utils.PropertiesFileUtil;
import org.json.simple.JSONObject;

@RestController
public class AboutInformation extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(AboutInformation.class);
	public static Properties p = PropertiesFileUtil.loadFromStream("about.properties");
	public static String version = (String) p.get("version");

	@RequestMapping(value = API.ABOUT, method = RequestMethod.GET, produces = {API.API_CHARSET})
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getSupportedLocales(HttpServletRequest request) throws Exception {
		JSONObject info = new JSONObject();
		info.put("version", version);
		return super.handleResponse(200, "about", info);
	}

}
