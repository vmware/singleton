/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vmware.vip.api.rest.APIV2;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * This java class mainly is to integrate the swagger-UI into spring boot,
 * provide a way to access vIP-server API from web UI for test or else.
 */
@Configuration
@EnableOpenApi
public class SwaggerConfig {
	

	@Bean
	public Docket createRestApi2() {

			return new Docket(DocumentationType.OAS_30).apiInfo(apiInfo(APIV2.V)).select()
	        .apis(RequestHandlerSelectors.basePackage("com.vmware.l10n.source.controller"))
	        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
	        .paths(PathSelectors.any()).build();

		
	}


	/**
	 * Build API information, include build number, service url, build date,
	 * changeset and so on.
	 */
	private ApiInfo apiInfo(String version) {
		return new ApiInfoBuilder().title("L10n REST APIs").contact(new Contact("VMWare G11n Team", null, null))
				.version(version)
				.build();
	}

	
	
	@Controller
	@ApiIgnore
	@ConditionalOnProperty(value = "springfox.documentation.swagger-ui.enabled")
	public static class SwaggerResourceController  {

		 @RequestMapping ( "/swagger-ui.html" ) 
	     public  void forwardNewURL(HttpServletRequest req, HttpServletResponse resp) throws Exception{ 
			 resp.sendRedirect("/swagger-ui/index.html");
	     } 
	
	}

}
