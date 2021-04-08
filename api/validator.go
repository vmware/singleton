/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package api

import (
	"reflect"
	"regexp"
	"strings"

	"github.com/gin-gonic/gin/binding"
	"github.com/go-playground/locales/en"
	ut "github.com/go-playground/universal-translator"
	"github.com/go-playground/validator/v10"
	enTranslations "github.com/go-playground/validator/v10/translations/en"
)

const (
	numberAndDotString                = `^\d+(\.\d+)*$`
	letterAndNumberAndValidCharString = `[A-Za-z0-9_\-\.]+`
	letterArrayString                 = `^(\s*[a-zA-Z]+\s*)(,\s*[a-zA-Z]+\s*)*$`

	localeString = `[A-Za-z0-9_\-\.]+`
)

var (
	numberAndDotRegx                = regexp.MustCompile(numberAndDotString)
	letterAndNumberAndValidCharRegx = regexp.MustCompile(`^` + letterAndNumberAndValidCharString + `$`)
	letterArrayRegx                 = regexp.MustCompile(letterArrayString)

	versionRegex    = numberAndDotRegx
	componentRegex  = letterAndNumberAndValidCharRegx
	localeRegex     = regexp.MustCompile(`^` + localeString + `$`)
	componentsRegex = regexp.MustCompile(`^` + letterAndNumberAndValidCharString + `(,\s*` + letterAndNumberAndValidCharString + `)*$`)
	localesRegex    = regexp.MustCompile(`^` + localeString + `(,\s*` + localeString + `)*$`)
	languageRegex   = localeRegex
	regionRegex     = localeRegex
	scopeRegex      = letterArrayRegx
	keyRegex        = letterAndNumberAndValidCharRegx
)

var (
	uni                 *ut.UniversalTranslator
	ValidatorTranslator ut.Translator
)

func InitValidator() {
	v, ok := binding.Validator.Engine().(*validator.Validate)
	if !ok {
		return
	}

	v.RegisterTagNameFunc(func(fld reflect.StructField) string {
		tagValue := fld.Tag.Get("uri")
		if tagValue == "" {
			tagValue = fld.Tag.Get("form")
			if tagValue == "" {
				tagValue = fld.Tag.Get("json")
			}
		}
		name := strings.SplitN(tagValue, ",", 2)[0]
		if name == "-" {
			return ""
		}
		return name
	})

	enTranslator := en.New()
	uni = ut.New(enTranslator, enTranslator)
	ValidatorTranslator, _ = uni.GetTranslator("en")
	_ = enTranslations.RegisterDefaultTranslations(v, ValidatorTranslator)

	var validatorMap = map[string]*regexp.Regexp{
		VersionAPIKey:    versionRegex,
		ComponentAPIKey:  componentRegex,
		LocaleAPIKey:     localeRegex,
		LanguageAPIKey:   languageRegex,
		RegionAPIKey:     regionRegex,
		ScopeAPIKey:      scopeRegex,
		ComponentsAPIKey: componentsRegex,
		LocalesAPIKey:    localesRegex,
		KeyAPIKey:        keyRegex,
	}
	for name, regex := range validatorMap {
		name, regex := name, regex
		_ = v.RegisterValidation(name, func(fl validator.FieldLevel) bool {
			return regex.MatchString(fl.Field().Interface().(string))
		})
		_ = v.RegisterTranslation(name, ValidatorTranslator, func(ut ut.Translator) error {
			return ut.Add(name, "{0} '{1}' is invalid!", true)
		}, func(ut ut.Translator, fe validator.FieldError) string {
			t, _ := ut.T(name, fe.Field(), fe.Value().(string))
			return t
		})
	}
}

func ExtractErrorMsg(err error) string {
	if vErrors, ok := err.(validator.ValidationErrors); ok {
		builder := strings.Builder{}
		for _, e := range vErrors {
			builder.WriteString(e.Translate(ValidatorTranslator))
			builder.WriteString("; ")
		}
		return builder.String()
	}
	return err.Error()
}
