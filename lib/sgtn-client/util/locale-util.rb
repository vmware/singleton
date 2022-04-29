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

    def self.get_best_locale(locale, component = nil)
      candidates = Config.available_locales(component)
      if candidates.nil? || candidates.empty?
        raise SgtnClient::SingletonError, "component '#{component}' doesn't exist!"
      end

      return get_fallback_locale if locale.nil?

      locale = locale.to_s
      return get_fallback_locale if locale.empty?

      get_best_match(locale.gsub('_', '-'), candidates)
    end

    def self.is_source_locale(locale = nil)
      locale == get_source_locale
    end

    def self.get_best_match(locale, candidates)
      locale = MAP_LOCALES[locale] || locale
      return locale if candidates.include?(locale)

      index = locale.rindex('-')
      return get_fallback_locale if index.nil?

      get_best_match(locale[0...index], candidates)
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

    private_class_method :get_best_match
  end
end
