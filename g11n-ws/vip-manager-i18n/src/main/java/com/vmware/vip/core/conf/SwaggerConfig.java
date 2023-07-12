/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import com.vmware.vip.core.login.VipAuthConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

        return builder.build();
    }

    @Bean
    public GroupedOpenApi singletV2Api() {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder().group("v2")
                .packagesToScan("com.vmware.vip.i18n.api.v2");
        builder.addOpenApiCustomizer(openApi -> openApi.info(generateInfo("v2")));
        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(value = "vipservice.authority.enable")
    public GroupedOpenApi serviceloginApi() {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder().group("authentication")
                .packagesToScan("com.vmware.vip.core.login.controller");
         logger.info("init authority swagger ui");
        return builder.build();
    }


   // @Bean
    public OpenAPI singletonOpenAPI() {
        String version = "build number:" + buildNumber + ", build date:" + buildDate + ", branch:" + branch;
        Contact contact = new Contact();
        contact.setName("VMWare G11n Team");
        Info info = new Info().title("Singleton REST APIs")
                .description("Singleton manager APIs")
                .version(version)
                .contact(contact)
                .license(new License().name("Apache 2.0").url("http://springdoc.org"));

        OpenAPI openAPI = new OpenAPI();
        //openAPI.addSecurityItem()
        openAPI.setInfo(info);
        if (StringUtils.isNotEmpty(hostUrl)) {
            logger.info("new host url: {}", hostUrl);
            Server server = new Server();
            server.setUrl(hostUrl);
            server.setDescription(hostDesp);
            openAPI.addServersItem(server);
        }
        openAPI.setComponents(generateComps());
        return openAPI;
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


    private Components generateComps() {
        Components comps = new Components();
        Parameter paramToken = new Parameter()
                .name("token")
                .required(true)
                .in("header")
                .description("Authorization token")
                .schema(new StringSchema());
		Parameter appIdParam = new Parameter()
				.name("appId")
				.required(true)
				.in("header")
				.description("the app Id")
				.schema(new StringSchema());

        comps.addParameters("auth", paramToken);
		comps.addParameters("appId", appIdParam);
        return comps;
    }

    /**
     @Bean public Docket createRestApi2() {
     if (authConfig.getAuthSwitch().equalsIgnoreCase("true")) {
     return new Docket(DocumentationType.OAS_30).groupName(APIV2.V).apiInfo(apiInfo(APIV2.V)).select()
     .apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v2")).paths(PathSelectors.any())
     .build().globalRequestParameters(createGlobalParameter());
     } else {

     return new Docket(DocumentationType.OAS_30).groupName(APIV2.V).apiInfo(apiInfo(APIV2.V)).select()
     .apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v2")).paths(PathSelectors.any())
     .build();
     }
     }

     @Bean public Docket createRestApi1() {
     if (authConfig.getAuthSwitch().equalsIgnoreCase("true")) {
     return new Docket(DocumentationType.OAS_30).groupName(APIV1.V).apiInfo(apiInfo(APIV1.V)).select()
     .apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v1")).paths(PathSelectors.any())
     .build().globalRequestParameters(createGlobalParameter());
     } else {
     return new Docket(DocumentationType.OAS_30).groupName(APIV1.V).apiInfo(apiInfo(APIV1.V)).select()
     .apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v1")).paths(PathSelectors.any())
     .build();
     }
     }



     private List<RequestParameter> createGlobalParameter(){
     List<RequestParameter> pars = new ArrayList<RequestParameter>();
     RequestParameterBuilder rpbToken = new RequestParameterBuilder();
     rpbToken.name("token").description("token code").in(ParameterType.HEADER)
     .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING))).required(false);

     RequestParameterBuilder rpbAppId = new RequestParameterBuilder();
     rpbAppId.name("appId").description("the app Id").in(ParameterType.HEADER)
     .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING))).required(false);

     pars.add(rpbToken.build());
     pars.add(rpbAppId.build());

     return pars;
     }



     /**
      * Build API information, include build number, service url, build date,
      * changeset and so on.
     */
/**
 private ApiInfo apiInfo(String version) {
 return new ApiInfoBuilder().title("VIP REST APIs").contact(new Contact("VMWare G11n Team", null, null))
 .version(version + ", build number:" + buildNumber + ", build date:" + buildDate + ", branch:" + branch)
 .build();
 }

 public VipAuthConfig getAuthConfig() {
 return authConfig;
 }

 @Bean public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
 ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
 EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
 WebEndpointProperties webEndpointProperties, Environment environment) {
 List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
 Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
 allEndpoints.addAll(webEndpoints);
 allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
 allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
 String basePath = webEndpointProperties.getBasePath();
 EndpointMapping endpointMapping = new EndpointMapping(basePath);
 boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment,
 basePath);
 return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
 corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath),
 shouldRegisterLinksMapping);
 }

 private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment,
 String basePath) {
 return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath)
 || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
 }




 private static final String DEFAULT_PATH = "/i18n/api/doc";

 @Controller
 @ApiIgnore
 @RequestMapping(DEFAULT_PATH)
 @ConditionalOnProperty(value = "springfox.documentation.swagger-ui.enabled")
 public static class SwaggerResourceController {
 @Value("${springfox.documentation.swagger-ui.base-url:}") private String swaggerBaseUrl;

 @RequestMapping("/swagger-ui.html") public void forwardNewURL(HttpServletRequest req, HttpServletResponse resp) throws Exception {
 String redirectPath = fixup(swaggerBaseUrl) + "/swagger-ui/index.html";
 resp.sendRedirect(redirectPath);
 }

 private String fixup(String swaggerBaseUrl) {
 return StringUtils.trimTrailingCharacter(nullToEmpty(swaggerBaseUrl), '/');
 }

 }
 **/
}
