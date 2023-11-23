/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package formatting

import (
	v2 "sgtnserver/api/v2"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

type formattingRouter struct{}

func (r *formattingRouter) Init(e *gin.RouterGroup) {
	logger.Log.Debug("Initialize formatting router")

	group := e.Group("/formatting")
	{
		group.GET("/date/localizedDate", GetLocalizedDate)
		group.GET("/number/localizedNumber", GetLocalizedNumber)
	}
}
func init() {
	v2.Register(&formattingRouter{})
}
