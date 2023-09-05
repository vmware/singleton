/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package countryflag

import (
	"context"
	"fmt"
	"strings"

	flagbindata "sgtnserver/internal/bindata/flag"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"

	"go.uber.org/zap"
)

type dao struct {
	baseFolder string
}

func (d dao) GetFlag(ctx context.Context, region string, scale FlagScale) (data string, returnErr error) {
	filePath := fmt.Sprintf(d.baseFolder+"%s/%s.svg", scale, strings.ToUpper(region))

	logger.FromContext(ctx).Debug("read a flag file", zap.String("path", filePath))

	contents, err := flagbindata.Asset(filePath)
	if err != nil {
		if fmt.Sprintf("Asset %s not found", filePath) == err.Error() {
			returnErr = sgtnerror.StatusBadRequest.WrapErrorWithMessage(err, "not found")
		} else {
			returnErr = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(err, "fail to read file")
		}

		logger.FromContext(ctx).Error(returnErr.Error())
	}

	return string(contents), returnErr
}
