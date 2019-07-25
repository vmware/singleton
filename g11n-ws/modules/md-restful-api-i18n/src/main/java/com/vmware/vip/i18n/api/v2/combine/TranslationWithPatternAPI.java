/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.combine;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.i18n.dto.TranslationWithPatternDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.i18n.api.base.TranslationWithPatternAction;

import io.swagger.annotations.ApiOperation;

@RestController("v2-TranslationWithPattern")
public class TranslationWithPatternAPI extends TranslationWithPatternAction {

   /**
    * This is use to get the combine pattern and translation
    *
    */
   @ApiOperation(value = APIOperation.TRANSLATION_WITH_PATTERN_VALUE, notes = APIOperation.TRANSLATION_WITH_PATTERN_NOTES)
   @RequestMapping(value = APIV2.TRANSLATION_WITH_PATTERN, method = RequestMethod.POST, produces = {
         API.API_CHARSET })
   @ResponseStatus(HttpStatus.OK)
   public APIResponseDTO getRranslationWithPattern(
         @RequestBody TranslationWithPatternDTO data) throws Exception {
      return super.getTransPattern(data);
   }

}
