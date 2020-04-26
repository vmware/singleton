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

func TestAvailableConfig(t *testing.T) {
	Convey("Available config", t, func() {

		Convey("boolean are true, config can be created(P0)", func() {
			cfPath := "testdata/Config/configTrue.yaml"
			cfg, err := sgtn.NewConfig(cfPath)
			So(err, ShouldBeNil)
			So(cfg, ShouldNotBeNil)
		})

		Convey("boolean are false, config can be created(P0)", func() {
			cfPath := "testdata/Config/configFalse.yaml"
			cfg, err := sgtn.NewConfig(cfPath)
			So(err, ShouldBeNil)
			So(cfg, ShouldNotBeNil)
		})
	})

	SkipConvey("singlton server info is empty, config can be created(P1)", t, func() {
		cfPath := "testdata/Config/configServerEmpty.yaml"
		cfg, err := sgtn.NewConfig(cfPath)
		So(err, ShouldBeNil)
		So(cfg, ShouldNotBeNil)
	})

	SkipConvey("enable cache with invail value(12ab), config can't be created(P1)", t, func() {
		// message := "strconv.ParseBool: parsing \"aaaa\": invalid syntax"
		cfPath := "testdata/Config/configInvalidCacheValue.yaml"
		cfg, err := sgtn.NewConfig(cfPath)
		So(err, ShouldNotBeNil)
		So(cfg, ShouldBeNil)
		fmt.Printf(err.Error())

	})

	SkipConvey("initialize cache with invail value(12ab), config can't be created(P1)", t, func() {
		// message := "strconv.ParseBool: parsing \"aaaa\": invalid syntax"
		cfPath := "testdata/Config/configInvalidCacheValue.yaml"
		cfg, err := sgtn.NewConfig(cfPath)
		So(err, ShouldNotBeNil)
		So(cfg, ShouldBeNil)
		fmt.Printf(err.Error())

	})

	SkipConvey("cache expired time with invail value(12ab), config can't be created(P1)", t, func() {
		// message := "strconv.ParseBool: parsing \"aaaa\": invalid syntax"
		cfPath := "testdata/Config/configInvaildCacheExpiredTime.yaml"
		cfg, err := sgtn.NewConfig(cfPath)
		So(err, ShouldNotBeNil)
		So(cfg, ShouldBeNil)
		fmt.Printf(err.Error())

	})

	SkipConvey("default locale test", t, func() {
		Convey("default locale is stringlist, can't create config", func() {
			cfPath := "testdata/Config/configDefaultLocaleList.yaml"
			cfg, err := sgtn.NewConfig(cfPath)
			So(err, ShouldNotBeNil)
			So(cfg, ShouldBeNil)
		})

		Convey("default locale is empty, can create config", func() {
			cfPath := "testdata/Config/configDefaultLocaleEmpty.yaml"
			cfg, err := sgtn.NewConfig(cfPath)
			So(err, ShouldBeNil)
			So(cfg, ShouldNotBeNil)
		})

	})

	SkipConvey("local bundle test", t, func() {
		Convey("local bundle path with non-ASCII character, can create config", func() {
			cfPath := "testdata/Config/configLocalBundlePathNonASCII.yaml"
			cfg, err := sgtn.NewConfig(cfPath)
			So(err, ShouldBeNil)
			So(cfg, ShouldNotBeNil)
		})

		Convey("local bundle path is empty, can create config", func() {
			cfPath := "testdata/Config/configLocalBundlePathEmpty.yaml"
			cfg, err := sgtn.NewConfig(cfPath)
			So(err, ShouldBeNil)
			So(cfg, ShouldNotBeNil)
		})

		Convey("Server and local bundle path is empty, can create config", func() {
			cfPath := "testdata/Config/configServerAndLocalBundlePathEmpty.yaml"
			cfg, err := sgtn.NewConfig(cfPath)
			So(err, ShouldBeNil)
			So(cfg, ShouldNotBeNil)
		})

	})

	SkipConvey("version with invail value([1.2]), config can't be created(P1)", t, func() {

		cfPath := "testdata/Config/configInvalidVersionValue.yaml"
		cfg, err := sgtn.NewConfig(cfPath)
		So(err, ShouldNotBeNil)
		So(cfg, ShouldBeNil)
		//fmt.Printf(err.Error())

	})

	SkipConvey("product name is empty, config can be created(P1)", t, func() {
		cfPath := "testdata/Config/configProductEmpty.yaml"
		cfg, err := sgtn.NewConfig(cfPath)
		So(err, ShouldBeNil)
		So(cfg, ShouldNotBeNil)
		//fmt.Printf(err.Error())

	})

	SkipConvey("No yaml file, config can't be created(P1)", t, func() {
		cfPath := "testdata/Config/NotExisted.yaml"
		cfg, err := sgtn.NewConfig(cfPath)
		So(err, ShouldNotBeNil)
		So(cfg, ShouldBeNil)
		fmt.Printf(err.Error())

	})

	SkipConvey("No product property in yaml file, config can't be created(P1)", t, func() {
		cfPath := "testdata/Config/configNoProductProperty.yaml"
		cfg, err := sgtn.NewConfig(cfPath)
		So(err, ShouldBeNil)
		So(cfg, ShouldNotBeNil)
		//fmt.Printf(err.Error())

	})

	//Convey("InAvailable config", t, func() {

	// errmsgs := [...]string{
	// 	1: "Type mismatch: expected bool, float64, int or string; got []interface {}",
	// 	2: "yaml: control characters are not allowed",
	// 	3: "Type mismatch: expected bool or string; got []interface {}",
	// 	4: "strconv.ParseBool: parsing \"aaaa\": invalid syntax",
	// 	5: "time: invalid duration abcds",
	// 	6: "strconv.ParseInt: parsing \"abcd\": invalid syntax",
	// 	7: "Type mismatch: expected float64, int or string; got []interface {}",
	// }
	// data := []struct {
	// 	old, new, errmsg string
	// }{
	// 	{"productName: .*", "productName: [1,2]", errmsgs[1]},
	// 	{"version: .*", "version: [1,2]", errmsgs[1]},
	// 	{"singletonServer: .*", "singletonServer: [1,2]", errmsgs[1]},
	// 	{"singletonServer: .*", "singletonServer: " + string(31), errmsgs[2]},
	// 	{"enableCache: .*", "enableCache: [1,2]", errmsgs[3]},
	// 	{"enableCache: .*", "enableCache: aaaa", errmsgs[4]},
	// 	{"initializeCache: .*", "initializeCache: [1,2]", errmsgs[3]},
	// 	{"cacheExpiredTime: .*", "cacheExpiredTime: [1,2]", errmsgs[7]},
	// 	{"cacheExpiredTime: .*", "cacheExpiredTime: abcd", errmsgs[6]},
	// 	{"defaultLocale: .*", "defaultLocale: [1,2]", errmsgs[1]},
	// 	{"localBundles: .*", "localBundles: [1,2]", errmsgs[1]},
	// }

	// var fileBytes []byte
	// //var errA error
	// var cfPathA string
	// cfPathA = "testdata/Config/config.yaml"
	// fileBytes, _ = ioutil.ReadFile(cfPathA)

	// for _, entry := range data {
	// 	//sgtn.Logger.Debug(fmt.Sprintf("test data: %#v", entry))
	// 	re := regexp.MustCompile(entry.old)
	// 	newCfgString := re.ReplaceAllLiteral(fileBytes, []byte(entry.new))
	// 	cfgA, errA := sgtn.NewConfig(string(newCfgString))
	// 	So(cfgA, ShouldBeNil)
	// 	So(errA, ShouldBeNil)
	// 	So(errA.Error(), ShouldContain, entry.errmsg)
	// 	//assert.Contains(suite.T(), err.Error(), entry.errmsg)
	// }

	// Convey("singlton server info is empty, config can be created", func() {
	// 	cfPath := "testdata/Config/config.yaml"
	// 	cfg, err := sgtn.NewConfig(cfPath)
	// 	So(err, ShouldBeNil)
	// 	So(cfg, ShouldNotBeNil)
	// })

	// Convey("boolean are false", func() {
	// 	cfPath := "testdata/Config/configfalse.yaml"
	// 	cfg, err := sgtn.NewConfig(cfPath)
	// 	So(err, ShouldBeNil)
	// 	So(cfg, ShouldNotBeNil)
	// })
	//})
}
