/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.about;

import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.about.exception.AboutAPIException;
import com.vmware.vip.core.about.service.version.IVersionService;
import com.vmware.vip.i18n.api.base.BaseAction;

/**
 * APIs for getting version information
 */
@RestController("v2-AboutVersionAPI")
public class AboutVersionAPI extends BaseAction {
    @Autowired
    private IVersionService versionService;

    /**
     * API for getting the build's version information, including service's version info and product's translation's version info.
     *
     * @param productName
     * @param version
     * @return
     * @throws AboutAPIException
     */
    @ApiOperation(value = APIOperation.ABOUT_VERSION_VALUE, notes = APIOperation.ABOUT_VERSION_NOTES)
    @RequestMapping(value = APIV2.VERSION, method = RequestMethod.GET, produces = { API.API_CHARSET })
    public APIResponseDTO getVersionInfo(
            @ApiParam(name = APIParamName.PRODUCT_NAME, required = false, value = APIParamValue.PRODUCT_NAME) @RequestParam(required = false) String productName,
            @ApiParam(name = APIParamName.VERSION, required = false, value = APIParamValue.VERSION) @RequestParam(required = false) String version) throws AboutAPIException {
        if(!StringUtils.isEmpty(productName) && !StringUtils.isEmpty(version)) {
            String availableVersion = super.getAvailableVersion(productName, version);
            return super.handleVersionFallbackResponse(version, availableVersion, versionService.getBuildVersion(productName, availableVersion));
        }else{
            return super.handleResponse(APIResponseStatus.OK, versionService.getBuildVersion(productName, version));
        }
    }
}
