# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'multi_json'

module SgtnClient
  module Common
    autoload :BundleID, 'sgtn-client/common/data'
  end

  module TranslationLoader
    class LocalTranslation
      BUNDLE_PREFIX = 'messages_'.freeze
      BUNDLE_SUFFIX = '.json'.freeze

      def initialize(config)
        @base_path = Pathname.new(config['translation_bundle']) + config['product_name'] + config['version']
      end

      def load_bundle(component, locale)
        # TODO: make sure data is valid
        return if locale == SgtnClient::LocaleUtil::REAL_SOURCE_LOCALE # only return when NOT querying source

        file_name = BUNDLE_PREFIX + locale + BUNDLE_SUFFIX
        file_path = @base_path + component + file_name

        bundle_data = JSON.parse(File.read(file_path))
        messages = bundle_data['messages']

        raise SgtnClient::SingletonError, 'no messages in bundle.' unless messages

        messages
      end

      def available_bundles
        # TODO: make sure data is valid
        bundles = Set.new
        @base_path.glob('*/*.json') do |f|
          locale = f.basename.to_s.sub(/\A#{BUNDLE_PREFIX}/i, '').sub(/#{BUNDLE_SUFFIX}\z/i, '')
          bundles.add SgtnClient::Common::BundleID.new(f.parent.basename.to_s, locale)
        end
        bundles
      end
    end
  end
end
