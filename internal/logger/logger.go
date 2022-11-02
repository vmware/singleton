/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package logger

import (
	"os"

	"sgtnserver/internal/config"

	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
	"gopkg.in/natefinch/lumberjack.v2"
)

var (
	// Log ...
	Log *zap.Logger

	// SLog Zap Sugar Logger
	SLog *zap.SugaredLogger

	// LogLevel ...
	// LogLevel zap.AtomicLevel
)

// InitLogger .
func InitLogger() {
	var cores []zapcore.Core

	logLevel := config.Settings.LOG.ConsoleLevel

	// First, define our level-handling logic.
	highPriority := zap.LevelEnablerFunc(func(lvl zapcore.Level) bool {
		return lvl >= zapcore.ErrorLevel && lvl >= logLevel
	})
	lowPriority := zap.LevelEnablerFunc(func(lvl zapcore.Level) bool {
		return lvl < zapcore.ErrorLevel && lvl >= logLevel
	})

	// High-priority output should also go to standard error, and low-priority
	// output should also go to standard out.
	consoleDebugging := zapcore.Lock(os.Stdout)
	consoleErrors := zapcore.Lock(os.Stderr)

	// file log
	if config.Settings.LOG.Filename != "" {
		w := zapcore.AddSync(&lumberjack.Logger{
			Filename:   config.Settings.LOG.Filename,
			MaxSize:    config.Settings.LOG.MaxSize,
			MaxBackups: config.Settings.LOG.MaxBackups,
			MaxAge:     config.Settings.LOG.MaxAge,
		})
		lumberjackCore := zapcore.NewCore(
			zapcore.NewJSONEncoder(zap.NewProductionEncoderConfig()),
			w,
			config.Settings.LOG.Level,
		)
		cores = append(cores, lumberjackCore)
	}

	// Console Log
	consoleEncoder := zapcore.NewConsoleEncoder(zap.NewDevelopmentEncoderConfig())

	cores = append(cores,
		zapcore.NewCore(consoleEncoder, consoleErrors, highPriority),
		zapcore.NewCore(consoleEncoder, consoleDebugging, lowPriority))
	core := zapcore.NewTee(cores...)

	Log = zap.New(core, zap.AddStacktrace(highPriority))

	SLog = Log.Sugar()
	Log.Debug("Created logger")
}

func init() {
	InitLogger()
}
