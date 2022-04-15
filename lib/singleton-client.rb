# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :LocaleUtils, 'sgtn-client/locale-util'
  autoload :StringUtils, 'sgtn-client/string-util'
  autoload :SingletonError, 'sgtn-client/exceptions'
  autoload :Config, 'sgtn-client/core/config'
  module Common
    autoload :BundleID, 'sgtn-client/common/data'
  end
end

module Singleton
  module Base
    # Write methods which delegates to the configuration object
    %w[locale backend default_locale available_locales default_separator
       exception_handler load_path enforce_available_locales].each do |method|
      module_eval <<-DELEGATORS, __FILE__, __LINE__ + 1
        def #{method}
          config.#{method}
        end

        def #{method}=(value)
          config.#{method} = (value)
        end
      DELEGATORS
    end

    def translate(key, component, locale: nil, **kwargs)
      translate!(key, component, locale: locale, **kwargs)
    rescue StandardError => e
      SgtnClient.logger.error "Could not translate #{key}, #{component}, #{locale}. error: #{e}"
      block_given? ? yield : key
    end
    alias t translate

    # raise error when translation is not found
    def translate!(key, component, locale: nil, **kwargs)
      SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] key: #{key}, component: #{component}, locale: #{locale}, args: #{kwargs}"

      locale = locale.nil? ? self.locale : LocaleUtil.get_best_locale(locale)

      result = get_bundle(component, locale)&.fetch(key.to_s, nil)
      raise SingletonError 'translation is missing' if result.nil?

      if kwargs.empty?
        result
      else
        locale = result.locale if result.is_a?(StringUtil)
        result.localize(locale) % kwargs
      end
    end
    alias t! translate!

    def get_translations(component, locale = nil)
      get_translations(component, locale)
    rescue StandardError => e
      SgtnClient.logger.error "Could not get translations for component: #{component}, locale: #{locale}. error: #{e}"
      { 'component' => component, 'locale' => locale, 'messages' => {} }
    end

    def get_translations!(component, locale = nil)
      SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] component: #{component}, locale: #{locale}"

      locale = locale.nil? ? self.locale : LocaleUtil.get_best_locale(locale)

      { 'component' => component, 'locale' => locale, 'messages' => get_bundle(component, locale) || {} }
    end

    # Executes block with given locale set.
    def with_locale(tmp_locale = nil)
      if tmp_locale.nil?
        yield
      else
        current_locale = locale
        self.locale = tmp_locale
        begin
          yield
        ensure
          self.locale = current_locale
        end
      end
    end

    def locale
      Thread.current[:locale] || LocaleUtil.get_default_locale
    end

    def locale=(value)
      Thread.current[:locale] = value
    end

    private

    def get_bundle(component, locale)
      id = Common::BundleID.new(component, locale)
      available_bundles = Config.available_bundles
      unless available_bundles.nil? || available_bundles.empty? || available_bundles.include?(id)
        raise SingletonError "get an unavailable bundle: #{id}"
      end

      Config.loader.get_bundle(component, locale)
    end
  end

  extend Base
end

module Sgtn # :nodoc:
  extend Singleton::Base
end

I18n.backend = I18n::Backend::Chain.new(I18n::Backend::SingletonBackend.new, I18n.backend)
