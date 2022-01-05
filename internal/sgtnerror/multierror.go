/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtnerror

import (
	"fmt"
	"strings"

	"github.com/hashicorp/go-multierror"
)

type MultiError struct {
	multiErrors *multierror.Error
	nilErrorNum int
}

func Append(err error, errs ...error) *MultiError {
	switch err := err.(type) {
	case *MultiError:
		// Typed nils can reach here, so initialize if we are nil
		if err == nil {
			err = &MultiError{multiErrors: &multierror.Error{ErrorFormat: FormatFunc}}
		}

		// Go through each error and flatten
		for _, e := range errs {
			switch e := e.(type) {
			case *MultiError:
				if e != nil {
					err.multiErrors = multierror.Append(err.multiErrors, e.multiErrors)
				} else {
					err.nilErrorNum++
				}
			default:
				if e != nil {
					err.multiErrors = multierror.Append(err.multiErrors, e)
				} else {
					err.nilErrorNum++
				}
			}
		}

		return err
	default:
		newErrs := make([]error, 0, len(errs)+1)
		newErrs = append(newErrs, err)
		newErrs = append(newErrs, errs...)

		return Append(&MultiError{multiErrors: &multierror.Error{ErrorFormat: FormatFunc}}, newErrs...)
	}
}

func (e *MultiError) Error() string {
	if e.ErrorOrNil() == nil {
		return ""
	}
	return e.multiErrors.Error()
}

func (e *MultiError) Errors() []error {
	if e.ErrorOrNil() == nil {
		return []error{}
	}

	return e.multiErrors.Errors
}

func (e *MultiError) ErrorOrNil() error {
	if e == nil || e.multiErrors.ErrorOrNil() == nil {
		return nil
	}

	return e
}

func (e *MultiError) IsAllFailed() bool {
	if e.ErrorOrNil() != nil {
		return e.nilErrorNum == 0
	}

	return false
}

func FormatFunc(es []error) string {
	if len(es) == 1 {
		return GetUserMessage(es[0])
	}

	points := make([]string, len(es))
	for i, err := range es {
		points[i] = GetUserMessage(err)
	}

	return fmt.Sprintf(
		"%d errors occurred. %s",
		len(es), strings.Join(points, "; "))
}
