/*
 * Copyright 2022 VMware, Inc.
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

	if IsHTTPSuccess(se.HTTPCode) {
		if _, ok := c.Get(VerFallbackKey); ok {
			se = &BusinessError{
				Code:    sgtnerror.StatusVersionFallbackTranslation.Code(),
				UserMsg: sgtnerror.StatusVersionFallbackTranslation.Message() + ": " + se.UserMsg}
		}
	}

	if ce := c.MustGet(LoggerKey).(*zap.Logger).Check(zap.DebugLevel, ""); ce != nil {
		ce.Write(zap.Any("business response", se))
	}

	// HTTP code is always 200
	c.JSON(http.StatusOK, Response{Error: se, Data: data})
}

func AbortWithError(c *gin.Context, err error) {
	GetLogger(c).Error(err.Error())
	bError := ToBusinessError(err)
	c.AbortWithStatusJSON(http.StatusOK, Response{Error: bError})
}

func ExtractParameters(c *gin.Context, uriPart, formPart interface{}) (err error) {
	if uriPart != nil {
		if err = c.ShouldBindUri(uriPart); err != nil {
			AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(ExtractErrorMsg(err)))
			return err
		}
	}

	if formPart != nil {
		if err = c.ShouldBindQuery(formPart); err != nil {
			AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(ExtractErrorMsg(err)))
			return err
		}
	}

	return nil
}

// ToBusinessError ...
func ToBusinessError(err error) *BusinessError {
	if err == nil {
		return err_success
	}

	switch e := err.(type) {
	case *sgtnerror.MultiError:
		if e.ErrorOrNil() == nil {
			return err_success
		}
		if e.IsAllFailed() {
			// If all the operations are failed, return the first error code.
			for _, err := range e.Errors() {
				if se, ok := err.(sgtnerror.Error); ok {
					return &BusinessError{Code: se.Code(), HTTPCode: se.HTTPCode(), UserMsg: e.Error()}
				}
			}
			return &BusinessError{Code: sgtnerror.UnknownError.Code(), HTTPCode: sgtnerror.UnknownError.HTTPCode(), UserMsg: e.Error()}
		} else {
			return err_207
		}
	case sgtnerror.Error:
		return &BusinessError{Code: e.Code(), HTTPCode: e.HTTPCode(), UserMsg: e.Message()}
	default:
		return &BusinessError{Code: sgtnerror.UnknownError.Code(), HTTPCode: sgtnerror.UnknownError.HTTPCode(), UserMsg: err.Error()}
	}
}
