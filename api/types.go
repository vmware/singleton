/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package api

type (
	Response struct {
		Error *BusinessError `json:"response"`
		Data  interface{}    `json:"data,omitempty"`
	}

	BusinessError struct {
		HTTPCode int    `json:"-"`
		Code     int    `json:"code"`
		UserMsg  string `json:"message"`
	}
)
