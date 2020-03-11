/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type (
	// Cache the interface of Cache
	Cache interface {
		// GetLocales Get locale list
		GetLocales() []string

		// SetLocales Store locale list
		SetLocales(locales []string)

		// GetComponents Get component list
		GetComponents() []string

		// SetComponents Store component list
		SetComponents(components []string)

		// GetComponentMessages Get component messages
		GetComponentMessages(locale, component string) (data ComponentMsgs, found bool)

		// SetComponentMessages Store component messages
		SetComponentMessages(locale, component string, data ComponentMsgs)
	}

	// ComponentMsgs The interface of a component's messages
	ComponentMsgs interface {
		// Get Get a message by key
		Get(key string) (value string, found bool)

		// Size Get the size of messages
		Size() int
	}

	transMsgs interface {
		Put(key compAsKey, value ComponentMsgs)
		Get(key compAsKey) (value ComponentMsgs, found bool)
		Size() int
		Clear()
	}

	compAsKey struct {
		locale    string
		component string
	}

	// Logger The logger interface
	Logger interface {
		Debug(message string)
		Info(message string)
		Warn(message string)
		Error(message string)
	}

	// Translation interface of translation
	Translation interface {
		// GetLocaleList Get locale list
		GetLocaleList() ([]string, error)

		// GetComponentList Get component list
		GetComponentList() ([]string, error)

		// GetStringMessage Get a message with optional arguments
		GetStringMessage(locale string, component string, key string, args ...string) (string, error)

		// GetComponentMessages Get component messages
		GetComponentMessages(locale string, component string) (ComponentMsgs, error)
	}
)
