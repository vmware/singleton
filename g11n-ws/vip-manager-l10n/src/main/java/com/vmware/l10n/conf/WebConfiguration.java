/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import com.vmware.l10n.source.dao.AllowListDao;
import com.vmware.vip.api.rest.l10n.L10nI18nAPI;

/**
 * Web Configuration
 */
@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {
	private static Logger logger = LoggerFactory.getLogger(WebConfiguration.class);

	@Value("${csp.api.auth.enable:false}")
	private String cspAuthFlag;
	
	@Value("${source.collect.request.max-size}")
	private Integer sourceCollectReqSize;

	@Value("${config.client.requestIds:}")
	private String requestIdsStr;

	@Autowired
	private CspValidateService cspValidateService;

	@Autowired
	private AllowListDao allowlistDao;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// CSP authentication
		if (cspAuthFlag.equalsIgnoreCase("true")) {
			logger.info("add enable CSP authentication interceptor");
			registry.addInterceptor(new CspAuthInterceptor(cspValidateService))
			.addPathPatterns(L10nI18nAPI.BASE_COLLECT_SOURCE_PATH + "/api/v2/translation/**", L10nI18nAPI.BASE_COLLECT_SOURCE_PATH + "/api/v1/translation/**");
		}
		logger.info("add source collection validation interceptor");
		registry.addInterceptor(new CollectSourceValidationInterceptor(allowlistDao.getAllowList(), requestIdsStr))
		.addPathPatterns(L10nI18nAPI.BASE_COLLECT_SOURCE_PATH + "/api/v2/translation/**", L10nI18nAPI.BASE_COLLECT_SOURCE_PATH + "/api/v1/translation/**");
		registry.addInterceptor(new CollectSourceReqBodyInterceptor(this.sourceCollectReqSize))
		.addPathPatterns(L10nI18nAPI.BASE_COLLECT_SOURCE_PATH + "/api/v2/translation/products/**");
	
	}
	
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		UrlPathHelper urlPathHelper = new UrlPathHelper();
		urlPathHelper.setUrlDecode(false);
		urlPathHelper.setAlwaysUseFullPath(true);
		configurer.setUseSuffixPatternMatch(false);
		configurer.setUseRegisteredSuffixPatternMatch(true);
		configurer.setUrlPathHelper(urlPathHelper);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*");
	}

}
