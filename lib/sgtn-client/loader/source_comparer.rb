# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    module SourceComparer
      def load_bundle(component, locale)
        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}" }

        # source locale and old source locale don't need comparison because they are bases of comparison
        real_locale = cache_to_real_map[locale]
        return super(component, real_locale) if real_locale

        source_bundle_thread = Thread.new { load_bundle(component, LocaleUtil.get_source_locale) }
        translation_bundle = super

        begin
          old_source_bundle = translation_bundle.origin.load_bundle(component, old_source_locale(locale))
        rescue StandardError => e
          SgtnClient.logger.error "[#{__FILE__}][#{__callee__}] failed to load old source bundle. component:#{component}. error: #{e}"
        end

        begin
          source_bundle = source_bundle_thread.value
        rescue StandardError => e
          SgtnClient.logger.error "[#{__FILE__}][#{__callee__}] failed to load source bundle. component:#{component}. error: #{e}"
          return translation_bundle
        end

        compare_source(translation_bundle, old_source_bundle, source_bundle)
      end

      protected

      def old_source_locale(_locale)
        LocaleUtil.get_source_locale
      end

      def compare_source(translation_bundle, old_source_bundle, source_bundle)
        source_bundle.each do |k, v|
          if translation_bundle[k].nil? || (!old_source_bundle.nil? && old_source_bundle[k] != v)
            translation_bundle[k] = LocalizedString.new(v, LocaleUtil.get_source_locale) if v
          end
        end
        translation_bundle
      end

      private

      def cache_to_real_map
        @cache_to_real_map ||= {
          LocaleUtil.get_source_locale => CONSTS::REAL_SOURCE_LOCALE
        }.freeze
      end
    end
  end
end
