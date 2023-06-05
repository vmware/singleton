/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"fmt"
	"net"
	"os"
	"sgtnserver/api"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"
	"strconv"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
)

func TestServer(t *testing.T) {
	logger.Log.Sync()

	done := capture()

	oldLog := logger.Log
	defer func() {
		logger.Log = oldLog
	}()

	core := zapcore.NewCore(zapcore.NewConsoleEncoder(zap.NewDevelopmentEncoderConfig()), zapcore.Lock(os.Stdout), zapcore.DebugLevel)
	logger.Log = zap.New(core)

	oldSechema := config.Settings.Server.Schema
	config.Settings.Server.Schema = "https&http"
	defer func() {
		config.Settings.Server.Schema = oldSechema
	}()

	api.StartServer()
	rawConnect("localhost", []string{strconv.Itoa(config.Settings.Server.HTTPPort), strconv.Itoa(config.Settings.Server.HTTPSPort)})
	api.ShutdownServer()

	output, _ := done()
	assert.Contains(t, output, fmt.Sprintf("Start 'https' server listening on :%d", config.Settings.Server.HTTPSPort))
	assert.Contains(t, output, fmt.Sprintf("Start 'http' server listening on :%d", config.Settings.Server.HTTPPort))
	assert.Contains(t, output, fmt.Sprintf("Opened localhost:%d", config.Settings.Server.HTTPSPort))
	assert.Contains(t, output, fmt.Sprintf("Opened localhost:%d", config.Settings.Server.HTTPPort))
	assert.Contains(t, output, "Server stopped")
}

func rawConnect(host string, ports []string) {
	for _, port := range ports {
		timeout := time.Second
		conn, err := net.DialTimeout("tcp", net.JoinHostPort(host, port), timeout)
		if err != nil {
			fmt.Println("Connecting error:", err)
		}
		if conn != nil {
			defer conn.Close()
			fmt.Println("Opened", net.JoinHostPort(host, port))
		}
	}
}
