/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

 package CustomMethod

 import (
	 "os"
 
	 "github.com/rs/zerolog"
	 "github.com/rs/zerolog/log"
 )
 
 type defaultLogger struct {
	 logger zerolog.Logger
 }
 
 func newLogger() *defaultLogger {
	 l := new(defaultLogger)
	 file, err := os.Create("logfile.txt")
	 if err != nil {
		 log.Fatal().Err(err).Msg("无法创建日志文件")
	 }
	 //defer file.Close()
	 
	 zerolog.TimeFieldFormat = zerolog.TimeFormatUnix
	 zerolog.SetGlobalLevel(zerolog.DebugLevel)
 
	 l.logger = zerolog.New(file).With().Timestamp().Logger()
	
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