/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

import "sgtnserver/api/v2/cldr"

type (
	PatternByLocaleReq struct {
		Locale string `form:"locale" binding:"required,locale"`
		cldr.PatternScope
	}

	PatternData cldr.PatternData
)
