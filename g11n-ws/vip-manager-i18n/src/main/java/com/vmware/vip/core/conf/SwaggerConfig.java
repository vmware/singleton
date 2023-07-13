/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import com.vmware.vip.core.login.VipAuthConfig;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.List;


/**
 * This java class mainly is to integrate the swagger-UI into spring boot,
 * provide a way to access vIP-server API from web UI for test or else.
 */

@Configuration
public class SwaggerConfig {

    private static Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    /**
     * The date of build, it's recorded in 'application.properties' and displayed on
     * swagger-UI.
     */
    @Value("${build.number.builddate}")
    private String buildDate;

    /**
     * The current version of vip-server
     */
    @Value("${build.number.branch}")
    private String branch;

    /**
     * The build number, it's loaded from jenkins.
     */
    @Value("${build.number.buildnumber}")
    private String buildNumber;


    @Value("${springdoc.swagger-ui.server.url:}")
    private String hostUrl;

    @Value("${springdoc.swagger-ui.server.description:Inferred Url}")
    private String hostDesp;


    @Autowired
    private VipAuthConfig authConfig;


    @Bean
    public GroupedOpenApi singletV1Api() {

        GroupedOpenApi.Builder builder = GroupedOpenApi.builder().group("v1")
                .packagesToScan("com.vmware.vip.i18n.api.v1");
        builder.addOpenApiCustomizer(openApi -> {
            openApi.info(generateInfo("v1"));
            if (StringUtils.isNotEmpty(hostUrl)) {
                logger.info("new host url: {}", hostUrl);
                Server server = new Server();
                server.setUrl(hostUrl);
                server.setDescription(hostDesp);
                List<Server> list = new ArrayList<>();
                list.add(server);
                openApi.servers(list);
            }
        });
        if (authConfig.getAuthSwitch().equalsIgnoreCase("true")) {
            builder.addOperationCustomizer(customGlobalHeaders());
        }
        return builder.build();
    }

    @Bean
    public GroupedOpenApi singletV2Api() {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder().group("v2")
                .packagesToScan("com.vmware.vip.i18n.api.v2");
        builder.addOpenApiCustomizer(openApi -> {
            openApi.info(generateInfo("v2"));
            if (StringUtils.isNotEmpty(hostUrl)) {
                logger.info("new host url: {}", hostUrl);
                Server server = new Server();
                server.setUrl(hostUrl);
                server.setDescription(hostDesp);
                List<Server> list = new ArrayList<>();
                list.add(server);
                openApi.servers(list);
            }
        });
        if (authConfig.getAuthSwitch().equalsIgnoreCase("true")) {
            builder.addOperationCustomizer(customGlobalHeaders());
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(value = "vipservice.authority.enable")
    public GroupedOpenApi serviceloginApi() {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder().group("authentication")
                .packagesToScan("com.vmware.vip.core.login.controller");
         logger.info("init authority swagger ui");
        builder.addOpenApiCustomizer(openApi -> openApi.info(generateInfo("authentication")));
        return builder.build();
    }


    private  Info generateInfo(String versionStr){
        String version = versionStr + ",build number:" + buildNumber + ", build date:" + buildDate + ", branch:" + branch;
        Contact contact = new Contact();
        contact.setName("VMWare G11n Team");
        Info info = new Info().title("Singleton REST APIs")
                .description("Singleton manager APIs")
                .version(version)
                .contact(contact)
                .license(new License().name("Apache 2.0").url("http://springdoc.org"));

        return info;
    }



    private OperationCustomizer customGlobalHeaders() {

        return (Operation operation, HandlerMethod handlerMethod) -> {

            Parameter tokenParam = new Parameter()
                    .in(ParameterIn.HEADER.toString())
                    .schema(new StringSchema())
                    .name("token")
                    .description("token code")
                    .required(true);

            Parameter appIdParam = new Parameter()
                    .in(ParameterIn.HEADER.toString())
                    .schema(new StringSchema())
                    .name("appId")
                    .description("the app Id")
                    .required(true);

            operation.addParametersItem(tokenParam);
            operation.addParametersItem(appIdParam);

            return operation;
        };
    }

}
