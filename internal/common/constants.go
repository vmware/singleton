/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package common

import "regexp"

const (
	ParamAnd = "&"
	ParamSep = ","
)

var (
	ParamAndSplitRegexp = regexp.MustCompile(`\s*` + ParamAnd + `\s*`)
	ParamSepSplitRegexp = regexp.MustCompile(`\s*` + ParamSep + `\s*`)
)

const (
	FailToReadCache = "Fail to read from cache: '%s'"
)
