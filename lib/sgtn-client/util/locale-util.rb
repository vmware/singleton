# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'concurrent/map'

module SgtnClient
  class LocaleUtil # :nodoc:
    MAP_LOCALES = {
      'zh-cn' => 'zh-hans',
      'zh-tw' => 'zh-hant',
      'zh-hans-cn' => 'zh-hans',
      'zh-hant-tw' => 'zh-hant'
    }.freeze
    LOCALE_SEPARATOR = '-'
    EN_LOCALE = 'en'

    @locale_match_results = Concurrent::Map.new do |hash, component|
      components = SgtnClient.config.available_components
      unless components.empty? || components.include?(component)
        raise SingletonError, "component '#{component}' doesn't exist!"
      end

      hash[component] = Concurrent::Map.new
    end
    @lowercase_locales_map = Concurrent::Map.new

    def self.get_best_locale(locale, component)
      return locale if SgtnClient.config.available_locales(component).include?(locale)

      component_result = @locale_match_results[component]
      component_result[locale] ||= begin
        if component_result.size >= 50
          component_result.delete(component_result.keys[Random.rand(component_result.size)])
        end

        locale = locale.to_s
        if locale.empty?
          get_fallback_locale
        else
          candidates = lowercase_locales_map(component)
          if candidates.empty?
            locale
          else
            get_best_match(locale.gsub('_', LOCALE_SEPARATOR).downcase, candidates)
          end
        end
      end
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
      EN_LOCALE
    end

    def self.get_default_locale
      EN_LOCALE
    end

    def self.get_fallback_locale
      locale_fallbacks[0]
    end

    def self.locale_fallbacks
      @locale_fallbacks ||= ([get_default_locale, get_source_locale, EN_LOCALE].uniq(&:to_s) - [nil, '']).freeze
    end

    def self.lowercase_locales_map(component)
      @lowercase_locales_map[component] ||= SgtnClient.config.available_locales(component).each_with_object({}) do |locale, memo|
        memo[locale.to_s.downcase] = locale
      end
    end

    def self.reset_locale_data(type, component = nil)
      return unless type == :available_locales

      if component.nil?
        @locale_match_results.clear
        @lowercase_locales_map.clear
      else
        @locale_match_results.delete(component)
        @lowercase_locales_map.delete(component)
      end
    end

    SgtnClient.config.add_observer(self, :reset_locale_data)

    private_class_method :get_best_match, :lowercase_locales_map, :reset_locale_data
  end
end
