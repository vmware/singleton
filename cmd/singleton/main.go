/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package main

import (
	"sgtnserver/api"
	_ "sgtnserver/api/actuator"
	_ "sgtnserver/api/v1/cldr"
	_ "sgtnserver/api/v1/formatting"
	_ "sgtnserver/api/v1/translation"
	_ "sgtnserver/api/v2/cldr"
	_ "sgtnserver/api/v2/combine"
	_ "sgtnserver/api/v2/formatting"
	_ "sgtnserver/api/v2/translation"
	"sgtnserver/internal/logger"
)

func main() {
	defer logger.Log.Sync()

	api.StartServer()
	api.WaitForSignals()
	api.ShutdownServer()
}
