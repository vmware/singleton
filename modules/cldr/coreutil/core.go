/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package coreutil

import (
	"context"

	"sgtnserver/modules/cldr/cldrcache"
	"sgtnserver/modules/cldr/dao"

	jsoniter "github.com/json-iterator/go"
)

func GetCoreData(ctx context.Context, t dao.CoreDataType) (data jsoniter.Any, err error) {
	err = cldrcache.GetCoreData(ctx, t, &data)
	return
}
