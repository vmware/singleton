/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cachetest

import (
	"fmt"
	"log"
	"os/exec"
	"testing"
	"time"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestCacheManagement123(t *testing.T) {
	Convey("Only offline mode support", t, func() {

		cfPath := "configCacheLocalBundles.json"
		cfg, _ := sgtn.LoadConfig(cfPath)
		sgtn.Initialize(cfg)
		translation := sgtn.GetTranslation()
		name, version := "GoClientTest", "2.0"
		//Try to skip this test case in CI, because the added the new Component and locale won't clean up and may affect subsequent testing
		SkipConvey("Cache isn't expired: Get all components and locales translation(P0) when service has updates", func() {

			comlist, _ := translation.GetComponentList(name, version)
			localelist, _ := translation.GetLocaleList(name, version)
			fmt.Print(comlist)
			fmt.Print(localelist)
			So(len(comlist), ShouldEqual, 4)
			So(len(localelist), ShouldEqual, 9)
			cmd := exec.Command(bat_path + "Addcomponent_da1.0.0.bat")
			b, _ := cmd.Output()
			log.Println(string(b))
			time.Sleep(time.Duration(15) * time.Second)
			comlist1, _ := translation.GetComponentList(name, version)
			localelist1, _ := translation.GetLocaleList(name, version)
			fmt.Print(comlist1)
			fmt.Print(localelist1)
			So(len(comlist1), ShouldEqual, 4)
			So(len(localelist1), ShouldEqual, 9)
			time.Sleep(time.Duration(5) * time.Second)
			comlist2, _ := translation.GetComponentList(name, version)
			localelist2, _ := translation.GetLocaleList(name, version)
			fmt.Print(comlist2)
			fmt.Print(localelist2)
			So(len(comlist2), ShouldEqual, 4)
			So(len(localelist2), ShouldEqual, 9)

		})

		SkipConvey("Get string translation from cache(P0) when localebundle has update, this component only in localebundle", func() {

			commsg, _ := translation.GetComponentMessages(name, version, "fr", "default1")
			fmt.Print(commsg)
			value, _ := commsg.Get("language")
			fmt.Println(value)
			So(value, ShouldEqual, "FR Langue")
			defer func() {
				cmd := exec.Command(bat_path + "relocalefr2.0.bat")
				b, _ := cmd.Output()
				log.Println(string(b))
			}()
			cmd := exec.Command(bat_path + "modifylocalefr2.0.bat")
			b, _ := cmd.Output()
			log.Println(string(b))
			time.Sleep(time.Duration(50) * time.Second)
			commsg1, _ := translation.GetComponentMessages(name, version, "fr", "default1")
			fmt.Print(commsg1)
			value1, _ := commsg1.Get("language")
			fmt.Println(value1)
			So(value1, ShouldEqual, "FR Langue")
			time.Sleep(time.Duration(5) * time.Second)
			commsg2, _ := translation.GetComponentMessages(name, version, "fr", "default1")
			fmt.Print(commsg2)
			value2, _ := commsg2.Get("language")
			fmt.Println(value2)
			So(value2, ShouldEqual, "FR Langue")
			time.Sleep(time.Duration(5) * time.Second)
			commsg3, _ := translation.GetComponentMessages(name, version, "fr", "default1")
			fmt.Print(commsg3)
			value3, _ := commsg3.Get("language")
			fmt.Println(value3)
			So(value3, ShouldEqual, "FR Langue")
		})

		SkipConvey("Cache isn't expired: Get component translation from cache(P0) when service has updates", func() {

			commsg, _ := translation.GetComponentMessages(name, version, "fr", "about")
			fmt.Print(commsg)
			value, _ := commsg.Get("about.message")
			fmt.Println(value)
			So(value, ShouldEqual, "test fr key")
			defer func() {
				cmd := exec.Command(bat_path + "RevertString_fr1.0.0.bat")
				b, _ := cmd.Output()
				log.Println(string(b))
			}()
			cmd := exec.Command(bat_path + "ModifyString_fr1.0.0.bat")
			b, _ := cmd.Output()
			log.Println(string(b))
			time.Sleep(time.Duration(15) * time.Second)
			commsg1, _ := translation.GetComponentMessages(name, version, "fr", "about")
			fmt.Print(commsg1)
			value1, _ := commsg1.Get("about.message")
			fmt.Println(value1)
			So(value1, ShouldEqual, "test fr key")
			time.Sleep(time.Duration(5) * time.Second)
			commsg2, _ := translation.GetComponentMessages(name, version, "fr", "about")
			fmt.Print(commsg2)
			value2, _ := commsg2.Get("about.message")
			fmt.Println(value2)
			So(value2, ShouldEqual, "test fr key")
		})

	})

	// SkipConvey("Only server mode support, but cache is expired", t, func() {

	// 	cfPath := "configCacheLocalBundles.json"
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

	// })

	// //need to check if there is cacheDisable option
	// SkipConvey("Disable cache and enable initialize cache", t, func() {

	// 	cfPath := "configCacheLocalBundles.json"
	// 	cfg, _ := sgtn.LoadConfig(cfPath)
	// 	sgtn.Initialize(cfg)
	// 	translation := sgtn.GetTranslation()
	// 	name, version := "GoClientTest", "1.0.0"
	// 	// comlist, _ := translation.GetComponentList()
	// 	// fmt.Print(comlist)
	// 	// localelist, _ := translation.GetLocaleList()
	// 	// fmt.Print(localelist)

	// 	Convey("Disable cache: Get string translation from cache(P0)", func() {

	// 		//time.Sleep(time.Duration(15) * time.Second)

	// 		tran1, _ := translation.GetStringMessage(name, "1.0.1", "de", Defaultcom, commonkey)
	// 		fmt.Print(tran1)
	// 		tran2, _ := translation.GetStringMessage(name, "1.0.1", "de", Defaultcom, commonkey)
	// 		fmt.Print(tran2)
	// 		//So(tran1, ShouldEqual, commonvalue)
	// 		// So(cfg, ShouldNotBeNil)

	// 	})

	// 	SkipConvey("Disable cache: Get component translation from cache(P0)", func() {

	// 		//time.Sleep(time.Duration(15) * time.Second)

	// 		commsg, _ := translation.GetComponentMessages(name, version, "es", Defaultcom)
	// 		fmt.Print(commsg)
	// 		value, _ := commsg.Get(commonkey)

	// 		//So(commsg.Size(), ShouldEqual, 5)
	// 		So(value, ShouldEqual, "La traducción está lista para este componente.")

	// 	})

	// })
}
