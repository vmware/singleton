/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type source interface {
	// GetComponentList Get component list
	GetComponentList(name, version string) ([]string, error)

	// GetStringMessage Get a message with optional arguments
	// GetStringMessage(name, version, component, key string, args ...string) (string, error)

	// GetComponentMessages Get component messages
	GetComponentMessages(name, version, component string) (ComponentMsgs, error)

	// Get(key, component string) (value string, found bool)

	// Keys(component string) []string
}
