/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.about;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.about.exception.AboutAPIException;
import com.vmware.vip.core.about.service.version.BuildVersionDTO;
import com.vmware.vip.core.about.service.version.BundleVersionDTO;
import com.vmware.vip.core.about.service.version.IVersionService;
import com.vmware.vip.core.about.service.version.ServiceVersionDTO;
import com.vmware.vip.i18n.api.base.BaseAction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(summary = APIOperation.ABOUT_VERSION_VALUE, description = APIOperation.ABOUT_VERSION_NOTES)
    @RequestMapping(value = APIV2.VERSION, method = RequestMethod.GET, produces = { API.API_CHARSET })
    public APIResponseDTO getVersionInfo(
            @Parameter(name = APIParamName.PRODUCT_NAME, required = false, description = APIParamValue.PRODUCT_NAME) @RequestParam(required = false) String productName,
            @Parameter(name = APIParamName.VERSION, required = false, description = APIParamValue.VERSION) @RequestParam(required = false) String version) throws AboutAPIException {
        BuildVersionDTO buildVersionDTO = new BuildVersionDTO();
        ServiceVersionDTO serviceVersionDTO = versionService.getServiceVersion();
        buildVersionDTO.setService(serviceVersionDTO);
        if(StringUtils.isEmpty(productName) && StringUtils.isEmpty(version)) {
            return super.handleResponse(APIResponseStatus.OK, buildVersionDTO);
        }else if(!StringUtils.isEmpty(productName) && !StringUtils.isEmpty(version)){
            String availableVersion = super.getAvailableVersion(productName, version);
            BundleVersionDTO bundleVersionDTO = versionService.getBundleVersion(productName, availableVersion);
            buildVersionDTO.setBundle(bundleVersionDTO);
            return super.handleVersionFallbackResponse(version, availableVersion, buildVersionDTO);
        }else{
            return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(), ConstantsMsg.PRODUCT_OR_VERSION_MISSING, null);
        }
    }
}
