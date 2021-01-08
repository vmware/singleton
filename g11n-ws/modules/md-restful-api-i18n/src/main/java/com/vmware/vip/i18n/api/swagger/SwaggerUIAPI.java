/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.swagger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.API;

@RestController
public class SwaggerUIAPI {

    @RequestMapping(value = "/i18n/api/doc/swagger-ui", method = RequestMethod.GET, produces = { API.API_CHARSET })
    public void swaggerUI(HttpServletRequest request, HttpServletResponse response) throws Exception{
        request.getRequestDispatcher("/swagger-ui.html").forward(request, response);
    }

}
