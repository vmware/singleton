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
	testInst := resetInst(&testCfg)
	bundle := testInst.trans.bundle

	comps, err := bundle.getComponents(name, version)

	assert.Nil(t, err)
	assert.Equal(t, 2, len(comps))
}

func TestBundleGetLocales(t *testing.T) {
	testInst := resetInst(&testCfg)

	bundle := testInst.trans.bundle

	locales, err := bundle.getLocales(name, version)
	logger.Debug(fmt.Sprintf("%#v\n", locales))
	assert.Nil(t, err)
	assert.Equal(t, 16, len(locales))
}

func TestBundleGetCompMessages(t *testing.T) {
	testInst := resetInst(&testCfg)

	bundle := testInst.trans.bundle

	locale := "fr"
	comp := "sunglow"
	msgs, err := bundle.getComponentMessages(name, version, locale, comp)
	assert.Nil(t, err)
	assert.Equal(t, 4, msgs.Size())
}
