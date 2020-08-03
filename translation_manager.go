/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
)

type transMgr struct {
	Translation
	fallbackChain []string
}

func newTransMgr(t Translation, fblocales []string) *transMgr {
	return &transMgr{Translation: t, fallbackChain : fblocales}
}

// GetStringMessage Get a message with optional arguments
func (t *transMgr) GetStringMessage(name, version, locale, component, key string, args ...string) (string, error) {
	message, err := t.Translation.GetStringMessage(name, version, locale, component, key, args...)
	if err == nil {
		return message, nil
	}

	i := indexIgnoreCase(t.fallbackChain, locale)
	for m := i + 1; m < len(t.fallbackChain); m++ {
		logger.Warn(fmt.Sprintf("fall back to locale '%s'", t.fallbackChain[m]))
		message, err = t.Translation.GetStringMessage(name, version, t.fallbackChain[m], component, key, args...)
		if err == nil {
			break
		}
	}
	if err != nil {
		return "", err
	}

	return message, nil
}
