/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cachetest

import (
	"fmt"
	"testing"
	"time"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestExternalCacheManagement(t *testing.T) {
	Convey("Enable cache and initialize cache, and cache isn't expired", t, func() {

		// cfPath := "testdata/Config/configCacheInitalLocalBundles.yaml"
		cfPath := "configCacheInitalService.yaml"
		cfg, _ := sgtn.NewConfig(cfPath)
		inst, _ := sgtn.NewInst(*cfg)
		cs := NewCache()
		inst.RegisterCache(cs)
		inst.InitializeCache()

		fmt.Println(inst)
		translation := inst.GetTranslation()

		cscomlist := cs.GetComponents()
		fmt.Printf("cache component list: %s\n", cscomlist)

		Convey("Enable initialize cache: Get all components and locales translation(P0)", func() {

			comlist, _ := translation.GetComponentList()
			localelist, _ := translation.GetLocaleList()
			fmt.Print(comlist)
			fmt.Print(localelist)

			//expectedcomlist := []string{DefaultComponent, RESX, about, contact}
			So(len(comlist), ShouldEqual, 4)
			So(len(localelist), ShouldEqual, 9)

		})

		Convey("Enable initialize cache: Get string translation from cache(P0)", func() {

			tran1, _ := translation.GetStringMessage("en", Defaultcom, commonkey)
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, commonvalue)
			// So(cfg, ShouldNotBeNil)

		})

		Convey("Enable initialize cache: Get component translation from cache(P0)", func() {

			commsg, _ := translation.GetComponentMessages("es", Defaultcom)
			fmt.Print(commsg)
			value, _ := commsg.Get(commonkey)

			So(commsg.Size(), ShouldEqual, 5)
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

	SkipConvey("Enable cache and initialize cache, but cache is expired", t, func() {

		cfPath := "configCacheInitalServiceExpired.yaml"
		cfg, _ := sgtn.NewConfig(cfPath)
		inst, _ := sgtn.NewInst(*cfg)
		fmt.Print(inst)
		translation := inst.GetTranslation()
		// comlist, _ := translation.GetComponentList()
		// fmt.Print(comlist)
		// localelist, _ := translation.GetLocaleList()
		// fmt.Print(localelist)

		Convey("cache expired: Get string translation from cache(P0)", func() {

			time.Sleep(time.Duration(15) * time.Second)

			tran1, _ := translation.GetStringMessage("en", Defaultcom, commonkey)
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, commonvalue)
			// So(cfg, ShouldNotBeNil)

		})

		Convey("cache expired: Get component translation from cache(P0)", func() {

			time.Sleep(time.Duration(15) * time.Second)

			commsg, _ := translation.GetComponentMessages("es", Defaultcom)
			fmt.Print(commsg)
			value, _ := commsg.Get(commonkey)

			So(commsg.Size(), ShouldEqual, 5)
			So(value, ShouldEqual, "La traducción está lista para este componente.")

		})

		// Convey("cache expired: Get multiple components translation from cache(P0)", func() {

		// 	time.Sleep(time.Duration(15) * time.Second)

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

	SkipConvey("Disable cache and enable initialize cache", t, func() {

		cfPath := "configCacheDisable.yaml"
		cfg, _ := sgtn.NewConfig(cfPath)
		inst, _ := sgtn.NewInst(*cfg)
		fmt.Print(inst)
		translation := inst.GetTranslation()
		// comlist, _ := translation.GetComponentList()
		// fmt.Print(comlist)
		// localelist, _ := translation.GetLocaleList()
		// fmt.Print(localelist)

		Convey("Disable cache: Get string translation from cache(P0)", func() {

			//time.Sleep(time.Duration(15) * time.Second)

			tran1, _ := translation.GetStringMessage("en", Defaultcom, commonkey)
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, commonvalue)
			// So(cfg, ShouldNotBeNil)

		})

		Convey("Disable cache: Get component translation from cache(P0)", func() {

			//time.Sleep(time.Duration(15) * time.Second)

			commsg, _ := translation.GetComponentMessages("es", Defaultcom)
			fmt.Print(commsg)
			value, _ := commsg.Get(commonkey)

			So(commsg.Size(), ShouldEqual, 5)
			So(value, ShouldEqual, "La traducción está lista para este componente.")

		})

		// Convey("Disable cache: Get multiple components translation from cache(P0)", func() {

		// 	//time.Sleep(time.Duration(15) * time.Second)

		// 	coms := []string{Defaultcom, "about"}
		// 	commsg, err := translation.GetComponentsMessages("es", coms)
		// 	fmt.Print(err)
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
}
