/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type source interface {
	// GetComponentList Get component list
	GetComponentList(name, version string) ([]string, error)

	// GetComponentMessages Get component messages
	GetComponentMessages(name, version, component string) (ComponentMsgs, error)
}
