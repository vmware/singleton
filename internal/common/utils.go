/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package common

import (
	"context"
	"sgtnserver/internal/logger"
	"strings"
	"time"
)

// Contains ...
// return < 0 not found
//        >=0 the index
func Contains(s []string, e string) int {
	for i, a := range s {
		if a == e {
			return i
		}
	}
	return -1
}

// ContainsIgnoreCase
// return < 0 not found
//        >=0 the index
func ContainsIgnoreCase(s []string, e string) int {
	for i, a := range s {
		if strings.EqualFold(a, e) {
			return i
		}
	}
	return -1
}

func TitleCase(s string) string {
	if s == "" {
		return s
	}

	if s[0] >= 'a' && s[0] <= 'z' {
		bts := []byte(s)
		bts[0] = bts[0] - 32
		return string(bts)
	}

	return s
}

// DoOrWait ...
func DoOrWait(ctx context.Context, doing chan struct{}, doer func() error, waiter func() error, todo bool) {
	if todo {
		var err error
		defer func() {
			defer close(doing)
			if err == nil {
				start := time.Now()
				for {
					if err := waiter(); err == nil {
						break
					}
					if time.Since(start) > time.Millisecond*30 {
						logger.FromContext(ctx).DPanic("Time out to wait for cache ready. Suggest to wait more time!")
						break
					}
					time.Sleep(time.Microsecond)
				}
			}
		}()
		err = doer()
	} else {
		<-doing
		waiter()
	}
}

func ToGenericArray(x []string) []interface{} {
	s := make([]interface{}, len(x))
	for i, v := range x {
		s[i] = v
	}
	return s
}
