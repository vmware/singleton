/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import com.vmware.vip.api.rest.APIV2;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * This java class mainly is to integrate the swagger-UI into spring boot,
 * provide a way to access vIP-server API from web UI for test or else.
 */
@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI springL10nOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("L10n REST APIs")
						.description("Singleton L10n manager APIs")
						.version(APIV2.V)
						.license(new License().name("Apache 2.0").url("http://springdoc.org")))
				.externalDocs(new ExternalDocumentation());
	}



}
