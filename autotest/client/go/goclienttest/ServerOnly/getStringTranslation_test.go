/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package ServerOnly

import (
	"fmt"
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestGetStringTranslation(t *testing.T) {

	cfPath := "configServerOnly.yaml"
	cfg, _ := sgtn.NewConfig(cfPath)
	inst, _ := sgtn.NewInst(*cfg)
	fmt.Print(inst)
	translation := inst.GetTranslation()

	Convey("Service-requestlocale: Get request locale's translation from service", t, func() {

		Convey("Service-requestlocale: Get common strings successfully with en(P0)", func() {

			tran1, _ := translation.GetStringMessage("en", Defaultcom, commonkey)
			// fmt.Print(tran1)

			So(tran1, ShouldEqual, commonvalue)
			// So(cfg, ShouldNotBeNil)
		})

		Convey("Service-requestlocale: Get placehodler strings successfully with fr(P0)", func() {

			tran2, _ := translation.GetStringMessage("fr", Defaultcom, holderkey, "a", "b")
			// fmt.Print(tran2)

			So(tran2, ShouldEqual, "L'opérateur 'a' n'est pas pris en charge pour la propriété 'b'.")
		})

		Convey("Service-requestlocale: Get long and html tag strings successfully with fr(P0)", func() {

			tran2, _ := translation.GetStringMessage("fr", Defaultcom, htmlkey)
			// should add check if send out http request as the previous case has cached fr component translation

			So(tran2, ShouldEqual, frhtmlvalue)
		})

		SkipConvey("Service-requestlocale: non-existing key, return key(P1)", func() {

			tran2, _ := translation.GetStringMessage("fr", Defaultcom, "non-existing.key")
			So(tran2, ShouldEqual, "non-existing.key")
		})
	})

	Convey("Service-defaultlocale: Get default locale's translation from service", t, func() {

		// cfPath := "testdata/Config/config.yaml"
		// cfg, _ := sgtn.NewConfig(cfPath)
		// inst, _ := sgtn.NewInst(*cfg)
		// fmt.Print(inst)
		// translation := inst.GetTranslation()

		Convey("Service-defaultlocale: request unsupport locale(ru) in service, get default locale translation from service(P0)", func() {

			tran1, _ := translation.GetStringMessage("ru", Defaultcom, commonkey)

			//should return default locale translation as expect service to return nothing with 404. But now, service return en.
			//So(tran1, ShouldEqual, dlcncommonvalue)
			So(tran1, ShouldEqual, dlcncommonvalue)
		})

		SkipConvey("Service-defaultlocale: request empty locale() and key is not existing, get key returned(P1)", func() {

			tran2, _ := translation.GetStringMessage("", Defaultcom, "non-existing.key")
			So(tran2, ShouldEqual, "non-existing.key")
		})

		SkipConvey("Service-defaultlocale: request non-existing component in service, get key returned(P1)", func() {

			tran2, _ := translation.GetStringMessage("fr", "NonExistingComponent", commonkey)
			So(tran2, ShouldEqual, commonkey)
		})
	})

}
