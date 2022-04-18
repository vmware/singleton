# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'sgtn-client/common/data'

module SgtnClient::TranslationLoader
  class Source
    def initialize(config)
      # raise error if config['source_bundle']
      @source_bundle_path = Pathname.new(config['source_bundle'])
    end

    def load_bundle(component, locale = nil)
      return if locale && locale != SgtnClient::LocaleUtil::REAL_SOURCE_LOCALE # only return when querying source

      SgtnClient.logger.debug "[Source][getBundle]component=#{component}"

      total_messages = {}

      (@source_bundle_path + component).glob('**/*.{yml, yaml}') do |f|
        bundle = YAML.load(File.read(f))
        total_messages.merge!(bundle&.first&.last) # TODO: Warning about inconsistent source locale
      end

      total_messages.empty? ? nil : total_messages
    end

    def available_bundles
      bundles = Set.new
      @source_bundle_path.glob('*/') do |component|
        component.glob('**/*.{yml, yaml}') do |_|
          bundles << Common::BundleID.new(component.basename.to_s, LocaleUtil.get_source_locale)
          break
        end
      end
      bundles
    end

    # def available_locales
    #   Set.new([LocaleUtil.get_source_locale])
    # end

    # def available_components
    #   SgtnClient.logger.debug '[Source][available_components]'

    #   components = Set.new
    #   @source_bundle_path.glob('*/') do |f| # TODO: folder shouldn't be empty?
    #     components << f.basename.to_s
    #   end
    #   components
    # end
  end
end
