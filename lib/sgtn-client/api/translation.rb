# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'request_store'

module SgtnClient
  autoload :StringUtil, 'sgtn-client/util/string-util'

  module Translation
    module Implementation
      # <b>DEPRECATED:</b> Please use <tt>Singleton:translate</tt> instead.
      def getString(component, key, locale)
        SgtnClient.logger.debug "[Translation.getString]component: #{component}, key: #{key}, locale: #{locale}"
        translate(key, component, locale: locale) { nil }
      end

      # <b>DEPRECATED:</b> Please use <tt>Singleton:translate</tt> instead.
      def getString_p(component, key, plural_args, locale)
        SgtnClient.logger.debug "[Translation][getString_p]component=#{component}, key=#{key}, locale=#{locale}"
        translate(key, component, locale: locale, **plural_args) { nil }
      end

      # <b>DEPRECATED:</b> Please use <tt>Singleton:translate</tt> instead.
      def getString_f(component, key, args, locale, *optionals)
        SgtnClient.logger.debug "[Translation][getString_f]component=#{component}, key=#{key}, locale=#{locale}"
        s = translate(key, component, locale: locale) { nil }
        return nil if s.nil?

        if args.is_a?(Hash)
          args.each do |source, arg|
            s.gsub! "{#{source}}", arg
          end
        elsif args.is_a?(Array)
          s = s % args
        end
        s
      end

      # <b>DEPRECATED:</b> Please use <tt>Singleton:get_translations</tt> instead.
      def getStrings(component, locale)
        warn '[DEPRECATION] `getStrings` is deprecated.  Please use `Singleton:get_translations` instead.'
        get_translations(component, locale)
      end

      def translate(key, component, locale: nil, **kwargs, &block)
        translate!(key, component, locale: locale, **kwargs, &block)
      rescue StandardError => e
        SgtnClient.logger.debug "couldn't translate #{key}, #{component}, #{locale}, #{kwargs}. error: #{e}"
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
          locale = result.locale if result.is_a?(SgtnClient::StringUtil)
          result.localize(locale) % kwargs
        end
      end
      alias t! translate!

      def get_translations(component, locale = nil)
        get_translations!(component, locale)
      rescue StandardError => e
        SgtnClient.logger.error "couldn't get translations for component: #{component}, locale: #{locale}. error: #{e}"
        { 'component' => component, 'locale' => locale, 'messages' => {} }
      end

      def get_translations!(component, locale = nil)
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

      def getTranslation(component, key, locale)
        locale = SgtnClient::LocaleUtil.get_best_locale(locale)
        items = get_cs(component, locale)
        items&.fetch(key, nil)
      end

      def get_cs(component, locale)
        get_bundle(component, locale)
      rescue StandardError => e
        SgtnClient.logger.error "[#{method(__callee__).owner}.#{__callee__}] failed to get a bundle. component: #{component}, locale: #{locale}"
        SgtnClient.logger.error e
        nil
      end

      def get_bundle(component, locale)
        id = SgtnClient::Common::BundleID.new(component, locale)
        bundles = SgtnClient::Config.available_bundles
        unless bundles.nil? || bundles.empty? || bundles.include?(id)
          raise SgtnClient::SingletonError, 'bundle is unavailable.'
        end

        SgtnClient::Config.loader.get_bundle(component, locale)
      end
    end
    extend Implementation
  end
end
