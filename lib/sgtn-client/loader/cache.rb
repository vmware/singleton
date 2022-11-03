# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'concurrent/map'

module SgtnClient
  module TranslationLoader
    module Cache # :nodoc:
      def initialize(*)
        @cache_hash = Concurrent::Map.new
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

    module CacheFiller # :nodoc:
      def load_bundle(component, locale)
        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}] CacheFiller, component=#{component}, locale=#{locale}" }

        @cache_hash[Common::BundleID.new(component, locale)] = super
      end

      def available_bundles
        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}] CacheFiller" }

        item = super
        old_item = @cache_hash[CONSTS::AVAILABLE_BUNDLES_KEY]
        if item != old_item # only update if different
          @cache_hash[CONSTS::AVAILABLE_BUNDLES_KEY] = Common::SetData.new(item)
        else # if same, don't need to update the data, but update last_update
          old_item.last_update = Time.now
          old_item
        end
      end
    end
  end
end
