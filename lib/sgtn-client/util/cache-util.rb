# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'time'

module SgtnClient
  class CacheUtil
    def self.get_cache(cache_key)
      SgtnClient::Core::Cache.get(cache_key)
    end

    def self.clear_cache
      SgtnClient::Core::Cache.clear
    end

    def self.write_cache(cache_key, items)
      return nil if items.nil? || items.empty?

      env = Config.default_environment
      cache_expiry_period = Config.configurations[env]['cache_expiry_period']
      # expired after 24 hours
      cache_expiry_period = 24 * 60 if cache_expiry_period.nil?
      SgtnClient::Core::Cache.put(cache_key, items, cache_expiry_period)
    end

    def self.get_cachekey(component, locale)
      env = Config.default_environment
      product_name = Config.configurations[env]['product_name']
      version = Config.configurations[env]['version'].to_s
      product_name + '_' + version + '_' + component + '_' + locale
    end

    def self.is_expired(cache_item)
      cache_item[:expiry] < Time.now
    end

  end
end
