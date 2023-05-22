/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.formatting.pattern;

import com.vmware.i18n.l2.service.pattern.ICountryFlagService;
import com.vmware.vip.api.rest.*;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

@RestController("v2-FormattingCountryFlagAPI")
public class FormattingCountryFlagAPI {

    @Autowired
    private ICountryFlagService countryFlagService;
    /**
     * Get country flag with region and level parameter.
     */
    @ApiOperation(value = APIOperation.FORMAT_PATTERN_COUNTRY_FLAG_VALUE, notes = APIOperation.FORMAT_PATTERN_COUNTRY_FLAG_NOTES)
    @GetMapping(value = APIV2.FORMAT_PATTERN_COUNTRY_FLAG_GET)
    @ResponseStatus(HttpStatus.OK)
    public void getCountryFlagWithRegion(
            @ApiParam(name = APIParamName.REGION, required = true, value = APIParamValue.REGION) @RequestParam(value = APIParamName.REGION, required = true) String region,
            @ApiParam(name = APIParamName.SCALE, required = false, value = APIParamValue.COUNTRY_FLAG_SCALE) @RequestParam(value = APIParamName.SCALE, required = false) Integer scale,
            HttpServletResponse resp) throws Exception{
        int s = scale == null ? 1 : scale.intValue();

        resp.setContentType("image/svg+xml");
        WritableByteChannel writeChannel = Channels.newChannel(resp.getOutputStream());
        try (FileChannel fileChannel = this.countryFlagService.getCountryFlagChannel(region, s)){
            fileChannel.transferTo(0, fileChannel.size(), writeChannel);
        }

    }

}
