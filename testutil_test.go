/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"bytes"
	"flag"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"os"
	"path/filepath"
	"reflect"
	"runtime"
	"strconv"
	"strings"
	"sync/atomic"
	"testing"
	"time"

	json "github.com/json-iterator/go"
	"github.com/pkg/errors"
	"github.com/rs/zerolog"
	"github.com/rs/zerolog/log"
	"gopkg.in/h2non/gock.v1"
)

var name, version, component = "SgtnTest", "1.0.0", "sunglow"
var locale, localeDefault, localeSource, localeUnsupported = "zh-Hans", "fr", localeEn, "xxx"
var OldZhValue = "消息"
var nonexistentComponent = "comp-notexist"

var enComponentID = dataItemID{iType: itemComponent, Name: name, Version: version, Locale: localeEn, Component: component}
var componentID = dataItemID{iType: itemComponent, Name: name, Version: version, Locale: locale, Component: component}
var componentsID = dataItemID{iType: itemComponents, Name: name, Version: version}
var localesID = dataItemID{iType: itemLocales, Name: name, Version: version}

var ServerURL = "https://SingletonServer:8090"
var testCfg Config
var mockData map[string]MockMapping

var loglevel = flag.Int("loglevel", 0, "sets log level to 0(debug), 1(info)...")

func init() {
	newLogger := log.Output(zerolog.ConsoleWriter{Out: os.Stderr})
	SetLogger(&defaultLogger{newLogger})
}

func TestMain(m *testing.M) {
	if !flag.Parsed() {
		flag.Parse()
	}
	zerolog.SetGlobalLevel(zerolog.Level(*loglevel))

	cfg, err := LoadConfig("testdata/conf/config.json")
	if err != nil {
		panic(err)
	}
	testCfg = *cfg

	mockData = ReadMockJSONs("testdata/mock/mappings")

	m.Run()
	os.Exit(0)
}

func PrintRespBody(url string, resp *http.Response) io.Reader {
	fmt.Printf("The url is:%s\n", url)
	fmt.Printf("The response from server is:\n %#v\n", resp)
	bodyBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println(err.Error())
		return resp.Body
	}

	bodyString := string(bodyBytes)
	fmt.Printf("The response body from server is:\n %#v\n", bodyString)

	return bytes.NewReader(bodyBytes)
}

func Trace(msg string) func() {
	start := time.Now()
	logger.Debug(fmt.Sprintf("---Enter %s", msg))
	return func() {
		logger.Debug(fmt.Sprintf("---Exit  %s (%s)", msg, time.Since(start)))
	}
}

//!+Display

func Display(name string, x interface{}) {
	fmt.Printf("Display %s (%T):\n", name, x)
	display(name, reflect.ValueOf(x))
}

//!-Display

// formatAtom formats a value without inspecting its internal structure.
// It is a copy of the the function in gopl.io/ch11/format.
func formatAtom(v reflect.Value) string {
	switch v.Kind() {
	case reflect.Invalid:
		return "invalid"
	case reflect.Int, reflect.Int8, reflect.Int16,
		reflect.Int32, reflect.Int64:
		return strconv.FormatInt(v.Int(), 10)
	case reflect.Uint, reflect.Uint8, reflect.Uint16,
		reflect.Uint32, reflect.Uint64, reflect.Uintptr:
		return strconv.FormatUint(v.Uint(), 10)
	// ...floating-point and complex cases omitted for brevity...
	case reflect.Bool:
		if v.Bool() {
			return "true"
		}
		return "false"
	case reflect.String:
		return strconv.Quote(v.String())
	case reflect.Chan, reflect.Func, reflect.Ptr,
		reflect.Slice, reflect.Map:
		return v.Type().String() + " 0x" +
			strconv.FormatUint(uint64(v.Pointer()), 16)
	default: // reflect.Array, reflect.Struct, reflect.Interface
		return v.Type().String() + " value"
	}
}

//!+display
func display(path string, v reflect.Value) {
	fmt.Printf("\tkind: %s\n", v.Kind())
	switch v.Kind() {
	case reflect.Invalid:
		fmt.Printf("%s = invalid\n", path)
	case reflect.Slice, reflect.Array:
		for i := 0; i < v.Len(); i++ {
			display(fmt.Sprintf("%s[%d]", path, i), v.Index(i))
		}
	case reflect.Struct:
		for i := 0; i < v.NumField(); i++ {
			fieldPath := fmt.Sprintf("%s.%s", path, v.Type().Field(i).Name)
			display(fieldPath, v.Field(i))
		}
	case reflect.Map:
		for _, key := range v.MapKeys() {
			display(fmt.Sprintf("%s[%s]", path,
				formatAtom(key)), v.MapIndex(key))
		}
	case reflect.Ptr:
		if v.IsNil() {
			fmt.Printf("%s = nil\n", path)
		} else {
			display(fmt.Sprintf("(*%s)", path), v.Elem())
		}
	case reflect.Interface:
		if v.IsNil() {
			fmt.Printf("%s = nil\n", path)
		} else {
			fmt.Printf("%s.type = %s\n", path, v.Elem().Type())
			display(path+".value", v.Elem())
		}
	default: // basic types, channels, funcs
		fmt.Printf("%s = %s\n", path, formatAtom(v))
	}
}

