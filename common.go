/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"strings"

	"github.com/pkg/errors"
)

// ComponentMsgs The interface of a component's messages
type ComponentMsgs interface {
	// Get Get a message by key
	Get(key string) (value string, found bool)
}

// Logger The logger interface
type Logger interface {
	Debug(message string)
	Info(message string)
	Warn(message string)
	Error(message string)
}

type dataItemID struct {
	iType                            itemType
	Name, Version, Locale, Component string
}

type stackTracer interface {
	StackTrace() errors.StackTrace
}

//!+ error definition

type serverError struct {
	code         int
	businessCode int
	msg          string
	businessMsg  string
}

func (e *serverError) Error() string {
	return fmt.Sprintf("Error from server is HTTP code: %d, message: %s, business code: %d, message: %s",
		e.code, e.msg, e.businessCode, e.businessMsg)
}

//!- error definition

//!+ dataItem
type itemType int8

const (
	itemComponent itemType = iota
	itemLocales
	itemComponents
)

type dataItem struct {
	id    dataItemID
	data  interface{}
	attrs interface{}
}

//!- dataItem

func indexIgnoreCase(slices []string, item string) int {
	for i, s := range slices {
		if strings.EqualFold(s, item) {
			return i
		}
	}

	return -1
}
