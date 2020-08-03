/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"io/ioutil"

	jsoniter "github.com/json-iterator/go"
)

// Config Singleton configuration
type Config struct {
	ServerURL     string `json:"online_service_url"`
	LocalBundles  string `json:"offline_resources_base_url"`
	DefaultLocale string `json:"default_locale"`
}

// LoadConfig Create a new Singleton configuration instance
func LoadConfig(path string) (*Config, error) {
	contents, err := ioutil.ReadFile(path)
	if err != nil {
		return nil, err
	}

	var cfg Config
	if err := jsoniter.Unmarshal(contents, &cfg); err != nil {
		return nil, err
	}

	logger.Debug(fmt.Sprintf("Created a new config: %#v", cfg))

	return &cfg, nil
}
