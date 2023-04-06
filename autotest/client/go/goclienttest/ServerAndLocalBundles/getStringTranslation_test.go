package ServerAndLocalBundles

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestGetStringTranslationReturnReqLocale(t *testing.T) {

	cfPath := "config.json"
	cfg, _ := sgtn.LoadConfig(cfPath)
	sgtn.Initialize(cfg)
	translation := sgtn.GetTranslation()

	Convey("localbundles-request: Get request locale's translation from localbundle", t, func() {



		Convey("localbundles-requestlocale: Get common strings successfully from localbundle(P0)", func() {

			tran1, _ := translation.GetStringMessage("GoClientTest", "1.0.0", "zh-Hans", "DefaultComponent", "message.translation.available")


			So(tran1, ShouldEqual, "该组件已准备好翻译。")
		})


		Convey("localbundles-requestlocale: Get placehodler strings and locale fallback to default locale(P0)", func() {

			tran2, _ := translation.GetStringMessage("GoClientTest", "1.0.0", "da", "DefaultComponent", "message.argument", "a", "b")
			fmt.Print(tran2)
			So(tran2, ShouldEqual, "Der Operator 'a' unterstützt die Eigenschaft 'b' nicht.")
		})

		Convey("localbundles-requestlocale: Get common strings and locale fallback to loacle source", func() {

			tran2, _ := translation.GetStringMessage("GoClientTest", "1.0.0", "ru", "default1", "language")
			// should add check if send out http request as the previous case has cached fr component translation

			So(tran2, ShouldEqual, "en Language")
		})

		Convey("localbundles-requestlocale: Get long and html tag strings and locale is empty(P0)", func() {

			tran2, _ := translation.GetStringMessage("GoClientTest", "1.0.0", "", "DefaultComponent", "message.url")
			// should add check if send out http request as the previous case has cached fr component translation

			So(tran2, ShouldEqual, "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>Die geplante Wartung wurde gestartet. </strong></span></p><p>Wichtige Informationen zur Wartung finden Sie hier: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>")
		})

		SkipConvey("localbundles-requestlocale: version fallback--- discuss bug", func() {

			tran2, _ := translation.GetStringMessage("GoClientTest", "1.0.1", "de", "DefaultComponent", "message.translation.available")
			// should add check if send out http request as the previous case has cached fr component translation

			So(tran2, ShouldEqual, "该组件已准备好翻译。")
		})

		
	})

	Convey("localbundles-request:abnormal scenario about getstringtranslation", t, func() {

		Convey("product name not exist(P1)", func() {

			//messages_zh-Hans.json of component "contact" isn't in service but in localbundle
			tran1, _ := translation.GetStringMessage("notexist", "1.0.0", "abc", "contact", "contact.title")

			//zh-Hans is default locale
			So(tran1, ShouldEqual, "contact.title")
		})


		Convey("version is not exist", func() {

			tran1, _ := translation.GetStringMessage("GoClientTest", "abc", "en", "contact", "contact.title")

			//zh-Hans is default locale
			So(tran1, ShouldEqual, "contact.title")
		})

		Convey("component is not exist", func() {

			tran1, _ := translation.GetStringMessage("GoClientTest", "1.0.0", "fr", "abc", "contact.title")

			//zh-Hans is default locale
			So(tran1, ShouldEqual, "contact.title")
		})

		Convey("key is not exist", func() {

			tran2, _ := translation.GetStringMessage("GoClientTest", "1.0.0", "en", "contact", "non-existing.key")
			So(tran2, ShouldEqual, "non-existing.key")
		})

		// SkipConvey("element is not abnormal-sucess", func() {

		// 	tran2, err := translation.GetStringMessage("GoClientTest", "1.0.0", "contact", "non-existing.key")
		// 	So(tran2, ShouldEqual, "non-existing.key")
		// 	fmt.Print(err)
		// })
	})

	Convey("localbundle-requestlocalex: Get request locale's translation from localBundles", t, func() {

		Convey("localbundles-requestlocale: request a locale(ko) doesn't in service but in localbundle, get request locale translation from localbundles(P1)", func() {

			//no messages_ko.json file in service but in localbundle
			tran1, _ := translation.GetStringMessage("GoClientTest", "1.0.0", "ko", "about", "about.title")

			//should return default locale translation as expect service to return nothing with 404. But now, service return en.
			//So(tran1, ShouldEqual, dlcncommonvalue)
			So(tran1, ShouldEqual, "에 대 한")
		})

		Convey("localbundles-defaultlocale: request a non-existing locale(abc) and a component isn't in service but in localbundle, get default locale translation from localbundles(P1)", func() {

			//messages_zh-Hans.json of component "contact" isn't in service but in localbundle
			tran1, _ := translation.GetStringMessage("GoClientTest", "1.0.0", "abc","contact" , "contact.message")
			fmt.Print("abc en translation: ", tran1)

			//should return default locale translation as expect service to return nothing with 404. But now, service return en.
			So(tran1, ShouldEqual, "Ihrer Kontaktseite.")

		})

		Convey("localbundles-defaultlocale: product only in local(P1)", func() {

			//messages_zh-Hans.json of component "contact" isn't in service but in localbundle
			tran1, _ := translation.GetStringMessage("GoClientTestlocal", "1.0.0", "de","DefaultComponent" , "message.translation.available")
			fmt.Print("abc en translation: ", tran1)

			//should return default locale translation as expect service to return nothing with 404. But now, service return en.
			So(tran1, ShouldEqual, "Die Übersetzung ist für diese Komponente bereit.")

		})

	})



	


}
