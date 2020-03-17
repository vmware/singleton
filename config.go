/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
)

// Config Singleton configuration
type Config struct {
	OnlineServiceURL string `json:"online_service_url"`

	DefaultLocale string `json:"default_locale"`

	OfflineResourcesBaseURL string `json:"offline_resources_base_url"`
	CacheExpiredTime        int64  `json:"cacheExpiredTime"` //seconds

	EnableCache bool `json:"enable_cache"`

	Components []transKey `json:"components"`
}

// NewConfig Create a new Singleton configuration instance
func NewConfig(path string) (*Config, error) {
	contents, err := ioutil.ReadFile(path)
	if err != nil {
		return nil, err
	}

	var cfg Config
	if err := json.Unmarshal(contents, &cfg); err != nil {
		return nil, err
	}

	cfg.EnableCache = true

	logger.Debug(fmt.Sprintf("Created a new config: %#v", cfg))

	return &cfg, nil
}
