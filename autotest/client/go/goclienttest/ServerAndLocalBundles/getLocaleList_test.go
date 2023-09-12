package ServerAndLocalBundles

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestLocaleList(t *testing.T) {

	cfPath := "config.json"
	cfg, _ := sgtn.LoadConfig(cfPath)
	sgtn.Initialize(cfg)
	translation := sgtn.GetTranslation()

	Convey("Get locale list from service", t, func() {

		Convey("Get all locales successfully(P0)", func() {

			localelist, err := translation.GetLocaleList("GoClientTest", "1.0")
			fmt.Print(localelist)
			fmt.Print(err)

			So(len(localelist), ShouldEqual, 11)
			// So(cfg, ShouldNotBeNil)
		})

	})

	SkipConvey("Get locale list from localbundles for the product isn't in service but in localbunldes", t, func() {

		//inst2, _ := replaceInst(cfg)
		// fmt.Printf("localbunlde path: %s\n", inst2.GetConfig().LocalBundles)
		// fmt.Printf("server name: %s\n", inst2.GetConfig().SingletonServer)  
		//translation2 := inst2.GetTranslation()

		Convey("Get all components successfully(P0)", func() {

			localelist1, err1 := translation.GetLocaleList("GoClientTest2", "1.0")
			fmt.Print(localelist1)
			fmt.Print(err1)

			So(len(localelist1), ShouldEqual, 4)
			// So(cfg, ShouldNotBeNil)
		})
	})

	SkipConvey("Get nothing for locale list since the product isn't in service and localbunldes(P1)", t, func() {

		localelist2, err2 := translation.GetLocaleList("NonExistingProduct", "1.0")
		fmt.Print(localelist2)
		fmt.Print(err2)

		So(len(localelist2), ShouldEqual, 0)
		//So(err2.Error(), ShouldBeNil)

	})

}
