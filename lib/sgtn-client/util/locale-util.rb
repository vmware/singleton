# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  class LocaleUtil
    MAP_LOCALES = {
      'zh-cn' => 'zh-hans',
      'zh-tw' => 'zh-hant',
      'zh-hans-cn' => 'zh-hans',
      'zh-hant-tw' => 'zh-hant'
    }.freeze
    LOCALE_SEPARATOR = '-'

    def self.get_best_locale(locale)
      return locale if SgtnClient.config.available_locales.include?(locale)

      return get_fallback_locale if locale.nil?

      locale = locale.to_s
      return get_fallback_locale if locale.empty?

      get_best_match(locale.gsub('_', LOCALE_SEPARATOR).downcase)
    end

    def self.is_source_locale(locale = nil)
      locale == get_source_locale
    end

    def self.get_best_match(locale)
      locale = MAP_LOCALES[locale] || locale
      lowercase_locales_map[locale] or begin
        index = locale.rindex(LOCALE_SEPARATOR)
        return get_fallback_locale if index.nil?

        get_best_match(locale[0...index])
      end
    end

    def self.get_source_locale
      'en'
    end

    def self.get_default_locale
      'en'
    end

    def self.get_fallback_locale
      @fallback_locale ||= get_default_locale || get_source_locale || 'en'
    end

    def self.lowercase_locales_map
      @lowercase_locales_map ||= SgtnClient.config.available_locales.each_with_object({}) do |locale, memo|
        memo[locale.to_s.downcase] = locale
      end
    end

    def self.reset_available_locales(type)
      @lowercase_locales_map = nil if type == :available_locales
    end

    SgtnClient.config.add_observer(self, :reset_available_locales)

    private_class_method :get_best_match, :lowercase_locales_map, :reset_available_locales
  end
end
