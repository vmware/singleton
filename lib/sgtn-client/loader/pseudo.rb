# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    module Pseudo # :nodoc:
      def load_bundle(component, locale)
        return super unless locale == Sgtn::PSEUDO_LOCALE

        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}" }

        begin
          old_source_bundle_thread = Thread.new { load_bundle(component, CONSTS::OLD_SOURCE_LOCALE) }
          source_bundle = load_bundle(component, LocaleUtil.get_source_locale)
          old_source_bundle = old_source_bundle_thread.value
        rescue StandardError => e
          SgtnClient.logger.error "[#{__FILE__}][#{__callee__}] failed to load source(or old source) bundle. component:#{component}. error: #{e}"
          raise e
        end

        translation_bundle = if old_source_bundle.origin == source_bundle.origin
                               source_bundle
                             else
                               compare_source(old_source_bundle, old_source_bundle, source_bundle)
                             end
        tag = source_bundle.origin.pseudo_tag
        oldtag = old_source_bundle.origin.pseudo_tag
        translation_bundle.each do |key, value|
          translation_bundle[key] = if value.is_a?(LocalizedString)
                                      "#{tag}#{value}#{tag}"
                                    else
                                      "#{oldtag}#{value}#{oldtag}"
                                    end
        end
        translation_bundle
      end
    end
  end
end
