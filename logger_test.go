/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"bytes"
	"testing"

	"github.com/rs/zerolog"
	"github.com/stretchr/testify/assert"
)

func TestLogger(t *testing.T) {

	saved := logger
	defer func() { logger = saved }()

	buf := new(bytes.Buffer)
	logger = &defaultLogger{zerolog.New(buf).With().Timestamp().Logger()}

	msg := "Test Warn Level"
	logger.Warn(msg)

	assert.Contains(t, buf.String(), msg)
}
