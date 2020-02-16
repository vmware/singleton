/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

// api url
const (
	i18nApiroot          = "/i18n/api/v2"
	typeTranslationConst = "translation"
	apiTranslatoin       = i18nApiroot + "/" + typeTranslationConst

	// product-based
	productLocaleListGetConst    = apiTranslatoin + "/products/{" + productNameConst + "}/versions/{" + versionConst + "}/localelist"
	productComponentListGetConst = apiTranslatoin + "/products/{" + productNameConst + "}/versions/{" + versionConst + "}/componentlist"

	// component-based
	componentTranslationGetConst = apiTranslatoin + "/products/{" + productNameConst + "}/versions/{" + versionConst + "}/locales/{" + localeConst + "}/components/{" + componentConst + "}"
)

// api param name
const (
	productNameConst = "productname"
	versionConst     = "version"
	componentConst   = "component"
	localeConst      = "locale"
)
