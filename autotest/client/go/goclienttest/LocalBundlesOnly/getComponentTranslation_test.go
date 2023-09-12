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

func TestGetComponentTranslation(t *testing.T) {

	cfPath := "config.json"
	cfg, _ := sgtn.LoadConfig(cfPath)
	sgtn.Initialize(cfg)
	translation := sgtn.GetTranslation()


	Convey("component-Service-requestlocale: Get request locale's translation from service", t, func() {


		Convey("component-Service-requestlocale: Get existing component successfully with fr from service(P0)", func() {

			commsg, _ := translation.GetComponentMessages("GoClientTest", "1.0.0", "fr", "DefaultComponent")
			fmt.Print("fr translation: ", commsg)
			value, _ := commsg.Get("message.translation.available")

			So(value, ShouldEqual, "La traduction est prête pour ce composant.xxx")

		})

		Convey("component-Service-requestlocale: version fallbackP0)", func() {

			commsg, _ := translation.GetComponentMessages("GoClientTest", "1.0.0", "en", "DefaultComponent")
			fmt.Print("fr translation: ", commsg)
			value, _ := commsg.Get("message.translation.available")

			So(value, ShouldEqual, "Translation is ready for this component.xxx")

		})



	})

	SkipConvey("component-Service-nothing: Get nothing from service", t, func() {

		Convey("component-service-nothing: not exist product(P1)", func() {

			commsg, err := translation.GetComponentMessages("notexist", "1.0.0", "de", "DefaultComponent")
			fmt.Println("translation for empty locale: ", commsg)
			fmt.Println("error for empty locale: ", err)
			//value, _ := commsg.Get(commonkey)

			So(commsg, ShouldBeNil)


		})


		Convey("component-service-nothing: not exist version(P1)", func() {

			commsg, err := translation.GetComponentMessages("GoClientTest", "1.0.1", "de", "DefaultComponent")
			fmt.Println("translation for empty locale: ", commsg)
			fmt.Println("error for empty locale: ", err)
			//value, _ := commsg.Get(commonkey)

			So(commsg, ShouldBeNil)


		})


		
		Convey("component-service-nothing: not exist locale(P1)", func() {

			commsg, err := translation.GetComponentMessages("GoClientTest", "1.0.0", "abc", "DefaultComponent")
			fmt.Println("translation for empty locale: ", commsg)
			fmt.Println("error for empty locale: ", err)
			//value, _ := commsg.Get(commonkey)

			So(commsg, ShouldBeNil)


		})

		Convey("component-service-nothing: not exist component", func() {

			commsg, err := translation.GetComponentMessages("GoClientTest", "1.0.0", "de", "notexist")
			fmt.Println("translation for empty locale: ", commsg)
			fmt.Println("error for empty locale: ", err)
			//value, _ := commsg.Get(commonkey)

			So(commsg, ShouldBeNil)


		})


	})
}
