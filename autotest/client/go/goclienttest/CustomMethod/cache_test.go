package CustomMethod

import (
	"fmt"
	"testing"
	"github.com/davecgh/go-spew/spew"
	. "github.com/smartystreets/goconvey/convey"

	sgtn "github.com/vmware/singleton"
)

func TestGetStringTranslation(t *testing.T) {

	cfPath := "configServerOnly.json"	
	cs := NewCache()
	sgtn.RegisterCache(cs)
	cfg, _ := sgtn.LoadConfig(cfPath)
	sgtn.Initialize(cfg)
	cs.Set("aaa","bbb")
	
	translation := sgtn.GetTranslation()

	Convey("cache register test", t, func() {
		Convey("cache register test", func() {

			tran1, _ := translation.GetComponentMessages("GoClientTest", "1.0.0", "zh-Hans", "DefaultComponent")
			value1, _ := cs.Get("aaa")
			spew.Dump(cs)
			So(value1, ShouldEqual, "bbb")
			message1,_ := tran1.Get("message.argument")
			fmt.Print(message1)
			So(message1, ShouldEqual, "运算符'{1}'不支持属性'{0}'。")

		})	
	})
}





