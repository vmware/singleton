/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type messageOrigin interface {
	Get(item *dataItem) error
	IsExpired(item *dataItem) bool
}

type messageOriginList []messageOrigin
