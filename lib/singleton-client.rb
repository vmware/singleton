# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'request_store'
require 'sgtn-client/util/locale-util'
require 'sgtn-client/util/string-util'
require 'sgtn-client/exceptions'
require 'sgtn-client/core/config'

module SgtnClient # :nodoc:
  autoload :TranslationLoader, 'sgtn-client/loader'

  module Common # :nodoc:
    autoload :BundleID, 'sgtn-client/common/data'
  end
end

module Singleton # :nodoc:
  module Base # :nodoc:
    # Gets configuration object.
    def load_config(config_file = "./config/singleton.yml")
      SgtnClient.load(config_file, SgtnClient::Config.default_environment)
    end

    def translate(key, component: component, locale: nil, **kwargs)
      translate!(key, component: component, locale: locale, **kwargs)
    rescue StandardError => e
      SgtnClient.logger.error "couldn't translate #{key}, #{component}, #{locale}. error: #{e}"
      block_given? ? yield : key
    end
    alias t translate

    # raise error when translation is not found
    def translate!(key, component: component, locale: nil, **kwargs)
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

    def get_translations(component: component, locale: nil)
      get_translations(component, locale)
    rescue StandardError => e
      SgtnClient.logger.error "couldn't get translations for component: #{component}, locale: #{locale}. error: #{e}"
      { 'component' => component, 'locale' => locale, 'messages' => {} }
    end

    def get_translations!(component: component, locale: nil)
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
      RequestStore.store[:locale] ||= LocaleUtil.get_fallback_locale
    end

    def locale=(value)
      RequestStore.store[:locale] = LocaleUtil.get_best_locale(value)
    end

    private

    def get_bundle(component, locale)
      id = Common::BundleID.new(component, locale)
      bundles = Config.available_bundles
      unless bundles.nil? || bundles.empty? || bundles.include?(id)
        raise SingletonError "bundle is unavailable. #{id}"
      end

      Config.loader.get_bundle(component, locale)
    end
  end

  extend Base
end

module Sgtn # :nodoc:
  extend Singleton::Base
end
