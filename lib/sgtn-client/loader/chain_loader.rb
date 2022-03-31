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
        rescue => e
          SgtnClient.logger.warn "Failed to load bundle from #{loader.class.name}: #{e}"
        end
        nil
      end
    end
  end
end
