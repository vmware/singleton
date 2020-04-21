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

func TestComponentList(t *testing.T) {
	Convey("Get all components successfully(P0)", t, func() {

		cfPath := "config.yaml"
		cfg, _ := sgtn.NewConfig(cfPath)
		inst, _ := sgtn.NewInst(*cfg)
		fmt.Print(inst)
		translation := inst.GetTranslation()

		comlist, err := translation.GetComponentList()
		fmt.Print(comlist)
		fmt.Print(err)

		So(len(comlist), ShouldEqual, 5)
		// So(cfg, ShouldNotBeNil)

	})

	SkipConvey("Get nothing when the localbundle path is incorrect(P1)", t, func() {

		cfPath := "confignull.yaml"
		cfg, _ := sgtn.NewConfig(cfPath)
		inst, _ := sgtn.NewInst(*cfg)
		fmt.Print(inst)
		translation := inst.GetTranslation()

		comlist1, err1 := translation.GetComponentList()
		fmt.Print(comlist1)
		fmt.Print(err1)

		So(len(comlist1), ShouldEqual, 0)
		// So(cfg, ShouldNotBeNil)

	})

}
