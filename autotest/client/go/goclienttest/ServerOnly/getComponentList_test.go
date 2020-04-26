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

func TestComponentList(t *testing.T) {
	Convey("Get component list from service", t, func() {

		cfPath := "configServerOnly.yaml"
		cfg, _ := sgtn.NewConfig(cfPath)
		inst, _ := sgtn.NewInst(*cfg)
		fmt.Print(inst)
		translation := inst.GetTranslation()

		Convey("Get all components successfully(P0)", func() {

			comlist, err := translation.GetComponentList()
			fmt.Print(comlist)
			fmt.Print(err)

			So(len(comlist), ShouldEqual, 4)
			// So(cfg, ShouldNotBeNil)
		})

	})

}
