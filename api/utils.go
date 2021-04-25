/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package api

import (
	"crypto/sha1"
	"fmt"
	"net/http"

	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
)

func IsHTTPSuccess(status int) bool {
	return status >= 200 && status < 300
}

// GenerateEtag Generates an Etag for given string.
func GenerateEtag(bts []byte, weak bool) string {
	tag := fmt.Sprintf("\"%d-%s\"", len(bts), fmt.Sprintf("%x", sha1.Sum(bts)))
	if weak {
		tag = "W/" + tag
	}

	return tag
}

func GetLogger(c *gin.Context) *zap.Logger {
	if l, ok := c.Get(LoggerKey); ok {
		return l.(*zap.Logger)
	}

	return logger.Log
}

func HandleResponse(c *gin.Context, data interface{}, err error) {
	se := ToBusinessError(err)
	httpCode := se.Code

	if c.Request.Method == http.MethodGet && IsHTTPSuccess(httpCode) {
		if _, ok := c.Get(VerFallbackKey); ok {
			se = &BusinessError{
				Code:    sgtnerror.StatusVersionFallbackTranslation.Code(),
				UserMsg: sgtnerror.StatusVersionFallbackTranslation.Message() + ": " + se.UserMsg}
		}
	}

	if ce := c.MustGet(LoggerKey).(*zap.Logger).Check(zap.DebugLevel, ""); ce != nil {
		ce.Write(zap.Any("business response", se))
	}

	c.JSON(httpCode, Response{Error: se, Data: data})
}

func AbortWithError(c *gin.Context, err error) {
	GetLogger(c).Error(err.Error())
	c.AbortWithStatusJSON(sgtnerror.GetCode(err), Response{Error: ToBusinessError(err)})
}

// ToBusinessError ...
func ToBusinessError(err error) *BusinessError {
	if err == nil {
		return &BusinessError{Code: sgtnerror.StatusSuccess.Code(), UserMsg: sgtnerror.StatusSuccess.Message()}
	}

	switch e := err.(type) {
	case *sgtnerror.MultiError:
		if e.ErrorOrNil() == nil {
			return &BusinessError{Code: sgtnerror.StatusSuccess.Code(), UserMsg: sgtnerror.StatusSuccess.Message()}
		}
		if e.IsAllFailed() {
			// If all the operations are failed, return the first error code.
			for _, err := range e.Errors() {
				if se, ok := err.(*sgtnerror.Error); ok {
					return &BusinessError{Code: se.Code(), UserMsg: e.Error()}
				}
			}
			return &BusinessError{Code: sgtnerror.UnknownError.Code(), UserMsg: e.Error()}
		}
		return &BusinessError{Code: sgtnerror.StatusPartialSuccess.Code(), UserMsg: sgtnerror.StatusPartialSuccess.Message()}
	case *sgtnerror.Error:
		if e == nil {
			return &BusinessError{Code: sgtnerror.StatusSuccess.Code(), UserMsg: sgtnerror.StatusSuccess.Message()}
		}
		return &BusinessError{Code: e.Code(), UserMsg: e.Message()}
	default:
		return &BusinessError{Code: sgtnerror.UnknownError.Code(), UserMsg: err.Error()}
	}
}
