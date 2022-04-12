# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    class Chain
      attr_accessor :loaders

      def initialize(*loaders)
        self.loaders = loaders
      end

      def load_bundle(component, locale)
        loaders.each do |loader|
          bundle = loader.load_bundle(component, locale)
          return bundle if bundle
        rescue StandardError => e
          SgtnClient.logger.warn "Failed to load bundle from #{loader.class.name}: #{e}"
        end
        nil
      end

      def available_bundles
        exception = nil
        total_data = Set.new

        loaders.each do |loader|
          begin
            item = loader.available_bundles
            total_data += item
          rescue StandardError => e
            exception ||= e
            SgtnClient.logger.error "[#{method(__callee__).owner}.#{__callee__}] failed on #{loader.class}: #{e}"
          end
        end

        raise exception if total_data.empty? && exception

        total_data
      end
    end
  end
end
