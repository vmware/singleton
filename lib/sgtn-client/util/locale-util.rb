# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  DEFAULT_LOCALES = %w[en de es fr ko ja zh-Hans zh-Hant].freeze

  MAP_LOCALES = {
    'zh-CN' => 'zh-Hans',
    'zh-TW' => 'zh-Hant',
    'zh-Hans-CN' => 'zh-Hans',
    'zh-Hant-TW' => 'zh-Hant'
  }.freeze

  class LocaleUtil
    def self.get_best_locale(locale)
      fallback(process_locale(locale))
    end

    def self.process_locale(locale = nil)
      locale ||= SgtnClient::Config.configurations.default
      locale.to_s
    end

    def self.is_source_locale(locale = nil)
      locale == get_source_locale
    end

    def self.fallback(locale)
      found = SgtnClient::DEFAULT_LOCALES.select { |e| e == locale }
      return found[0] unless found.empty?
      return SgtnClient::MAP_LOCALES[locale] if SgtnClient::MAP_LOCALES.key?(locale)

      parts = locale.split('-')
      if parts.size > 1
        f = SgtnClient::DEFAULT_LOCALES.select { |e| e == parts[0] }
        return f[0] unless f.empty?
      end
      locale
    end

    def self.get_source_locale
      env = SgtnClient::Config.default_environment
      source_locale = SgtnClient::Config.configurations[env]['default_language']
      source_locale || 'en'
    end
  end
end
