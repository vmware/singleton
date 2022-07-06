# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'time'

module SgtnClient
  class CacheUtil
    def self.get_cache(cache_key)
      Core::Cache.get(cache_key)
    end

    def self.clear_cache
      Core::Cache.clear
    end

    def self.write_cache(cache_key, items)
      return nil if items.nil? || items.empty?

      cache_expiry_period = SgtnClient.config.cache_expiry_period
      # expired after 24 hours
      cache_expiry_period = 24 * 60 if cache_expiry_period.nil?
      Core::Cache.put(cache_key, items, cache_expiry_period)
    end

    def self.is_expired(cache_item)
      cache_item[:expiry] < Time.now
    end

  end
end
