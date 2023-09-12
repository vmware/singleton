/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package CustomMethod

import (
	"fmt"

	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestExternalCacheManagement(t *testing.T) {

	Convey("logger and httprequest test", t, func() {
		log1:=newLogger()
		sgtn.SetLogger(log1)
		cfPath := "configServerOnly.json"
		cfg,_ := sgtn.LoadConfig(cfPath)
		sgtn.Initialize(cfg)
		log1.Warn(fmt.Sprintf("test warn log message"))
		sgtn.SetHTTPHeaders(map[string]string{
			"user": "test_user",
			"password": "test_password",
		})
		translation := sgtn.GetTranslation()
		name, version := "GoClientTest", "1.0"
		Convey("logger and httprequest test", func() {
			// id :=dataItemID(name, version, "de", "about", "about.message")
			tran1, _ := translation.GetStringMessage(name, version, "de", "DefaultComponent", "messages.welcome")
			log1.Debug(fmt.Sprintf("test Debug log message"))
			log1.Info(fmt.Sprintf("test Info log message"))
			log1.Error(fmt.Sprintf("test Error log message"))
			So(tran1, ShouldEqual, "Willkommen")
		})
	})
}
