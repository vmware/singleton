package LocalBundlesOnly

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestGetStringlocalsourceTranslation(t *testing.T) {

	cfPath := "configlocalsource.json"

	cfg, _ := sgtn.LoadConfig(cfPath)

	// var sourceMessages = map[string]string{"messages.welcome": "Welcome","message.translation.available":"testsource test","messages.only":"Hello only"}
	// var testMessages = map[string]string{"test.welcome": "Welcome test","test.translation.available":"testsource test","test.only":"Hello only"}

	// var sourceComponent = sgtn.NewMapComponentMsgs(sourceMessages, "en", "DefaultComponent")
	// var testComponent = sgtn.NewMapComponentMsgs(testMessages, "en", "test")
	// sgtn.RegisterSource("testsource", "1.0.0", []sgtn.ComponentMsgs{testComponent,sourceComponent })
	sgtn.Initialize(cfg)
	translation := sgtn.GetTranslation()



	Convey("localbundles-requestlocale: Get request locale's translation from localbundle", t, func() {



		Convey("regester equal 1", func() {

			tran1, _ := translation.GetStringMessage("testsource", "1.0.0", "fr", "DefaultComponent", "messages.welcome")
			tran2, _ := translation.GetStringMessage("testsource", "1.0.0", "en", "DefaultComponent", "messages.welcome")


			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Bienvenue")
			So(tran2, ShouldEqual, "Welcome")

		})

		Convey("regester equal and da", func() {

			tran1, _ := translation.GetStringMessage("testsource", "1.0.0", "da", "DefaultComponent", "messages.welcome")
			tran2, _ := translation.GetStringMessage("testsource", "1.0.0", "en", "DefaultComponent", "messages.welcome")

			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Willkommen")
			So(tran2, ShouldEqual, "Welcome")
		})

		Convey("regester not equal 1", func() {

			tran1, _ := translation.GetStringMessage("testsource", "1.0.0", "de", "DefaultComponent", "message.translation.available")

			tran2, _ := translation.GetStringMessage("testsource", "1.0.0", "en", "DefaultComponent", "message.translation.available")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "testsource test")
			So(tran2, ShouldEqual, "testsource test")
		})

		Convey("regester not equal and da", func() {

			tran1, _ := translation.GetStringMessage("testsource", "1.0.0", "de", "DefaultComponent", "message.translation.available")

			tran2, _ := translation.GetStringMessage("testsource", "1.0.0", "en", "DefaultComponent", "message.translation.available")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "testsource test")
			So(tran2, ShouldEqual, "testsource test")
		})

		Convey("regester not equal and source only in testsourceed source", func() {

			tran1, _ := translation.GetStringMessage("testsource", "1.0.0", "de", "DefaultComponent", "messages.only")

			tran2, _ := translation.GetStringMessage("testsource", "1.0.0", "en", "DefaultComponent", "messages.only")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Hello only")
			So(tran2, ShouldEqual, "Hello only")
		})

		Convey("regester not equal and source only in service en----bug1", func() {

			tran1, _ := translation.GetStringMessage("testsource", "1.0.0", "de", "DefaultComponent", "messages.hello")

			tran2, _ := translation.GetStringMessage("testsource", "1.0.0", "en", "DefaultComponent", "messages.hello")
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, "Hallo")
			So(tran2, ShouldEqual, "messages.hello")
		})

		Convey("regester the component only in testsourceed source", func() {

			tran1, _ := translation.GetStringMessage("testsource", "1.0.0", "de", "test", "test.welcome")
			tran2, _ := translation.GetStringMessage("testsource", "1.0.0", "en", "test", "test.welcome")


			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Welcome test")
			So(tran2, ShouldEqual, "Welcome test")
		})

		Convey("regester the component only in source", func() {

			tran1, _ := translation.GetStringMessage("testsource", "1.0.0", "de", "contact", "contact.message")

			tran2, _ := translation.GetStringMessage("testsource", "1.0.0", "en", "contact", "contact.message")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Ihrer Kontaktseite.")
			
			So(tran2, ShouldEqual, "Your contact page.")
		})


		Convey("regester the component only in source and source not equal", func() {

			tran1, _ := translation.GetStringMessage("testsource", "1.0.0", "de", "contact", "contact.title")

			tran2, _ := translation.GetStringMessage("testsource", "1.0.0", "en", "contact", "contact.title")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Contact")
			
			So(tran2, ShouldEqual, "Contact")
		})



		SkipConvey("regester the component only in testsourceed source--- bug2", func() {

			tran1, _ := translation.GetStringMessage("GoClientTest", "1.0", "da", "contact", "contact.title")


			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Kontakt")
		})

		SkipConvey("component-localbundle-requestlocale: request locale(fr) is in localebundle, return request locale translation from localbundles(P0)", func() {

			commsg, _ := translation.GetComponentMessages("testsource", "1.0.0", "en", "DefaultComponent")
			fmt.Print("\n###########################################################################\n")
			fmt.Print(commsg)
			// value, _ := commsg.Get(messages.welcome)

			// //So(commsg.Size(), ShouldEqual, 6)
			// So(value, ShouldEqual, frmessagevalue)
			// So(cfg, ShouldNotBeNil)
		})

		// Convey("component-localbundle-requestlocale: xxxxxxxxxxxxxxxxxx", func() {

		// 	locales :=[]string{"de"}
		// 	components := []string{"DefaultComponent"}

		// 	commsg1, err := translation.GetComponentsMessages("testsource", "1.0.0", locales, components)
		// 	So(err, ShouldBeNil)
		// 	fmt.Println("output: ",commsg1)
		// 	for _, bundle:=range commsg1 {
		// 		fmt.Printf("current bundle: %#v\n", bundle)
		// 		So(bundle.Component(), ShouldBeIn, components)
		// 		So(bundle.Locale(), ShouldBeIn, locales)

		// 		var key, expectedValue string
		// 		key,expectedValue = "message.translation.available", "支持："
		// 		actualValue, found:= bundle.Get(key)
		// 		So(found, ShouldEqual, true)
		// 		So(actualValue, ShouldEqual, expectedValue)
		// 	}

		// })



	})

}
