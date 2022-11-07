# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    module Pseudo # :nodoc:
      protected

      def old_source_locale(locale)
        locale == Sgtn::PSEUDO_LOCALE ? CONSTS::REAL_SOURCE_LOCALE : super
      end

      def compare_source(translation_bundle, old_source_bundle, source_bundle)
        if translation_bundle.locale == Sgtn::PSEUDO_LOCALE
          return translation_bundle if translation_bundle.origin == source_bundle.origin

          tag = source_bundle.origin.pseudo_tag
          super.transform_values! { |v| v.is_a?(LocalizedString) ? "#{tag}#{v}#{tag}" : v }
        else
          super
        end
      end
    end
  end
end
