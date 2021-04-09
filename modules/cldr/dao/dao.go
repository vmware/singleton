/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package dao

import (
	"context"
	"fmt"
	"os"
	"strings"

	"sgtnserver/internal/bindata"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"

	jsoniter "github.com/json-iterator/go"
	"go.uber.org/zap"
)

var json = jsoniter.ConfigDefault

func GetCoreData(ctx context.Context, dataType CoreDataType, data interface{}) error {
	log := logger.FromContext(ctx)
	log.Debug("Read core locale data from storage", zap.String("type", coreDataTypeStrings[dataType]))

	info := getItemInfoOfCoreGroup(dataType)
	if info.filePath == "" {
		err := sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidDataType, CoreDataTypeNames[dataType])
		log.Error(err.Error())
		return err
	}

	err := sgtnerror.StatusInternalServerError.WrapErrorWithMessage(readDataFromBinary(info.filePath, data, info.jsonPath...), "fail to read cldr data %v", coreDataTypeStrings[dataType])
	if err != nil {
		log.Error(err.Error())
	}
	return err
}

func GetLocaleData(ctx context.Context, dataType, locale string, data interface{}) error {
	log := logger.FromContext(ctx)
	log.Debug("Read locale data from storage", zap.String("locale", locale), zap.String("type", dataType))

	info := getItemInfoOfLocaleGroup(dataType)
	if info.filePath == "" {
		err := sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidDataType, dataType)
		log.Error(err.Error())
		return err
	}

	filePath := fmt.Sprintf(info.filePath, locale)
	err := readDataFromBinary(filePath, data, info.jsonPath...)
	if err != nil {
		if os.IsNotExist(err) || strings.Contains(err.Error(), "not found") {
			err = sgtnerror.StatusNotFound.WrapErrorWithMessage(err, filePath)
			log.Warn(err.Error())
		} else {
			err = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(err, filePath)
			log.Error(err.Error())
		}
	}
	return err
}

func readDataFromBinary(filePath string, data interface{}, jsonPath ...interface{}) error {
	bts, err := bindata.Asset(filePath)
	if err != nil {
		return err
	}
	v := json.Get(bts, jsonPath...)
	if err := v.LastError(); err != nil {
		return err
	}

	v.ToVal(data)

	return nil
}
