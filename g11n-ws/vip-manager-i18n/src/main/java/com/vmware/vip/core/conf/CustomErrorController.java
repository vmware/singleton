/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @RequestMapping(value = ERROR_PATH)
    public String error() {
        return "{\n" +
                "    \"code\": 404,\n" +
                "    \"message\": \"Not Found\"\n" +
                "  }";
    }

    public String getErrorPath() {
        return ERROR_PATH;
    }
}
