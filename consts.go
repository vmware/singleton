/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

// api url
const (
	apiRoot        = "/i18n/api/v2/translation"
	aipProductRoot = apiRoot + "/products/{" + productNameConst + "}/versions/{" + versionConst + "}"

	// product-based
	productTranslationGetConst   = aipProductRoot
	productLocaleListGetConst    = aipProductRoot + "/localelist"
	productComponentListGetConst = aipProductRoot + "/componentlist"
)

// api param name
const (
	productNameConst = "productname"
	versionConst     = "version"
	componentsConst  = "components"
	localesConst     = "locales"
)

const (
	httpHeaderCacheControl = "Cache-Control"
	httpHeaderETag         = "ETag"
	httpHeaderIfNoneMatch  = "If-None-Match"
)

const (
	cacheDefaultExpires = 864000 //seconds
)
