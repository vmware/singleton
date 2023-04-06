/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cachetest

import (
	"fmt"
	// "log"
	// "os/exec"
	"testing"
	"time"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestCacheManagement1(t *testing.T) {
	// SkipConvey("Only server mode support, and cache isn't expired", t, func() {

	// 	cfPath := "configCacheServiceExpired.json"
	// 	cfg, _ := sgtn.LoadConfig(cfPath)
	// 	sgtn.Initialize(cfg)
	// 	translation := sgtn.GetTranslation()
	// 	name, version := "GoClientTest", "1.0.0"
	// 	//Try to skip this test case in CI, because the added the new Component and locale won't clean up and may affect subsequent testing
	// 	SkipConvey("Cache isn't expired: Get all components and locales translation(P0) when service has updates", func() {

	// 		comlist, _ := translation.GetComponentList(name, version)
	// 		localelist, _ := translation.GetLocaleList(name, version)
	// 		fmt.Print(comlist)
	// 		fmt.Print(localelist)
	// 		So(len(comlist), ShouldEqual, 4)
	// 		So(len(localelist), ShouldEqual, 9)
	// 		cmd := exec.Command(bat_path + "Addcomponent_da1.0.0.bat")
	// 		b, _ := cmd.Output()
	// 		log.Println(string(b))
	// 		time.Sleep(time.Duration(15) * time.Second)
	// 		comlist1, _ := translation.GetComponentList(name, version)
	// 		localelist1, _ := translation.GetLocaleList(name, version)
	// 		fmt.Print(comlist1)
	// 		fmt.Print(localelist1)
	// 		So(len(comlist1), ShouldEqual, 4)
	// 		So(len(localelist1), ShouldEqual, 9)
	// 		time.Sleep(time.Duration(5) * time.Second)
	// 		comlist2, _ := translation.GetComponentList(name, version)
	// 		localelist2, _ := translation.GetLocaleList(name, version)
	// 		fmt.Print(comlist2)
	// 		fmt.Print(localelist2)
	// 		So(len(comlist2), ShouldEqual, 4)
	// 		So(len(localelist2), ShouldEqual, 9)

	// 	})

	// 	Convey("Cache isn't expired: Get string translation from cache(P0) when service has updates", func() {

	// 		tran1, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
	// 		fmt.Print(tran1)
	// 		So(tran1, ShouldEqual, "test de key")
	// 		defer func() {
	// 			cmd := exec.Command(bat_path + "RevertString_de1.0.0.bat")
	// 			b, _ := cmd.Output()
	// 			log.Println(string(b))
	// 		}()
	// 		cmd := exec.Command(bat_path + "ModifyString_de1.0.0.bat")
	// 		b, _ := cmd.Output()
	// 		log.Println(string(b))
	// 		time.Sleep(time.Duration(15) * time.Second)
	// 		tran2, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
	// 		fmt.Print(tran2)
	// 		So(tran2, ShouldEqual, "test de key")
	// 		time.Sleep(time.Duration(5) * time.Second)
	// 		tran3, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
	// 		fmt.Print(tran3)
	// 		So(tran3, ShouldEqual, "test de key")

	// 	})

	// 	Convey("Cache isn't expired: Get component translation from cache(P0) when service has updates", func() {

	// 		commsg, _ := translation.GetComponentMessages(name, version, "fr", "about")
	// 		fmt.Print(commsg)
	// 		value, _ := commsg.Get("about.message")
	// 		fmt.Println(value)
	// 		So(value, ShouldEqual, "test fr key")
	// 		defer func() {
	// 			cmd := exec.Command(bat_path + "RevertString_fr1.0.0.bat")
	// 			b, _ := cmd.Output()
	// 			log.Println(string(b))
	// 		}()
	// 		cmd := exec.Command(bat_path + "ModifyString_fr1.0.0.bat")
	// 		b, _ := cmd.Output()
	// 		log.Println(string(b))
	// 		time.Sleep(time.Duration(15) * time.Second)
	// 		commsg1, _ := translation.GetComponentMessages(name, version, "fr", "about")
	// 		fmt.Print(commsg1)
	// 		value1, _ := commsg1.Get("about.message")
	// 		fmt.Println(value1)
	// 		So(value1, ShouldEqual, "test fr key")
	// 		time.Sleep(time.Duration(5) * time.Second)
	// 		commsg2, _ := translation.GetComponentMessages(name, version, "fr", "about")
	// 		fmt.Print(commsg2)
	// 		value2, _ := commsg2.Get("about.message")
	// 		fmt.Println(value2)
	// 		So(value2, ShouldEqual, "test fr key")
	// 	})

	// })

	// Convey("Only server mode support, but cache is expired", t, func() {

	// 	cfPath := "configCacheServiceExpired.json"
	// 	cfg, _ := sgtn.LoadConfig(cfPath)
	// 	sgtn.Initialize(cfg)
	// 	translation := sgtn.GetTranslation()
	// 	name, version := "GoClientTest", "1.0"

	// 	SkipConvey("Cache expired: Get all components and locales translation(P0) when service has updates", func() {

	// 		comlist, _ := translation.GetComponentList(name, version)
	// 		localelist, _ := translation.GetLocaleList(name, version)
	// 		fmt.Print(comlist)
	// 		fmt.Print(localelist)
	// 		So(len(comlist), ShouldEqual, 4)
	// 		So(len(localelist), ShouldEqual, 9)
	// 		cmd := exec.Command(bat_path + "Addcomponent_da1.0.bat")
	// 		b, _ := cmd.Output()
	// 		log.Println(string(b))
	// 		time.Sleep(time.Duration(55) * time.Second)
	// 		comlist1, _ := translation.GetComponentList(name, version)
	// 		localelist1, _ := translation.GetLocaleList(name, version)
	// 		fmt.Print(comlist1)
	// 		fmt.Print(localelist1)
	// 		So(len(comlist1), ShouldEqual, 4)
	// 		So(len(localelist1), ShouldEqual, 9)
	// 		time.Sleep(time.Duration(5) * time.Second)
	// 		comlist2, _ := translation.GetComponentList(name, version)
	// 		localelist2, _ := translation.GetLocaleList(name, version)
	// 		fmt.Print(comlist2)
	// 		fmt.Print(localelist2)
	// 		So(len(comlist2), ShouldEqual, 5)
	// 		So(len(localelist2), ShouldEqual, 10)

	// 		//expectedcomlist := []string{DefaultComponent, RESX, about, contact, "default"}
	// 		//So(len(comlist), ShouldEqual, 5)
	// 		//So(len(localelist), ShouldEqual, 9)

	// 	})

	// 	Convey("Cache expired: Get string translation from cache(P0) when service has updates", func() {

	// 		tran1, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
	// 		fmt.Print(tran1)
	// 		So(tran1, ShouldEqual, "test de key")
	// 		defer func() {
	// 			cmd := exec.Command(bat_path + "RevertString_de1.0.bat")
	// 			b, _ := cmd.Output()
	// 			log.Println(string(b))
	// 		}()
	// 		cmd := exec.Command(bat_path + "ModifyString_de1.0.bat")
	// 		b, _ := cmd.Output()
	// 		log.Println(string(b))
	// 		time.Sleep(time.Duration(50) * time.Second)
	// 		tran2, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
	// 		fmt.Print(tran2)
	// 		So(tran2, ShouldEqual, "test de key")
	// 		time.Sleep(time.Duration(5) * time.Second)
	// 		tran3, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
	// 		fmt.Print(tran3)
	// 		So(tran3, ShouldEqual, "test change de key")

	// 	})

	// 	Convey("Cache expired: Get component translation from cache(P0) when service has updates", func() {

	// 		commsg, _ := translation.GetComponentMessages(name, version, "fr", "about")
	// 		fmt.Print(commsg)
	// 		value, _ := commsg.Get("about.message")
	// 		fmt.Println(value)
	// 		So(value, ShouldEqual, "test fr key")
	// 		defer func() {
	// 			cmd := exec.Command(bat_path + "RevertString_fr1.0.bat")
	// 			b, _ := cmd.Output()
	// 			log.Println(string(b))
	// 		}()
	// 		cmd := exec.Command(bat_path + "ModifyString_fr1.0.bat")
	// 		b, _ := cmd.Output()
	// 		log.Println(string(b))
	// 		time.Sleep(time.Duration(50) * time.Second)
	// 		commsg1, _ := translation.GetComponentMessages(name, version, "fr", "about")
	// 		fmt.Print(commsg1)
	// 		value1, _ := commsg1.Get("about.message")
	// 		fmt.Println(value1)
	// 		So(value1, ShouldEqual, "test fr key")
	// 		time.Sleep(time.Duration(5) * time.Second)
	// 		commsg2, _ := translation.GetComponentMessages(name, version, "fr", "about")
	// 		fmt.Print(commsg2)
	// 		value2, _ := commsg2.Get("about.message")
	// 		fmt.Println(value2)
	// 		So(value2, ShouldEqual, "test change fr key")

	// 	})

	// 	// If cache item is expired, and online bundle fetch failed, offline fetch is triggered.
	// 	SkipConvey("Cache expired: Get component1 translation from cache(P0) when service has updates", func() {

	// 		commsg, _ := translation.GetComponentMessages(name, version, "fr", "about")
	// 		fmt.Print(commsg)
	// 		value, _ := commsg.Get("about.message")
	// 		fmt.Println(value)
	// 		So(value, ShouldEqual, "test fr key")
	// 		// defer func() {
	// 		// 	cmd := exec.Command(bat_path + "RevertString_fr1.0.bat")
	// 		// 	b, _ := cmd.Output()
	// 		// 	log.Println(string(b))
	// 		// }()
	// 		// cmd := exec.Command(bat_path + "ModifyString_fr1.0.bat")
	// 		// b, _ := cmd.Output()
	// 		// log.Println(string(b))
	// 		time.Sleep(time.Duration(50) * time.Second)
	// 		commsg1, _ := translation.GetComponentMessages(name, version, "fr", "about")
	// 		fmt.Print(commsg1)
	// 		value1, _ := commsg1.Get("about.message")
	// 		fmt.Println(value1)
	// 		//So(value1, ShouldEqual, "test fr key")
	// 		time.Sleep(time.Duration(5) * time.Second)
	// 		commsg2, _ := translation.GetComponentMessages(name, version, "fr", "about")
	// 		fmt.Print(commsg2)
	// 		value2, _ := commsg2.Get("about.message")
	// 		fmt.Println(value2)
	// 		//So(value2, ShouldEqual, "test change fr key")

	// 	})

	// })

	//need to check if there is cacheDisable option
	SkipConvey("Disable cache and enable initialize cache", t, func() {

		cfPath := "configCacheServiceExpired.json"
		cfg, _ := sgtn.LoadConfig(cfPath)
		sgtn.Initialize(cfg)
		translation := sgtn.GetTranslation()
		// name, version := "GoClientTest", "1.0.0"
		// comlist, _ := translation.GetComponentList()
		// fmt.Print(comlist)
		// localelist, _ := translation.GetLocaleList()
		// fmt.Print(localelist)

		// SkipConvey("Disable cache: Get string translation from cache(P0)", func() {

		// 	//time.Sleep(time.Duration(15) * time.Second)

		// 	tran1, _ := translation.GetStringMessage(name, "1.0.1", "de", Defaultcom, commonkey)
		// 	fmt.Print(tran1)
		// 	tran2, _ := translation.GetStringMessage(name, "1.0.1", "de", Defaultcom, commonkey)
		// 	fmt.Print(tran2)
		// 	//So(tran1, ShouldEqual, commonvalue)
		// 	// So(cfg, ShouldNotBeNil)

		// })

		// SkipConvey("Disable cache: Get component translation from cache(P0)", func() {

		// 	//time.Sleep(time.Duration(15) * time.Second)

		// 	commsg, _ := translation.GetComponentMessages(name, version, "es", Defaultcom)
		// 	fmt.Print(commsg)
		// 	value, _ := commsg.Get(commonkey)

		// 	//So(commsg.Size(), ShouldEqual, 5)
		// 	So(value, ShouldEqual, "La traducción está lista para este componente.")

		// })

		Convey("Cache expired: Get component translation from cache(P0) when service has updates", func() {


			locales :=[]string{"fr","en"}
			components := []string{"contact","about"}

			commsg1, _ := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)

			for _, bundle:=range commsg1 {
				fmt.Printf("current bundle: %#v\n", bundle)

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
			
			time.Sleep(time.Duration(40) * time.Second)

			commsg2, _ := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)
			for _, bundle:=range commsg2 {
				fmt.Printf("current bundle: %#v\n", bundle)

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

			time.Sleep(time.Duration(5) * time.Second)

			commsg3, _ := translation.GetComponentsMessages("GoClientTest", "10.0", locales, components)
			for _, bundle:=range commsg3 {
				fmt.Printf("current bundle: %#v\n", bundle)

				var key, expectedValue string
				if bundle.Component() == "contact" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "contact.support", "update-Soutien: 1"
					}else {
						key,expectedValue = "contact.support", "Support: 1"
					}
				}else if bundle.Component() == "about" {
					if bundle.Locale() == "fr" {
						key,expectedValue = "about.message", "La page Description de l'application.1"
					}else {
						key,expectedValue = "about.message", "Your application description page. 1"
					}	
				}
				actualValue, found:= bundle.Get(key)
				So(found, ShouldEqual, true)
				So(actualValue, ShouldEqual, expectedValue)
			}

		})

	})
}
