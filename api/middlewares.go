/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package api

import (
	"math/rand"
	"net"
	"net/http"
	"net/http/httputil"
	"os"
	"strings"
	"time"

	"github.com/gin-contrib/cors"

	"sgtnserver/internal/common"
	"sgtnserver/internal/config"

	brotli "github.com/anargu/gin-brotli"
	"github.com/gin-contrib/gzip"
	"github.com/gin-gonic/gin"
	"github.com/go-http-utils/headers"
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
)

// GinZap returns a gin.HandlerFunc (middleware) that logs requests using uber-go/zap.
func GinZap(log *zap.Logger) gin.HandlerFunc {
	rander := rand.New(rand.NewSource(time.Now().UnixNano()))

	var outsideIDTracer func(c *gin.Context) (fields []zap.Field)
	if len(config.Settings.HeaderOfTraceID) > 0 {
		headerNames := strings.Split(config.Settings.HeaderOfTraceID, ",")
		outsideIDTracer = func(c *gin.Context) (fields []zap.Field) {
			for _, headerName := range headerNames {
				if v := c.Request.Header.Get(headerName); len(v) > 0 {
					fields = append(fields, zap.String(headerName, v))
				}
			}
			return
		}
	}

	return func(c *gin.Context) {
		start := time.Now()

		// Add ID field to the logger
		newLog := log.With(zap.Uint32("traceId", rander.Uint32()))
		c.Set(LoggerKey, newLog)

		// Print start message
		if ce := newLog.Check(zap.InfoLevel, "Start a request"); ce != nil {
			fields := []zapcore.Field{
				zap.String("method", c.Request.Method),
				zap.String("ip", c.ClientIP()),
				zap.String("path", c.Request.URL.Path)}
			if query := c.Request.URL.RawQuery; len(query) > 0 {
				fields = append(fields, zap.String("query", query))
			}
			ce.Write(fields...)
		}

		// Print outside trace IDs
		if outsideIDTracer != nil {
			if ce := newLog.Check(zap.InfoLevel, "Outside trace IDs"); ce != nil {
				if traceIDs := outsideIDTracer(c); len(traceIDs) > 0 {
					ce.Write(append([]zap.Field{zap.Namespace("IDs")}, traceIDs...)...)
				}
			}
		}

		defer func() {
			if ce := newLog.Check(zap.InfoLevel, "End a request"); ce != nil {
				ce.Write(zap.Int("status", c.Writer.Status()), zap.Duration("latency", time.Now().Sub(start)))
			}
		}()

		c.Next()
	}
}

func RecoveryWithZap(log *zap.Logger) gin.HandlerFunc {
	return func(c *gin.Context) {
		defer func() {
			err := recover()
			if err == nil {
				return
			}

			if zLog, ok := c.Get(LoggerKey); ok {
				log = zLog.(*zap.Logger)
			}

			// Check for a broken connection, as it is not really a
			// condition that warrants a panic stack trace.
			var brokenPipe bool
			if ne, ok := err.(*net.OpError); ok {
				if se, ok := ne.Err.(*os.SyscallError); ok {
					if strings.Contains(strings.ToLower(se.Error()), "broken pipe") || strings.Contains(strings.ToLower(se.Error()), "connection reset by peer") {
						brokenPipe = true
					}
				}
			}

			fields := []zapcore.Field{zap.Time("time", time.Now()), zap.String("path", c.Request.URL.Path), zap.Any("error", err)}
			if gin.IsDebugging() {
				httpRequest, _ := httputil.DumpRequest(c.Request, true)
				hds := strings.Split(string(httpRequest), "\r\n")
				for idx, header := range hds {
					current := strings.Split(header, ":")
					if current[0] == "Authorization" {
						hds[idx] = current[0] + ": *"
					}
				}
				fields = append(fields, zap.Strings("headers", hds), zap.ByteString("request", httpRequest))
			}
			log.Error("[Recovery from panic]", fields...)

			// If the connection is dead, we can't write a status to it.
			if brokenPipe {
				c.Abort()
			} else {
				c.AbortWithStatus(http.StatusInternalServerError)
			}
		}()

		c.Next()
	}
}

func CompressResponse() func(*gin.Context) {
	var processors []func(c *gin.Context) bool
	for _, algorithm := range strings.Split(config.Settings.Server.CompressionAlgorithm, common.ParamAnd) {
		switch algorithm {
		case CompressionBrotli:
			brCompressor := brotli.Brotli(brotli.DefaultCompression)
			handler := func(c *gin.Context) bool {
				if strings.Contains(c.Request.Header.Get(headers.AcceptEncoding), CompressionBrotli) {
					brCompressor(c)
					return true
				}
				return false
			}
			processors = append(processors, handler)
		case CompressionGzip:
			gzipCompressor := gzip.Gzip(gzip.DefaultCompression)
			handler := func(c *gin.Context) bool {
				if strings.Contains(c.Request.Header.Get(headers.AcceptEncoding), CompressionGzip) {
					gzipCompressor(c)
					return true
				}
				return false
			}
			processors = append(processors, handler)
		}

	}

	return func(c *gin.Context) {
		for _, processor := range processors {
			if processor(c) {
				break
			}
		}
	}
}

func HandleCrossDomain() gin.HandlerFunc {
	return cors.New(cors.Config{
		AllowOrigins:     strings.Split(config.Settings.CrossDomain.AllowOrigin, ","),
		AllowMethods:     strings.Split(config.Settings.CrossDomain.AllowMethods, ","),
		AllowHeaders:     strings.Split(config.Settings.CrossDomain.AllowHeaders, ","),
		AllowCredentials: config.Settings.CrossDomain.AllowCredentials,
		MaxAge:           time.Duration(config.Settings.CrossDomain.MaxAge) * time.Second,
	})
}
