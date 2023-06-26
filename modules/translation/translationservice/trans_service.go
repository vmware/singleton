/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translationservice

import (
	"context"
	"strings"

	"sgtnserver/internal/cache"
	"sgtnserver/internal/common"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/translation"
	"sgtnserver/modules/translation/bundleinfo"
	"sgtnserver/modules/translation/dao"
	"sgtnserver/modules/translation/translationcache"

	"github.com/fatih/structs"
	jsoniter "github.com/json-iterator/go"
	"go.uber.org/zap"
)

type Service struct {
	msgOrigin translation.MessageOrigin
}

// GetAvailableComponents ...
func (ts Service) GetAvailableComponents(ctx context.Context, name, version string) (data []string, returnErr error) {
	log := logger.FromContext(ctx)
	log.Debug("Get supported component list", zap.String(translation.Name, name), zap.String(translation.Version, version))
	components, ok := bundleinfo.GetComponentNames(name, version)
	if !ok {
		returnErr = sgtnerror.StatusNotFound.WithUserMessage(translation.ReleaseNonexistent, name, version)
		log.Error(returnErr.Error())
		return nil, returnErr
	}

	return convertSetToList(components), nil
}

// GetAvailableLocales ...
func (ts Service) GetAvailableLocales(ctx context.Context, name, version string) (data []string, returnErr error) {
	log := logger.FromContext(ctx)
	log.Debug("Get supported locale list", zap.String(translation.Name, name), zap.String(translation.Version, version))
	locales, ok := bundleinfo.GetLocaleNames(name, version)
	if !ok {
		returnErr = sgtnerror.StatusNotFound.WithUserMessage(translation.ReleaseNonexistent, name, version)
		log.Error(returnErr.Error())
		return nil, returnErr
	}

	for _, e := range locales.Values() {
		locale := e.(string)
		if locale != translation.Latest {
			data = append(data, locale)
		}
	}
	return data, nil
}

// GetAvailableBundles ...
func (ts Service) GetAvailableBundles(ctx context.Context, name, version string) (data []translation.CompactBundleID, returnErr error) {
	log := logger.FromContext(ctx)
	log.Debug("Get available bundle IDs", zap.String(translation.Name, name), zap.String(translation.Version, version))
	IDs, ok := bundleinfo.GetAvailableBundles(name, version)
	if !ok {
		returnErr = sgtnerror.StatusNotFound.WithUserMessage(translation.ReleaseNonexistent, name, version)
		log.Error(returnErr.Error())
		return nil, returnErr
	}
	values := IDs.Values()
	data = make([]translation.CompactBundleID, 0, len(values))
	for _, v := range values {
		id := v.(translation.CompactBundleID)
		if id.Locale != translation.Latest {
			data = append(data, id)
		}
	}
	return data, nil
}

// GetMultipleBundles Get translation of multiple bundles
func (ts Service) GetMultipleBundles(ctx context.Context, name, version, localeString, componentString string) (*translation.Release, error) {
	log := logger.FromContext(ctx)
	log.Debug("Get bundles", zap.String(translation.Name, name), zap.String(translation.Version, version),
		zap.String("locales", localeString), zap.String("components", componentString))

	var bundleIDs = []translation.CompactBundleID{}
	var err error
	if localeString == "" && componentString == "" {
		bundleIDs, err = ts.GetAvailableBundles(ctx, name, version)
		if err != nil {
			return nil, err
		}
	} else {
		var components, locales []string
		if localeString != "" {
			locales = PickupLocales(name, version, strings.Split(localeString, common.ParamSep))
		} else {
			locales, err = ts.GetAvailableLocales(ctx, name, version)
			if err != nil {
				err = sgtnerror.StatusNotFound.WithUserMessage(translation.ReleaseNonexistent, name, version)
				log.Error(err.Error())
				return nil, err
			}
		}
		if componentString != "" {
			components = strings.Split(componentString, common.ParamSep)
		} else {
			components, err = ts.GetAvailableComponents(ctx, name, version)
			if err != nil {
				err = sgtnerror.StatusNotFound.WithUserMessage(translation.ReleaseNonexistent, name, version)
				log.Error(err.Error())
				return nil, err
			}
		}

		for _, comp := range components {
			for _, locale := range locales {
				bundleIDs = append(bundleIDs, translation.CompactBundleID{Locale: locale, Component: comp})
			}
		}
	}

	bundles, err := ts.getMultipleBundles(ctx, name, version, bundleIDs)
	return &translation.Release{Name: name, Version: version, Bundles: bundles}, err
}

