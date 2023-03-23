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

func TestLocaleList(t *testing.T) {
	cfPath := "config.json"
	cfg, _ := sgtn.LoadConfig(cfPath)
	//fmt.Print(cfg)
	sgtn.Initialize(cfg)
	//fmt.Print(inst)
	translation := sgtn.GetTranslation()

	Convey("Get all locales successfully(P0)", t, func() {

		localelist, err := translation.GetLocaleList("GoClientTest", "1.0")
		fmt.Print(localelist)
		fmt.Print(err)
		So(localelist, ShouldContain, "es")
		So(len(localelist), ShouldEqual, 10)
		// So(cfg, ShouldNotBeNil)
	})

	Convey("Get nothing for non-existing product-(P1)", t, func() {

		localelist1, err1 := translation.GetLocaleList("NoxxxxxxxxxxxExistcomponent", "1.0")
		fmt.Println(localelist1)
		fmt.Println(err1)

		So(len(localelist1), ShouldEqual, 0)
		//So(err1, ShouldBeNil)
		//So(cfg, ShouldNotBeNil)

	})

}
