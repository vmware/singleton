/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translationservice

// type allowlist struct {
// 	service translation.Service
// }

// func (l allowlist) GetBundle(ctx context.Context, id *translation.BundleID) (*translation.Bundle, error) {
// 	if !IsReleaseAllowed(id.Name, id.Version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, id.Name, id.Version)
// 	}

// 	return l.service.GetBundle(ctx, id)
// }

// func (l allowlist) GetString(ctx context.Context, id *translation.MessageID) (*translation.StringMessage, error) {
// 	if !IsReleaseAllowed(id.Name, id.Version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, id.Name, id.Version)
// 	}

// 	return l.service.GetString(ctx, id)
// }

// func (l allowlist) GetStrings(ctx context.Context, id *translation.BundleID, keys []string) (*translation.Bundle, error) {
// 	if !IsReleaseAllowed(id.Name, id.Version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, id.Name, id.Version)
// 	}

// 	return l.service.GetStrings(ctx, id, keys)
// }

// func (l allowlist) GetStringWithSource(ctx context.Context, id *translation.MessageID, source string) (map[string]interface{}, error) {
// 	if !IsReleaseAllowed(id.Name, id.Version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, id.Name, id.Version)
// 	}

// 	return l.service.GetStringWithSource(ctx, id, source)
// }

// func (l allowlist) GetMultipleBundles(ctx context.Context, name, version, localeString, componentString string) (data *translation.Release, err error) {
// 	if !IsReleaseAllowed(name, version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, name, version)
// 	}

// 	return l.service.GetMultipleBundles(ctx, name, version, localeString, componentString)
// }

// func (l allowlist) GetAvailableLocales(ctx context.Context, name, version string) (data []string, returnErr error) {
// 	if !IsReleaseAllowed(name, version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, name, version)
// 	}

// 	return l.service.GetAvailableLocales(ctx, name, version)
// }

// func (l allowlist) GetAvailableComponents(ctx context.Context, name, version string) (data []string, returnErr error) {
// 	if !IsReleaseAllowed(name, version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, name, version)
// 	}

// 	return l.service.GetAvailableComponents(ctx, name, version)
// }

// func (l allowlist) PutBundles(ctx context.Context, bundleData []*translation.Bundle) error {
// 	// TODO
// 	if len(bundleData) > 0 {
// 		name, version := bundleData[0].ID.Name, bundleData[0].ID.Version
// 		if !IsReleaseAllowed(name, version) {
// 			return errors.Errorf(translation.ReleaseNonexistent, name, version)
// 		}
// 	}

// 	return l.service.PutBundles(ctx, bundleData)
// }

// func (l allowlist) GetTranslationStatus(ctx context.Context, id *translation.BundleID) (map[string]interface{}, error) {
// 	if !IsReleaseAllowed(id.Name, id.Version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, id.Name, id.Version)
// 	}

// 	return l.service.GetTranslationStatus(ctx, id)
// }
