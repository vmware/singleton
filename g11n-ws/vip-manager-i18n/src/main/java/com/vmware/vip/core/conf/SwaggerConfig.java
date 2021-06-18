/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.core.login.VipAuthConfig;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiResourceController;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.web.Swagger2Controller;

/**
 * This java class mainly is to integrate the swagger-UI into spring boot,
 * provide a way to access vIP-server API from web UI for test or else.
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(value = "swagger-ui.enable")
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

		return new Docket(DocumentationType.SWAGGER_2).groupName("authentication").select()
				.apis(RequestHandlerSelectors.basePackage("com.vmware.vip.core.login.controller"))
				.paths(PathSelectors.any()).build().apiInfo(apiInfo("interface test doc"));

	}

	@Bean
	public Docket createRestApi2() {
		if (authConfig.getAuthSwitch().equalsIgnoreCase("true")) {
			return new Docket(DocumentationType.SWAGGER_2).groupName(APIV2.V).apiInfo(apiInfo(APIV2.V)).select()
					.apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v2")).paths(PathSelectors.any())
					.build().globalOperationParameters(createParameter());
		} else {

			return new Docket(DocumentationType.SWAGGER_2).groupName(APIV2.V).apiInfo(apiInfo(APIV2.V)).select()
					.apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v2")).paths(PathSelectors.any())
					.build();
		}
	}

	@Bean
	public Docket createRestApi1() {
		if (authConfig.getAuthSwitch().equalsIgnoreCase("true")) {
			return new Docket(DocumentationType.SWAGGER_2).groupName(APIV1.V).apiInfo(apiInfo(APIV1.V)).select()
					.apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v1")).paths(PathSelectors.any())
					.build().globalOperationParameters(createParameter());
		} else {
			return new Docket(DocumentationType.SWAGGER_2).groupName(APIV1.V).apiInfo(apiInfo(APIV1.V)).select()
					.apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v1")).paths(PathSelectors.any())
					.build();
		}
	}

	private List<Parameter> createParameter() {
		List<Parameter> pars = new ArrayList<Parameter>();
		ParameterBuilder token = new ParameterBuilder();
		token.name("token").description("token code").modelRef(new ModelRef("string"))
				.parameterType("header").required(false).build();
		
		ParameterBuilder appId = new ParameterBuilder();
		appId.name("appId").description("the app Id").modelRef(new ModelRef("string"))
				.parameterType("header").required(false).build();

		pars.add(token.build());
		pars.add(appId.build());

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

	private static final String DEFAULT_PATH = "/i18n/api/doc";

	/**
	 * add the swaggerUI static mapping configuration
	 */
	@Bean
	@ConditionalOnProperty(value = "swagger-ui.enable")
	public SimpleUrlHandlerMapping swaggerUrlHandlerMapping(ServletContext servletContext,
			@Value("${swagger.mapping.order:10}") int order) throws Exception {
		SimpleUrlHandlerMapping urlHandlerMapping = new SimpleUrlHandlerMapping();
		Map<String, ResourceHttpRequestHandler> urlMap = new HashMap<String, ResourceHttpRequestHandler>();
		{
			PathResourceResolver pathResourceResolver = new PathResourceResolver();
			pathResourceResolver.setAllowedLocations(new ClassPathResource("META-INF/resources/webjars/"));
			pathResourceResolver.setUrlPathHelper(new UrlPathHelper());

			ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
			resourceHttpRequestHandler
					.setLocations(Arrays.asList(new ClassPathResource("META-INF/resources/webjars/")));
			resourceHttpRequestHandler.setResourceResolvers(Arrays.asList(pathResourceResolver));
			resourceHttpRequestHandler.setServletContext(servletContext);
			resourceHttpRequestHandler.afterPropertiesSet();
			urlMap.put(DEFAULT_PATH + "/webjars/**", resourceHttpRequestHandler);
		}
		{
			PathResourceResolver pathResourceResolver = new PathResourceResolver();
			pathResourceResolver.setAllowedLocations(new ClassPathResource("META-INF/resources/"));
			pathResourceResolver.setUrlPathHelper(new UrlPathHelper());

			ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
			resourceHttpRequestHandler.setLocations(Arrays.asList(new ClassPathResource("META-INF/resources/")));
			resourceHttpRequestHandler.setResourceResolvers(Arrays.asList(pathResourceResolver));
			resourceHttpRequestHandler.setServletContext(servletContext);
			resourceHttpRequestHandler.afterPropertiesSet();
			urlMap.put(DEFAULT_PATH + "/**", resourceHttpRequestHandler);
		}
		urlHandlerMapping.setUrlMap(urlMap);
		urlHandlerMapping.setOrder(order);
		return urlHandlerMapping;
	}
	
  /**
   * mapping the swaggerUI JSON resource
   */
	@Controller
	@ApiIgnore
	@RequestMapping(DEFAULT_PATH)
	@ConditionalOnProperty(value = "swagger-ui.enable")
	public static class SwaggerResourceController implements InitializingBean {

		@Autowired(required=false)
		private ApiResourceController apiResourceController;

		@Autowired(required=false)
		private Environment environment;

		@Autowired(required=false)
		private DocumentationCache documentationCache;

		@Autowired(required=false)
		private ServiceModelToSwagger2Mapper mapper;

		@Autowired(required=false)
		private JsonSerializer jsonSerializer;

		private Swagger2Controller swagger2Controller;

		@Override
		public void afterPropertiesSet() {
			swagger2Controller = new Swagger2Controller(environment, documentationCache, mapper, jsonSerializer);
		}
		
		@RequestMapping("/swagger-resources/configuration/security")
		@ResponseBody
		public ResponseEntity<SecurityConfiguration> securityConfiguration() {
			return apiResourceController.securityConfiguration();
		}

		@RequestMapping("/swagger-resources/configuration/ui")
		@ResponseBody
		public ResponseEntity<UiConfiguration> uiConfiguration() {
			return apiResourceController.uiConfiguration();
		}

		@RequestMapping("/swagger-resources")
		@ResponseBody
		public ResponseEntity<List<SwaggerResource>> swaggerResources() {
			return apiResourceController.swaggerResources();
		}

		@RequestMapping(value = "/v2/api-docs", method = RequestMethod.GET, produces = { "application/json",
				"application/hal+json" })
		@ResponseBody
		public ResponseEntity<Json> getDocumentation(
				@RequestParam(value = "group", required = false) String swaggerGroup,
				HttpServletRequest servletRequest) {
			return swagger2Controller.getDocumentation(swaggerGroup, servletRequest);
		}
	}

}
