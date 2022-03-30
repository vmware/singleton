# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :Source, 'sgtn-client/loader/source'
end
module SgtnClient::TranslationLoader::SourceComparer
  def load_bundle(component, locale)
    # source locale always uses source bundles
    return SgtnClient::TranslationLoader::Source.load_bundle(component) if SgtnClient::LocaleUtil.is_source_locale(locale)

    translation_bundle_thread = Thread.new { super(component, locale) }
    old_source_bundle = super(component, SgtnClient::LocaleUtil.get_source_locale)
    source_bundle = SgtnClient::TranslationLoader::Source.load_bundle(component)
    translation_bundle = translation_bundle_thread.value

    compare_source(translation_bundle, old_source_bundle, source_bundle)
  end

  private

  def compare_source(translation_bundle, old_source_bundle, source_bundle)
    return translation_bundle if translation_bundle.nil? || source_bundle.nil? || old_source_bundle.nil?

    old_source_messages = old_source_bundle['messages']
    translation_messages = translation_bundle['messages']
    translation_bundle['messages'] = new_translation_messages = {}
    source_bundle['messages'].each do |key, value|
      translation = translation_messages[key]
      new_translation_messages[key] = if old_source_messages[key] == value && !translation.nil?
                                        translation
                                      else
                                        SgtnClient::StringUtil.new(value, SgtnClient::LocaleUtil.get_source_locale)
                                      end
    end
    translation_bundle
  end
end
