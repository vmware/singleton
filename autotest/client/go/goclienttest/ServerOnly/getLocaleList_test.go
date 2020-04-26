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

func TestLocaleList(t *testing.T) {

	Convey("Get locale list from service", t, func() {

		Convey("Get all locales successfully(P0)", func() {

			cfPath := "configServerOnly.yaml"
			cfg, _ := sgtn.NewConfig(cfPath)
			inst, _ := sgtn.NewInst(*cfg)
			fmt.Print(inst)
			translation := inst.GetTranslation()

			localelist, err := translation.GetLocaleList()
			fmt.Print(localelist)
			fmt.Print(err)

			So(len(localelist), ShouldEqual, 9)
			// So(cfg, ShouldNotBeNil)
		})

		SkipConvey("Get nothing for non-existing product-(P1)", func() {

			cfPath := "configServerOnlyNull.yaml"
			cfg, _ := sgtn.NewConfig(cfPath)
			inst, _ := sgtn.NewInst(*cfg)
			fmt.Print(inst)
			translation := inst.GetTranslation()

			// inst1, _ := replaceInst(cfg)
			// fmt.Printf("localbunlde path: %s\n", inst1.GetConfig().LocalBundles) //结果确实是空的
			// fmt.Printf("product name: %s\n", inst1.GetConfig().Name)             //结果确实是空的

			//translation1 := inst1.GetTranslation()
			localelist, err := translation.GetLocaleList()
			fmt.Println(localelist)
			fmt.Println(err)

			So(len(localelist), ShouldEqual, 0)
			So(err.Error(), ShouldContainSubstring, "Fail to load from server. The code is: 404, message is: Failed to get locale list for NonExistingProduct/1.0.0")
			// So(cfg, ShouldNotBeNil)
		})

	})

}
