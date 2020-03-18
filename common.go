/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import "fmt"

type (

	// ComponentMsgs The interface of a component's messages
	ComponentMsgs interface {
		// Get Get a message by key
		Get(key string) (value string, found bool)

		// Size Get the size of messages
		Size() int
	}

	// Logger The logger interface
	Logger interface {
		Debug(message string)
		Info(message string)
		Warn(message string)
		Error(message string)
	}

	errorType uint16
	sgtnError struct {
		errorType errorType
		errorCode int
		errorMsg  string
	}
)

const (
	serverError errorType = iota
	clientError
)

func (et errorType) String() string {
	switch et {
	case serverError:
		return "Error from server"
	default:
		return ""
	}
}

func (e sgtnError) Error() string {
	return fmt.Sprintf("%s is code: %d, message: %s", e.errorType.String(), e.errorCode, e.errorMsg)
}
