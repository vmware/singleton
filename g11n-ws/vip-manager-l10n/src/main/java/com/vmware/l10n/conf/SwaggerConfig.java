/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vmware.vip.api.rest.APIV2;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This java class mainly is to integrate the swagger-UI into spring boot,
 * provide a way to access vIP-server API from web UI for test or else.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket createRestApi2() {

			return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo(APIV2.V)).select()
					.apis(RequestHandlerSelectors.basePackage("com.vmware.l10n.source.controller")).paths(PathSelectors.any())
					.build();
		
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




}
