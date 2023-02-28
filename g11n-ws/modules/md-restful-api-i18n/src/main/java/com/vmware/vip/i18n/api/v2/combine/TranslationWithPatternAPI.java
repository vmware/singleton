/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.combine;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.i18n.dto.TranslationWithPatternDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.i18n.api.base.TranslationWithPatternAction;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@RestController("v2-TranslationWithPattern")
public class TranslationWithPatternAPI extends TranslationWithPatternAction {

   /**
    * This is use to get the combine pattern and translation
    *
    */
   @ApiIgnore
   @ApiOperation(value = APIOperation.TRANSLATION_WITH_PATTERN_VALUE, notes = APIOperation.TRANSLATION_WITH_PATTERN_NOTES)
   @Deprecated
   @RequestMapping(value = APIV2.TRANSLATION_WITH_PATTERN, method = RequestMethod.POST, produces = {
         API.API_CHARSET })
   @ResponseStatus(HttpStatus.OK)
   public APIResponseDTO getRranslationWithPattern(
         @RequestBody TranslationWithPatternDTO data) throws Exception {
      return super.getTransPattern(data);
   }
   
   
   @ApiOperation(value = APIOperation.TRANSLATION_WITH_PATTERN_VALUE, notes = APIOperation.TRANSLATION_WITH_PATTERN_NOTES)
   @RequestMapping(value = APIV2.TRANSLATION_WITH_PATTERN, method = RequestMethod.GET, produces = {
         API.API_CHARSET })
   @ResponseStatus(HttpStatus.OK)
  public APIResponseDTO getRranslationWithPattern(
          @ApiParam(name = APIParamName.COMBINE, required = true, value = APIParamValue.COMBINE) @RequestParam(value = APIParamName.COMBINE) int combine,
          @ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @RequestParam(APIParamName.PRODUCT_NAME) String productName,
          @ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION) String version,
          @ApiParam(name = APIParamName.COMPONENTS, required = true, value = APIParamValue.COMPONENTS) @RequestParam(value = APIParamName.COMPONENTS) String components,
          @ApiParam(name = APIParamName.LANGUAGE, required = true, value = APIParamValue.LANGUAGE) @RequestParam(value = APIParamName.LANGUAGE) String language,
          @ApiParam(name = APIParamName.SCOPE, required = true, value = APIParamValue.SCOPE) @RequestParam(value = APIParamName.SCOPE) String scope,
          @ApiParam(name = APIParamName.REGION, required=false, value = APIParamValue.REGION) @RequestParam(value = APIParamName.REGION, required=false, defaultValue="") String region,
          @ApiParam(name = APIParamName.PSEUDO, required=false, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
          @ApiParam(name = APIParamName.SCOPE_FILTER, required = false, value = APIParamValue.SCOPE_FILTER) @RequestParam(value = APIParamName.SCOPE_FILTER, required = false) String scopeFilter,
          HttpServletRequest req)  throws Exception  {
      
       return super.getTransPattern(combine, productName, version, components, language, scope, region, pseudo, scopeFilter);
   }

}
