# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'sgtn-client/common/data'

module SgtnClient
  module TranslationLoader
    class Source
      def initialize(config)
        @source_bundle_path = Pathname.new(config['source_bundle'])
      end

      def load_bundle(component, locale = nil)
        # TODO: make sure data is valid
        return if locale && locale != SgtnClient::LocaleUtil::REAL_SOURCE_LOCALE # only return when querying source

        SgtnClient.logger.debug "[Source]#{__method__}: component=#{component}"

        total_messages = {}

        (@source_bundle_path + component).glob('**/*.{yml, yaml}') do |f|
          bundle = YAML.safe_load(File.read(f))
          messages = bundle&.first&.last # TODO: Warning about inconsistent source locale
          if messages.is_a?(Hash)
            total_messages.merge!(messages)
          else
            SgtnClient.logger.error "Illegal bundle data in #{f}"
          end
        end

        total_messages.empty? ? nil : total_messages
      end

      def available_bundles
        # TODO: make sure data is valid
        bundles = Set.new
        @source_bundle_path.glob('*/') do |component|
          component.glob('**/*.{yml, yaml}') do |_|
            bundles << SgtnClient::Common::BundleID.new(component.basename.to_s, SgtnClient::LocaleUtil.get_source_locale)
            break
          end
        end
        bundles
      end
    end
  end
end
