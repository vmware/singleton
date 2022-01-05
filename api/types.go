/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package api

import "sgtnserver/internal/sgtnerror"

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

var (
	err_success = &BusinessError{Code: sgtnerror.StatusSuccess.Code(), HTTPCode: sgtnerror.StatusSuccess.HTTPCode(), UserMsg: sgtnerror.StatusSuccess.Message()}
	err_207     = &BusinessError{Code: sgtnerror.StatusPartialSuccess.Code(), HTTPCode: sgtnerror.StatusPartialSuccess.HTTPCode(), UserMsg: sgtnerror.StatusPartialSuccess.Message()}
)
