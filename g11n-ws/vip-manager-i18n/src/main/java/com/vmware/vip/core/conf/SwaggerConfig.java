/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import static springfox.documentation.builders.BuilderDefaults.nullToEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.core.login.VipAuthConfig;

import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * This java class mainly is to integrate the swagger-UI into spring boot,
 * provide a way to access vIP-server API from web UI for test or else.
 */

@Configuration
@EnableOpenApi
public class SwaggerConfig {

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

	@Autowired
	private VipAuthConfig authConfig;

	@Bean
	@ConditionalOnProperty(value = "vipservice.authority.enable")
	public Docket serviceloginApi() {

		return new Docket(DocumentationType.OAS_30).groupName("authentication").select()
				.apis(RequestHandlerSelectors.basePackage("com.vmware.vip.core.login.controller"))
				.paths(PathSelectors.any()).build().apiInfo(apiInfo("interface test doc"));

	}

	@Bean
	public Docket createRestApi2() {
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

	@Bean
	public Docket createRestApi1() {
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
	private ApiInfo apiInfo(String version) {
		return new ApiInfoBuilder().title("VIP REST APIs").contact(new Contact("VMWare G11n Team", null, null))
				.version(version + ", build number:" + buildNumber + ", build date:" + buildDate + ", branch:" + branch)
				.build();
	}

	public VipAuthConfig getAuthConfig() {
		return authConfig;
	}

	@Bean
	public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
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
		@Value("${springfox.documentation.swagger-ui.base-url:}")
		private String swaggerBaseUrl;

		@RequestMapping("/swagger-ui.html")
		public void forwardNewURL(HttpServletRequest req, HttpServletResponse resp) throws Exception {
			String redirectPath = fixup(swaggerBaseUrl) + "/swagger-ui/index.html";
			resp.sendRedirect(redirectPath);
		}

		private String fixup(String swaggerBaseUrl) {
			return StringUtils.trimTrailingCharacter(nullToEmpty(swaggerBaseUrl), '/');
		}

	}
	 
	
}
