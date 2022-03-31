# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :StringUtil, 'sgtn-client/util/string-util'
end

module SgtnClient::TranslationLoader::SourceComparer
  def load_bundle(component, locale)
    # source locale and old source locale don't need comparison because they are bases of comparison
    if SgtnClient::LocaleUtil.cache_to_real_map.key?(locale)
      return super(component, SgtnClient::LocaleUtil.cache_to_real_map[locale])
    end

    old_source_bundle_thread = Thread.new { load_bundle(component, SgtnClient::LocaleUtil::OLD_SOURCE_LOCALE) }
    source_bundle_thread = Thread.new { load_bundle(component, SgtnClient::LocaleUtil.get_source_locale) }

    translation_bundle = super(component, locale)
    old_source_bundle = old_source_bundle_thread.value
    source_bundle = source_bundle_thread.value

    # TODO: use etags to avoid unnecessary comparison
    compare_source(translation_bundle, old_source_bundle, source_bundle)
  end

  private

  def compare_source(translation_bundle, old_source_bundle, source_bundle)
    return translation_bundle if translation_bundle.nil? || source_bundle.nil? || old_source_bundle.nil?

    source_bundle.each do |key, value|
      if old_source_bundle[key] != value || translation_bundle[key].nil?
        translation_bundle[key] = SgtnClient::StringUtil.new(value, SgtnClient::LocaleUtil.get_source_locale)
      end
    end
    translation_bundle
  end
end
