# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
require 'set'

module SgtnClient
  SUPPORTED_LOCALES = %w[en de es fr ko ja zh-Hans zh-Hant zh de-CH].freeze # TODO get this from service in online mode
  
  MAP_LOCALES = {
    'zh-CN' => 'zh-Hans',
    'zh-TW' => 'zh-Hant',
    'zh-Hans-CN' => 'zh-Hans',
    'zh-Hant-TW' => 'zh-Hant'
  }.freeze

  class LocaleUtil
    OLD_SOURCE_LOCALE = 'old_source_locale'
    REAL_SOURCE_LOCALE = 'latest'

    def self.get_best_locale(locale)
      return get_default_locale if locale.nil?

      locale = locale.to_s
      return get_default_locale if locale.empty?

      get_best_match(locale)
    end

    def self.is_source_locale(locale = nil)
      locale == get_source_locale
    end

    def self.get_best_match(locale)
      locale = locale.gsub('_', '-')
      locale = SgtnClient::MAP_LOCALES[locale] if SgtnClient::MAP_LOCALES.key?(locale)
      return locale if SUPPORTED_LOCALES.include?(locale)
      return LocaleUtil.get_source_locale if locale.index('-').nil?
      get_best_match(locale.slice(0..(locale.rindex('-')-1)) )  
    end

    def self.get_source_locale
      'en'
    end

    def self.get_default_locale
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]['default_language'] || 'en'
    end

    def self.cache_to_real_map
      @cache_to_real_map ||= {
        get_source_locale => REAL_SOURCE_LOCALE,
        OLD_SOURCE_LOCALE => get_source_locale
      }
    end

    private_class_method :get_best_match
  end
end
