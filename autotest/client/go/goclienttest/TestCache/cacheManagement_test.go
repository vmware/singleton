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

func TestCacheManagement(t *testing.T) {
	SkipConvey("Enable cache and initialize cache, and cache isn't expired", t, func() {

		cfPath := "configCacheInitalLocalBundles.json"
		cfg, _ := sgtn.LoadConfig(cfPath)
		sgtn.Initialize(cfg)
		translation := sgtn.GetTranslation()
		name, version := "GoClientTest", "1.0.0"

		Convey("Enable initialize cache: Get all components and locales translation(P0)", func() {

			comlist, _ := translation.GetComponentList(name, version)
			localelist, _ := translation.GetLocaleList(name, version)
			fmt.Print(comlist)
			fmt.Print(localelist)
			

			//expectedcomlist := []string{DefaultComponent, RESX, about, contact, "default"}
			//So(len(comlist), ShouldEqual, 5)
			So(len(localelist), ShouldEqual, 9)

		})

		Convey("Enable initialize cache: Get string translation from cache(P0)", func() {

			tran1, _ := translation.GetStringMessage(name, version, "en", Defaultcom, commonkey)
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, commonvalue)
			// So(cfg, ShouldNotBeNil)

		})

		Convey("Enable initialize cache: Get component translation from cache(P0)", func() {

			commsg, _ := translation.GetComponentMessages(name, version, "es", Defaultcom)
			fmt.Print(commsg)
			value, _ := commsg.Get(commonkey)

			//So(commsg.Size(), ShouldEqual, 5)
			So(value, ShouldEqual, "La traducción está lista para este componente.")

		})

	})

	SkipConvey("Enable cache and initialize cache, but cache is expired", t, func() {

		cfPath := "configCacheInitalServiceExpired.json"
		cfg, _ := sgtn.LoadConfig(cfPath)
		sgtn.Initialize(cfg)
		//fmt.Print(inst)
		translation := sgtn.GetTranslation()
		// comlist, _ := translation.GetComponentList()
		// fmt.Print(comlist)
		// localelist, _ := translation.GetLocaleList()
		// fmt.Print(localelist)
		name, version := "GoClientTest", "1.0.0"

		Convey("cache expired: Get string translation from cache(P0)", func() {

			//time.Sleep(time.Duration(15) * time.Second)
			//wd, _ := os.Getwd()
			//log.Println(wd)
			defer func() {
				cmd := exec.Command(bat_path + "RevertString_de.bat")
				b, _ := cmd.Output()
				log.Println(string(b))
			}()
			tran1, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
			fmt.Print(tran1)
			//So(tran1, ShouldEqual, commonvalue)
			cmd := exec.Command(bat_path + "ModifyString_de.bat")
			b, _ := cmd.Output()
			log.Println(string(b))
			time.Sleep(time.Duration(50) * time.Second)
			tran2, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
			fmt.Print(tran2)
			//So(tran2, ShouldEqual, commonvalue)
			time.Sleep(time.Duration(5) * time.Second)
			tran3, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
			fmt.Print(tran3)

			//So(tran2, ShouldEqual, commonvalue)
			// So(cfg, ShouldNotBeNil)

		})

		SkipConvey("cache expired: Get component translation from cache(P0)", func() {

			time.Sleep(time.Duration(15) * time.Second)

			commsg, _ := translation.GetComponentMessages(name, version, "es", Defaultcom)
			fmt.Print("commsg: ", commsg)
			value, _ := commsg.Get(commonkey)

			//So(commsg.Size(), ShouldEqual, 5)
			So(value, ShouldEqual, "La traducción está lista para este componente.")

		})

	})

	//need to check if there is cacheDisable option
	SkipConvey("Disable cache and enable initialize cache", t, func() {

		cfPath := "configCacheInitalServiceExpired.json"
		cfg, _ := sgtn.LoadConfig(cfPath)
		sgtn.Initialize(cfg)
		translation := sgtn.GetTranslation()
		name, version := "GoClientTest", "1.0.0"
		// comlist, _ := translation.GetComponentList()
		// fmt.Print(comlist)
		// localelist, _ := translation.GetLocaleList()
		// fmt.Print(localelist)

		Convey("Disable cache: Get string translation from cache(P0)", func() {

			//time.Sleep(time.Duration(15) * time.Second)

			tran1, _ := translation.GetStringMessage(name, version, "en", Defaultcom, commonkey)
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, commonvalue)
			// So(cfg, ShouldNotBeNil)

		})

		Convey("Disable cache: Get component translation from cache(P0)", func() {

			//time.Sleep(time.Duration(15) * time.Second)

			commsg, _ := translation.GetComponentMessages(name, version, "es", Defaultcom)
			fmt.Print(commsg)
			value, _ := commsg.Get(commonkey)

			//So(commsg.Size(), ShouldEqual, 5)
			So(value, ShouldEqual, "La traducción está lista para este componente.")

		})

	})
}
