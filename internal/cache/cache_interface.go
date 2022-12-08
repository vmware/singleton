/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cache

// Cache the interface of cache
type Cache interface {
	// Get
	Get(key interface{}) (interface{}, error)

	// Set
	Set(key, value interface{}) error

	// Delete
	// Delete(key interface{}) error

	// Clear
	Clear() error

	// Wait cache is ready
	Wait()
}
