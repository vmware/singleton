/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.core.Interceptor.APICacheControlInterceptor;
import com.vmware.vip.core.Interceptor.APICrossDomainInterceptor;
import com.vmware.vip.core.Interceptor.APISourceInterceptor;
import com.vmware.vip.core.Interceptor.APIValidationInterceptor;
import com.vmware.vip.core.auth.interceptor.AuthInterceptor;
import com.vmware.vip.core.auth.interceptor.VipAPIAuthInterceptor;
import com.vmware.vip.core.csp.service.TokenService;
import com.vmware.vip.core.login.VipAuthConfig;
import com.vmware.vip.core.messages.service.product.IProductService;

/**
 * Web Configuration
 */
@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {
	
	private static Logger logger = LoggerFactory.getLogger(WebConfiguration.class);
	/**
	 * source collection on-off,if true,the interceptor will collect new source
	 * otherwise not, it's loaded and init from "application.properties"
	 */
	@Value("${source.cache.flag}")
	private String sourceCacheFlag;

	/**
	 * l10n server url,it's loaded and init from "application.properties"
	 */
	@Value("${source.cache.server.url}")
	private String sourceCacheServerUrl;

	@Value("${csp.api.auth.enable}")
	private String cspAuthFlag;

	@Value("${vipservice.cross.domain.enable}")
	private String crossDomainFlag;

	@Value("${vipservice.cross.domain.alloworigin}")
	private String allowOrigin;

	@Value("${vipservice.cross.domain.allowmethods}")
	private String allowMethods;

	@Value("${vipservice.cross.domain.allowheaders}")
	private String allowHeaders;

	@Value("${vipservice.cross.domain.allowCredentials}")
	private String allowCredentials;

	@Value("${vipservice.cross.domain.maxage}")
	private String maxAge;
	
	@Value("${swagger-ui.enable}")
	private boolean swagger2enable;

	@Value("${cache-control.value:}")
	private String cacheControlValue;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private VipAPIAuthInterceptor apiAuthInter;

	@Autowired
	private VipAuthConfig authConfig;
	
	@Autowired
	private IProductService productService;

	/**
	 * Add ETag into response header for data cache
	 */
	@Bean
	public ShallowEtagHeaderFilter shallowETagHeaderFilter() {
		return new ShallowEtagHeaderFilter();
	}

	/**
	 * Add interceptors for security or source collection, the filter path will
	 * be configured here
	 *
	 * @param registry
	 *            The interceptor to add
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		/*
		 * registry.addInterceptor(new APISecurityInterceptor())
		 * .addPathPatterns(ConstantsKeys.TOKEN_INTERCEP_PATH)
		 * .excludePathPatterns("/i18n/api/v1/security/authentication");
		 */

		// Request Validation
		InterceptorRegistration apival = registry.addInterceptor(new APIValidationInterceptor(productService.getWhiteList())).addPathPatterns("/**").excludePathPatterns(API.I18N_API_ROOT+"doc/**");

		// authentication

		if (authConfig.getAuthSwitch().equalsIgnoreCase("true")) {
			logger.info("add enable authentication interceptor");
			apival.excludePathPatterns("/auth/**");
			registry.addInterceptor(apiAuthInter)
					
					.addPathPatterns(API.I18N_API_ROOT + APIV1.V + "/**")
					.addPathPatterns(API.I18N_API_ROOT + APIV2.V + "/**")
					.addPathPatterns(APIV1.COMPONENTS).excludePathPatterns(API.I18N_API_ROOT + APIV2.V+"/api-docs");

		}

		// CSP authentication
		if (cspAuthFlag.equalsIgnoreCase("true")) {
			logger.info("add enable CSP authentication interceptor");
			registry.addInterceptor(new AuthInterceptor(sourceCacheFlag, tokenService))
					.addPathPatterns(API.I18N_API_ROOT + APIV2.V + "/**");
		}
		// Source collection
		if (sourceCacheFlag.equalsIgnoreCase("true")) {
			logger.info("add enable Source collection interceptor");
			registry.addInterceptor(new APISourceInterceptor(sourceCacheServerUrl))
					.addPathPatterns(API.I18N_API_ROOT + APIV1.V + "/**")
					.addPathPatterns(API.I18N_API_ROOT + APIV2.V + "/**");
		}
		//cross domain
		if (crossDomainFlag.equalsIgnoreCase("true")) {
			logger.info("add enable cross domain interceptor");
			Set<String> allowSet = new HashSet<String>(Arrays.asList(allowOrigin.split(",")));
			registry.addInterceptor(new APICrossDomainInterceptor(allowSet, allowHeaders, allowMethods, allowCredentials, maxAge))
			.addPathPatterns(API.I18N_API_ROOT + APIV1.V + "/**")
			.addPathPatterns(API.I18N_API_ROOT + APIV2.V + "/**");
		}
		//cacheControl
		if (StringUtils.isNotEmpty(this.cacheControlValue)) {
			registry.addInterceptor(new APICacheControlInterceptor(this.cacheControlValue)).addPathPatterns(API.I18N_API_ROOT + APIV2.V + "/**");
		}
	}

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

	/**
	 * For swagger-ui static files(css,js) import
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		if(swagger2enable) {
			registry.addResourceHandler("/i18n/api/doc/webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/");	
		} else {
			registry.addResourceHandler("/swagger-ui.html")
			.addResourceLocations("classpath:/META-INF/swagger-ui.html");
		}
			
	}
	
	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

}
