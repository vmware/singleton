/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package authentication

type AppIdHeader struct {
	AppId string `header:"appId" binding:"required"`
}
