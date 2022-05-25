# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'concurrent'
require 'set'
require 'sgtn-client/common/hash'

module SgtnClient
  class LocaleUtil
    MAP_LOCALES = {
      'zh-cn' => 'zh-hans',
      'zh-tw' => 'zh-hant',
      'zh-hans-cn' => 'zh-hans',
      'zh-hant-tw' => 'zh-hant'
    }.freeze
    LOCALE_SEPARATOR = '-'
    @locale_match_results = SgtnClient::Common::ConcurrentHash.new { |hash, key| hash[key] = SgtnClient::Common::ConcurrentHash.new }
    @lowercase_locales_map = SgtnClient::Common::ConcurrentHash.new

    def self.get_best_locale(locale, component)
      component_result = @locale_match_results[component]
      component_result[locale] ||= begin
        component_result.shift if component_result.size >= 50

        if Config.available_locales(component).include?(locale)
          locale
        elsif locale.nil?
          get_fallback_locale
        else
          locale = locale.to_s
          if locale.empty?
            get_fallback_locale
          else
            candidates = lowercase_locales_map(component)
            if candidates.nil? || candidates.empty?
              raise SgtnClient::SingletonError, "component '#{component}' doesn't exist!"
            end

            get_best_match(locale.gsub('_', LOCALE_SEPARATOR).downcase, candidates)
          end
        end
      end
    end

    def self.is_source_locale(locale = nil)
      locale == get_source_locale
    end

    def self.get_best_match(locale, candidates)
      locale = MAP_LOCALES[locale] || locale
      candidates[locale] or begin
        index = locale.rindex(LOCALE_SEPARATOR)
        return get_fallback_locale if index.nil?

        get_best_match(locale[0...index], candidates)
      end
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

    def self.lowercase_locales_map(component)
      @lowercase_locales_map[component] ||= Config.available_locales(component).each_with_object({}) do |locale, memo|
        memo[locale.to_s.downcase] = locale
      end
    end

    def self.reset_locale_data(type)
      return unless type == :available_locales

      @locale_match_results.clear
      @lowercase_locales_map.clear
    end

    SgtnClient::Config.add_observer(self, :reset_locale_data)

    private_class_method :get_best_match, :lowercase_locales_map, :reset_locale_data
  end
end
