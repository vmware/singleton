/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;

@RestController
public class BaseAPI {

    @ModelAttribute
    public void validateToken(HttpServletRequest request,
            @RequestParam(value = APIParamName.PRODUCT_NAME) String productName,
            @RequestParam(value = APIParamName.VERSION) String version) {
        APIResponseDTO respDTO = new APIResponseDTO();
        respDTO.setSignature((String) request.getSession().getAttribute(ConstantsKeys.SIGNATURE));
        respDTO.setResponse(APIResponseStatus.OK);

        if (StringUtils.isEmpty(request.getParameter(ConstantsKeys.TOKEN).trim()) || StringUtils.isEmpty(productName.trim())
                || StringUtils.isEmpty(version.trim())) {
            respDTO.setResponse(APIResponseStatus.INVALID_TOKEN);
        } else {
            String[] nameVersionArr = request.getParameter(ConstantsKeys.TOKEN).split(EncryptUtil.SHA512(ConstantsKeys.VIP.getBytes()));
            if (nameVersionArr.length == 2) {
                if (!EncryptUtil.SHA512((productName + ConstantsChar.QUESTIONMARK + version).getBytes()).equals(nameVersionArr[0])) {
                    respDTO.setResponse(APIResponseStatus.INVALID_TOKEN);
                }
            } else {
                respDTO.setResponse(APIResponseStatus.INVALID_TOKEN);
            }
        }
        request.setAttribute(ConstantsKeys.VALIDATE_TOKEN_RESULT, respDTO);
    }

}
