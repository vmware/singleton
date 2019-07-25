/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.base;
/**
 * 
 *
 * @author shihu
 *
 */
public interface PropertyContantKeys {
	//HTTPS_PROTOCOL
	public static final String HTTPS_PROTOCOL = "https://";
	public static final int HTTP_CONNECT_TIMEOUT = 5000;
	public static final int HTTP_READ_TIMEOUT = 5000;
	public static final String HTTP_MOTHED_GET = "GET";
	public static final String HTTP_MOTHED_POST = "POST";
	public static final String HTTP_MOTHED_PUT = "PUT";
	public static final String HTTP_MOTHED_DELETE = "DELETE";
	
	
	//source file type
	public static final String DEFAULT_COMPONENT = "default";
	public static final String DEFAULT_MSG_FILE_NAME = "messages";
	public static final String DEFAULT_SOURCE_ROOT = "i18n";
	public static final String DEFAULT_SOURCE_TYPE = ".json";
	
	//url
    public static final String L10N_API_ROOT = "/l10n/api/";
    public static final String TYPE_SOURCE = "source";
	public static final String I18n_Source_Collect_Url="/i18n/api/v2/translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}/keys/{key}";
	
	
	
	

}
