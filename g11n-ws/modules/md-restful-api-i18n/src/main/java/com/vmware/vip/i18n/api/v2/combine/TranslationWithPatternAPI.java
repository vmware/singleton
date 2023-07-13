/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.combine;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

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


@RestController("v2-TranslationWithPattern")
public class TranslationWithPatternAPI extends TranslationWithPatternAction {

   /**
    * This is use to get the combine pattern and translation
    *
    */
   @Hidden
   @Operation(summary = APIOperation.TRANSLATION_WITH_PATTERN_VALUE, description = APIOperation.TRANSLATION_WITH_PATTERN_NOTES)
   @Deprecated
   @RequestMapping(value = APIV2.TRANSLATION_WITH_PATTERN, method = RequestMethod.POST, produces = {
         API.API_CHARSET })
   @ResponseStatus(HttpStatus.OK)
   public APIResponseDTO getRranslationWithPattern(
         @RequestBody TranslationWithPatternDTO data) throws Exception {
      return super.getTransPattern(data);
   }
   
   
   @Operation(summary = APIOperation.TRANSLATION_WITH_PATTERN_VALUE, description = APIOperation.TRANSLATION_WITH_PATTERN_NOTES)
   @RequestMapping(value = APIV2.TRANSLATION_WITH_PATTERN, method = RequestMethod.GET, produces = {
         API.API_CHARSET })
   @ResponseStatus(HttpStatus.OK)
  public APIResponseDTO getRranslationWithPattern(
          @Parameter(name = APIParamName.COMBINE, required = true, description = APIParamValue.COMBINE) @RequestParam(value = APIParamName.COMBINE) int combine,
          @Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @RequestParam(APIParamName.PRODUCT_NAME) String productName,
          @Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION) String version,
          @Parameter(name = APIParamName.COMPONENTS, required = true, description = APIParamValue.COMPONENTS) @RequestParam(value = APIParamName.COMPONENTS) String components,
          @Parameter(name = APIParamName.LANGUAGE, required = true, description = APIParamValue.LANGUAGE) @RequestParam(value = APIParamName.LANGUAGE) String language,
          @Parameter(name = APIParamName.SCOPE, required = true, description = APIParamValue.SCOPE) @RequestParam(value = APIParamName.SCOPE) String scope,
          @Parameter(name = APIParamName.REGION, required=false, description = APIParamValue.REGION) @RequestParam(value = APIParamName.REGION, required=false, defaultValue="") String region,
          @Parameter(name = APIParamName.PSEUDO, required=false, description = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
          @Parameter(name = APIParamName.SCOPE_FILTER, required = false, description = APIParamValue.SCOPE_FILTER) @RequestParam(value = APIParamName.SCOPE_FILTER, required = false) String scopeFilter,
          HttpServletRequest req)  throws Exception  {
      
       return super.getTransPattern(combine, productName, version, components, language, scope, region, pseudo, scopeFilter);
   }

}
