# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'request_store'

module SgtnClient
  module Translation
    module Implementation
      # <b>DEPRECATED:</b> Please use <tt>Sgtn:translate</tt> instead.
      def getString(component, key, locale)
        SgtnClient.logger.debug "[Translation.getString]component: #{component}, key: #{key}, locale: #{locale}"
        translate(key, component, locale) { nil }
      end

      # <b>DEPRECATED:</b> Please use <tt>Sgtn:translate</tt> instead.
      def getString_p(component, key, plural_args, locale)
        SgtnClient.logger.debug "[Translation][getString_p]component=#{component}, key=#{key}, locale=#{locale}"
        translate(key, component, locale, **plural_args) { nil }
      end

      # <b>DEPRECATED:</b> Please use <tt>Sgtn:translate</tt> instead.
      def getString_f(component, key, args, locale, *_optionals)
        SgtnClient.logger.debug "[Translation][getString_f]component=#{component}, key=#{key}, locale=#{locale}"
        s = translate(key, component, locale) { nil }
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

      # <b>DEPRECATED:</b> Please use <tt>Sgtn:get_translations</tt> instead.
      def getStrings(component, locale)
        get_translations(component, locale)
      end

      def translate(key, component, locale = nil, **kwargs)
        SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] key: #{key}, component: #{component}, locale: #{locale}, args: #{kwargs}"

        begin
          locale = SgtnClient::LocaleUtil.get_best_locale(locale || self.locale, component)

          result = get_bundle(component, locale)&.fetch(key, nil)
        rescue StandardError => e
          SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] translation is missing. {#{key}, #{component}, #{locale}}. #{e}"
          result = nil
        end

        if result.nil?
          return key unless block_given?

          result = yield
          return if result.nil?
        end

        if kwargs.empty?
          result
        else
          locale = result.locale if result.is_a?(SgtnClient::StringUtil)
          result.localize(locale) % kwargs
        end
      end
      alias t translate

      def get_translations(component, locale = nil)
        SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] component: #{component}, locale: #{locale}"

        locale = SgtnClient::LocaleUtil.get_best_locale(locale || self.locale, component)
        items = get_bundle(component, locale)

        { 'component' => component, 'locale' => locale, 'messages' => items } if items
      rescue StandardError => e
        SgtnClient.logger.error "[#{method(__callee__).owner}.#{__callee__}] translation missing. {#{component}, #{locale}}. #{e}"
        nil
      end

      def locale
        RequestStore.store[:locale] ||= SgtnClient::LocaleUtil.get_fallback_locale
      end

      def locale=(value)
        RequestStore.store[:locale] = value
      end

      private

      def get_bundle(component, locale)
        get_bundle!(component, locale)
      rescue StandardError => e
        SgtnClient.logger.error "[#{method(__callee__).owner}.#{__callee__}] failed to get a bundle. component: #{component}, locale: #{locale}"
        SgtnClient.logger.error e
        nil
      end

      def get_bundle!(component, locale)
        SgtnClient::Config.loader.get_bundle(component, locale)
      rescue StandardError
        # delete the locale from the available_bundles of component to avoid repeated calls to server
        bundles = SgtnClient::Config.available_bundles
        bundles.delete(SgtnClient::Common::BundleID.new(component, locale))
        SgtnClient::Config.available_locales(component)&.delete(locale)
        raise
      end
    end

    extend Implementation
  end
end
