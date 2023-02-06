/*
 * Copyright 2020-2023 VMware, Inc.
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

	Set(key, value string)

	Component() string

	Locale() string

	Range(func(key, value string) bool)
}

// Logger The logger interface
type Logger interface {
	Debug(message string)
	Info(message string)
	Warn(message string)
	Error(message string)
}

type releaseID struct {
	Name, Version string
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

const (
	localeLatest = "latest"
	localeEn     = "en"
)

type dataItem struct {
	id     dataItemID
	data   interface{}
	origin messageOrigin
	attrs  *itemCacheInfo
}

//!- dataItem

//!+ messageOrigin
type messageOrigin interface {
	Get(item *dataItem) error
	IsExpired(item *dataItem) bool
}

type messageOriginList []messageOrigin

//!- messageOrigin

func indexIgnoreCase(slices []string, item string) int {
	for i, s := range slices {
		if strings.EqualFold(s, item) {
			return i
		}
	}

	return -1
}

func uniqueStrings(slices ...[]string) []string {
	uniqueMap := map[string]bool{}

	for _, strSlice := range slices {
		for _, str := range strSlice {
			uniqueMap[str] = true
		}
	}

	// Create a slice with the capacity of unique items
	// This capacity make appending flow much more efficient
	result := make([]string, 0, len(uniqueMap))

	for key := range uniqueMap {
		result = append(result, key)
	}

	return result
}
