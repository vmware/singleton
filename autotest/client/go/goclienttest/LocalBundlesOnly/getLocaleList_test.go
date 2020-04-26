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

	Convey("Get all locales successfully(P0)", t, func() {

		cfPath := "config.yaml"
		cfg, _ := sgtn.NewConfig(cfPath)
		//fmt.Print(cfg)
		inst, _ := sgtn.NewInst(*cfg)
		//fmt.Print(inst)
		translation := inst.GetTranslation()

		localelist, err := translation.GetLocaleList()
		fmt.Print(localelist)
		fmt.Print(err)

		So(len(localelist), ShouldEqual, 9)
		// So(cfg, ShouldNotBeNil)
	})

	SkipConvey("Get nothing for non-existing product-(P1)", t, func() {

		cfPath1 := "confignull.yaml"
		cfg1, _ := sgtn.NewConfig(cfPath1)
		//fmt.Print(cfg)
		inst1, _ := sgtn.NewInst(*cfg1)
		//fmt.Print(inst)
		translation1 := inst1.GetTranslation()

		localelist1, err1 := translation1.GetLocaleList()
		fmt.Println(localelist1)
		fmt.Println(err1)

		So(len(localelist1), ShouldEqual, 0)
		So(err1, ShouldBeNil)
		// So(cfg, ShouldNotBeNil)

	})

}
