# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :StringUtil, 'sgtn-client/util/string-util'
  autoload :LocaleUtil, 'sgtn-client/util/locale-util'

  module TranslationLoader
    autoload :CONSTS, 'sgtn-client/loader/consts'

    module SourceComparer
      def load_bundle(component, locale)
        # source locale and old source locale don't need comparison because they are bases of comparison
        real_locale = cache_to_real_map[locale]
        return super(component, real_locale) if real_locale

        old_source_bundle_thread = Thread.new { load_bundle(component, CONSTS::OLD_SOURCE_LOCALE) }
        translation_bundle = super(component, locale)
        source_bundle = get_bundle(component, LocaleUtil.get_source_locale) # source is in cache and does not expire.
        old_source_bundle = old_source_bundle_thread.value

        compare_source(translation_bundle, old_source_bundle, source_bundle)
      end

      private

      def compare_source(translation_bundle, old_source_bundle, source_bundle)
        return translation_bundle if translation_bundle.nil? || source_bundle.nil? || old_source_bundle.nil?

        source_bundle.each do |key, value|
          if old_source_bundle[key] != value || translation_bundle[key].nil?
            translation_bundle[key] = SgtnClient::StringUtil.new(value, LocaleUtil.get_source_locale)
          end
        end
        translation_bundle
      end

      def cache_to_real_map
        @cache_to_real_map ||= {
          LocaleUtil.get_source_locale => CONSTS::REAL_SOURCE_LOCALE,
          CONSTS::OLD_SOURCE_LOCALE => LocaleUtil.get_source_locale
        }.freeze
      end
    end
  end
end
