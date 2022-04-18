# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'request_store'
require_relative 'singleton-ruby'

module Singleton # :nodoc:
  module Base # :nodoc:
    # load configuration from a file
    def load_config(*args)
      SgtnClient.load(*args)
    end

    def translate(key, component, locale: nil, **kwargs, &block)
      translate!(key, component, locale: locale, **kwargs, &block)
    rescue StandardError => e
      SgtnClient.logger.error "couldn't translate #{key}, #{component}, #{locale}, #{kwargs}. error: #{e}"
      key
    end
    alias t translate

    # raise error when translation is not found
    def translate!(key, component, locale: nil, **kwargs)
      SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] key: #{key}, component: #{component}, locale: #{locale}, args: #{kwargs}"

      locale = locale.nil? ? self.locale : SgtnClient::LocaleUtil.get_best_locale(locale)

      result = get_bundle(component, locale)&.fetch(key.to_s, nil)
      result = yield if result.nil? && block_given?
      raise SgtnClient::SingletonError, 'translation is missing' if result.nil?

      if kwargs.empty?
        result
      else
        locale = result.locale if result.is_a?(StringUtil)
        result.localize(locale) % kwargs
      end
    end
    alias t! translate!

    def get_translations(component, locale: nil)
      get_translations!(component, locale: locale)
    rescue StandardError => e
      SgtnClient.logger.error "couldn't get translations for component: #{component}, locale: #{locale}. error: #{e}"
      { 'component' => component, 'locale' => locale, 'messages' => {} }
    end

    def get_translations!(component, locale: nil)
      SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] component: #{component}, locale: #{locale}"

      locale = locale.nil? ? self.locale : SgtnClient::LocaleUtil.get_best_locale(locale)

      { 'component' => component, 'locale' => locale, 'messages' => get_bundle(component, locale) || {} }
    end

    def locale
      RequestStore.store[:locale] ||= SgtnClient::LocaleUtil.get_fallback_locale
    end

    def locale=(value)
      RequestStore.store[:locale] = SgtnClient::LocaleUtil.get_best_locale(value)
    end

    private

    def get_bundle(component, locale)
      id = SgtnClient::Common::BundleID.new(component, locale)
      bundles = SgtnClient::Config.available_bundles
      raise SgtnClient::SingletonError, "bundle is unavailable. #{id}" unless bundles.nil? || bundles.empty? || bundles.include?(id)

      SgtnClient::Config.loader.get_bundle(component, locale)
    end
  end

  extend Base
end
