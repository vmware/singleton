# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'json'
require 'pathname'

module SgtnClient
  module TranslationLoader
    class LocalTranslation
      BUNDLE_PREFIX = 'messages_'.freeze
      BUNDLE_SUFFIX = '.json'.freeze

      def initialize(config)
        @base_path = Pathname.new(config['translation_bundle']) + config['product_name'] + config['version'].to_s
      end

      def load_bundle(component, locale)
        return if locale == CONSTS::REAL_SOURCE_LOCALE # return when querying source

        SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] component=#{component}, locale=#{locale}"

        file_name = BUNDLE_PREFIX + locale + BUNDLE_SUFFIX
        file_path = @base_path + component + file_name

        bundle_data = JSON.parse(File.read(file_path))
        messages = bundle_data['messages']

        raise SingletonError, "no messages in local bundle file: #{file_path}." unless messages

        messages
      end

      def available_bundles
        SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}]"

        @available_bundles ||= begin
          @base_path.glob('*/*.json').reduce(Set.new) do |bundles, f|
            locale = f.basename.to_s.sub(/\A#{BUNDLE_PREFIX}/i, '').sub(/#{BUNDLE_SUFFIX}\z/i, '')
            bundles.add Common::BundleID.new(f.parent.basename.to_s, locale)
          end
        end
      end
    end
  end
end
