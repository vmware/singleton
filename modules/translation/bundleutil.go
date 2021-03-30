/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

func GetBundleFilename(locale string) string {
	return BundlePrefix + locale + BundleSuffix
}
