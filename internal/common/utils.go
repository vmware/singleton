/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package common

import (
	"context"
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
func DoAndCheck(ctx context.Context, done chan struct{}, doer func() error, checker func()) (err error) {
	defer func() {
		if err := recover(); err != nil { // If error happens, close the channel
			close(done)
			panic(err)
		}
	}()

	err = doer()
	if err != nil {
		return
	}

	const waitTime = time.Second
	timeout := time.After(waitTime)
	ready := make(chan struct{})

	go func() {
		checker()
		ready <- struct{}{}
	}()

	go func() {
		defer close(done) // Close the channel

		select {
		case <-ready:
		case <-timeout:
			logger.FromContext(ctx).DPanic("Time out to wait for cache ready. Suggest to wait more time!", zap.Duration("waitTime", waitTime))
		}
	}()

	return
}

// func WaitForOperation(ctx context.Context, operation func(), waitDuration time.Duration) {
// 	timeout := time.After(waitDuration)
// 	ready := make(chan struct{})

// 	go func() {
// 		operation()
// 		ready <- struct{}{}
// 	}()

// 	select {
// 	case <-ready:
// 	case <-timeout:
// 		logger.FromContext(ctx).DPanic("Time out to wait for cache ready. Suggest to wait more time!", zap.Duration("duration", waitDuration))
// 	}
// }

func ToGenericArray(x []string) []interface{} {
	s := make([]interface{}, len(x))
	for i, v := range x {
		s[i] = v
	}
	return s
}

func IsZeroOfUnderlyingType(x interface{}) bool {
	return reflect.DeepEqual(x, reflect.Zero(reflect.TypeOf(x)).Interface())
}
