/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

// api url
const (
	apiRoot        = "i18n/api/v2/translation"
	aipProductRoot = apiRoot + "/products/%s/versions/%s"

	// product-based
	productTranslationGetConst   = aipProductRoot
	productLocaleListGetConst    = aipProductRoot + "/localelist"
	productComponentListGetConst = aipProductRoot + "/componentlist"
	bundleGetConst               = aipProductRoot + "/locales/%s/components/%s"
)

const (
	httpHeaderCacheControl = "Cache-Control"
	httpHeaderETag         = "ETag"
	httpHeaderIfNoneMatch  = "If-None-Match"
)

const (
	cacheDefaultExpires = 86400 // seconds
)
