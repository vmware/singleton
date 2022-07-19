// Code generated by github.com/99designs/gqlgen, DO NOT EDIT.

package model

import (
	"sgtnserver/types"
)

type Bundle struct {
	ID        string      `json:"id"`
	Product   string      `json:"product"`
	Version   string      `json:"version"`
	Component string      `json:"component"`
	Locale    string      `json:"locale"`
	Messages  types.Bytes `json:"messages"`
}

type Locales struct {
	ID           string   `json:"id"`
	LanguageTags []string `json:"languageTags"`
}
