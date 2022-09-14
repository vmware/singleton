/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package actuator

import (
	"net/http"
	"sgtnserver/api"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
	"github.com/sinhashubham95/go-actuator"
)

type router struct{}

func (r *router) Init(g *gin.RouterGroup) {
	logger.Log.Debug("Initialize actuator router")

	g.GET("/actuator/*endpoint", actuatorHandler(g))
}

func init() {
	api.Register(&router{})
}

func actuatorHandler(g *gin.RouterGroup) func(c *gin.Context) {
	h := actuator.GetActuatorHandler(&actuator.Config{Endpoints: []int{actuator.Info}})
	return func(c *gin.Context) {
		if c.Param("endpoint") == "/health" {
			c.JSON(http.StatusOK, gin.H{"status": "UP"})
		} else {
			h(c.Writer, c.Request)
		}
	}
}
