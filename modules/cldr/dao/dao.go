/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package dao

import (
	"context"
	"fmt"
	"os"
	"strings"

	cldrbindata "sgtnserver/internal/bindata/cldr"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"

	jsoniter "github.com/json-iterator/go"
	"go.uber.org/zap"
)

// this is for contextTransforms, only part of locales have this data. Save result to avoid querying from storage repeatedly
// var nonexistentMap sync.Map

type cldrDAO struct{}

func (cldrDAO) GetCoreData(ctx context.Context, dataType cldr.CoreDataType, data interface{}) error {
	log := logger.FromContext(ctx)
	log.Debug("Read core locale data from storage", zap.String("type", coreDataTypeStrings[dataType]))

	info := getItemInfoOfCoreGroup(dataType)
	if info.filePath == "" {
		err := sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidDataType, coreDataTypeStrings[dataType])
		log.Error(err.Error())
		return err
	}

	err := sgtnerror.StatusInternalServerError.WrapErrorWithMessage(readDataFromBinary(info.filePath, data, info.jsonPath...), "fail to read cldr data %v", coreDataTypeStrings[dataType])
	if err != nil {
		log.Error(err.Error())
	}
	return err
}

func (cldrDAO) GetLocaleData(ctx context.Context, locale, dataType string, data interface{}) error {
	log := logger.FromContext(ctx)
	log.Debug("Read locale data from storage", zap.String("locale", locale), zap.String("type", dataType))

	info := getItemInfoOfLocaleGroup(dataType)
	if info.filePath == "" {
		err := sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidDataType, dataType)
		log.Error(err.Error())
		return err
	}

	filePath := fmt.Sprintf(info.filePath, locale)
	// if _, ok := nonexistentMap.Load(filePath); ok {
	// 	return sgtnerror.StatusNotFound.WithUserMessage("Locale is '%s', type is %v", locale, dataType)
	// }
	err := readDataFromBinary(filePath, data, info.jsonPath...)
	if err != nil {
		if os.IsNotExist(err) || strings.Contains(err.Error(), "not found") {
			err = sgtnerror.StatusNotFound.WrapErrorWithMessage(err, filePath)
			log.Warn(err.Error())
			// nonexistentMap.Store(filePath, nil)
		} else {
			err = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(err, filePath)
			log.Error(err.Error())
		}
	}
	return err
}

func readDataFromBinary(filePath string, data interface{}, jsonPath ...interface{}) error {
	bts, err := cldrbindata.Asset(filePath)
	if err != nil {
		return err
	}
	v := jsoniter.ConfigDefault.Get(bts, jsonPath...)
	if err := v.LastError(); err != nil {
		return err
	}

	v.ToVal(data)

	return nil
}

func GetDAO() cldrDAO {
	return cldrDAO{}
}
