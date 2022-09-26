/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

type TranslationStatus int8

func (s TranslationStatus) IsReady() bool                { return s == TranslationValid || s == PseudoFound }
func (s TranslationStatus) String() string               { return statusString[s] }
func (s TranslationStatus) MarshalText() ([]byte, error) { return []byte(statusString[s]), nil }

const (
	TranslationValid TranslationStatus = iota
	FallbackToEn
	FallbackToSource
	SourceUpdated
	PseudoFound
	PseudoNotFound
)

var statusString = [...]string{
	TranslationValid: "The translation is found and returned",
	FallbackToEn:     "The translation is not found, English found, return the English as translation",
	FallbackToSource: "The translation is not found, English not found, return the received source",
	SourceUpdated:    "The translation is not found or it is not latest, return the received source",
	PseudoFound:      "The pseudo translation is found",
	PseudoNotFound:   "The pseudo translation is not found, return the received source with pseudo tag",
}