func (ts Service) getMultipleBundles(ctx context.Context, name, version string, bundleIDs []translation.CompactBundleID) (data []*translation.Bundle, err error) {
	var returnErr *sgtnerror.MultiError
	for _, id := range bundleIDs {
		b, err := ts.msgOrigin.GetBundle(ctx, &translation.BundleID{Name: name, Version: version, Locale: id.Locale, Component: id.Component})
		if err == nil {
			data = append(data, b)
		}
		returnErr = sgtnerror.Append(returnErr, err)
	}

	return data, returnErr.ErrorOrNil()
}

// GetBundle ...
func (ts Service) GetBundle(ctx context.Context, id *translation.BundleID) (*translation.Bundle, error) {
	name, version, locale, component := id.Name, id.Version, id.Locale, id.Component
	logger.FromContext(ctx).Debug("Get a bundle", zap.String(translation.Name, name), zap.String(translation.Version, version),
		zap.String(translation.Locale, locale), zap.String(translation.Component, component))

	id.Locale = PickupLocales(name, version, []string{locale})[0]
	return ts.msgOrigin.GetBundle(ctx, id)
}

// GetStrings ...
func (ts Service) GetStrings(ctx context.Context, id *translation.BundleID, keys []string) (*translation.Bundle, error) {
	name, version, locale, component := id.Name, id.Version, id.Locale, id.Component
	logger.FromContext(ctx).Debug("Get translations of multiple keys", zap.String(translation.Name, name), zap.String(translation.Version, version),
		zap.String(translation.Locale, locale), zap.String(translation.Component, component), zap.Strings("keys", keys))

	id.Locale = PickupLocales(name, version, []string{locale})[0]
	bundle, err := ts.msgOrigin.GetBundle(ctx, id)
	if err != nil {
		return nil, err
	}

	var returnErr *sgtnerror.MultiError
	allMsgs := make(map[string]jsoniter.Any)
	bundle.Messages.ToVal(&allMsgs)
	messagesToReturn := make(map[string]jsoniter.Any, len(keys))
	for _, key := range keys {
		msg := allMsgs[key]
		if msg == nil {
			returnErr = sgtnerror.Append(returnErr, sgtnerror.StatusNotFound.WithUserMessage(translation.KeyNotFound, key))
		} else {
			messagesToReturn[key] = msg
			returnErr = sgtnerror.Append(returnErr, nil)
		}
	}
	marshaled, _ := jsoniter.Marshal(messagesToReturn)
	return &translation.Bundle{ID: bundle.ID, Messages: jsoniter.Get(marshaled)}, returnErr.ErrorOrNil()
}

// GetString ...
func (ts Service) GetString(ctx context.Context, id *translation.MessageID) (*translation.StringMessage, error) {
	b, err := ts.GetBundle(ctx, &translation.BundleID{Name: id.Name, Version: id.Version, Locale: id.Locale, Component: id.Component})
	if err != nil {
		return nil, err
	}

	anyValue := b.Messages.Get(id.Key)
	if anyValue.LastError() == nil {
		return &translation.StringMessage{
				Name:        id.Name,
				Version:     id.Version,
				Locale:      b.ID.Locale,
				Component:   id.Component,
				Key:         id.Key,
				Translation: anyValue.ToString()},
			nil
	}

	return nil, sgtnerror.StatusNotFound.WrapErrorWithMessage(translation.ErrStringNotFound, "Fail to get translation for key '%s'", id.Key)
	// Log.Debug(returnErr)//Don't log because of key is very small granularity
}

func (ts Service) GetStringWithSource(ctx context.Context, id *translation.MessageID, source string) (result map[string]interface{}, returnErr error) {
	var stringTrans, locale string
	var status translation.TranslationStatus
	msg, err := ts.GetString(ctx, id)

	msgEn, errEn := msg, err
	if id.Locale != translation.EnLocale {
		idEn := *id
		idEn.Locale = translation.EnLocale
		msgEn, errEn = ts.GetString(ctx, &idEn)
	}
	if source != "" {
		if errEn != nil || msgEn.Translation != source {
			stringTrans, locale, status = source, translation.EnLocale, translation.SourceUpdated
		} else {
			if err == nil {
				stringTrans, locale, status = msg.Translation, msg.Locale, translation.TranslationValid
			} else {
				if errEn == nil {
					stringTrans, locale, status = msgEn.Translation, msgEn.Locale, translation.FallbackToEn
				} else {
					stringTrans, locale, status = source, translation.EnLocale, translation.FallbackToSource
				}
			}
		}
	} else {
		if err == nil {
			stringTrans, locale, status = msg.Translation, msg.Locale, translation.TranslationValid
		} else {
			if errEn == nil {
				stringTrans, locale, status = msgEn.Translation, msgEn.Locale, translation.FallbackToEn
			} else {
				return nil, err
			}
		}
	}

	return map[string]interface{}{
			"productName": id.Name,
			"version":     id.Version,
			"locale":      locale,
			"component":   id.Component,
			"key":         id.Key,
			"translation": stringTrans,
			"source":      source,
			"status":      status},
		nil
}

