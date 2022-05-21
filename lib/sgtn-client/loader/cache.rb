# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    module Cache # :nodoc:
      def initialize(*)
        @cache_hash = SgtnClient::Common::ConcurrentHash.new
        super
      end

      # get from cache, return expired data immediately
      def get_bundle(component, locale)
        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}" }

        result = @cache_hash[Common::BundleID.new(component, locale)] || load_bundle(component, locale)
      ensure
        load_bundle(component, locale, sync: false) if result&.expired?
      end

      def available_bundles
        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}]" }

        result = @cache_hash[CONSTS::AVAILABLE_BUNDLES_KEY] || super
      ensure
        super(sync: false) if result&.expired?
      end
    end

    module CacheFiller
      def load_bundle(component, locale)
        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}" }

        item = SgtnClient::Common::BundleData[super]
        item.last_update = Time.now
        @cache_hash[Common::BundleID.new(component, locale)] = item
      end

      def available_bundles
        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}]" }

        item = super
        #CacheUtil.write_cache(CONSTS::AVAILABLE_BUNDLES_KEY, item) if item # TODO: don't save when empty
        #item
        return if item.nil? || item.empty?

        item = SgtnClient::Common::SetData.new(item)
        old_item = @cache_hash[CONSTS::AVAILABLE_BUNDLES_KEY]
        if item != old_item # only update if different
          @cache_hash[CONSTS::AVAILABLE_BUNDLES_KEY] = item
        else # if same, don't need to update the data, but update last_update
          old_item.last_update = Time.now
          old_item
        end
      end
    end
  end
end
