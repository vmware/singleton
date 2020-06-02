/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package main

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"
	sgtn "github.com/vmware/singleton"
)

func TestCreatAndGetInstance(t *testing.T) {
	Convey("Available Instance creation and get", t, func() {

		Convey("Config is available, instance can be created and got(P0)", func() {
			cfPath := "testdata/Config/config.json"
			testcfg, _ := sgtn.LoadConfig(cfPath)
			sgtn.Initialize(testcfg)
			translation := sgtn.GetTranslation()
			fmt.Println(translation)

			// gotinst, ok := sgtn.GetInst(testcfg.Name)

			// So(loaded, ShouldBeFalse)
			// So(inst, ShouldNotBeNil)
			// So(ok, ShouldBeTrue)
			// So(gotinst, ShouldEqual, inst)

		})

		// 	SkipConvey("Config is available, first instance is returned and got(P1)", func() {
		// 		cfPath := "testdata/Config/config.yaml"
		// 		testcfg, _ := sgtn.NewConfig(cfPath)
		// 		inst, loaded := sgtn.NewInst(*testcfg)
		// 		gotinst, ok := sgtn.GetInst(testcfg.Name)

		// 		So(loaded, ShouldBeTrue)
		// 		So(inst, ShouldNotBeNil)
		// 		So(ok, ShouldBeTrue)
		// 		So(gotinst, ShouldEqual, inst)
		// 	})

		// 	Convey("Config is available, new second instance is created and got(P0)", func() {
		// 		seccfPath := "testdata/Config/configFalse.yaml"
		// 		sectestcfg, _ := sgtn.NewConfig(seccfPath)
		// 		secinst, secloaded := sgtn.NewInst(*sectestcfg)
		// 		secgotinst, secok := sgtn.GetInst(sectestcfg.Name)

		// 		// fmt.Println(*secinst)
		// 		// fmt.Println(&secinst)
		// 		So(secloaded, ShouldBeFalse)
		// 		So(secinst, ShouldNotBeNil)

		// 		So(secok, ShouldBeTrue)
		// 		So(secgotinst, ShouldEqual, secinst)
		// 	})
		// })

		// Convey("Inavailable Instance creation and can't be got", t, func() {
		// 	//cfPath := "testdata/Config/config.yaml"
		// 	testcfg, _ := sgtn.NewConfig("Non-existing config")
		// 	testinst, _ := sgtn.NewInst(*testcfg)
		// 	// gotinst, ok := sgtn.GetInst(testcfg.Name)

		// 	So(testcfg, ShouldBeNil)
		// 	So(testinst, ShouldBeNil)
		// 	// So(ok, ShouldBeTrue)
		// 	// So(gotinst, ShouldEqual, inst)
		// })

		// SkipConvey("Config with empty product name is available, instance can be created and got(P2)", t, func() {
		// 	cfPath := "testdata/Config/configProductEmpty.yaml"
		// 	testcfg, _ := sgtn.NewConfig(cfPath)
		// 	testinst, _ := sgtn.NewInst(*testcfg)
		// 	gotinst, ok := sgtn.GetInst(testcfg.Name)

		// 	// So(loaded, ShouldBeFalse)
		// 	So(testinst, ShouldNotBeNil)
		// 	So(ok, ShouldBeTrue)
		// 	So(gotinst, ShouldEqual, testinst)
		// })

		// SkipConvey("Get non-existing instance, instance can't be got(P2)", t, func() {
		// 	//cfPath := "testdata/Config/configProductEmpty.yaml"
		// 	//testcfg, _ := sgtn.NewConfig(cfPath)
		// 	//testinst, _ := sgtn.NewInst(*testcfg)
		// 	gotinst, ok := sgtn.GetInst("non-existinginstance")

		// 	So(ok, ShouldBeFalse)
		// 	So(gotinst, ShouldBeNil)
	})

}
