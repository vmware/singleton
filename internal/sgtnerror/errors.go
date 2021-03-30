/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtnerror

import (
	"fmt"
	"net/http"
)

var (
	StatusSuccess        = Error{code: http.StatusOK, message: "OK"}
	StatusPartialSuccess = Error{code: 207, message: "Successful Partially"}

	StatusBadRequest    = Error{code: http.StatusBadRequest, message: "Bad Request"}
	StatusNotFound      = Error{code: http.StatusNotFound, message: "Not Found"}
	StatusInvalidToken  = Error{code: 498, message: "Invalid Token"}
	StatusTokenRequired = Error{code: 499, message: "Token Required"}

	StatusVersionFallbackTranslation = Error{code: 604, message: "Version has been fallen back"}

	StatusInternalServerError = Error{code: http.StatusInternalServerError, message: "Internal Server Error"}
	UnknownError              = Error{code: 520, message: "Unknown Error"}
)

type Error struct {
	cause   error
	code    int
	message string
}

func (e Error) Code() int {
	return e.code
}

func (e Error) Message() string {
	return e.message
}

func (e Error) WithUserMessage(msg string, args ...interface{}) error {
	message := fmt.Sprintf(msg, args...)
	return &Error{
		cause:   nil,
		code:    e.code,
		message: e.message + ":" + message}
}

func (e Error) WrapError(err error) error {
	return e.WrapErrorWithMessage(err, "")
}

func (e Error) WrapErrorWithMessage(err error, userMsg string, args ...interface{}) error {
	if err == nil {
		return nil
	}

	message := e.message
	if len(userMsg) > 0 {
		message += ":" + fmt.Sprintf(userMsg, args...)
	}
	return &Error{
		cause:   err,
		code:    e.code,
		message: message}
}

func (e Error) Error() string {
	var msg string
	if len(e.message) > 0 {
		msg += e.message
	}

	if e.cause != nil {
		msg += " : " + e.cause.Error()
	}

	return msg
}

type (
	coded interface {
		Code() int
	}
)

func GetCode(e error) int {
	if c, ok := e.(coded); ok {
		return c.Code()
	}
	return UnknownError.code
}
