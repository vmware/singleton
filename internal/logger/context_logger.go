/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package logger

import (
	"context"

	"go.uber.org/zap"
)

type key int

const loggerKey key = 0

func NewContext(ctx context.Context, d interface{}) context.Context {
	if ctx == nil {
		ctx = context.Background()
	}
	return context.WithValue(ctx, loggerKey, d)
}

func FromContext(ctx context.Context) *zap.Logger {
	if ctx != nil {
		if logger, ok := ctx.Value(loggerKey).(*zap.Logger); ok {
			return logger
		}
	}

	return Log
}