//!-display

func ReadMockJSONs(rootpath string) map[string]MockMapping {
	results := map[string]MockMapping{}

	wf := func(path string, info os.FileInfo, _ error) error {
		if info.IsDir() {
			return nil
		}

		bs, err := ioutil.ReadFile(path)
		if err != nil {
			return err
		}

		result := MockMappings{}
		err = json.Unmarshal(bs, &result)
		if err != nil {
			return errors.Errorf("Error when reading %s. Error: %s", info.Name(), err.Error())
		}

		for _, v := range result.Mappings {
			results[v.ID] = v
		}

		return nil
	}

	err := filepath.Walk(rootpath, wf)

	if err != nil {
		fmt.Printf("error reading mock data! %q: %v\n", rootpath, err)
	}

	return results
}

type TimesMock struct {
	name string
	time int
}

type MockMappings struct {
	Mappings []MockMapping `json:"mappings"`
}

type MockMapping struct {
	ID string `json:"id"`

	Request struct {
		URL     string            `json:"url"`
		Method  string            `json:"method"`
		Headers map[string]string `json:"headers"`
		Params  map[string]string `json:"params"`
		Times   int               `json:"times"`
	} `json:"request"`

	Response struct {
		Status       int               `json:"status"`
		Headers      map[string]string `json:"headers"`
		BodyFileName string            `json:"bodyFileName"`
	} `json:"response"`

	Headers struct {
	} `json:"headers"`
}

func EnableTimesMock(mocks []TimesMock) {
	for _, m := range mocks {
		EnableMockDataWithTimes(m.name, m.time)
	}
}
func EnableMultipleMockData(keys []string) (req *gock.Request) {
	for _, key := range keys {
		req = EnableMockDataWithTimes(key, 1)
	}
	return
}
func EnableMultipleMockDataWithTimes(keys []string, times int) (req *gock.Request) {
	for _, key := range keys {
		req = EnableMockDataWithTimes(key, times)
	}
	return
}

func EnableMockData(key string) *gock.Request {
	return EnableMockDataWithTimes(key, 1)
}

func EnableMockDataWithTimes(key string, times int) *gock.Request {
	logger.Debug(fmt.Sprintf("Enabling mock %s, times %d", key, times))
	data := mockData[key]

	req := gock.New(testCfg.ServerURL)
	switch data.Request.Method {
	case "GET":
		req.Get(data.Request.URL)
	}
	req.Times(times)
	for k, v := range data.Request.Headers {
		req.MatchHeader(k, v)
	}
	for k, v := range data.Request.Params {
		req.MatchParam(k, v)
	}
	resp := req.Reply(data.Response.Status)
	resp.SetHeaders(data.Response.Headers)
	if data.Response.BodyFileName != "" {
		resp.File("./testdata/mock/__files/" + data.Response.BodyFileName)
	}

	return req
}

func fileNotExist(filepath string) (bool, error) {
	_, err := os.Stat(filepath)
	if os.IsNotExist(err) {
		return true, nil
	}

	return false, err
}

func fileExist(filepath string) (bool, error) {
	if _, err := os.Stat(filepath); err == nil {
		return true, nil
	} else if os.IsNotExist(err) {
		return false, nil
	} else {
		// Schrodinger: file may or may not exist. See err for details.
		return false, err
	}
}

// This isn't thread safe because Go runs tests parallel possibly.
func clearCache() {
	logger.Debug("clearcache")
	cache = newCache()
}

func curFunName() string {
	pc := make([]uintptr, 15)
	n := runtime.Callers(2, pc)
	frames := runtime.CallersFrames(pc[:n])
	frame, _ := frames.Next()
	return frame.Function[strings.LastIndex(frame.Function, "/")+1:]
}

func resetInst(cfg *Config, f func()) {
	mapSource = registeredSource{make(map[releaseID](map[string]ComponentMsgs))}
	inst = &instance{}
	cache = newCache()
	if f != nil {
		f()
	}
	Initialize(cfg)
}

func expireCache(info *itemCacheInfo) {
	info.setTime(atomic.LoadInt64(&info.lastUpdate) - info.age)
}

func getCacheService() *cacheService {
	return GetTranslation().(*transMgr).Translation.(*transInst).msgOrigin.(*cacheService)
}

func getCacheInfo(id dataItemID) *itemCacheInfo {
	cachedItem, _ := cache.Get(id)
	return cachedItem.(*dataItem).attrs
}
