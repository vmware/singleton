/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vmware.vip.api.domain.DbApiUrl;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 
 *
 * @author shihu
 *
 */
@Configuration
public class Swagger2Conf {
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName(DbApiUrl.API_ROOT).apiInfo(apiInfo(DbApiUrl.API_ROOT))
				.select().apis(RequestHandlerSelectors.basePackage("com.vmware.vip.api.rest"))
				.paths(PathSelectors.any()).build();
	}

	private ApiInfo apiInfo(String baseDir) {
		return new ApiInfoBuilder().title("VIP DB Restful API").description("VIP DB Restful API document")
				.contact(new Contact("VMWare G11n Team", null, null)).version(baseDir + "1.0").build();
	}
}
