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

	"github.com/davecgh/go-spew/spew"
	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestExternalCacheManagement(t *testing.T) {

	SkipConvey("Enable cache and initialize cache, and cache isn't expired", t, func() {

		// cfPath := "testdata/Config/configCacheInitalLocalBundles.yaml"
		cs := NewCache()
		sgtn.RegisterCache(cs)
		cfPath := "configCacheServiceExpired.json"
		cfg, _ := sgtn.LoadConfig(cfPath)
		sgtn.Initialize(cfg)
		//fmt.Printf("%#v", cs)
		//spew.Dump(cs)

		translation := sgtn.GetTranslation()
		name, version := "GoClientTest", "1.0"
		Convey("Cache expired: Get string translation from cache(P0) when service has updates", func() {

			tran1, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
			fmt.Print(tran1)
			So(tran1, ShouldEqual, "test de key")
			cs.Clear()
			defer func() {
				cmd := exec.Command(bat_path + "RevertString_de1.0.bat")
				b, _ := cmd.Output()
				log.Println(string(b))
			}()
			cmd := exec.Command(bat_path + "ModifyString_de1.0.bat")
			b, _ := cmd.Output()
			log.Println(string(b))
			time.Sleep(time.Duration(10) * time.Second)
			tran2, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
			fmt.Print(tran2)
			value1, _ := cs.Get("about.message")
			fmt.Print(value1)
			//So(tran2, ShouldEqual, "test de key")
			time.Sleep(time.Duration(5) * time.Second)
			tran3, _ := translation.GetStringMessage(name, version, "de", "about", "about.message")
			fmt.Print(tran3)
			So(tran3, ShouldEqual, "test change de key")
			value2, _ := cs.Get("about.message")
			fmt.Print(value2)

		})

		SkipConvey("Enable initialize cache: Get all components and locales translation(P0)", func() {

			comlist, _ := translation.GetComponentList(name, version)
			localelist, _ := translation.GetLocaleList(name, version)
			fmt.Print(comlist)
			fmt.Print(localelist)
			tran1, _ := translation.GetStringMessage(name, version, "en", "about", "about.message")
			fmt.Print(tran1)
			fmt.Printf("%#v", cs)
			time.Sleep(time.Duration(5) * time.Second)
			spew.Dump(cs)
			value1, found1 := cs.Get("about.message")
			fmt.Printf("cache is: %s\n", value1)
			fmt.Printf("cache err: %t\n", found1)
			//cscomlist1, found1 := cs.Get(0xc0000671d0)
			//fmt.Printf("cache component list1: %s\n", cscomlist1)
			//fmt.Printf("cache component list1: %t\n", found1)
			//time.Sleep(time.Duration(15) * time.Second)
			// cs.Set("eee", "uuu")
			// cscomlist2, _ := cs.Get("eee")

			//expectedcomlist := []string{DefaultComponent, RESX, about, contact}
			//So(len(comlist), ShouldEqual, 4)
			//So(len(localelist), ShouldEqual, 9)

		})

		SkipConvey("Enable initialize cache: Get string translation from cache(P0)", func() {

			tran1, _ := translation.GetStringMessage(name, version, "en", Defaultcom, commonkey)
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, commonvalue)
			// So(cfg, ShouldNotBeNil)

		})

		SkipConvey("Enable initialize cache: Get component translation from cache(P0)", func() {

			commsg, _ := translation.GetComponentMessages(name, version, "es", Defaultcom)
			fmt.Print(commsg)
			value, _ := commsg.Get(commonkey)

			//So(commsg.Size(), ShouldEqual, 5)
			So(value, ShouldEqual, "La traducción está lista para este componente.")

		})

		// Convey("Enable initialize cache: Get multiple components translation from cache(P0)", func() {

		// 	coms := []string{Defaultcom, "about"}
		// 	commsg, _ := translation.GetComponentsMessages("es", coms)
		// 	// fmt.Print(tran1)
		// 	So(len(commsg), ShouldEqual, 2)

		// 	commsg1 := commsg[Defaultcom]
		// 	commsg2 := commsg["about"]

		// 	value1, _ := commsg1.Get(commonkey)
		// 	value2, _ := commsg2.Get("about.title")

		// 	So(commsg1.Size(), ShouldEqual, 5)
		// 	So(commsg2.Size(), ShouldEqual, 3)
		// 	So(value1, ShouldEqual, "La traducción está lista para este componente.")
		// 	So(value2, ShouldEqual, "es_About")

		// })

	})

	// Need to check all of case when investigate cache feature
	// SkipConvey("Enable cache and initialize cache, but cache is expired", t, func() {

	// 	cfPath := "configCacheInitalServiceExpired.yaml"
	// 	cfg, _ := sgtn.NewConfig(cfPath)
	// 	inst, _ := sgtn.NewInst(*cfg)
	// 	fmt.Print(inst)
	// 	translation := inst.GetTranslation()
	// 	// comlist, _ := translation.GetComponentList()
	// 	// fmt.Print(comlist)
	// 	// localelist, _ := translation.GetLocaleList()
	// 	// fmt.Print(localelist)

	// 	Convey("cache expired: Get string translation from cache(P0)", func() {

	// 		time.Sleep(time.Duration(15) * time.Second)

	// 		tran1, _ := translation.GetStringMessage("en", Defaultcom, commonkey)
	// 		// fmt.Print(tran1)

	// 		So(tran1, ShouldEqual, commonvalue)
	// 		// So(cfg, ShouldNotBeNil)

	// 	})

	// 	Convey("cache expired: Get component translation from cache(P0)", func() {

	// 		time.Sleep(time.Duration(15) * time.Second)

	// 		commsg, _ := translation.GetComponentMessages("es", Defaultcom)
	// 		fmt.Print(commsg)
	// 		value, _ := commsg.Get(commonkey)

	// 		So(commsg.Size(), ShouldEqual, 5)
	// 		So(value, ShouldEqual, "La traducción está lista para este componente.")

	// 	})

	// 	// Convey("cache expired: Get multiple components translation from cache(P0)", func() {

	// 	// 	time.Sleep(time.Duration(15) * time.Second)

	// 	// 	coms := []string{Defaultcom, "about"}
	// 	// 	commsg, _ := translation.GetComponentsMessages("es", coms)
	// 	// 	// fmt.Print(tran1)
	// 	// 	So(len(commsg), ShouldEqual, 2)

	// 	// 	commsg1 := commsg[Defaultcom]
	// 	// 	commsg2 := commsg["about"]

	// 	// 	value1, _ := commsg1.Get(commonkey)
	// 	// 	value2, _ := commsg2.Get("about.title")

	// 	// 	So(commsg1.Size(), ShouldEqual, 5)
	// 	// 	So(commsg2.Size(), ShouldEqual, 3)
	// 	// 	So(value1, ShouldEqual, "La traducción está lista para este componente.")
	// 	// 	So(value2, ShouldEqual, "es_About")

	// 	// })

	// })

	//Need to check if still have initialized option
	// SkipConvey("Disable cache and enable initialize cache", t, func() {

	// 	cfPath := "configCacheDisable.yaml"
	// 	cfg, _ := sgtn.NewConfig(cfPath)
	//   inst, _ := sgtn.NewInst(*cfg)

	// 	fmt.Print(inst)
	// 	translation := inst.GetTranslation()
	// 	// comlist, _ := translation.GetComponentList()
	// 	// fmt.Print(comlist)
	// 	// localelist, _ := translation.GetLocaleList()
	// 	// fmt.Print(localelist)

	// 	Convey("Disable cache: Get string translation from cache(P0)", func() {

	// 		//time.Sleep(time.Duration(15) * time.Second)

	// 		tran1, _ := translation.GetStringMessage("en", Defaultcom, commonkey)
	// 		// fmt.Print(tran1)

	// 		So(tran1, ShouldEqual, commonvalue)
	// 		// So(cfg, ShouldNotBeNil)

	// 	})

	// 	Convey("Disable cache: Get component translation from cache(P0)", func() {

	// 		//time.Sleep(time.Duration(15) * time.Second)

	// 		commsg, _ := translation.GetComponentMessages("es", Defaultcom)
	// 		fmt.Print(commsg)
	// 		value, _ := commsg.Get(commonkey)

	// 		So(commsg.Size(), ShouldEqual, 5)
	// 		So(value, ShouldEqual, "La traducción está lista para este componente.")

	// 	})

	// 	// Convey("Disable cache: Get multiple components translation from cache(P0)", func() {

	// 	// 	//time.Sleep(time.Duration(15) * time.Second)

	// 	// 	coms := []string{Defaultcom, "about"}
	// 	// 	commsg, err := translation.GetComponentsMessages("es", coms)
	// 	// 	fmt.Print(err)
	// 	// 	So(len(commsg), ShouldEqual, 2)

	// 	// 	commsg1 := commsg[Defaultcom]
	// 	// 	commsg2 := commsg["about"]

	// 	// 	value1, _ := commsg1.Get(commonkey)
	// 	// 	value2, _ := commsg2.Get("about.title")

	// 	// 	So(commsg1.Size(), ShouldEqual, 5)
	// 	// 	So(commsg2.Size(), ShouldEqual, 3)
	// 	// 	So(value1, ShouldEqual, "La traducción está lista para este componente.")
	// 	// 	So(value2, ShouldEqual, "es_About")

	// 	// })

	// })
}
