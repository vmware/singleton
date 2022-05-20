# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient # :nodoc:
  autoload :CacheUtil, 'sgtn-client/util/cache-util'

  module TranslationLoader # :nodoc:
    autoload :CONSTS, 'sgtn-client/loader/consts'

    module Cache # :nodoc:
      # get from cache, return expired data immediately
      def get_bundle(component, locale)
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}"

        key = Common::BundleID.new(component, locale)
        cache_item = CacheUtil.get_cache(key)
        if cache_item
          load_bundle(component, locale, sync: false) if CacheUtil.is_expired(cache_item)
          cache_item[:items]
        else
          load_bundle(component, locale)
        end
      end

      def available_bundles
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}]"

        cache_item = CacheUtil.get_cache(CONSTS::AVAILABLE_BUNDLES_KEY)
        if cache_item
          super(sync: false) if CacheUtil.is_expired(cache_item)
          cache_item[:items]
        else
          super
        end
      end
    end

    module CacheFiller # :nodoc:
      def load_bundle(component, locale)
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}"

        item = super
        CacheUtil.write_cache(Common::BundleID.new(component, locale), item) if item
        item
      end

      def available_bundles
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}]"

        item = super
        CacheUtil.write_cache(CONSTS::AVAILABLE_BUNDLES_KEY, item) if item
        item
      end
    end
  end
end
