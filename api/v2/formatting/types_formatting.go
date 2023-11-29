/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package formatting

// Request

type (
	DateReq struct {
		Locale   string `form:"locale" binding:"locale"`
		LongDate int64  `form:"longDate" binding:"numeric"`
		Pattern  string `form:"pattern" binding:"required"`
	}

	NumberRequest struct {
		Locale string `form:"locale" binding:"locale"`
		Number string `form:"number" binding:"numeric"`
		Scale  int    `form:"scale"  binding:"number"`
	}
)

// Response

type (
	DateResp struct {
		Pattern       string `json:"pattern"`
		Locale        string `json:"locale"`
		LongDate      int64  `json:"longDate"`
		FormattedDate string `json:"formattedDate"`
	}

	NumberResp struct {
		Locale          string `json:"locale"`
		Number          string `json:"number"`
		Scale           string `json:"scale"`
		FormattedNumber string `json:"formattedNumber"`
	}
)