// PutBundles ...
func (ts Service) PutBundles(ctx context.Context, bundleData []*translation.Bundle) error {
	logger.FromContext(ctx).Debug("Put bundles")
	var returnErr *sgtnerror.MultiError
	for _, bd := range bundleData {
		err := ts.msgOrigin.PutBundle(ctx, bd)
		returnErr = sgtnerror.Append(returnErr, err)
	}

	return returnErr.ErrorOrNil()
}

// DeleteBundle ...
// func (ts Service) DeleteBundle(ctx context.Context, bundleID *translation.BundleID) error {
// 	logger.FromContext(ctx).Debug("Delete a bundle", zap.Any("id", bundleID))
// 	return ts.msgOrigin.DeleteBundle(ctx, bundleID)
// }

// DeleteString ...
// func (ts Service) DeleteString(ctx context.Context, name, version, locale, component, key string) error {
// 	log := logger.FromContext(ctx)
// 	log.Debug("Delete a string", zap.String(translation.Name, name),
// 		zap.String(translation.Version, version), zap.String(translation.Locale, locale),
// 		zap.String(translation.Component, component), zap.String("key", key))

// 	bundle, err := ts.msgOrigin.GetBundle(ctx, &translation.BundleID{Name: name,
// 		Version: version, Locale: locale, Component: component})
// 	if err != nil {
// 		return err
// 	}

// 	messages, _ := bundle.Messages.GetInterface().(map[string]interface{})
// 	if messages == nil {
// 		returnErr := sgtnerror.StatusInternalServerError.WrapError(errors.New("bundle file content is wrong"))
// 		log.Error(returnErr.Error())
// 		return returnErr
// 	}
// 	if _, ok := messages[key]; !ok {
// 		return nil
// 	}

// 	delete(messages, key)
// 	bts, err := json.MarshalIndent(messages, "", "    ")
// 	if err != nil {
// 		returnErr := sgtnerror.StatusInternalServerError.WrapError(err)
// 		log.Error(returnErr.Error())
// 		return returnErr
// 	}

// 	bundle.Messages = json.Get(bts)
// 	return ts.msgOrigin.PutBundle(ctx, bundle)
// }

func (ts Service) ClearCache(ctx context.Context) (err error) {
	if m, ok := ts.msgOrigin.(*translationcache.TransCacheMgr); ok {
		err = m.ClearCache(ctx)
	}
	if err == nil {
		err = bundleinfo.RefreshBundleInfo(ctx)
	}

	return
}

func (ts Service) GetTranslationStatus(ctx context.Context, id *translation.BundleID) (map[string]interface{}, error) {
	logger.FromContext(ctx).Debug("Get a bundle's translation status", zap.String(translation.Name, id.Name), zap.String(translation.Version, id.Version),
		zap.String(translation.Locale, id.Locale), zap.String(translation.Component, id.Component))

	id.Locale = PickupLocales(id.Name, id.Version, []string{id.Locale})[0]

	const ready, notready = "1", "0"

	translationData, tErr := ts.GetBundle(ctx, id)
	if tErr != nil {
		return nil, sgtnerror.TranslationNotReady
	}

	latestID := *id
	latestID.Locale = translation.Latest
	latestData, latestErr := ts.GetBundle(ctx, &latestID)
	if latestErr != nil {
		return nil, sgtnerror.TranslationReady
	}

	var enErr error
	var enData = translationData
	if translationData.ID.Locale != translation.EnLocale {
		enID := *id
		enID.Locale = translation.EnLocale
		enData, enErr = ts.GetBundle(ctx, &enID)
		if enErr != nil {
			return nil, sgtnerror.TranslationNotReady
		}
	}

	var latestMessages, enMessages map[string]string
	latestData.Messages.ToVal(&latestMessages)
	enData.Messages.ToVal(&enMessages)

	var result = make(map[string]interface{}, len(latestMessages))
	var err = sgtnerror.TranslationReady
	for k, latestValue := range latestMessages {
		if latestValue == enMessages[k] {
			result[k] = ready
		} else {
			result[k] = notready
			err = sgtnerror.TranslationNotReady
		}
	}

	return result, err
}

var service Service

func newService() Service {
	logger.Log.Debug("Initialize translation service")

	origin := dao.GetInst()
	if config.Settings.Cache.Enable {
		origin = translationcache.NewCacheManager(origin, cache.NewCache("translation", structs.Map(config.Settings.Cache)))
	}

	bundleinfo.InitBundleInfo(origin)
	return Service{origin}
}

func GetService() Service {
	return service
}

func init() {
	service = newService()

	initLocaleMap()
}
