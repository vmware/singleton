/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package config

import (
	"flag"
	"io/ioutil"
	"log"
	"os"
	"time"

	"github.com/jaffee/commandeer"
	"github.com/spf13/pflag"
	"go.uber.org/zap/zapcore"
	"gopkg.in/yaml.v3"
)

func InitConfig(filePath string) {
	log.Printf("Initialize configuration with '%s'\n", filePath)

	contents, err := ioutil.ReadFile(filePath)
	if err != nil {
		log.Fatalf("Fail to read '%s': %+v", filePath, err)
	}

	err = yaml.Unmarshal(contents, &Settings)
	if err != nil {
		log.Fatalf("Fail to parse '%s': %+v", filePath, err)
	}

	Settings.Cache.Enable = true

	err = pflag.CommandLine.Parse(os.Args[1:])
	if err != nil {
		log.Fatalf("Fail to parse arguments from CLI: %+v", err)
	}
}

func init() {
	var cfPath = flag.String("config", "config/config.yaml", "the config file")

	pflag.CommandLine.AddGoFlagSet(flag.CommandLine)

	err := commandeer.Flags(pflag.CommandLine, &Settings)
	if err != nil {
		log.Fatalf("calling Flags: %v", err)
	}

	err = pflag.CommandLine.Parse(os.Args[1:])
	if err != nil {
		log.Fatalf("Fail to parse arguments from CLI: %+v", err)
	}

	InitConfig(*cfPath)
}

type Config struct {
	SwaggerUI bool `yaml:"SwaggerUI"`

	BundleLocation string `yaml:"BundleLocation"`

	LocalBundle struct {
		BasePath string `yaml:"BasePath"`
	} `yaml:"LocalBundle"`

	S3Bundle struct {
		PublicKeyFile   string `yaml:"PublicKeyFile"`
		AccessKey       string `yaml:"AccessKey"`
		SecretKey       string `yaml:"SecretKey"`
		RoleArn         string `yaml:"RoleArn"`
		SessionDuration int32  `yaml:"SessionDuration"`
		Region          string `yaml:"Region"`
		BucketName      string `yaml:"BucketName"`
		BundleRoot      string `yaml:"BundleRoot"`
	} `yaml:"S3Bundle"`

	RefreshBundleInterval time.Duration `yaml:"RefreshBundleInterval"`
	HeaderOfTraceID       string        `yaml:"HeaderOfTraceID"`

	Server struct {
		RunMode              string        `yaml:"RunMode"`
		Schema               string        `yaml:"Schema"`
		HTTPPort             int           `yaml:"HTTPPort"`
		HTTPSPort            int           `yaml:"HTTPSPort"`
		ReadTimeout          time.Duration `yaml:"ReadTimeout"`
		WriteTimeout         time.Duration `yaml:"WriteTimeout"`
		CertFile             string        `yaml:"CertFile"`
		KeyFile              string        `yaml:"KeyFile"`
		MaxHeaderBytes       int           `yaml:"MaxHeaderBytes"`
		CacheControl         string        `yaml:"CacheControl"`
		CompressionAlgorithm string        `yaml:"CompressionAlgorithm"`
	} `yaml:"Server"`

	LOG struct {
		// Filename is the file to write logs to.  Backup log files will be retained
		// in the same directory.  It uses <processname>-lumberjack.log in
		// os.TempDir() if empty.
		Filename string `json:"Filename" yaml:"Filename"`

		// MaxSize is the maximum size in megabytes of the log file before it gets
		// rotated. It defaults to 100 megabytes.
		MaxSize int `json:"MaxSize" yaml:"MaxSize"`

		// MaxAge is the maximum number of days to retain old log files based on the
		// timestamp encoded in their filename.  Note that a day is defined as 24
		// hours and may not exactly correspond to calendar days due to daylight
		// savings, leap seconds, etc. The default is not to remove old log files
		// based on age.
		MaxAge int `json:"MaxAge" yaml:"MaxAge"`

		// MaxBackups is the maximum number of old log files to retain.  The default
		// is to retain all old log files (though MaxAge may still cause them to get
		// deleted.)
		MaxBackups int `json:"MaxBackups" yaml:"MaxBackups"`

		Level zapcore.Level `json:"Level" yaml:"Level"`

		ConsoleLevel zapcore.Level `json:"ConsoleLevel" yaml:"ConsoleLevel"`
	} `yaml:"LOG"`

	Cache struct {
		Enable      bool          //`json:"Enable" yaml:"Enable"`
		MaxEntities int64         `json:"MaxEntities" yaml:"MaxEntities"`
		Expiration  time.Duration `json:"Expiration" yaml:"Expiration"`
	} `json:"Cache" yaml:"Cache"`

	CrossDomain struct {
		Enable           bool          `json:"Enable" yaml:"Enable"`
		AllowCredentials bool          `json:"AllowCredentials" yaml:"AllowCredentials"`
		AllowOrigin      string        `json:"AllowOrigin" yaml:"AllowOrigin"`
		AllowMethods     string        `json:"AllowMethods" yaml:"AllowMethods"`
		AllowHeaders     string        `json:"AllowHeaders" yaml:"AllowHeaders"`
		MaxAge           time.Duration `json:"MaxAge" yaml:"MaxAge"`
	} `json:"CrossDomain" yaml:"CrossDomain"`

	AllowListFile string `json:"AllowListFile" yaml:"AllowListFile"`
}

var Settings Config
