/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package LocalBundlesOnly

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestComponentList(t *testing.T) {

	cfPath := "config.json"
	cfg, _ := sgtn.LoadConfig(cfPath)
	sgtn.Initialize(cfg)
	translation := sgtn.GetTranslation()

	Convey("Get all components successfully(P0)", t, func() {

		comlist, err := translation.GetComponentList("GoClientTest", "1.0.0")
		fmt.Print(comlist)
		fmt.Print(err)
		//fmt.Print("\ntest add value")

		So(len(comlist), ShouldEqual, 5)
		So(comlist, ShouldContain, "about")
		// So(cfg, ShouldNotBeNil)

	})

	Convey("Get nothing for non-existing product-(P1)", t, func() {

		comlist1, err1 := translation.GetComponentList("NonExistenceProduct", "1.0")
		fmt.Print(comlist1)
		fmt.Print(err1)

		So(len(comlist1), ShouldEqual, 0)
		// So(cfg, ShouldNotBeNil)

	})

}
