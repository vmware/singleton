/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.image;

import com.vmware.i18n.l2.service.image.ICountryFlagService;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.ValidationException;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

@RestController("v2-ImageAPI")
public class ImageAPI {

    @Autowired
    private ICountryFlagService countryFlagService;
    /**
     * Get country flag with region and level parameter.
     */
    @Operation(summary = APIOperation.IMAGE_COUNTRY_FLAG_VALUE, description = APIOperation.IMAGE_COUNTRY_FLAG_NOTES)
    @GetMapping(value = APIV2.IMAGE_COUNTRY_FLAG_GET)
    @ResponseStatus(HttpStatus.OK)
    public void getCountryFlagWithRegion(
            @Parameter(name = APIParamName.REGION, required = true, description = APIParamValue.REGION) @RequestParam(value = APIParamName.REGION, required = true) String region,
            @Parameter(name = APIParamName.SCALE, required = false, description = APIParamValue.COUNTRY_FLAG_SCALE) @RequestParam(value = APIParamName.SCALE, required = false) Integer scale,
            @Parameter(name = APIParamName.IMAGE_TYPE, required = false, description = APIParamValue.IMAGE_TYPE) @RequestParam(value = APIParamName.IMAGE_TYPE, required = false) String image_type,
            HttpServletResponse resp) throws Exception {
        int s = scale == null ? 1 : scale.intValue();
        String imgType = StringUtils.isEmpty(image_type) ? ConstantsKeys.JSON : image_type.toLowerCase();
        if (!ConstantsKeys.IMAGE_TYPE_MAP.containsKey(imgType)){
           throw new ValidationException(String.format(ValidationMsg.IMAGE_TYPE_NOT_VALIDE, image_type));
        }
        resp.setContentType(ConstantsKeys.IMAGE_TYPE_MAP.get(imgType));
        WritableByteChannel writeChannel = Channels.newChannel(resp.getOutputStream());

        try (FileChannel fileChannel = this.countryFlagService.getCountryFlagChannel(region, s, imgType)){
            fileChannel.transferTo(0, fileChannel.size(), writeChannel);
        }

    }

}
