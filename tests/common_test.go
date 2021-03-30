/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"errors"
	"math/rand"
	"sync"
	"sync/atomic"
	"testing"
	"time"

	"sgtnserver/internal/common"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"

	"github.com/stretchr/testify/assert"
	"go.uber.org/zap"
)

func TestDoOrWait(t *testing.T) {
	group := sync.WaitGroup{}

	doneCount := 0
	waiterCount := int64(0)
	doing := make(chan struct{})
	doer := func() error {
		doneCount++
		time.Sleep(time.Millisecond)
		group.Done()
		return nil
	}
	waiter := func() error {
		atomic.AddInt64(&waiterCount, 1)
		time.Sleep(time.Millisecond)
		group.Done()
		return nil
	}

	maxNumber := 200
	r := rand.Intn(maxNumber)
	for i := 0; i < maxNumber; i++ {
		todo := false
		if i == r {
			group.Add(1)
			todo = true
		}
		group.Add(1)
		go common.DoOrWait(logger.NewContext(nil, logger.Log.With(zap.Int("thread", i))), doing, doer, waiter, todo)
	}

	group.Wait()
	assert.Equal(t, 1, doneCount)
	assert.Equal(t, int64(maxNumber), waiterCount)
}

func TestMultiError(t *testing.T) {
	firstError := errors.New("first error")
	secondError := errors.New("second error")
	thirdError := errors.New("third error")
	tests := []struct {
		testName           string
		firstError         error
		errorsToAdd        []error
		wantedErrorsLength int
		isNilError         bool
		isAllFailed        bool
	}{
		{testName: "Nil+", firstError: nil, errorsToAdd: nil,
			wantedErrorsLength: 0, isNilError: true, isAllFailed: false},

		{testName: "NoNil+", firstError: firstError, errorsToAdd: nil,
			wantedErrorsLength: 1, isNilError: false, isAllFailed: true},

		{testName: "NilMulti+", firstError: (*sgtnerror.MultiError)(nil), errorsToAdd: nil,
			wantedErrorsLength: 0, isNilError: true, isAllFailed: false},

		{testName: "NonNilMulti+", firstError: sgtnerror.Append(firstError), errorsToAdd: nil,
			wantedErrorsLength: 1, isNilError: false, isAllFailed: true},

		{testName: "Nil+Nil", firstError: nil, errorsToAdd: nil,
			wantedErrorsLength: 0, isNilError: true, isAllFailed: false},

		{testName: "NoNil+NonNil", firstError: firstError, errorsToAdd: []error{secondError},
			wantedErrorsLength: 2, isNilError: false, isAllFailed: true},

		{testName: "NilMulti+NilMulti", firstError: (*sgtnerror.MultiError)(nil), errorsToAdd: []error{(*sgtnerror.MultiError)(nil)},
			wantedErrorsLength: 0, isNilError: true, isAllFailed: false},

		{testName: "NonNilMulti+NonNilMulti", firstError: sgtnerror.Append(firstError), errorsToAdd: []error{sgtnerror.Append(secondError)},
			wantedErrorsLength: 2, isNilError: false, isAllFailed: true},

		{testName: "Nil+Nil+Nil", firstError: nil, errorsToAdd: []error{nil, nil},
			wantedErrorsLength: 0, isNilError: true, isAllFailed: false},

		{testName: "NoNil+Nil+NonNil", firstError: firstError, errorsToAdd: []error{nil, secondError},
			wantedErrorsLength: 2, isNilError: false, isAllFailed: false},

		{testName: "NilMulti+NilMulti+NilMulti", firstError: (*sgtnerror.MultiError)(nil), errorsToAdd: []error{(*sgtnerror.MultiError)(nil), (*sgtnerror.MultiError)(nil)},
			wantedErrorsLength: 0, isNilError: true, isAllFailed: false},

		{testName: "NonNilMulti+NonNilMulti+NonNilMulti", firstError: sgtnerror.Append(firstError), errorsToAdd: []error{sgtnerror.Append(secondError), sgtnerror.Append(thirdError)},
			wantedErrorsLength: 3, isNilError: false, isAllFailed: true},

		{testName: "Nil+Nil+NonNilMulti", firstError: nil, errorsToAdd: []error{nil, sgtnerror.Append(secondError)},
			wantedErrorsLength: 1, isNilError: false, isAllFailed: false},

		{testName: "NonNilMulti+NonNilMulti+Nil", firstError: sgtnerror.Append(firstError), errorsToAdd: []error{sgtnerror.Append(secondError), nil},
			wantedErrorsLength: 2, isNilError: false, isAllFailed: false},

		{testName: "Nil+Nil", firstError: nil, errorsToAdd: nil,
			wantedErrorsLength: 0, isNilError: true, isAllFailed: false},

		{testName: "Nil+NonNil", firstError: nil, errorsToAdd: []error{secondError},
			wantedErrorsLength: 1, isNilError: false, isAllFailed: false},

		{testName: "NilMulti+NoNil", firstError: (*sgtnerror.MultiError)(nil), errorsToAdd: []error{secondError},
			wantedErrorsLength: 1, isNilError: false, isAllFailed: true},
	}

	for _, tt := range tests {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			actualMultiError := sgtnerror.Append(tt.firstError, tt.errorsToAdd...)

			assert.Equal(t, tt.wantedErrorsLength, len(actualMultiError.Errors()))

			if tt.isNilError {
				assert.Nil(t, actualMultiError.ErrorOrNil())
			} else {
				assert.NotNil(t, actualMultiError.ErrorOrNil())
			}
			assert.Equal(t, tt.isAllFailed, actualMultiError.IsAllFailed())
		})
	}
}
