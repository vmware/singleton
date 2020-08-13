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

	Convey("component-localbundles-requestlocale: Get request locale's translation from localbundles", t, func() {

		Convey("component-localbundle-requestlocale: request locale(fr) is in localebundle, return request locale translation from localbundles(P0)", func() {

			commsg, _ := translation.GetComponentMessages("GoClientTest", "1.0.0", "fr", contactcom)
			fmt.Print(commsg)
			value, _ := commsg.Get(messagekey)

			//So(commsg.Size(), ShouldEqual, 6)
			So(value, ShouldEqual, frmessagevalue)
			// So(cfg, ShouldNotBeNil)
		})

		// SkipConvey("component-localbundle-requestlocale: request non-existing locale(abc) is in localebundle(P1)", func() {

		// 	commsg, err := translation.GetComponentMessages("abc", contactcom)
		// 	//fmt.Println("commsg:", commsg)
		// 	//fmt.Println("err", err)
		// 	So(commsg, ShouldBeNil)
		// 	So(err.Error(), ShouldContainSubstring, "open ..\\testdata\\localBundles\\GoClientTest\\1.0.0\\contact\\messages_abc.json: The system cannot find the file specifie")

		// })

		// SkipConvey("component-localbundle-nothing: request empty locale() and non-existing component, return nothing and error(P1)", func() {

		// 	commsg, err := translation.GetComponentMessages("", "non-exiting-component")
		// 	fmt.Print(commsg)
		// 	fmt.Print(err)
		// 	//value, _ := commsg.Get(messagekey)

		// 	So(commsg, ShouldBeNil)
		// 	So(err.Error(), ShouldContainSubstring, "open ..\\testdata\\localBundles\\GoClientTest\\1.0.0\\non-exiting-component: The system cannot find the file specified")
		// 	// So(cfg, ShouldNotBeNil)
		// })

	})

}
