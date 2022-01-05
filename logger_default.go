/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"os"

	"github.com/rs/zerolog"
)

type defaultLogger struct {
	logger zerolog.Logger
}

func newLogger() Logger {
	l := new(defaultLogger)

	zerolog.TimeFieldFormat = zerolog.TimeFormatUnix
	zerolog.SetGlobalLevel(zerolog.InfoLevel)

	l.logger = zerolog.New(os.Stderr).With().Timestamp().Logger()

	return l
}

func (l *defaultLogger) Debug(message string) {
	l.logger.Debug().Msg(message)
}

func (l *defaultLogger) Info(message string) {
	l.logger.Info().Msg(message)
}
func (l *defaultLogger) Warn(message string) {
	l.logger.Warn().Msg(message)
}
func (l *defaultLogger) Error(message string) {
	l.logger.Error().Msg(message)
}
