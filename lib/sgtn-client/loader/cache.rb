# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :CacheUtil, 'sgtn-client/util/cache-util'

  module TranslationLoader
    autoload :CONSTS, 'sgtn-client/loader/consts'

    module Cache # :nodoc:
      @queue = Queue.new
      Thread.new do
        loop do
          begin
            id, block = Cache.instance_variable_get(:@queue).pop
            cache_item = SgtnClient::CacheUtil.get_cache(id)
            next unless cache_item && SgtnClient::CacheUtil.is_expired(cache_item)

            block.call
          rescue StandardError => e
            SgtnClient.logger.error "an error occured while loading #{id}."
            SgtnClient.logger.error e
          end
        end
      end

      # get from cache, return expired data immediately
      def get_bundle(component, locale)
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}"

        key = SgtnClient::Common::BundleID.new(component, locale)
        cache_item = SgtnClient::CacheUtil.get_cache(key)
        if cache_item
          if SgtnClient::CacheUtil.is_expired(cache_item)
            # refresh in background
            Cache.instance_variable_get(:@queue) << [key, proc { load_bundle(component, locale) }]
          end
          return cache_item.dig(:items)
        end

        load_bundle(component, locale) # refresh synchronously if not in cache
      end

      # load and save to cache
      def load_bundle(component, locale)
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}"

        key = SgtnClient::Common::BundleID.new(component, locale)
        item = super
        SgtnClient::CacheUtil.write_cache(key, item) if item
        item
      rescue StandardError => e
        SgtnClient.logger.error "[TranslationLoader::Cache][load_bundle]#{component}/#{locale}, error=#{e.message}"
        SgtnClient.logger.error e.backtrace
        nil
      end

      def available_bundles
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}]"
        cache_item = SgtnClient::CacheUtil.get_cache(CONSTS::AVAILABLE_BUNDLES_KEY)
        if cache_item
          if SgtnClient::CacheUtil.is_expired(cache_item)
            Cache.instance_variable_get(:@queue) << [CONSTS::AVAILABLE_BUNDLES_KEY, proc do
              item = super
              SgtnClient::CacheUtil.write_cache(CONSTS::AVAILABLE_BUNDLES_KEY, item) if item
            end]
          end
          return cache_item.dig(:items)
        end

        item = super
        SgtnClient::CacheUtil.write_cache(CONSTS::AVAILABLE_BUNDLES_KEY, item) if item
        item
      end
    end
  end
end
