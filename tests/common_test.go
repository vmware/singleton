/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"context"
	"errors"
	"math/rand"
	"sync"
	"sync/atomic"
	"testing"
	"time"

	"sgtnserver/internal/cache"
	"sgtnserver/internal/common"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"

	"github.com/fatih/structs"
	"github.com/stretchr/testify/assert"
	"go.uber.org/zap"
)

func TestDoAndCheck(t *testing.T) {
	group := sync.WaitGroup{}

	var err error
	doneCount := int64(0)
	waiterCount := int64(0)
	done := make(chan struct{})
	doer := func() error {
		atomic.AddInt64(&doneCount, 1)
		time.Sleep(time.Millisecond)
		return nil
	}
	checker := func() { time.Sleep(time.Millisecond) }
	waiter := func() { atomic.AddInt64(&waiterCount, 1) }

	maxNumber := 200
	r := rand.Intn(maxNumber)
	for i := 0; i < maxNumber; i++ {
		group.Add(1)
		go func(n int) {
			if n == r {
				err = common.DoAndCheck(logger.NewContext(context.TODO(), logger.Log.With(zap.Int("thread", n))), done, doer, checker, time.Second)
			} else {
				<-done
				waiter()
			}
			group.Done()
		}(i)
	}

	group.Wait()
	assert.Nil(t, err)
	assert.Equal(t, int64(1), doneCount)
	assert.Equal(t, int64(maxNumber-1), waiterCount)
}

func TestDoAndCheckTimeOut(t *testing.T) {
	group := sync.WaitGroup{}

	var err error
	done := make(chan struct{})
	doer := func() error { return nil }
	checker := func() { time.Sleep(time.Second) }
	waiter := func() {}

	maxNumber := 2
	r := rand.Intn(maxNumber)
	for i := 0; i < maxNumber; i++ {
		group.Add(1)
		go func(n int) {
			if n == r {
				err = common.DoAndCheck(logger.NewContext(context.TODO(), logger.Log.With(zap.Int("thread", n))), done, doer, checker, time.Millisecond)
			} else {
				<-done
				waiter()
			}
			group.Done()
		}(i)
	}

	group.Wait()

	assert.EqualError(t, err, "time out to wait for cache ready")
}

func TestMultiError(t *testing.T) {
	firstError := errors.New("first error")
	secondError := errors.New("second error")
	thirdError := errors.New("third error")
	tests := []struct {
		testName           string
		firstError         error
		errorsToAdd        []error
		wantedErrorsNumber int
		isNilError         bool
		isAllFailed        bool
	}{
		{testName: "Nil+", firstError: nil, errorsToAdd: nil,
			wantedErrorsNumber: 0, isNilError: true, isAllFailed: false},

		{testName: "NoNil+", firstError: firstError, errorsToAdd: nil,
			wantedErrorsNumber: 1, isNilError: false, isAllFailed: true},

		{testName: "NilMulti+", firstError: (*sgtnerror.MultiError)(nil), errorsToAdd: nil,
			wantedErrorsNumber: 0, isNilError: true, isAllFailed: false},

		{testName: "NonNilMulti+", firstError: sgtnerror.Append(firstError), errorsToAdd: nil,
			wantedErrorsNumber: 1, isNilError: false, isAllFailed: true},

		{testName: "Nil+Nil", firstError: nil, errorsToAdd: nil,
			wantedErrorsNumber: 0, isNilError: true, isAllFailed: false},

		{testName: "NoNil+NonNil", firstError: firstError, errorsToAdd: []error{secondError},
			wantedErrorsNumber: 2, isNilError: false, isAllFailed: true},

		{testName: "NilMulti+NilMulti", firstError: (*sgtnerror.MultiError)(nil), errorsToAdd: []error{(*sgtnerror.MultiError)(nil)},
			wantedErrorsNumber: 0, isNilError: true, isAllFailed: false},

		{testName: "NonNilMulti+NonNilMulti", firstError: sgtnerror.Append(firstError), errorsToAdd: []error{sgtnerror.Append(secondError)},
			wantedErrorsNumber: 2, isNilError: false, isAllFailed: true},

		{testName: "Nil+Nil+Nil", firstError: nil, errorsToAdd: []error{nil, nil},
			wantedErrorsNumber: 0, isNilError: true, isAllFailed: false},

		{testName: "NoNil+Nil+NonNil", firstError: firstError, errorsToAdd: []error{nil, secondError},
			wantedErrorsNumber: 2, isNilError: false, isAllFailed: false},

		{testName: "NilMulti+NilMulti+NilMulti", firstError: (*sgtnerror.MultiError)(nil), errorsToAdd: []error{(*sgtnerror.MultiError)(nil), (*sgtnerror.MultiError)(nil)},
			wantedErrorsNumber: 0, isNilError: true, isAllFailed: false},

		{testName: "NonNilMulti+NonNilMulti+NonNilMulti", firstError: sgtnerror.Append(firstError), errorsToAdd: []error{sgtnerror.Append(secondError), sgtnerror.Append(thirdError)},
			wantedErrorsNumber: 3, isNilError: false, isAllFailed: true},

		{testName: "Nil+Nil+NonNilMulti", firstError: nil, errorsToAdd: []error{nil, sgtnerror.Append(secondError)},
			wantedErrorsNumber: 1, isNilError: false, isAllFailed: false},

		{testName: "NonNilMulti+NonNilMulti+Nil", firstError: sgtnerror.Append(firstError), errorsToAdd: []error{sgtnerror.Append(secondError), nil},
			wantedErrorsNumber: 2, isNilError: false, isAllFailed: false},

		{testName: "Nil+Nil", firstError: nil, errorsToAdd: nil,
			wantedErrorsNumber: 0, isNilError: true, isAllFailed: false},

		{testName: "Nil+NonNil", firstError: nil, errorsToAdd: []error{secondError},
			wantedErrorsNumber: 1, isNilError: false, isAllFailed: false},

		{testName: "NilMulti+NoNil", firstError: (*sgtnerror.MultiError)(nil), errorsToAdd: []error{secondError},
			wantedErrorsNumber: 1, isNilError: false, isAllFailed: true},
	}

	for _, tt := range tests {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			actualMultiError := sgtnerror.Append(tt.firstError, tt.errorsToAdd...)

			assert.Equal(t, tt.wantedErrorsNumber, len(actualMultiError.Errors()))

			if tt.isNilError {
				assert.Nil(t, actualMultiError.ErrorOrNil())
			} else {
				assert.NotNil(t, actualMultiError.ErrorOrNil())
			}
			assert.Equal(t, tt.isAllFailed, actualMultiError.IsAllFailed())
		})
	}
}

func TestCacheExpiration(t *testing.T) {
	expirationDuration := time.Millisecond * 10
	settings := structs.Map(config.Settings.Cache)
	settings["Expiration"] = expirationDuration

	newCache := cache.NewCache("test_expiration", settings)

	key, value := "key", "value"
	err := newCache.Set(key, value)
	assert.Nil(t, err)

	time.Sleep(expirationDuration)
	actual, err := newCache.Get(key)
	assert.NotNil(t, err)
	assert.Nil(t, actual)
}
