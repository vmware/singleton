/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import (
	"context"
)

type Service interface {
	GetBundle(context.Context, *BundleID) (*Bundle, error)

	// DeleteBundle(context.Context, *BundleID) error

	GetString(ctx context.Context, id *MessageID) (*StringMessage, error)

	GetStrings(context.Context, *BundleID, []string) (*Bundle, error)

	GetStringWithSource(ctx context.Context, id *MessageID, source string) (map[string]interface{}, error)

	GetMultipleBundles(ctx context.Context, name, version, localeString, componentString string) (data *Release, err error)

	GetAvailableLocales(ctx context.Context, name, version string) (data []string, returnErr error)

	GetAvailableComponents(ctx context.Context, name, version string) (data []string, returnErr error)

	PutBundles(ctx context.Context, bundleData []*Bundle) error

	GetTranslationStatus(ctx context.Context, id *BundleID) (map[string]interface{}, error)
}
