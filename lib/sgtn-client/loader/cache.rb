# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :CacheUtil, 'sgtn-client/util/cache-util'
end

module SgtnClient::TranslationLoader::Cache # :nodoc:
  # get from cache, return expired data immediately
  def get_bundle(component, locale)
    key = SgtnClient::CacheUtil.get_cachekey(component, locale)
    SgtnClient.logger.debug "[#{self.class}][#{__method__}] #{key}"
    cache_item = SgtnClient::CacheUtil.get_cache(key)
    if cache_item
      if SgtnClient::CacheUtil.is_expired(cache_item) && !SgtnClient::LocaleUtil.is_source_locale(locale)
        SgtnClient.logger.debug "[#{self.class}][#{__method__}] Bundle cache is expired. key=#{key}"
        Thread.new { load_bundle(component, locale) } # TODO: Use one thread # refresh in background
      end
      return cache_item.dig(:items)
    end

    load_bundle(component, locale) # refresh synchronously if not in cache
  end

  # load and save to cache
  def load_bundle(component, locale)
    SgtnClient.logger.debug "[#{self.class}][#{__method__}] #{component}/#{locale}"
    key = SgtnClient::CacheUtil.get_cachekey(component, locale)
    item = super
    SgtnClient::CacheUtil.write_cache(key, item)
    item
  rescue StandardError => e
    SgtnClient.logger.error "[TranslationLoader::Cache][load_bundle]#{component}/#{locale}, error=#{e.message}"
    SgtnClient.logger.error e.backtrace
    nil
  end

  AVAILABLE_BUNDLES_KEY = 'available_bundles'
  def available_bundles
    SgtnClient.logger.debug "[#{self.class}][#{__method__}]"
    cache_item = SgtnClient::CacheUtil.get_cache(AVAILABLE_BUNDLES_KEY)
    if cache_item.nil?
      item = super
      SgtnClient::CacheUtil.write_cache(AVAILABLE_BUNDLES_KEY, item) if item
      item
    else
      if SgtnClient::CacheUtil.is_expired(cache_item)
        Thread.new do
          item = super
          SgtnClient::CacheUtil.write_cache(AVAILABLE_BUNDLES_KEY, item) if item
        end
      end
      cache_item.dig(:items)
    end
  rescue StandardError => e
    SgtnClient.logger.error "[TranslationLoader::Cache][#{__method__}], error=#{e.message}"
    SgtnClient.logger.error e.backtrace
    nil
  end
end
