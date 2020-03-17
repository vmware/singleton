/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

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
)
