/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"context"
	"net/http"
	"testing"
	"time"

	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/formatting"

	"github.com/stretchr/testify/assert"
)

func TestSimpleDateTime(t *testing.T) {
	data := []struct {
		testName        string
		LongDate        int64
		pattern, locale string
		expected        string
		wantedCode      int
	}{
		{testName: "test en-US", LongDate: 1472728030290, pattern: "MMMdy", locale: "en-US", expected: "Sep12016", wantedCode: http.StatusOK},
		{testName: "test zh-Hans", LongDate: 1472728030290, pattern: "MMMdy", locale: "zh-Hans", expected: "9月12016", wantedCode: http.StatusOK},
		{testName: "test fr", LongDate: 1472728030290, pattern: "MMMMd", locale: "fr", expected: "septembre1", wantedCode: http.StatusOK},
		{testName: "test en-AB", LongDate: 1472728030290, pattern: "MMMMd", locale: "en-AB", expected: "", wantedCode: sgtnerror.StatusNotFound.Code()},
	}

	for _, d := range data {
		d := d

		t.Run(d.testName, func(t *testing.T) {
			// t.Parallel()

			seconds, milliseconds := d.LongDate/1000, d.LongDate%1000*1000*1000
			tm := time.Unix(seconds, milliseconds)
			log.Infof("time is:%+v", tm)

			actual, err := formatting.SimpleFormatDateTime(context.TODO(), tm, d.pattern, d.locale)
			if d.wantedCode == http.StatusOK {
				assert.Nil(t, err)
				assert.Equal(t, d.expected, actual)
			} else {
				assert.Equal(t, "", actual)
				assert.Equal(t, d.wantedCode, sgtnerror.GetCode(err))
			}
		})
	}
}
