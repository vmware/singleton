/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"sync"
)

type carrier struct {
	ch   chan struct{}
	item *dataItem
	err  error
}

type singleLoader struct {
	messageOrigin
	carriers sync.Map
}

func (l *singleLoader) Get(item *dataItem) error {
	actual, loaded := l.carriers.LoadOrStore(item.id, carrier{ch: make(chan struct{}), item: item})
	w := actual.(carrier)
	if !loaded {
		defer func() {
			close(w.ch)
			l.carriers.Delete(item.id)
		}()

		logger.Info(fmt.Sprintf("Start fetching ID: %+v", item.id))

		w.err = l.messageOrigin.Get(item)
		w.item = item
		return w.err
	} else { // For the routines waiting
		<-w.ch
		*item = *w.item
		return w.err
	}
}
