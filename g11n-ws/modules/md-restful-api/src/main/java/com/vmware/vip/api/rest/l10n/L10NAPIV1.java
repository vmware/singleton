/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest.l10n;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIParamName;

public class L10NAPIV1 {
    public static final String V = "v1";
    
    public static final String API_L10N = API.L10N_API_ROOT+ V + "/" + API.TYPE_SOURCE;
    
    public static final String UPDATE_TRANSLATION_L10N = API_L10N + "/products/{"+APIParamName.PRODUCT_NAME+"}/versions/{" + APIParamName.VERSION2 + "}";  
    public static final String CREATE_SOURCE_GET       = API_L10N + "/products/{"+APIParamName.PRODUCT_NAME+"}/components/{"+APIParamName.COMPONENT+"}/keys/{"+APIParamName.KEY2+"}";
    public static final String CREATE_SOURCE_POST      = API_L10N + "/products/{"+APIParamName.PRODUCT_NAME+"}/components/{"+APIParamName.COMPONENT+"}/keys/{"+APIParamName.KEY2+"}";
    public static final String GRM_SEND_SOURCE = "/api/v1/l10n/{"+APIParamName.PRODUCT_NAME+"}/{"+APIParamName.VERSION+"}/{"+APIParamName.COMPONENT+"}?locale={"+APIParamName.LOCALE+"}";
    public static final String SYNC_TRANSLATION_GIT_L10N      = API_L10N + "/products/sync/translation/git";
    public static final String SYNC_TRANSLATION_CACHE_L10N      = API_L10N + "/products/sync/translation/cache";
}
