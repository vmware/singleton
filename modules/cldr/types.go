/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

type LanguageData struct {
	Territories []string `json:"_territories"`
	Scripts     []string `json:"_scripts"`
}

type LocaleAlias struct {
	Replacement string `json:"_replacement"`
}
