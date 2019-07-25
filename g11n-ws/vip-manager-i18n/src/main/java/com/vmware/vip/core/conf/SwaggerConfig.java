/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.core.login.VipAuthConfig;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This java class mainly is to integrate the swagger-UI into spring boot,
 * provide a way to access vIP-server API from web UI for test or else.
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(value = "swagger-ui.enable")
public class SwaggerConfig {

    /**
     * The date of build, it's recorded in 'application.properties'
     * and displayed on swagger-UI.
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
	   public Docket serviceloginApi(){  
	       
	        	return new Docket(DocumentationType.SWAGGER_2)
	        			.groupName("Authorization")  
	        			.select()  
	        			.apis(RequestHandlerSelectors.basePackage("com.vmware.vip.core.login.controller")) 
	        			.paths(PathSelectors.any())  
	        			.build()  
	        			.apiInfo(apiInfo("interface test doc")); 
	        
	    }  
    
    
    @Bean
    public Docket createRestApi2() {
    	if(authConfig.getAuthSwitch().equalsIgnoreCase("true")) {
    		return new Docket(DocumentationType.SWAGGER_2)
    				.groupName(APIV2.V).apiInfo(apiInfo(APIV2.V)).select()
    				.apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v2"))
    				.paths(PathSelectors.any()).build().globalOperationParameters(createParameter());
    	}else {
    		
    		return new Docket(DocumentationType.SWAGGER_2)
    				.groupName(APIV2.V).apiInfo(apiInfo(APIV2.V)).select()
    				.apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v2"))
    				.paths(PathSelectors.any()).build();
    	}
    }
    
    @Bean
    public Docket createRestApi1() {
	if(authConfig.getAuthSwitch().equalsIgnoreCase("true")) {
		  return new Docket(DocumentationType.SWAGGER_2)
				     .groupName(APIV1.V).apiInfo(apiInfo(APIV1.V)).select()
	                .apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v1"))
	                .paths(PathSelectors.any()).build().globalOperationParameters(createParameter());
    	}else {
        return new Docket(DocumentationType.SWAGGER_2)
        		.groupName(APIV1.V).apiInfo(apiInfo(APIV1.V)).select()
                .apis(RequestHandlerSelectors.basePackage("com.vmware.vip.i18n.api.v1"))
                .paths(PathSelectors.any()).build();
    	}
    }



    
    
    private List<Parameter> createParameter() {
    	ParameterBuilder tokenPar = new ParameterBuilder();  
    	List<Parameter> pars = new ArrayList<Parameter>();  
    	tokenPar.name("authorization").description("authorization code").modelRef(new ModelRef("string")).parameterType("header").required(false).build();  
    	
    	
    	pars.add(tokenPar.build()); 
    	
    	return pars;
    }
    
    
    
    
    
    
    /**
     * Build API information, include build number, service url, build date, changeset and so on.
     */
    private ApiInfo apiInfo(String version) {
        return new ApiInfoBuilder().title("VIP REST APIs")
                .contact(new Contact("VMWare G11n Team", null, null)).version(version + ", build number:"
                        + buildNumber + ", build date:" + buildDate + ", branch:" + branch)
                .build();
    }


	public VipAuthConfig getAuthConfig() {
		return authConfig;
	}

}
