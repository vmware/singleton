/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package serverinfo

import (
	"context"

	"sgtnserver/internal/config"
)

func GetServerInfo(_ctx context.Context) interface{} {
	return config.Settings.BuildInfo
}
