/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package interfaces

import (
	"context"
	"sgtnserver/modules/translation"
)

type SgtnBundles interface {
	ReadJSONFile(ctx context.Context, path string, data interface{}) error
	translation.MessageOrigin
}
