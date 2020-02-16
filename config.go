/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"io/ioutil"

	yaml "gopkg.in/yaml.v2"
)

// Config Singleton configuration
type Config struct {
	Name    string
	Version string

	SingletonServer string `yaml:"singletonServer"`

	EnableCache     bool `yaml:"enableCache"`
	InitializeCache bool `yaml:"initializeCache"`

	CacheExpiredTime int64 `yaml:"cacheExpiredTime"` //seconds

	DefaultLocale string `yaml:"defaultLocale"`

	LocalBundles string `yaml:"localBundles"`
}

// NewConfig Create a new Singleton configuration instance
func NewConfig(path string) (*Config, error) {
	contents, err := ioutil.ReadFile(path)
	if err != nil {
		return nil, err
	}

	var cfg Config
	err = yaml.Unmarshal(contents, &cfg)
	if err != nil {
		return nil, err
	}

	logger.Debug(fmt.Sprintf("Created a new config: %#v", cfg))

	return &cfg, nil
}
