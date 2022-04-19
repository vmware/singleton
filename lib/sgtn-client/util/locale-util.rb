# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
require 'set'

module SgtnClient
  class LocaleUtil
    MAP_LOCALES = {
      'zh-CN' => 'zh-Hans',
      'zh-TW' => 'zh-Hant',
      'zh-Hans-CN' => 'zh-Hans',
      'zh-Hant-TW' => 'zh-Hant'
    }.freeze

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
      locale = MAP_LOCALES[locale] if MAP_LOCALES.key?(locale)
      return locale if Config.available_locales.include?(locale)
      return get_source_locale if locale.index('-').nil?

      get_best_match(locale.slice(0..(locale.rindex('-') - 1)))
    end

    def self.get_source_locale
      'en'
    end

    def self.get_default_locale
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]['default_language'] || 'en'
    end

    private_class_method :get_best_match
  end
end
