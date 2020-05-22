/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import com.vmware.vip.api.rest.l10n.L10nI18nAPI;
import com.vmware.vip.common.constants.ConstantsChar;

/**
 * Web Configuration
 */
@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

	@Value("${source.collect.locales:en}")
	private String collectLocales;
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CollectSourceValidationInterceptor(parseLocales(this.collectLocales))).addPathPatterns(L10nI18nAPI.BASE_COLLECT_SOURCE_PATH + "/api/**");
	}
	
	private List<String> parseLocales(String localesString){
		String[] locales = localesString.split(ConstantsChar.COMMA);
		List<String> localeList = new ArrayList<>();
		for(int i=0; i<locales.length; i++) {
			localeList.add(locales[i].trim());
		}
		return localeList;
	}
}
