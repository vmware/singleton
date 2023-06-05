/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import (
	jsoniter "github.com/json-iterator/go"
)

type Release struct {
	Name    string
	Version string
	Pseudo  bool
	Bundles []*Bundle
}

type (
	// Bundle ...
	Bundle struct {
		ID       BundleID
		Pseudo   bool
		Messages jsoniter.Any
	}

	// BundleID ...
	BundleID struct {
		Name      string
		Version   string
		Locale    string
		Component string
	}

	MessageID struct {
		Name      string
		Version   string
		Locale    string
		Component string
		Key       string
	}

	// CompactBundleID ...
	CompactBundleID struct {
		Locale    string
		Component string
	}
)

type (
	// StringMessage ...
	StringMessage struct {
		Name    string `json:"productName"`
		Version string `json:"version"`

		Locale    string `json:"locale"`
		Component string `json:"component"`

		Key         string `json:"key"`
		Translation string `json:"translation"`
	}

	// BundleFile ...
	BundleFile struct {
		Component string       `json:"component"`
		Locale    string       `json:"locale"`
		Messages  jsoniter.Any `json:"messages"`
	}
)
