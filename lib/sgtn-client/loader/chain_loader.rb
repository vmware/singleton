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
        end
        nil
      end

      def available_locales; end

      def available_components; end
    end
  end
end
