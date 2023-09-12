package ServerAndLocalBundles

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

	Convey("Get component list from service", t, func() {

		Convey("Get all components successfully(P0)", func() {

			comlist, err := translation.GetComponentList("GoClientTest", "1.0.0")
			fmt.Print(comlist)
			fmt.Print(err)

			So(len(comlist), ShouldEqual, 5)
			// So(cfg, ShouldNotBeNil)
		})

	})

	SkipConvey("Get component list from localbundles that the product isn't in service", t, func() {

		Convey("Get all components successfully(P0)", func() {

			comlist1, err1 := translation.GetComponentList("GoClientTest2", "1.0")
			fmt.Print("localbundle component list: ", comlist1)
			fmt.Print("localbundle component list error: ", err1)

			So(len(comlist1), ShouldEqual, 2)
			// So(cfg, ShouldNotBeNil)
		})
	})

	Convey("Get nothing about component list since the product isn't in service and localbundles", t, func() {

		comlist1, err1 := translation.GetComponentList("NonExistingProduct", "1.0")
		fmt.Print(comlist1)
		fmt.Print(err1)

		So(len(comlist1), ShouldEqual, 0)
		//So(err1.Error(), ShouldNotBeNil)
	})

}
