/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestBundleGetComponents(t *testing.T) {
	testCfg := backCfg
	testInst, _ := replaceInst(&testCfg)

	bundle := testInst.trans.dService.bundle

	comps, err := bundle.getComponents()

	assert.Nil(t, err)
	assert.Equal(t, 2, len(comps))
}

func TestBundleGetLocales(t *testing.T) {
	testCfg := backCfg
	testInst, _ := replaceInst(&testCfg)

	bundle := testInst.trans.dService.bundle

	locales, err := bundle.getLocales()
	logger.Debug(fmt.Sprintf("%#v\n", locales))
	assert.Nil(t, err)
	assert.Equal(t, 16, len(locales))
}

func TestBundleGetCompMessages(t *testing.T) {
	testCfg := backCfg
	testInst, _ := replaceInst(&testCfg)

	bundle := testInst.trans.dService.bundle

	locale := "fr"
	comp := "sunglow"
	msgs, err := bundle.getComponentMessages(locale, comp)
	assert.Nil(t, err)
	assert.Equal(t, 4, msgs.Size())
}
