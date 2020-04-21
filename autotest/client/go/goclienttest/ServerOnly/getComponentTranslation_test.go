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

func TestGetComponentTranslation(t *testing.T) {

	cfPath := "configServerOnly.yaml"
	cfg, _ := sgtn.NewConfig(cfPath)
	inst, _ := sgtn.NewInst(*cfg)
	fmt.Print(inst)
	translation := inst.GetTranslation()
	Convey("component-Service-requestlocale: Get request locale's translation from service", t, func() {

		Convey("component-Service-requestlocale: Get existing component successfully with en from service(P0)", func() {

			commsg, _ := translation.GetComponentMessages("en", Defaultcom)
			fmt.Print("en translation: ", commsg)
			value, _ := commsg.Get(commonkey)

			So(commsg.Size(), ShouldEqual, 5)
			So(value, ShouldEqual, commonvalue)
			// So(cfg, ShouldNotBeNil)
		})

		Convey("component-Service-requestlocale: Get existing component successfully with fr from service(P0)", func() {

			commsg, _ := translation.GetComponentMessages("fr", Defaultcom)
			fmt.Print("fr translation: ", commsg)
			value, _ := commsg.Get(commonkey)

			So(commsg.Size(), ShouldEqual, 5)
			So(value, ShouldEqual, frcommonvalue)
			// So(cfg, ShouldNotBeNil)
		})
	})

	Convey("component-Service-nothing: Get nothing from service", t, func() {

		SkipConvey("component-service-nothing: request empty locale(), return nothing from service(P1)", func() {

			commsg, err := translation.GetComponentMessages("", Defaultcom)
			fmt.Println("translation for empty locale: ", commsg)
			fmt.Println("error for empty locale: ", err)
			//value, _ := commsg.Get(commonkey)

			So(commsg, ShouldBeNil)
			So(err.Error(), ShouldContainSubstring, "Getting failed, status code is: 404")

		})

		Convey("component-service-nothing: request unsupported locale(ru), return nothing from service(P0)", func() {

			commsg, err := translation.GetComponentMessages("ru", Defaultcom)
			fmt.Print("translation for ru(not supported) locale: ", commsg)

			// value, _ := commsg.Get(commonkey)
			// So(commsg.Size(), ShouldEqual, 5)
			// So(value, ShouldEqual, commonvalue)
			// So(cfg, ShouldNotBeNil)

			So(commsg, ShouldBeNil)
			So(err.Error(), ShouldContainSubstring, "404")

		})

		SkipConvey("component-service-nothing: request non-existing component(abc), return nothing from service(P0)", func() {

			commsg, err := translation.GetComponentMessages("fr", "abc")
			fmt.Println("translation for non-existing component: ", commsg)
			fmt.Println("error for non-existing component: ", err)
			//value, _ := commsg.Get(commonkey)

			So(commsg, ShouldBeNil)

			So(err.Error(), ShouldContainSubstring, "Fail to load from server. The code is: 404, message is: Failed to get translation from data for GoClientTest/1.0.0")
		})
	})

}
