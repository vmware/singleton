/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import "context"

type Service interface {
	GetBundle(context.Context, *BundleID) (*Bundle, error)

	// DeleteBundle(context.Context, *BundleID) error

	GetString(ctx context.Context, name, version, locale, component, key string) (*StringMessage, error)

	GetMultipleBundles(ctx context.Context, name, version, localeString, componentString string) (data []*Bundle, err error)

	GetAvailableLocales(ctx context.Context, name, version string) (data []string, returnErr error)

	GetAvailableComponents(ctx context.Context, name, version string) (data []string, returnErr error)

	PutBundles(ctx context.Context, bundleData []*Bundle) error
}
