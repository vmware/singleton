/*
 * Copyright 2023-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package serverinfo

import (
	"context"

	"sgtnserver/internal/bindata"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"

	"go.uber.org/zap"
	"gopkg.in/yaml.v3"
)

type serverInfo struct {
	Name        string `json:"name,omitempty" yaml:"name"`
	Version     string `json:"version,omitempty" yaml:"version"`
	Author      string `json:"author,omitempty" yaml:"author"`
	CreatedBy   string `json:"createdBy,omitempty" yaml:"createdBy"`
	BuildDate   string `json:"buildDate,omitempty" yaml:"buildDate"`
	BuildNumber string `json:"buildNumber,omitempty" yaml:"buildNumber"`
	ChangeID    string `json:"changeId,omitempty" yaml:"changeId"`
}

var (
	filePath = "info.yaml"
	info     *serverInfo
)

func GetServerInfo(_ context.Context) (interface{}, error) {
	if info == nil {
		return nil, sgtnerror.StatusNotFound.WithUserMessage("service information is unavailable")
	}
	return info, nil
}

func init() {
	bts, err := bindata.Asset(filePath)
	if err != nil {
		logger.Log.Error("fail to read information file", zap.String("path", filePath), zap.Error(err))
		return
	}

	err = yaml.Unmarshal(bts, &info)
	if err != nil {
		logger.Log.Fatal("fail to parse information file", zap.String("path", filePath), zap.Error(err), zap.ByteString("contents", bts))
	}
}
