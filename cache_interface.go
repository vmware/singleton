/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

// Cache the interface of Cache
type Cache interface {
	// GetLocales Get locale list
	GetLocales(name, version string) []string

	// SetLocales Store locale list
	SetLocales(name, version string, locales []string)

	// GetComponents Get component list
	GetComponents(name, version string) []string

	// SetComponents Store component list
	SetComponents(name, version string, components []string)

	// GetComponentMessages Get component messages
	GetComponentMessages(name, version, locale, component string) (data ComponentMsgs, found bool)

	// SetComponentMessages Store component messages
	SetComponentMessages(name, version, locale, component string, data ComponentMsgs)
}
