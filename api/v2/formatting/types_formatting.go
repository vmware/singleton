/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package formatting

type (
	DateReq struct {
		Locale   string `form:"locale" binding:"required,locale"`
		LongDate int64  `form:"longDate" binding:"required"`
		Pattern  string `form:"pattern" binding:"required"`
	}

	DateResp struct {
		Pattern       string `json:"pattern"`
		Locale        string `json:"locale"`
		LongDate      int64  `json:"longDate"`
		FormattedDate string `json:"formattedDate"`
	}
)
