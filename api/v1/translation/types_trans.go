/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import "sgtnserver/api/v2/translation"

type (
	ReleaseID translation.ReleaseID

	GetStringReq struct {
		ReleaseID
		Locale    string `form:"locale" binding:"locale"`
		Component string `form:"component" binding:"component"`
		Key       string `form:"key" binding:"key"`
		Source    string `form:"source"`
		Pseudo    bool   `form:"pseudo" binding:"omitempty"`
	}
)
