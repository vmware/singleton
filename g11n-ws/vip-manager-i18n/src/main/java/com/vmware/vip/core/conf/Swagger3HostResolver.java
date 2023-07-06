/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vip.core.conf;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Component
public class Swagger3HostResolver implements WebMvcOpenApiTransformationFilter {
    private static Logger logger = LoggerFactory.getLogger(Swagger3HostResolver.class);

    @Value("${springfox.documentation.swagger-ui.server.url:}")
    private String hostUrl;

    @Value("${springfox.documentation.swagger-ui.server.description:Inferred Url}")
    private String hostDesp;

    @Override
    public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
        OpenAPI swagger = context.getSpecification();
        if (StringUtils.isNotEmpty(hostUrl)){
            List<Server> servers = swagger.getServers();
            for (Server server : servers){
                logger.info("------old server:{} new server {} -------", server.getUrl(), hostUrl);
                server.setUrl(hostUrl);
                server.setDescription(hostDesp);
            }
        }
        return swagger;
    }


    @Override
    public boolean supports(DocumentationType documentationType) {
        return documentationType.equals(DocumentationType.OAS_30);
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

}
