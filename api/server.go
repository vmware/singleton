/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package api

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"strings"
	"sync"
	"syscall"
	"time"

	"sgtnserver/internal/common"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"

	// "github.com/gin-contrib/pprof"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
)

var (
	routers []Router
	servers []*http.Server
)

func LogToZap(ginEngine *gin.Engine, zLogger *zap.Logger) {
	ginEngine.Use(GinZap(zLogger))
	ginEngine.Use(RecoveryWithZap(zLogger))
}

func InitServer() *gin.Engine {
	logger.Log.Debug("Initialize server")

	ginEngine := gin.New()

	// ginEngine.RemoveExtraSlash = true

	LogToZap(ginEngine, logger.Log)

	if config.Settings.CrossDomain.Enable {
		ginEngine.Use(HandleCrossDomain())
	}

	if len(config.Settings.Server.CompressionAlgorithm) > 0 {
		ginEngine.Use(CompressResponse())
	}

	ginEngine.Use(CC(false))

	InitValidator()

	for _, r := range routers {
		r.Init(&ginEngine.RouterGroup)
	}

	// ginEngine.GET("/loglevel", func(c *gin.Context) {
	// 	LogLevel.ServeHTTP(c.Writer, c.Request)
	// })
	// ginEngine.PUT("/loglevel", func(c *gin.Context) {
	// 	LogLevel.ServeHTTP(c.Writer, c.Request)
	// })

	return ginEngine
}

func Register(r Router) {
	routers = append(routers, r)
}

func StartServer() {
	gin.SetMode(config.Settings.Server.RunMode)

	ginEngine := InitServer()

	if config.Settings.Server.Schema == "" {
		logger.Log.Fatal("Schema can't be empty")
	}

	const startServerInfo = "Start '%s' server listening on %s"
	const startServerError = "Fail to start %s server: %+v"

	// pprof.Register(ginEngine)

	for _, schema := range strings.Split(config.Settings.Server.Schema, common.ParamAnd) {
		httpServer := http.Server{
			Handler:        ginEngine,
			ReadTimeout:    config.Settings.Server.ReadTimeout,
			WriteTimeout:   config.Settings.Server.WriteTimeout,
			MaxHeaderBytes: config.Settings.Server.MaxHeaderBytes,
		}
		servers = append(servers, &httpServer)

		go func(schema string) {
			var err error
			switch schema {
			case "http":
				httpServer.Addr = fmt.Sprintf(":%d", config.Settings.Server.HTTPPort)
				logger.Log.Info(fmt.Sprintf(startServerInfo, schema, httpServer.Addr))
				err = httpServer.ListenAndServe()
			case "https":
				httpServer.Addr = fmt.Sprintf(":%d", config.Settings.Server.HTTPSPort)
				logger.Log.Info(fmt.Sprintf(startServerInfo, schema, httpServer.Addr))
				err = httpServer.ListenAndServeTLS(config.Settings.Server.CertFile, config.Settings.Server.KeyFile)
			default:
				logger.Log.Fatal(fmt.Sprintf("Wrong schema type: %s", schema))
			}
			if err != nil && err != http.ErrServerClosed {
				logger.Log.Fatal(fmt.Sprintf(startServerError, schema, err))
			}
		}(schema)
	}
}

func WaitForSignals() {
	// Wait for interrupt signal to gracefully shutdown the server with
	// a timeout of 5 seconds.
	quit := make(chan os.Signal, 1)
	// kill (no param) default send syscall.SIGTERM
	// kill -2 is syscall.SIGINT
	// kill -9 is syscall.SIGKILL but can't be catch, so don't need add it
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
}

func ShutdownServer() {
	logger.Log.Info("Shutting down server...")

	// The context is used to inform the server it has 5 seconds to finish
	// the request it is currently handling
	var wg sync.WaitGroup
	for _, s := range servers {
		wg.Add(1)
		go func(srv *http.Server) {
			defer wg.Done()
			ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
			defer cancel()
			if err := srv.Shutdown(ctx); err != nil {
				logger.Log.Error(fmt.Sprintf("Server '%s' is forced to shutdown: %+v", srv.Addr, err))
			}
		}(s)
	}

	wg.Wait()

	logger.Log.Info("Server stopped")
}

type Router interface {
	Init(*gin.RouterGroup)
}
