# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
require 'set'

module SgtnClient
  class LocaleUtil
    OLD_SOURCE_LOCALE = 'old_source_locale'.freeze
    REAL_SOURCE_LOCALE = 'latest'.freeze

    SUPPORTED_LOCALES = %w[en de es fr ko ja zh-Hans zh-Hant zh de-CH].freeze # TODO: get this from service in online mode

    MAP_LOCALES = {
      'zh-CN' => 'zh-Hans',
      'zh-TW' => 'zh-Hant',
      'zh-Hans-CN' => 'zh-Hans',
      'zh-Hant-TW' => 'zh-Hant'
    }.freeze

    def self.get_best_locale(locale)
      return get_fallback_locale if locale.nil?

      locale = locale.to_s
      return get_fallback_locale if locale.empty?

      locale.gsub!('_', '-')
      get_best_match(locale)
    end

    def self.is_source_locale(locale = nil)
      locale == get_source_locale
    end

    def self.get_best_match(locale)
      locale = MAP_LOCALES[locale] || locale
      return locale if SUPPORTED_LOCALES.include?(locale)

      index = locale.rindex('-')
      return get_fallback_locale if index.nil?

      get_best_match(locale[0...index])
    end

    def self.get_source_locale
      'en'
    end

    def self.get_default_locale
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]['default_language']
    end

    def self.get_fallback_locale
      @fallback_locale ||= get_default_locale || get_source_locale || 'en'
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
