# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'set'

module SgtnClient
  module TranslationLoader
    class Chain
      attr_reader :loaders

      def initialize(*loaders)
        @loaders = loaders
      end

      def load_bundle(component, locale)
        exception = nil

        @loaders.each do |loader|
          begin
            bundle = loader.load_bundle(component, locale)
            return bundle if bundle
          rescue StandardError => e
            exception = e
            SgtnClient.logger.error "[#{__FILE__}][#{__callee__}] {component: #{component},locale: #{locale}}, failed on #{loader.class}: #{e}"
          end
        end

        raise exception || SingletonError.new("can't load component: #{component}, locale: #{locale}")
      end

      def available_bundles
        exception = nil
        total_data = Set.new

        @loaders.each do |loader|
          begin
            item = loader.available_bundles
            total_data += item
          rescue StandardError => e
            exception = e
            SgtnClient.logger.error "[#{__FILE__}][#{__callee__}] failed on #{loader.class}: #{e}"
          end
        end

        raise exception if total_data.empty? && exception

        total_data
      end
    end
  end
end
