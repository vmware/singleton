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
 
 func TestGetComponentsTranslation(t *testing.T) {
 
	 cfPath := "config.json"
	 cfg, _ := sgtn.LoadConfig(cfPath)
	 sgtn.Initialize(cfg)
	 translation := sgtn.GetTranslation()

	Convey("component-localbundle-return: Get data from localbundle", t, func() {

		SkipConvey("2 locales/components, get data from localbundle", func() {

			locales :=[]string{"fr","en"}
			components := []string{"contact","about"}

			commsg1, err := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)
			So(err, ShouldBeNil)
			fmt.Println("output: ",commsg1)
			So(len(commsg1),ShouldEqual, 4)

			for _, bundle:=range commsg1 {
				fmt.Printf("current bundle: %#v\n", bundle)
				So(bundle.Component(), ShouldBeIn, components)
				So(bundle.Locale(), ShouldBeIn, locales)

				var key, expectedValue string
				if bundle.Component() == "contact" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "contact.support", "Soutien:"
					}else {
						key,expectedValue = "contact.support", "Support:"
					}
				}else if bundle.Component() == "about" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "about.message", "La page Description de l'application."
					}else {
						key,expectedValue = "about.message", "Your application description page."
					}	
				}
				actualValue, found:= bundle.Get(key)
				So(found, ShouldEqual, true)
				So(actualValue, ShouldEqual, expectedValue)
			}

		})

		Convey("1 locale/component, get data from localbundle", func() {

			locales :=[]string{"zh-HANS"}
			components := []string{"contact"}

			commsg1, err := translation.GetComponentsMessages("GoClientTest", "1.0.0", locales, components)
			So(err, ShouldBeNil)
			fmt.Println("output: ",commsg1)
			So(len(commsg1),ShouldEqual, 1)

			for _, bundle:=range commsg1 {
				fmt.Printf("current bundle: %#v\n", bundle)
				So(bundle.Component(), ShouldBeIn, components)
				So(bundle.Locale(), ShouldBeIn, locales)

				var key, expectedValue string
				key,expectedValue = "contact.support", "支持："
				actualValue, found:= bundle.Get(key)
				So(found, ShouldEqual, true)
				So(actualValue, ShouldEqual, expectedValue)
			}

		})

		SkipConvey("all locales if not define specific locale, get data from localbundle", func() {

			locales :=[]string{}
			components := []string{"contact","about"}

			commsg1, err := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)
			So(err, ShouldBeNil)
			fmt.Println("output: ",commsg1)
			So(len(commsg1),ShouldEqual, 9)

			for _, bundle:=range commsg1 {
				fmt.Printf("current bundle: %#v\n", bundle)
				fmt.Printf("current Locales: %#v\n", bundle.Locale())
				fmt.Printf("current components: %#v\n", bundle.Component())

				So(bundle.Component(), ShouldBeIn, components)
				// So(bundle.Locale(), ShouldBeIn, locales)

				var key, expectedValue string
				if bundle.Component() == "contact" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "contact.support", "Soutien:"
					}else if bundle.Locale() == "en"{
						key,expectedValue = "contact.support", "Support:"
					}else if bundle.Locale() == "de-ch"{
						key,expectedValue = "contact.support", "Unterstützung:"
					}else if bundle.Locale() == "ja"{
						key,expectedValue = "contact.support", "サポート："
					}
				}else if bundle.Component() == "about" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "about.message", "La page Description de l'application."
					}else if bundle.Locale() == "ja"{
						key,expectedValue = "about.title", "に関しては"
					}else if bundle.Locale() == "ar"{
						key,expectedValue = "about.title", "حول"
					}else if bundle.Locale() == "de"{
						key,expectedValue = "about.message", "Ihrer Bewerbungs Beschreibung Seite."
					}else if bundle.Locale() == "en"{
						key,expectedValue = "about.title", "About"
					}
				}
				actualValue, found:= bundle.Get(key)
				fmt.Println("output: ", actualValue)
				
				So(actualValue, ShouldEqual, expectedValue)
				So(found, ShouldEqual, true)
			}

		})

		SkipConvey("all components if not define specific component, get data from localbundle", func() {

			locales :=[]string{"fr", "ja"}
			components := []string{}

			commsg1, err := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)
			So(err, ShouldBeNil)
			fmt.Println("output: ",commsg1)
			So(len(commsg1),ShouldEqual, 6)

			for _, bundle:=range commsg1 {
				fmt.Printf("current bundle: %#v\n", bundle)
				fmt.Printf("current Locales: %#v\n", bundle.Locale())
				fmt.Printf("current components: %#v\n", bundle.Component())

				// So(bundle.Component(), ShouldBeIn, components)
				So(bundle.Locale(), ShouldBeIn, locales)

				var key, expectedValue string
				if bundle.Component() == "contact" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "contact.support", "Soutien:"
					}else if bundle.Locale() == "en"{
						key,expectedValue = "contact.support", "Support:"
					}else if bundle.Locale() == "de-CH"{
						key,expectedValue = "contact.support", "Unterstützung:"
					}else if bundle.Locale() == "ja"{
						key,expectedValue = "contact.support", "サポート："
					}
				}else if bundle.Component() == "about" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "about.message", "La page Description de l'application."
					}else if bundle.Locale() == "ja"{
						key,expectedValue = "about.title", "に関しては"
					}else if bundle.Locale() == "ar"{
						key,expectedValue = "about.title", "حول"
					}else if bundle.Locale() == "de"{
						key,expectedValue = "about.message", "Ihrer Bewerbungs Beschreibung Seite."
					}else if bundle.Locale() == "en"{
						key,expectedValue = "about.title", "About"
					}
				}else if bundle.Component() == "DefaultComponent" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "messages.welcome", "Bienvenue"
					}else if bundle.Locale() == "ja"{
						key,expectedValue = "messages.welcome", "ようこそ"
					}
				}
				actualValue, found:= bundle.Get(key)
				fmt.Println("output: ", actualValue)
				So(found, ShouldEqual, true)
				So(actualValue, ShouldEqual, expectedValue)
			}

		})

		SkipConvey("all locales/components if not define specific locale/component, get data from localbundle", func() {

			locales :=[]string{}
			components := []string{}

			commsg1, err := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)
			So(err, ShouldBeNil)
			fmt.Println("output: ",commsg1)
			// the messages_lates.json will be included in offline mode, but service would not.
			So(len(commsg1),ShouldEqual, 13)

			for _, bundle:=range commsg1 {
				fmt.Printf("current bundle: %#v\n", bundle)
				fmt.Printf("current Locales: %#v\n", bundle.Locale())
				fmt.Printf("current components: %#v\n", bundle.Component())

				// So(bundle.Component(), ShouldBeIn, components)
				// So(bundle.Locale(), ShouldBeIn, locales)

				var key, expectedValue string
				if bundle.Component() == "contact" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "contact.support", "Soutien:"
					}else if bundle.Locale() == "en"{
						key,expectedValue = "contact.support", "Support:"
					}else if bundle.Locale() == "de-CH"{
						key,expectedValue = "contact.support", "Unterstützung:"
					}else if bundle.Locale() == "ja"{
						key,expectedValue = "contact.support", "サポート："
					}
				}else if bundle.Component() == "about" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "about.message", "La page Description de l'application."
					}else if bundle.Locale() == "ja"{
						key,expectedValue = "about.title", "に関しては"
					}else if bundle.Locale() == "ar"{
						key,expectedValue = "about.title", "حول"
					}else if bundle.Locale() == "de"{
						key,expectedValue = "about.message", "Ihrer Bewerbungs Beschreibung Seite."
					}else if bundle.Locale() == "en"{
						key,expectedValue = "about.title", "About"
					}
				}else if bundle.Component() == "DefaultComponent" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "messages.welcome", "Bienvenue"
					}else if bundle.Locale() == "ja"{
						key,expectedValue = "messages.welcome", "ようこそ"
					}else if bundle.Locale() == "es"{
						key,expectedValue = "messages.welcome", "Bienvenido"
					}else if bundle.Locale() == "it"{
						key,expectedValue = "messages.welcome", "Welcome"
					}
				}
				actualValue, found:= bundle.Get(key)
				fmt.Println("output: ", actualValue)
				So(found, ShouldEqual, true)
				So(actualValue, ShouldEqual, expectedValue)
			}

		})

		SkipConvey("version fallback: 2 locales/components, get data from localbundle", func() {

			locales :=[]string{"fr","en"}
			components := []string{"contact","about"}

			commsg1, err := translation.GetComponentsMessages("GoClientTest", "12.0", locales, components)
			So(err, ShouldBeNil)
			fmt.Println("output: ",commsg1)
			So(len(commsg1),ShouldEqual, 4)

			for _, bundle:=range commsg1 {
				fmt.Printf("current bundle: %#v\n", bundle)
				So(bundle.Component(), ShouldBeIn, components)
				So(bundle.Locale(), ShouldBeIn, locales)

				var key, expectedValue string
				if bundle.Component() == "contact" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "contact.support", "Soutien:"
					}else {
						key,expectedValue = "contact.support", "Support:"
					}
				}else if bundle.Component() == "about" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "about.message", "La page Description de l'application."
					}else {
						key,expectedValue = "about.message", "Your application description page."
					}	
				}
				actualValue, found:= bundle.Get(key)
				So(found, ShouldEqual, true)
				So(actualValue, ShouldEqual, expectedValue)
			}

		})

		SkipConvey("language fallback: 2 locales/components, get data from localbundle", func() {

			locales :=[]string{"fr-CA","en"}
			components := []string{"contact","about"}

			commsg1, err := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)
			So(err, ShouldBeNil)
			fmt.Println("output: ",commsg1)
			So(len(commsg1),ShouldEqual, 4)

			for _, bundle:=range commsg1 {
				fmt.Printf("current bundle: %#v\n", bundle)
				So(bundle.Component(), ShouldBeIn, components)
				So(bundle.Locale(), ShouldBeIn, locales)

				var key, expectedValue string
				if bundle.Component() == "contact" {
					if bundle.Locale() == "fr-CA" {
						key,expectedValue = "contact.support", "Soutien:"
					}else {
						key,expectedValue = "contact.support", "Support:"
					}
				}else if bundle.Component() == "about" {
					if bundle.Locale() == "fr-CA" {
						key,expectedValue = "about.message", "La page Description de l'application."
					}else {
						key,expectedValue = "about.message", "Your application description page."
					}	
				}
				actualValue, found:= bundle.Get(key)
				So(found, ShouldEqual, true)
				So(actualValue, ShouldEqual, expectedValue)
			}

		})

		SkipConvey("1 locale/2 components(one is existing, another isn't), get existing component data from localbundle", func() {

			locales :=[]string{"ar"}
			components := []string{"contact","about"}

			commsg1, err := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)
			So(err, ShouldBeNil)
			fmt.Println("output: ",commsg1)
			So(len(commsg1),ShouldEqual, 1)

			for _, bundle:=range commsg1 {
				fmt.Printf("current bundle: %#v\n", bundle)
				So(bundle.Component(), ShouldEqual, "about")
				So(bundle.Locale(), ShouldEqual, "ar")
			}

		})
	})

	SkipConvey("component-localbundle-error: invalid requested data", t, func() {

		Convey("invalid product name: error", func() {

			locales :=[]string{"fr","en"}
			components := []string{"contact","about"}

			_, err := translation.GetComponentsMessages("NotExistProduct", "10.0", locales, components)
			So(err.Error(), ShouldContainSubstring, "no translations are available for {product 'NotExistProduct', version '10.0', locales '[fr en]', components '[contact about]'}")

		})

		Convey("Nonexisting version name: error", func() {

			locales :=[]string{"fr","en"}
			components := []string{"contact","about"}

			_, err := translation.GetComponentsMessages("GoClientTest", "0.0.1", locales, components)
			So(err.Error(), ShouldContainSubstring, "no translations are available for {product 'GoClientTest', version '0.0.1', locales '[fr en]', components '[contact about]'}")

		})

		Convey("unsupported locale: error", func() {

			locales :=[]string{"ru"}
			components := []string{"contact","about"}

			_, err := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)
			So(err.Error(), ShouldContainSubstring, "no translations are available for {product 'GoClientTest', version '10.0', locales '[ru]', components '[contact about]'}")

		})

		Convey("nonexisting component: error", func() {

			locales :=[]string{"en"}
			components := []string{"contactabout"}

			_, err := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)
			So(err.Error(), ShouldContainSubstring, "no translations are available for {product 'GoClientTest', version '10.0', locales '[en]', components '[contactabout]'}")

		})
	})

}
