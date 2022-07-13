# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'pathname'
require 'set'
require 'yaml'

module SgtnClient
  module TranslationLoader
    class Source
      def initialize(config)
        @source_bundle_path = Pathname.new(config.source_bundle)
      end

      def load_bundle(component, locale = nil)
        return if locale && locale != CONSTS::REAL_SOURCE_LOCALE # return when NOT querying source

        SgtnClient.logger.debug { "[#{method(__callee__).owner}.#{__callee__}] component=#{component}" }

        total_messages = {}

        (@source_bundle_path + component).glob('**/*.{yml, yaml}') do |f|
          bundle = YAML.load(File.read(f))
          messages = bundle&.first&.last # TODO: Warn about inconsistent source locale
          if messages.is_a?(Hash)
            total_messages.merge!(messages)
          else
            SgtnClient.logger.error "[#{method(__callee__).owner}.#{__callee__}] invalid bundle data in #{f}"
          end
        end

        raise SingletonError, "no local source messages for component #{component}" if total_messages.empty?

        total_messages
      end

      def available_bundles
        SgtnClient.logger.debug { "[#{method(__callee__).owner}.#{__callee__}]" }

        @available_bundles ||= begin
          @source_bundle_path.children.select(&:directory?).reduce(Set.new) do |bundles, component|
            component.glob('**/*.{yml, yaml}') do |_|
              bundles << Common::BundleID.new(component.basename.to_s, LocaleUtil.get_source_locale)
              break bundles
            end || bundles
          end
        end
      end
    end
  end
end
