package LocalBundlesOnly

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestGetStringregesterTranslation(t *testing.T) {

	cfPath := "configregester.json"

	cfg, _ := sgtn.LoadConfig(cfPath)

	var sourceMessages = map[string]string{"messages.welcome": "Welcome","message.translation.available":"register test","messages.only":"Hello only"}
	var testMessages = map[string]string{"test.welcome": "Welcome test","test.translation.available":"register test","test.only":"Hello only"}

	var sourceComponent = sgtn.NewMapComponentMsgs(sourceMessages, "en", "DefaultComponent")
	var testComponent = sgtn.NewMapComponentMsgs(testMessages, "en", "test")
	sgtn.RegisterSource("Register", "1.0.0", []sgtn.ComponentMsgs{testComponent,sourceComponent })
	sgtn.Initialize(cfg)
	translation := sgtn.GetTranslation()



	Convey("localbundles-requestlocale: Get request locale's translation from localbundle", t, func() {



		Convey("regester equal 1", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "fr", "DefaultComponent", "messages.welcome")
			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.welcome")


			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Bienvenue")
			So(tran2, ShouldEqual, "Welcome")

		})

		Convey("regester equal and da", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "da", "DefaultComponent", "messages.welcome")
			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.welcome")

			fmt.Print(tran1)

			So(tran1, ShouldEqual, "환영합니다 da")
			So(tran2, ShouldEqual, "Welcome")
		})

		Convey("regester not equal 1", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "DefaultComponent", "message.translation.available")

			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "message.translation.available")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "register test")
			So(tran2, ShouldEqual, "register test")
		})

		Convey("regester not equal and da", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "DefaultComponent", "message.translation.available")

			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "message.translation.available")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "register test")
			So(tran2, ShouldEqual, "register test")
		})

		Convey("regester not equal and source only in registered source", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "DefaultComponent", "messages.only")

			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.only")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Hello only")
			So(tran2, ShouldEqual, "Hello only")
		})

		Convey("regester not equal and source only in service en----bug1", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "DefaultComponent", "messages.hello")

			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.hello")
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, "Hallo")
			So(tran2, ShouldEqual, "messages.hello")
		})

		Convey("regester the component only in registered source", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "test", "test.welcome")
			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "test", "test.welcome")


			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Welcome test")
			So(tran2, ShouldEqual, "Welcome test")
		})

		Convey("regester the component only in source", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "contact", "contact.message")

			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "contact", "contact.message")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Ihrer Kontaktseite.")
			
			So(tran2, ShouldEqual, "Your contact page.")
		})


		Convey("regester the component only in source and source not equal", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "contact", "contact.title")

			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "contact", "contact.title")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Contact")
			
			So(tran2, ShouldEqual, "Contact")
		})



		SkipConvey("regester the component only in registered source--- bug2", func() {

			tran1, _ := translation.GetStringMessage("GoClientTest", "1.0", "de", "contact", "contact.title")


			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Kontakt")
		})

		// Convey("component-localbundle-requestlocale: request locale(fr) is in localebundle, return request locale translation from localbundles(P0)", func() {

		// 	commsg, _ := translation.GetComponentMessages("Register", "1.0.0", "de", "DefaultComponent")
		// 	fmt.Print(commsg)
		// 	// value, _ := commsg.Get(messages.welcome)

		// 	// //So(commsg.Size(), ShouldEqual, 6)
		// 	// So(value, ShouldEqual, frmessagevalue)
		// 	// So(cfg, ShouldNotBeNil)
		// })

		// Convey("component-localbundle-requestlocale: request locale(fr) is in localebundle, return request locale translation from localbundles(P0)", func() {

		// 	locales :=[]string{"de"}
		// 	components := []string{"DefaultComponent"}

		// 	commsg1, err := translation.GetComponentsMessages("Register", "1.0.0", locales, components)
		// 	So(err, ShouldBeNil)
		// 	fmt.Println("output: ",commsg1)
		// 	for _, bundle:=range commsg1 {
		// 		fmt.Printf("current bundle: %#v\n", bundle)
		// 		So(bundle.Component(), ShouldBeIn, components)
		// 		So(bundle.Locale(), ShouldBeIn, locales)

		// 		var key, expectedValue string
		// 		key,expectedValue = "messages.welcome", "支持："
		// 		actualValue, found:= bundle.Get(key)
		// 		So(found, ShouldEqual, true)
		// 		So(actualValue, ShouldEqual, expectedValue)
		// 	}

		// })



	})

}
