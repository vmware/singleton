package ServerAndLocalBundles

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestGetStringregesterTranslation(t *testing.T) {

	cfPath := "config.json"

	cfg, _ := sgtn.LoadConfig(cfPath)

	var sourceMessages = map[string]string{"messages.welcome": "Welcome","message.translation.available":"register test","messages.only":"Hello only"}
	var testMessages = map[string]string{"test.welcome": "Welcome test","test.translation.available":"register test","test.only":"Hello only"}

	var sourceComponent = sgtn.NewMapComponentMsgs(sourceMessages, "en", "DefaultComponent")
	var testComponent = sgtn.NewMapComponentMsgs(testMessages, "en", "test")
	sgtn.RegisterSource("Register", "1.0.0", []sgtn.ComponentMsgs{testComponent,sourceComponent })
	sgtn.Initialize(cfg)
	translation := sgtn.GetTranslation()



	Convey("localbundles-requestlocale: Get request locale's translation from localbundle", t, func() {

		// Convey("regester equal ko-ch", func() {

		// 	tran1, _ := translation.GetStringMessage("Register", "1.0.0", "ja", "DefaultComponent", "messages.welcome")
		// 	tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.welcome")


		// 	fmt.Print("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n")
		// 	fmt.Print(tran1)
		// 	So(tran2, ShouldEqual, "Welcome")
		// 	//So(tran1, ShouldEqual, "Bienvenue")
		// 	//So(tran2, ShouldEqual, "Welcome")
		// 	commsg, _ := translation.GetComponentMessages("Register", "1.0.0", "ko", "DefaultComponent")
		// 	commsg1, _ := translation.GetComponentMessages("Register", "1.0.0", "fr", "DefaultComponent")
		// 	fmt.Print("**********************************************************************************1111\n")
		// 	fmt.Print(commsg)
		// 	fmt.Print("\n**********************************************************************************11112\n")
		// 	fmt.Print(commsg1)

		// })


		Convey("regester equal ko-ch 1", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "ja", "DefaultComponent", "messages.welcome")
			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.welcome")


			fmt.Print(tran1)

			So(tran1, ShouldEqual, "ようこそ test offline")
			So(tran2, ShouldEqual, "Welcome")

		})

		Convey("regester equal 1", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "fr", "DefaultComponent", "messages.welcome")
			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.welcome")


			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Bienvenue")
			So(tran2, ShouldEqual, "Welcome")

		})

		SkipConvey("regester equal and da", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "da", "DefaultComponent", "messages.welcome")
			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.welcome")

			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Willkommen")
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

		SkipConvey("regester not equal and source only in service en----bug1", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "DefaultComponent", "messages.hello")

			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en-US", "DefaultComponent", "messages.hello")
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, "Hallo")
			So(tran2, ShouldEqual, "Hello")
		})

		SkipConvey("regester the component only in registered source", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "test", "test.welcome")
			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "test", "test.welcome")


			fmt.Print(tran1)

			So(tran1, ShouldEqual, "test.welcome")
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


		Convey("regester fr only in locale bundle", func() {

			tran1, _ := translation.GetStringMessage("Register", "1.0.0", "ja", "DefaultComponent", "messages.welcome")

			tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.welcome")
			fmt.Print(tran1)

			So(tran1, ShouldEqual, "ようこそ test offline")
			
			So(tran2, ShouldEqual, "Welcome")
		})



		Convey("regester the component only in registered source--- bug2", func() {

			tran1, _ := translation.GetStringMessage("GoClientTest", "1.0.0", "de", "DefaultComponent", "messages.welcome")


			fmt.Print(tran1)

			So(tran1, ShouldEqual, "Willkommen")
		})




	})

}
