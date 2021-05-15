/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package api

import (
	"fmt"
	"regexp"
	"sgtnserver/internal/logger"
	"strings"

	"github.com/gin-gonic/gin/binding"
	"github.com/go-playground/locales/en"
	ut "github.com/go-playground/universal-translator"
	"github.com/go-playground/validator/v10"
)

const (
	letterAndNumberAndValidCharString      = `[A-Za-z0-9_\-\.]+`
	letterAndNumberAndValidCharStringError = "Incorrect %s(only allows letter, number, dot, underline, dash)"
)

var (
	letterAndNumberAndValidCharRegx = regexp.MustCompile(`^` + letterAndNumberAndValidCharString + `$`)
	versionRegex                    = regexp.MustCompile(`^\d+(\.\d+)*$`)
	componentsRegex                 = regexp.MustCompile(`^` + letterAndNumberAndValidCharString + `(,\s*` + letterAndNumberAndValidCharString + `)*$`)
	localesRegex                    = componentsRegex
	scopeRegex                      = regexp.MustCompile(`^(\s*[a-zA-Z]+\s*)(,\s*[a-zA-Z]+\s*)*$`)
)

var validatorInfoArray = [][]interface{}{
	{VersionAPIKey, versionRegex, "Incorrect " + VersionAPIKey + "(only allows number, dot. such as 1.0.0)"},
	{ComponentAPIKey, letterAndNumberAndValidCharRegx, fmt.Sprintf(letterAndNumberAndValidCharStringError, ComponentAPIKey)},
	{LocaleAPIKey, letterAndNumberAndValidCharRegx, fmt.Sprintf(letterAndNumberAndValidCharStringError, LocaleAPIKey)},
	{LanguageAPIKey, letterAndNumberAndValidCharRegx, fmt.Sprintf(letterAndNumberAndValidCharStringError, LanguageAPIKey)},
	{RegionAPIKey, letterAndNumberAndValidCharRegx, fmt.Sprintf(letterAndNumberAndValidCharStringError, RegionAPIKey)},
	{ScopeAPIKey, scopeRegex, "Incorrect " + ScopeAPIKey},
	{ComponentsAPIKey, componentsRegex, fmt.Sprintf(letterAndNumberAndValidCharStringError, ComponentsAPIKey)},
	{LocalesAPIKey, localesRegex, fmt.Sprintf(letterAndNumberAndValidCharStringError, LocalesAPIKey)},
	{KeyAPIKey, letterAndNumberAndValidCharRegx, fmt.Sprintf(letterAndNumberAndValidCharStringError, KeyAPIKey)},
}

var enTranslator ut.Translator

func InitValidator() {
	validate, ok := binding.Validator.Engine().(*validator.Validate)
	if !ok {
		return
	}

	enLocale := en.New()
	uTranslator := ut.New(enLocale)
	enTranslator, _ = uTranslator.GetTranslator(enLocale.Locale())

	for _, info := range validatorInfoArray {
		name, r := info[0].(string), info[1].(*regexp.Regexp)
		err := validate.RegisterValidation(name,
			func(fl validator.FieldLevel) bool {
				return r.MatchString(fl.Field().Interface().(string))
			})
		if err == nil {
			err = validate.RegisterTranslation(name, enTranslator,
				func(ut ut.Translator) error {
					return ut.Add(name, info[2].(string), true)
				},
				func(ut ut.Translator, fe validator.FieldError) string {
					t, err := ut.T(fe.Tag())
					if err != nil {
						logger.Log.Warn(err.Error())
					}
					return t
				})
		}
		if err != nil {
			logger.Log.Fatal(err.Error())
		}
	}
}

func ExtractErrorMsg(err error) string {
	if vErrors, ok := err.(validator.ValidationErrors); ok {
		msgs := make([]string, len(vErrors))
		for i, e := range vErrors {
			msgs[i] = e.Translate(enTranslator)
		}
		return strings.Join(msgs, "; ")
	}
	return err.Error()
}
