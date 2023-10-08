/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package countryflag

import (
	"context"

	"sgtnserver/internal/logger"

	"go.uber.org/zap"
)

var flagCacheInst = newFlagCache(dao{"flags/"})

func GetFlag(ctx context.Context, region string, scale FlagScale) (contents string, returnErr error) {
	logger.FromContext(ctx).Debug("get a flag", zap.String("region", region), zap.Any("scale", scale))

	return flagCacheInst.GetFlag(ctx, region, scale)
}
