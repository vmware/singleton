/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package common

import (
	"context"
	"errors"
	"reflect"
	"strings"
	"time"

	"sgtnserver/internal/logger"

	"go.uber.org/zap"
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
		bts[0] -= 32
		return string(bts)
	}

	return s
}

// DoAndCheck ...
func DoAndCheck(ctx context.Context, done chan struct{}, doer func() error, checker func(), duration time.Duration) (err error) {
	defer close(done) // Close the channel

	err = doer()
	if err != nil {
		return
	}

	timeout := time.After(duration)
	ready := make(chan struct{})

	go func() {
		checker()
		ready <- struct{}{}
	}()

	select {
	case <-ready:
	case <-timeout:
		err = errors.New("time out to wait for cache ready")
		logger.FromContext(ctx).Error(err.Error(), zap.Duration("waitTime", duration))
	}

	return
}

func IsZeroOfUnderlyingType(x interface{}) bool {
	return reflect.DeepEqual(x, reflect.Zero(reflect.TypeOf(x)).Interface())
}
