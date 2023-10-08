/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package countryflag

type FlagScale int8

const (
	Flag1x1 FlagScale = iota + 1
	Flag3x2
)

func (fs FlagScale) String() string {
	switch fs {
	case Flag1x1:
		return "1x1"
	case Flag3x2:
		return "3x2"
	default:
		return ""
	}
}
