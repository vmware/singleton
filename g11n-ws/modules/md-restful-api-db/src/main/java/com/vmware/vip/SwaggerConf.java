/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vmware.vip.api.domain.DbApiUrl;


/**
 * 
 *
 * @author shihu
 *
 */
@Configuration
public class SwaggerConf {

	@Bean
	public GroupedOpenApi singletV2Api() {
		GroupedOpenApi.Builder builder = GroupedOpenApi.builder().group(DbApiUrl.API_ROOT)
				.packagesToScan("com.vmware.vip.api.rest");
		builder.addOpenApiCustomizer(openApi -> {
			openApi.info(generateInfo(DbApiUrl.API_ROOT + "1.0"));

		});

		return builder.build();
	}


	private  Info generateInfo(String version){
		Contact contact = new Contact();
		contact.setName("VMWare G11n Team");
		Info info = new Info().title("VIP DB REST APIs")
				.description("VIP DB Restful API document")
				.version(version)
				.contact(contact)
				.license(new License().name("Apache 2.0").url("http://springdoc.org"));
		return info;
	}
}
