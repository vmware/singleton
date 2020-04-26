package LocalBundlesOnly

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestGetStringTranslation(t *testing.T) {

	cfPath := "config.yaml"

	cfg, _ := sgtn.NewConfig(cfPath)
	inst, _ := sgtn.NewInst(*cfg)
	fmt.Print(inst)
	translation := inst.GetTranslation()

	Convey("localbundles-requestlocale: Get request locale's translation from localbundle", t, func() {

		// cfPath := "config.yaml"

		// cfg, _ := sgtn.NewConfig(cfPath)
		// inst, _ := sgtn.NewInst(*cfg)
		// fmt.Print(inst)
		// translation := inst.GetTranslation()

		Convey("localbundles-requestlocale: Get common strings successfully from localbundle(P0)", func() {

			tran1, _ := translation.GetStringMessage("zh-Hans", Defaultcom, commonkey)
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, dlcncommonvalue)
			// So(cfg, ShouldNotBeNil)
		})

		Convey("localbundles-requestlocale: Get placehodler strings successfully from localbundle(P0)", func() {

			tran2, _ := translation.GetStringMessage("fr", Defaultcom, holderkey, "a", "b")

			So(tran2, ShouldEqual, "L'opérateur 'a' n'est pas pris en charge pour la propriété 'b'.")
		})

		Convey("localbundles-requestlocale: Get long and html tag strings successfully from localbundle(P0)", func() {

			tran2, _ := translation.GetStringMessage("fr", Defaultcom, htmlkey)
			// should add check if send out http request as the previous case has cached fr component translation

			So(tran2, ShouldEqual, frhtmlvalue)
		})

		Convey("localbundles-requestlocale: request a component(contact) is in localbundle but key isn't in localbundles, return key(P1)", func() {

			tran2, _ := translation.GetStringMessage("fr", "contact", "non-existing.key")
			So(tran2, ShouldEqual, "non-existing.key")
		})
	})

	Convey("localbundle-defaultlocale: Get default locale's translation from localBundles", t, func() {

		// cfPath := "testdata/Config/config.yaml"
		// cfg, _ := sgtn.NewConfig(cfPath)
		// inst, _ := sgtn.NewInst(*cfg)
		// fmt.Print(inst)
		// translation := inst.GetTranslation()

		Convey("localbundles-defaultlocale: request a non-existing locale(abc) and a component is in localbundle, get default locale translation from localbundles(P1)", func() {

			//messages_zh-Hans.json of component "contact" isn't in service but in localbundle
			tran1, _ := translation.GetStringMessage("abc", "contact", "contact.title")

			//zh-Hans is default locale
			So(tran1, ShouldEqual, "联系")
		})

		Convey("localbundles-defaultlocale: request a non-existing locale(abc) and non-existing component(abc) in localbundle, return key(P1)", func() {

			tran1, _ := translation.GetStringMessage("abc", "abc", "contact.title")

			//zh-Hans is default locale
			So(tran1, ShouldEqual, "contact.title")
		})

		Convey("localbundles-defaultlocale: request an existing locale(fr) and non-existing component(abc) in localbundle, return key(P1)", func() {

			tran1, _ := translation.GetStringMessage("fr", "abc", "contact.title")

			//zh-Hans is default locale
			So(tran1, ShouldEqual, "contact.title")
		})

		Convey("localbundles-defaultlocale: request a non-existing locale(abc), request a component(contact) is in localbundle and key isn't in localbundles, return key(P1)", func() {

			tran2, _ := translation.GetStringMessage("abc", "contact", "non-existing.key")
			So(tran2, ShouldEqual, "non-existing.key")
		})
	})

}
