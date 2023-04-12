/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package ServerOnly

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestComponentList(t *testing.T) {
	Convey("Get component list from service", t, func() {

		cfPath := "configServerOnly.json"
		cfg, _ := sgtn.LoadConfig(cfPath)
		sgtn.Initialize(cfg)
		translation := sgtn.GetTranslation()

		Convey("Get all components successfully(P0)", func() {

			comlist, err := translation.GetComponentList("GoClientTest", "1.0.0")
			fmt.Print(comlist)
			fmt.Print(err)

			So(len(comlist), ShouldEqual, 3)
			// So(cfg, ShouldNotBeNil)
		})

		Convey("Get nothing for non-existing product-(P1)", func() {

			comlist, err := translation.GetComponentList("NonExistingProduct", "1.0")
			fmt.Println(comlist)
			fmt.Println(err)

			So(len(comlist), ShouldEqual, 0)
		})

	})

}
