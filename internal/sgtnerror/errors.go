/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtnerror

import (
	"fmt"
	"net/http"
)

var (
	StatusSuccess        = Error{code: http.StatusOK, httpCode: http.StatusOK, message: "OK"}
	TranslationNotReady  = Error{code: 205, httpCode: http.StatusOK, message: "translations are not ready"}
	TranslationReady     = Error{code: 206, httpCode: http.StatusOK, message: "translations are ready"}
	StatusPartialSuccess = Error{code: 207, httpCode: http.StatusOK, message: "Successful Partially"}

	StatusBadRequest    = Error{code: http.StatusBadRequest, httpCode: http.StatusBadRequest, message: "Bad Request"}
	StatusUnauthorized  = Error{code: http.StatusUnauthorized, httpCode: http.StatusBadRequest, message: http.StatusText(http.StatusUnauthorized)}
	StatusNotFound      = Error{code: http.StatusNotFound, httpCode: http.StatusBadRequest}
	StatusInvalidToken  = Error{code: 498, httpCode: 498, message: "Invalid Token"}
	StatusTokenRequired = Error{code: 499, httpCode: 499, message: "Token Required"}

	StatusVersionFallbackTranslation = Error{code: 604, message: "Version has been fallen back"}

	StatusInternalServerError = Error{code: http.StatusInternalServerError, httpCode: http.StatusInternalServerError, message: "Internal Server Error"}
	UnknownError              = Error{code: 520, httpCode: 520, message: "Unknown Error"}
)

type Error struct {
	cause          error
	code, httpCode int
	message        string
}

func (e Error) Code() int {
	return e.code
}

func (e Error) HTTPCode() int {
	return e.httpCode
}

func (e Error) Message() string {
	return e.message
}

func (e Error) WithUserMessage(msg string, args ...interface{}) error {
	message := fmt.Sprintf(msg, args...)
	return Error{
		cause:    nil,
		code:     e.code,
		httpCode: e.httpCode,
		message:  message}
}

func (e Error) WrapErrorWithMessage(err error, userMsg string, args ...interface{}) error {
	if err == nil {
		return nil
	}

	return Error{
		cause:    err,
		code:     e.code,
		httpCode: e.httpCode,
		message:  fmt.Sprintf(userMsg, args...)}
}

func (e Error) Error() string {
	var msg string
	if len(e.message) > 0 {
		msg += e.message
	}

	if e.cause != nil {
		msg += ": " + e.cause.Error()
	}

	return msg
}

type (
	Coded interface {
		Code() int
	}

	Messager interface {
		Message() string
	}
)

func GetCode(e error) int {
	if c, ok := e.(Coded); ok {
		return c.Code()
	}
	return UnknownError.code
}

func GetUserMessage(err error) string {
	if e, ok := err.(Messager); ok {
		return e.Message()
	}
	return err.Error()
}
