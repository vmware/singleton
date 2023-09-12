package LocalBundlesOnly

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestGetStringregestercomponentTranslation(t *testing.T) {

	cfPath := "configregester.json"

	cfg, _ := sgtn.LoadConfig(cfPath)

	var sourceMessages = map[string]string{"messages.welcome": "Welcome","message.translation.available":"register test","messages.only":"Hello only"}
	var testMessages = map[string]string{"test.welcome": "Welcome test","test.translation.available":"register test","test.only":"Hello only"}

	var sourceComponent = sgtn.NewMapComponentMsgs(sourceMessages, "en", "DefaultComponent")
	var testComponent = sgtn.NewMapComponentMsgs(testMessages, "en", "test")
	sgtn.RegisterSource("Register", "1.0.0", []sgtn.ComponentMsgs{testComponent,sourceComponent })
	sgtn.Initialize(cfg)
	translation := sgtn.GetTranslation()



	SkipConvey("localbundles-requestlocale: Get request locale's translation from localbundle", t, func() {



		// Convey("regester equal 1", func() {

		// 	tran1, _ := translation.GetStringMessage("Register", "1.0.0", "fr", "DefaultComponent", "messages.welcome")
		// 	tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.welcome")

		// 	// fmt.Print("**********************************************************************************")
		// 	commsg, _ := translation.GetComponentMessages("Register", "1.0.0", "en", "DefaultComponent")
		// 	commsg1, _ := translation.GetComponentMessages("Register", "1.0.0", "fr", "DefaultComponent")
		// 	fmt.Print("**********************************************************************************1111\n")
		// 	fmt.Print(commsg)
		// 	fmt.Print("\n**********************************************************************************11112\n")
		// 	fmt.Print(commsg1)


		// 	So(tran1, ShouldEqual, "Bienvenue")
		// 	So(tran2, ShouldEqual, "Welcome")

		// })

		// Convey("regester equal and da", func() {

		// 	tran1, _ := translation.GetStringMessage("Register", "1.0.0", "da", "DefaultComponent", "messages.welcome")
		// 	tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "DefaultComponent", "messages.welcome")
		// 	fmt.Print("**********************************************************************************2222\n")
		// 	commsg, _ := translation.GetComponentMessages("Register", "1.0.0", "da", "DefaultComponent")
		// 	fmt.Print(commsg)

		// 	fmt.Print("\n##################################################################################22221\n")
		// 	commsg1, _ := translation.GetComponentMessages("Register", "1.0.0", "en", "DefaultComponent")
		// 	fmt.Print(commsg1)
		// 	fmt.Print(tran1)

		// 	//So(tran1, ShouldEqual, "Willkommen")
		// 	So(tran2, ShouldEqual, "Welcome")
		// })



		// Convey("regester the component only in registered source", func() {

		// 	tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "test", "test.welcome")
		// 	tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "test", "test.welcome")
		// 	commsg, _ := translation.GetComponentMessages("Register", "1.0.0", "de", "test")
		// 	fmt.Print("**********************************************************************************3333\n")
		// 	fmt.Print(commsg)
		// 	commsg1, _ := translation.GetComponentMessages("Register", "1.0.0", "en", "test")
		// 	fmt.Print("\n**********************************************************************************33331\n")
		// 	fmt.Print(commsg1)


		// 	fmt.Print(tran1)

		// 	So(tran1, ShouldEqual, "test.welcome")
		// 	So(tran2, ShouldEqual, "Welcome test")
		// })

		// Convey("regester the component only in source", func() {

		// 	tran1, _ := translation.GetStringMessage("Register", "1.0.0", "de", "contact", "contact.message")

		// 	tran2, _ := translation.GetStringMessage("Register", "1.0.0", "en", "contact", "contact.message")
		// 	fmt.Print(tran1)
		// 	commsg, _ := translation.GetComponentMessages("Register", "1.0.0", "de", "DefaultComponent")
		// 	fmt.Print("**********************************************************************************4444\n")
		// 	fmt.Print(commsg)
		// 	fmt.Print("\n**********************************************************************************44441\n")
		// 	commsg1, _ := translation.GetComponentMessages("Register", "1.0.0", "en", "DefaultComponent")
		// 	fmt.Print(commsg1)


		// 	So(tran1, ShouldEqual, "Ihrer Kontaktseite.")
			
		// 	So(tran2, ShouldEqual, "Your contact page.")
		// })


		Convey("test *************************", func() {

			locales :=[]string{"fr"}
			components := []string{"DefaultComponent","test"}

			commsg1, err := translation.GetComponentsMessages("Register", "1.0.0", locales, components)
			So(err, ShouldBeNil)
			fmt.Println("output: ",commsg1)
			for _, bundle:=range commsg1 {
				fmt.Printf("current bundle: %#v\n", bundle)
				So(bundle.Component(), ShouldBeIn, components)
				So(bundle.Locale(), ShouldBeIn, locales)
				fmt.Printf("\n********************************************************\n")
				var key string
				key = "message.translation.available"
				//actualValue, found:= bundle.Get(key)
				actualValue, _:= bundle.Get(key)
				fmt.Printf(actualValue)
				//So(found, ShouldEqual, true)
				//So(actualValue, ShouldEqual, expectedValue)
			}

		})


	


	})

}
