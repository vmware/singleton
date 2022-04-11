# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module Common
    autoload :BundleID, 'sgtn-client/common/data'
  end

  module TranslationLoader
    autoload :CONSTS, 'sgtn-client/loader/consts'

    class Source
      def initialize(config)
        # raise error if config['source_bundle']
        @source_bundle_path = Pathname.new(config['source_bundle'])
      end

      def load_bundle(component, locale = nil)
        return if locale && locale != CONSTS::REAL_SOURCE_LOCALE # only return when querying source

        SgtnClient.logger.debug "[Source][getBundle]component=#{component}"

        total_messages = {}

        (@source_bundle_path + component).glob('**/*.{yml, yaml}') do |f|
          bundle = YAML.load(File.read(f))
          total_messages.merge!(bundle&.first&.last) # TODO: Warning about inconsistent source locale
        end

        total_messages.empty? ? nil : total_messages
      end

      def available_bundles
        SgtnClient.logger.debug "[#{method(__method__).owner}.#{__method__}]"

        bundles = Set.new
        @source_bundle_path.glob('*/') do |component|
          component.glob('**/*.{yml, yaml}') do |_|
            bundles << Common::BundleID.new(component.basename.to_s, SgtnClient::LocaleUtil.get_source_locale)
            break
          end
        end
        bundles
      end
    end
  end
end
