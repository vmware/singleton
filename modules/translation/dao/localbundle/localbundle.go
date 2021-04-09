/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package localbundle

import (
	"context"
	"io/ioutil"
	"os"
	"path"
	"path/filepath"
	"strings"

	jsoniter "github.com/json-iterator/go"
	"go.uber.org/zap"

	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/translation"
)

var json = jsoniter.ConfigDefault

// LocalBundle ...
type LocalBundle struct {
	BasePath string
}

// NewLocalBundle ...
func NewLocalBundle(p string) *LocalBundle {
	return &LocalBundle{BasePath: p}
}

// GetBundleInfo ...
func (b *LocalBundle) GetBundleInfo(ctx context.Context) (data *translation.BundleInfo, returnErr error) {
	log := logger.FromContext(ctx)
	products, err := ioutil.ReadDir(b.BasePath)
	if err != nil {
		returnErr = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(err, "Fail to read dir '%s'", b.BasePath)
		log.Error(returnErr.Error())
		return nil, returnErr
	}

	bundleInfo := translation.NewBundleInfo()
	for _, product := range products {
		if !product.IsDir() {
			continue
		}
		productDir := path.Join(b.BasePath, product.Name())
		versions, err := ioutil.ReadDir(productDir)
		if err != nil {
			returnErr = sgtnerror.StatusInternalServerError.WrapError(err)
			log.Error(returnErr.Error())
			return nil, returnErr
		}

		for _, v := range versions {
			if !v.IsDir() {
				continue
			}
			verDir := path.Join(productDir, v.Name())
			components, err := ioutil.ReadDir(verDir)
			if err != nil {
				returnErr = sgtnerror.StatusInternalServerError.WrapError(err)
				log.Error(returnErr.Error())
				return nil, returnErr
			}

			var bundleIDs []translation.CompactBundleID
			for _, component := range components {
				if !component.IsDir() {
					continue
				}
				componentDir := path.Join(verDir, component.Name())
				bundles, err := ioutil.ReadDir(componentDir)
				if err != nil {
					returnErr = sgtnerror.StatusInternalServerError.WrapError(err)
					log.Error(returnErr.Error())
					return nil, returnErr
				}

				for _, bundle := range bundles {
					if bundle.IsDir() {
						continue
					}
					bundleName := bundle.Name()
					if strings.HasPrefix(bundleName, translation.BundlePrefix) && strings.HasSuffix(bundleName, translation.BundleSuffix) {
						locale := bundleName[len(translation.BundlePrefix) : len(bundleName)-len(translation.BundleSuffix)]
						bundleIDs = append(bundleIDs, translation.CompactBundleID{Locale: locale, Component: component.Name()})
					}
				}
			}
			bundleInfo.AddBundles(product.Name(), v.Name(), bundleIDs)
		}
	}

	return bundleInfo, nil
}

// GetBundle ...
func (b *LocalBundle) GetBundle(ctx context.Context, id *translation.BundleID) (data *translation.Bundle, returnErr error) {
	_, filePath := b.getBundlePath(id)
	contents, err := ioutil.ReadFile(filePath)
	if err == nil {
		var bf translation.BundleFile
		if err = json.Unmarshal(contents, &bf); err == nil {
			if !strings.EqualFold(bf.Locale, id.Locale) || !strings.EqualFold(bf.Component, id.Component) {
				logger.FromContext(ctx).Error("Bundle file content is wrong!",
					zap.String(translation.Locale, bf.Locale+"?="+id.Locale),
					zap.String(translation.Component, bf.Component+"?="+id.Component))
			}
			return &translation.Bundle{ID: *id, Messages: bf.Messages}, nil
		}
	}
	if os.IsNotExist(err) {
		returnErr = sgtnerror.StatusNotFound.WrapErrorWithMessage(err, translation.FailToReadBundle, id.Name, id.Version, id.Component, id.Locale)
	} else {
		returnErr = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(err, translation.FailToReadBundle, id.Name, id.Version, id.Component, id.Locale)
	}
	logger.FromContext(ctx).Error(returnErr.Error())
	return nil, returnErr
}

// PutBundle ...
func (b *LocalBundle) PutBundle(ctx context.Context, bundleData *translation.Bundle) (returnErr error) {
	bundle := &translation.BundleFile{Component: bundleData.ID.Component, Locale: bundleData.ID.Locale, Messages: bundleData.Messages}
	bts, err := json.MarshalIndent(bundle, "", "    ")
	if err != nil {
		returnErr = sgtnerror.StatusBadRequest.WrapError(err)
	} else {
		dirPath, fullPath := b.getBundlePath(&bundleData.ID)
		if err = os.MkdirAll(dirPath, 0755); err == nil {
			err = ioutil.WriteFile(fullPath, bts, 0644)
		}
		if err != nil {
			returnErr = sgtnerror.StatusInternalServerError.WrapError(err)
		}
	}

	if returnErr != nil {
		logger.FromContext(ctx).Error(returnErr.Error())
	}

	return returnErr
}

// DeleteBundle ...
// func (b *LocalBundle) DeleteBundle(ctx context.Context, bundleID *translation.BundleID) error {
// 	_, fullPath := b.getBundlePath(bundleID)

// 	err := os.Remove(fullPath)
// 	if err == nil || os.IsNotExist(err) {
// 		return nil
// 	}

// 	returnErr := sgtnerror.StatusInternalServerError.WrapError(err)
// 	logger.FromContext(ctx).Error(returnErr.Error())
// 	return returnErr
// }

func (b *LocalBundle) getBundlePath(id *translation.BundleID) (string, string) {
	fileName := translation.GetBundleFilename(id.Locale)
	dirPath := filepath.Join(b.BasePath, id.Name, id.Version, id.Component)
	fullPath := filepath.Join(dirPath, fileName)

	return dirPath, fullPath
}
