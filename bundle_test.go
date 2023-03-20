/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"os"
	"testing"

	"github.com/pkg/errors"
	"github.com/stretchr/testify/assert"
)

func TestBundleGetComponentList(t *testing.T) {

	newCfg := testCfg
	newCfg.ServerURL = ""
	resetInst(&newCfg, nil)

	comps, err := inst.trans.GetComponentList(name, version)

	assert.Nil(t, err)
	assert.Equal(t, 2, len(comps))
}

func TestBundleGetLocaleList(t *testing.T) {

	newCfg := testCfg
	newCfg.ServerURL = ""
	resetInst(&newCfg, nil)

	locales, err := inst.trans.GetLocaleList(name, version)
	logger.Debug(fmt.Sprintf("%#v\n", locales))
	assert.Nil(t, err)
	assert.Equal(t, 16, len(locales))
}

func TestBundleGetCompMessages(t *testing.T) {

	newCfg := testCfg
	newCfg.ServerURL = ""
	resetInst(&newCfg, nil)

	locale := "fr"
	component := "sunglow"
	msgs, err := inst.trans.GetComponentMessages(name, version, locale, component)
	assert.Nil(t, err)
	assert.Equal(t, 4, msgs.(*MapComponentMsgs).Size())
}

func TestBundleDirNonexistent(t *testing.T) {
	newCfg := testCfg
	newCfg.ServerURL = ""
	newCfg.LocalBundles = "Path Not Exist"
	resetInst(&newCfg, nil)

	_, err := inst.trans.GetComponentList(name, version)
	pathError := &os.PathError{}
	ok := errors.As(err, &pathError)
	assert.True(t, ok, "error isn't an PATH error: %s", err)

	bundleTranslation := &bundleDAO{newCfg.LocalBundles}
	_, err = bundleTranslation.GetLocaleList(name, version)
	ok = errors.As(err, &pathError)
	assert.True(t, ok, "error isn't an PATH error: %s", err)

	_, err = inst.trans.GetComponentMessages(name, version, locale, component)
	ok = errors.As(err, &pathError)
	assert.True(t, ok, "error isn't an PATH error: %s", err)
}
