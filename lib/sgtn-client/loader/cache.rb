# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :CacheUtil, 'sgtn-client/util/cache-util'

  module TranslationLoader
    module Cache # :nodoc:
      # get from cache, return expired data immediately
      def get_bundle(component, locale)
        SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] component=#{component}, locale=#{locale}"

        key = SgtnClient::CacheUtil.get_cachekey(component, locale)
        cache_item = SgtnClient::CacheUtil.get_cache(key)
        if cache_item
          if SgtnClient::CacheUtil.is_expired(cache_item) && !SgtnClient::LocaleUtil.is_source_locale(locale)
            SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] bundle cache is expired. key=#{key}"

            Thread.new do # TODO: Use one thread # refresh in background
              begin
                load_bundle(component, locale)
              rescue StandardError => e
                SgtnClient.logger.error "Error occured while loading: component=#{component}, locale=#{locale}"
                SgtnClient.logger.error e
              end
            end
          end
          return cache_item.dig(:items)
        end

        load_bundle(component, locale) # refresh synchronously if not in cache
      end

      # load and save to cache
      def load_bundle(component, locale)
        SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] component=#{component}, locale=#{locale}"

        key = SgtnClient::CacheUtil.get_cachekey(component, locale)
        item = super
        SgtnClient::CacheUtil.write_cache(key, item) if item
        item
      end

      AVAILABLE_BUNDLES_KEY = 'available_bundles'
      def available_bundles
        SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}]"
        cache_item = SgtnClient::CacheUtil.get_cache(AVAILABLE_BUNDLES_KEY)
        if cache_item
          if SgtnClient::CacheUtil.is_expired(cache_item)
            SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] available_bundles cache is expired."
            Thread.new do # TODO: Use one thread
              begin
                item = super
                SgtnClient::CacheUtil.write_cache(AVAILABLE_BUNDLES_KEY, item) if item
              rescue StandardError => e
                SgtnClient.logger.error 'Error occured while loading available bundles.'
                SgtnClient.logger.error e
              end
            end
          end
          return cache_item.dig(:items)
        end

        item = super
        SgtnClient::CacheUtil.write_cache(AVAILABLE_BUNDLES_KEY, item) if item
        item
      end
    end
  end
end
