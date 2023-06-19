/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"flag"
	"io"
	"math/rand"
	"net/http"
	"os"
	"path/filepath"
	"reflect"
	"strings"
	"testing"

	"sgtnserver/api"
	v2 "sgtnserver/api/v2"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"
	"sgtnserver/modules/translation"

	"github.com/gavv/httpexpect/v2"
	"github.com/gin-gonic/gin"
	jsoniter "github.com/json-iterator/go"
	"github.com/jucardi/go-osx/paths"
	"github.com/mackerelio/go-osstat/memory"
)

var (
	json          = jsoniter.ConfigDefault
	log           = logger.Log.Sugar()
	logFolder     = filepath.Dir(config.Settings.LOG.Filename) + string(os.PathSeparator)
	GinTestEngine *gin.Engine
)

const BaseURL = v2.APIRoot
const (
	GetBundleURL              = BaseURL + "/translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}"
	GetBundlesURL             = BaseURL + "/translation/products/{productName}/versions/{version}"
	PutBundlesURL             = BaseURL + "/translation/products/{productName}/versions/{version}"
	GetSupportedComponentsURL = BaseURL + "/translation/products/{productName}/versions/{version}/componentlist"
	GetSupportedLocalesURL    = BaseURL + "/translation/products/{productName}/versions/{version}/localelist"
	GetKeyURL                 = BaseURL + "/translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}/keys/{key}"
	GetKeysURL                = BaseURL + "/translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}/keys"
	GetRegionsOfLanguagesURL  = BaseURL + "/locale/regionList"
)
const (
	GetCombinedURL              = BaseURL + "/combination/translationsAndPattern"
	GetCombinedByPostURL        = GetCombinedURL
	GetSupportedLanguageListURL = BaseURL + "/locale/supportedLanguageList"
)
const (
	GetPatternByLangRegURL = BaseURL + "/formatting/patterns"
	GetPatternByLocaleURL  = BaseURL + "/formatting/patterns/locales/{locale}"
)

const (
	Name, Version, Locale, Component = "VPE", "1.0.0", "en", "sunglow"
	Language, Region                 = "en", "US"
	Key, Msg                         = "message", "Message-en"
)

var ID = translation.BundleID{Name: Name, Version: Version, Locale: Locale, Component: Component}

func TestMain(m *testing.M) {
	defer logger.Log.Sync()

	testArgs := os.Args[:1]
	for _, arg := range os.Args {
		if strings.HasPrefix(arg, "-test.") {
			testArgs = append(testArgs, arg)
		}
	}
	os.Args = testArgs
	log.Infof("CLI args are: %v", os.Args)

	flag.Parse()

	GinTestEngine = api.InitServer()

	m.Run()
}

func init() {
	// Rid of debug output
	gin.SetMode(gin.TestMode)

	printOSInfo()
	cwd, _ := os.Getwd()
	log.Infof("Current directory is: %s", cwd)

	wd := os.Getenv("WD")
	if len(wd) != 0 {
		log.Infof("WD environment variable is: %s", wd)
		os.Chdir(wd)
	} else {
		testDataPath := "testdata"
		if ok, err := paths.Exists(testDataPath); err != nil || !ok {
			logger.SLog.Debug("Project root isn't set. Please set WD environment variable to project root.\nTrying to find project root by testdata directory...")
			FindProjectRoot(testDataPath)
			cwd, _ = os.Getwd()
			log.Infof("Now current directory is: %s", cwd)
		}
	}
}

func CreateHTTPExpect(t *testing.T, ginEngine *gin.Engine) *httpexpect.Expect {
	// Create httpexpect instance
	return httpexpect.WithConfig(httpexpect.Config{
		Client: &http.Client{
			// Transport: httpexpect.NewBinder(etag.Handler(ginTestEngine, false)),
			Transport: httpexpect.NewBinder(ginEngine),
			Jar:       httpexpect.NewJar(),
		},
		Reporter: httpexpect.NewAssertReporter(t),
		Printers: []httpexpect.Printer{
			httpexpect.NewDebugPrinter(t, true),
		},
	})
}

func GetErrorAndData(r string) (bError *api.BusinessError, data interface{}) {
	body := new(api.Response)
	err := json.UnmarshalFromString(r, body)
	if err != nil {
		log.Error(err.Error())
	}
	data = body.Data
	bError = body.Error
	return
}

// Returns an int >= min, < max
func RandomInt(min, max int) int {
	return min + rand.Intn(max-min)
}

// Generate a random string of a-z chars with len
func RandomString(len int) string {
	bytes := make([]byte, len)
	for i := 0; i < len; i++ {
		bytes[i] = byte(RandomInt(97, 122))
	}
	return string(bytes)
}

func FindProjectRoot(testDataPath string) {
	currentRoot, _ := os.Getwd()
	for {
		ok, err := paths.Exists(filepath.Join(currentRoot, testDataPath))
		if err == nil && ok {
			logger.SLog.Infof("Found project root: %s", currentRoot)
			os.Chdir(currentRoot)
			break
		}

		newRoot := filepath.Join(currentRoot, "..")
		if newRoot == currentRoot {
			logger.SLog.Debug("Failed to find project root")
			break
		}
		currentRoot = newRoot
	}
}

// Contain judge if a slice contains an element
func Contain(list interface{}, target interface{}) int {
	listKind := reflect.TypeOf(list).Kind()
	if listKind == reflect.Slice || listKind == reflect.Array {
		listValue := reflect.ValueOf(list)
		for i := 0; i < listValue.Len(); i++ {
			// XXX - panics if slice element points to an unexported struct field
			// see https://golang.org/pkg/reflect/#Value.Interface
			if target == listValue.Index(i).Interface() {
				return i
			}
		}
	}
	return -1
}

// Replace stdout and stderr
func ReplaceStds() (reader, writer *os.File, revert func()) {
	rescueStdout, rescueStderr := os.Stdout, os.Stderr
	reader, writer, _ = os.Pipe()
	os.Stdout, os.Stderr = writer, writer

	revert = func() {
		os.Stdout, os.Stderr = rescueStdout, rescueStderr
	}
	return
}

func ReplaceLogger(tempLogFile string) func() {
	os.Remove(tempLogFile)
	oldLogFile := config.Settings.LOG.Filename

	config.Settings.LOG.Filename = tempLogFile
	logger.InitLogger()

	return func() {
		config.Settings.LOG.Filename = oldLogFile
		logger.InitLogger()
		os.Remove(tempLogFile)
	}
}

func printOSInfo() {
	memory, err := memory.Get()
	if err != nil {
		log.Errorf("%s", err)
		return
	}

	var oneM uint64 = 1024 * 1024
	log.Infof("memory total: %d MB", memory.Total/oneM)
	log.Infof("memory used: %d MB", memory.Used/oneM)
	log.Infof("memory cached: %d MB", memory.Cached/oneM)
	log.Infof("memory free: %d MB", memory.Free/oneM)
}

func capture() func() (string, error) {
	r, w, err := os.Pipe()
	if err != nil {
		panic(err)
	}

	done := make(chan error, 1)

	save := os.Stdout
	os.Stdout = w

	var buf strings.Builder

	go func() {
		_, err := io.Copy(&buf, r)
		r.Close()
		done <- err
	}()

	return func() (string, error) {
		os.Stdout = save
		w.Close()
		err := <-done
		return buf.String(), err
	}
}
