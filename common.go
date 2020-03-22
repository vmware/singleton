/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import "fmt"

// ComponentMsgs The interface of a component's messages
type ComponentMsgs interface {
	// Get Get a message by key
	Get(key string) (value string, found bool)

	// Size Get the size of messages
	Size() int
}

// Logger The logger interface
type Logger interface {
	Debug(message string)
	Info(message string)
	Warn(message string)
	Error(message string)
}

type (
	translationID struct {
		Name, Version string
	}

	componentID struct {
		Name, Version, Locale, Component string
	}
)

//!+ error definition

type serverError struct {
	code         int
	businessCode int
	msg          string
	businessMsg  string
}

func (e *serverError) Error() string {
	return fmt.Sprintf("Error from server is HTTP code: %d, message: %s, business code: %d, message: %s", e.code, e.msg, e.businessCode, e.businessMsg)
}

//!- error definition

//!+ dataItem
type itemType int

const (
	itemComponent itemType = iota
	itemLocales
	itemComponents
)

func (t itemType) String() string {
	switch t {
	case itemComponent:
		return "component"
	case itemLocales:
		return "locales"
	case itemComponents:
		return "components"
	default:
		return ""
	}
}

type dataItem struct {
	iType itemType
	id    interface{}
	data  interface{}
	attrs interface{}
}

//!- dataItem
