/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package decorator

import (
	"context"
	"fmt"
	"sync"

	"sgtnserver/internal/logger"
	"sgtnserver/modules/translation"
	"sgtnserver/modules/translation/bundleinfo"

	"go.uber.org/zap"
)

type decorator struct {
	translation.MessageOrigin
	locks sync.Map
}

func NewDAODecorator(dao translation.MessageOrigin) translation.MessageOrigin {
	return &decorator{MessageOrigin: dao}
}

func (d *decorator) GetBundleInfo(ctx context.Context) (*translation.BundleInfo, error) {
	logger.Log.Debug("Read bundle information from storage")
	return d.MessageOrigin.GetBundleInfo(ctx)
}

func (d *decorator) GetBundle(ctx context.Context, id *translation.BundleID) (*translation.Bundle, error) {
	// Currently it needs to update bundles after service starts, so don't verify here in order to query from storage.
	// if !bundleinfo.IsBundleExist(name, version, locale, component) {
	// 	returnErr := sgtnerror.StatusNotFound.WithUserMessage("Translation bundle %s/%s doesn't exist", locale, component)
	// 	Log.Error(returnErr)
	// 	return nil, returnErr
	// }

	logger.FromContext(ctx).Debug("Read a bundle from storage",
		zap.String(translation.Name, id.Name),
		zap.String(translation.Version, id.Version),
		zap.String(translation.Locale, id.Locale),
		zap.String(translation.Component, id.Component))

	// Lock before reading
	l, _ := d.locks.LoadOrStore(getKey(id), new(sync.RWMutex))
	locker := l.(*sync.RWMutex)
	locker.RLock()
	defer locker.RUnlock()

	b, err := d.MessageOrigin.GetBundle(ctx, id)
	if err == nil {
		bundleinfo.AddBundle(id)
	}
	return b, err
}

func (d *decorator) PutBundle(ctx context.Context, bundleData *translation.Bundle) error {
	log := logger.FromContext(ctx)
	log.Debug("Put a bundle",
		zap.String(translation.Name, bundleData.ID.Name),
		zap.String(translation.Version, bundleData.ID.Version),
		zap.String(translation.Locale, bundleData.ID.Locale),
		zap.String(translation.Component, bundleData.ID.Component))

	// Lock before writing
	l, _ := d.locks.LoadOrStore(getKey(&bundleData.ID), new(sync.RWMutex))
	locker := l.(*sync.RWMutex)
	locker.Lock()
	defer locker.Unlock()

	if ce := log.Check(zap.DebugLevel, "Bundle content"); ce != nil {
		ce.Write(zap.String("content", bundleData.Messages.ToString()))
	}
	err := d.MessageOrigin.PutBundle(ctx, bundleData)
	if err == nil {
		bundleinfo.AddBundle(&bundleData.ID)
	}
	return err
}

// func (d *decorator) DeleteBundle(ctx context.Context, bundleID *translation.BundleID) error {
// 	logger.FromContext(ctx).Debug("Delete a bundle",
// 		zap.String(translation.Name, bundleID.Name),
// 		zap.String(translation.Version, bundleID.Version),
// 		zap.String(translation.Locale, bundleID.Locale),
// 		zap.String(translation.Component, bundleID.Component))

// 	// Lock
// 	l, _ := d.locks.LoadOrStore(getKey(bundleID), new(sync.RWMutex))
// 	locker := l.(*sync.RWMutex)
// 	locker.Lock()
// 	defer locker.Unlock()

// 	err := d.MessageOrigin.DeleteBundle(ctx, bundleID)
// 	if err == nil {
// 		bundleinfo.DeleteBundle(bundleID)
// 	}
// 	return err
// }

func getKey(id *translation.BundleID) string {
	return fmt.Sprintf("%s:%s:%s:%s", id.Name, id.Version, id.Locale, id.Component)
}
