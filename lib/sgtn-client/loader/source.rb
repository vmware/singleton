# Copyright 2025 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'pathname'
require 'set'
require 'yaml'

module SgtnClient
  module TranslationLoader
    class Source # :nodoc:
      def initialize(config)
        @source_bundle_path = Pathname.new(config.source_bundle)
      end

      def load_bundle(component, locale = nil)
        return if locale && locale != CONSTS::REAL_SOURCE_LOCALE # return when NOT querying source

        SgtnClient.logger.debug { "[#{method(__callee__).owner}.#{__callee__}] component=#{component}" }

        total_messages = {}

        Pathname.glob(@source_bundle_path + component + '**/*.{yml, yaml}') do |f|
          bundle = YAML.safe_load(File.read(f), aliases: true)
          messages = bundle&.first&.last # TODO: Warn about inconsistent source locale
          if messages.is_a?(Hash)
            total_messages.merge!(messages)
          else
            SgtnClient.logger.error "[#{method(__callee__).owner}.#{__callee__}] invalid bundle data in #{f}"
          end
        end

        raise SingletonError, "no local source messages for component #{component}" if total_messages.empty?

        Common::BundleData.new(total_messages, origin: self, component: component, locale: LocaleUtil.get_source_locale)
      end

      def available_bundles
        SgtnClient.logger.debug { "[#{method(__callee__).owner}.#{__callee__}]" }

        @available_bundles ||= begin
          @source_bundle_path.children.select(&:directory?).reduce(Set.new) do |bundles, component|
            Pathname.glob(component + '**/*.{yml, yaml}') do |_|
              bundles << Common::BundleID.new(component.basename.to_s, LocaleUtil.get_source_locale)
              break bundles
            end || bundles
          end
        end
      end
    end
  end
end
