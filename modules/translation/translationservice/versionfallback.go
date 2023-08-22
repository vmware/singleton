/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translationservice

// type versionfallback struct {
// 	service translation.Service
// }

// func (l versionfallback) GetBundle(ctx context.Context, id *translation.BundleID) (*translation.Bundle, error) {
// 	if !IsReleaseAllowed(id.Name, id.Version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, id.Name, id.Version)
// 	}

// 	return l.service.GetBundle(ctx, id)
// }

// func (l versionfallback) GetString(ctx context.Context, id *translation.MessageID) (*translation.StringMessage, error) {
// 	if !IsReleaseAllowed(id.Name, id.Version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, id.Name, id.Version)
// 	}

// 	return l.service.GetString(ctx, id)
// }

// func (l versionfallback) GetStrings(ctx context.Context, id *translation.BundleID, keys []string) (*translation.Bundle, error) {
// 	if !IsReleaseAllowed(id.Name, id.Version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, id.Name, id.Version)
// 	}

// 	return l.service.GetStrings(ctx, id, keys)
// }

// func (l versionfallback) GetStringWithSource(ctx context.Context, id *translation.MessageID, source string) (map[string]interface{}, error) {
// 	if !IsReleaseAllowed(id.Name, id.Version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, id.Name, id.Version)
// 	}

// 	return l.service.GetStringWithSource(ctx, id, source)
// }

// func (l versionfallback) GetMultipleBundles(ctx context.Context, name, version, localeString, componentString string) (data *translation.Release, err error) {
// 	if !IsReleaseAllowed(name, version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, name, version)
// 	}

// 	return l.service.GetMultipleBundles(ctx, name, version, localeString, componentString)
// }

// func (l versionfallback) GetAvailableLocales(ctx context.Context, name, version string) (data []string, returnErr error) {
// 	if !IsReleaseAllowed(name, version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, name, version)
// 	}

// 	return l.service.GetAvailableLocales(ctx, name, version)
// }

// func (l versionfallback) GetAvailableComponents(ctx context.Context, name, version string) (data []string, returnErr error) {
// 	if !IsReleaseAllowed(name, version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, name, version)
// 	}

// 	return l.service.GetAvailableComponents(ctx, name, version)
// }

// func (l versionfallback) PutBundles(ctx context.Context, bundleData []*translation.Bundle) error {
// 	// TODO
// 	if len(bundleData) > 0 {
// 		name, version := bundleData[0].ID.Name, bundleData[0].ID.Version
// 		if !IsReleaseAllowed(name, version) {
// 			return errors.Errorf(translation.ReleaseNonexistent, name, version)
// 		}
// 	}

// 	return l.service.PutBundles(ctx, bundleData)
// }

// func (l versionfallback) GetTranslationStatus(ctx context.Context, id *translation.BundleID) (map[string]interface{}, error) {
// 	if !IsReleaseAllowed(id.Name, id.Version) {
// 		return nil, errors.Errorf(translation.ReleaseNonexistent, id.Name, id.Version)
// 	}

// 	return l.service.GetTranslationStatus(ctx, id)
// }
