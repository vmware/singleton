/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import "fmt"

const (
	ProductNonexistent    = "product '%s' doesn't exist"
	ReleaseNonexistent    = "%s/%s doesn't exist"
	FailToReadBundle      = "Fail to get translation for %s/%s/%s/%s"
	FailToGetBundleInfo   = "fail to get bundle info"
	WrongBundleContent    = "wrong data content in %v/%v/%v/%v"
	FailToStoreBundle     = "fail to store %v/%v/%v/%v"
	KeyNotFound           = "key '%s' isn't found"
	FailToReadFile        = "fail to read file: '%s'"
	ProductNotSupported   = "product '%v' isn't supported"
	ComponentNonexistent  = "component %s/%s/%s doesn't exist"
	LocaleNotSupported    = "locale '%s' isn't supported in %s/%s"
	BundleNonexistent     = "translation bundle %s/%s doesn't exist"
	BundleInfoUnavailable = "translation bundle information is unavailable"
	BundleInfoFailure     = "fail to get bundle information"
)

var ErrStringNotFound = fmt.Errorf("key isn't found")
