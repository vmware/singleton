/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"io/ioutil"

	json "github.com/json-iterator/go"
)

// Config Singleton configuration
type Config struct {
	ServerURL     string `json:"online_service_url"`
	LocalBundles  string `json:"offline_resources_base_url"`
	localSource   string //`json:"source_base_url"`
	DefaultLocale string `json:"default_locale"`
	SourceLocale  string `json:"source_locale"`
}

func (c *Config) GetSourceLocale() string {
	if c.SourceLocale == "" {
		return localeEn
	} else {
		return c.SourceLocale
	}
}

// LoadConfig Create a new Singleton configuration instance
func LoadConfig(path string) (*Config, error) {
	contents, err := ioutil.ReadFile(path)
	if err != nil {
		return nil, err
	}

	var cfg Config
	if err := json.Unmarshal(contents, &cfg); err != nil {
		return nil, err
	}

	logger.Info(fmt.Sprintf("Created a new config: %#v", cfg))

	return &cfg, nil
}
