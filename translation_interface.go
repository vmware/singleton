/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

// Translation interface of translation
type Translation interface {
	// GetLocaleList Get locale list
	GetLocaleList(name, version string) ([]string, error)

	// GetComponentList Get component list
	GetComponentList(name, version string) ([]string, error)

	// GetStringMessage Get a message with optional arguments
	GetStringMessage(name, version, locale, component, key string, args ...string) (string, error)

	// GetComponentMessages Get component messages
	GetComponentMessages(name, version, locale, component string) (ComponentMsgs, error)
}
