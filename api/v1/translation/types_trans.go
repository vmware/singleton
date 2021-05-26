/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import "sgtnserver/api/v2/translation"

type (
	ReleaseID translation.ReleaseID

	GetStringReq struct {
		ReleaseID
		Locale    string `form:"locale" binding:"locale"`
		Component string `form:"component" binding:"required,component"`
		Key       string `form:"key" binding:"required,key"`
		Source    string `form:"source"`
	}
)
