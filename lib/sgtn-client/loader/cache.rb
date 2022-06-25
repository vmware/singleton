# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    module Cache # :nodoc:
      # get from cache, return expired data immediately
      def get_bundle(component, locale)
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}"

        key = CacheUtil.get_cachekey(component, locale)
        cache_item = CacheUtil.get_cache(key)
        if cache_item
          if CacheUtil.is_expired(cache_item)
            Thread.new do # TODO: Use one thread # refresh in background
              begin
                load_bundle(component, locale)
              rescue StandardError => e
                SgtnClient.logger.error "an error occured while loading bundle: component=#{component}, locale=#{locale}"
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
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}"

        key = CacheUtil.get_cachekey(component, locale)
        item = super
        CacheUtil.write_cache(key, item) if item
        item
      end

      def available_bundles
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}]"

        cache_item = CacheUtil.get_cache(CONSTS::AVAILABLE_BUNDLES_KEY)
        if cache_item
          if CacheUtil.is_expired(cache_item)
            Thread.new do # TODO: Use one thread
              begin
                item = super
                CacheUtil.write_cache(CONSTS::AVAILABLE_BUNDLES_KEY, item) if item
              rescue StandardError => e
                SgtnClient.logger.error 'an error occured while loading available bundles.'
                SgtnClient.logger.error e
              end
            end
          end
          return cache_item.dig(:items)
        end

        item = super
        CacheUtil.write_cache(CONSTS::AVAILABLE_BUNDLES_KEY, item) if item
        item
      end
    end
  end
end
