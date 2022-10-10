# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'request_store'

module SgtnClient
  module Translation
    module Implementation
      # <b>DEPRECATED:</b> Please use <tt>Sgtn:translate</tt> instead.
      def getString(component, key, locale)
        SgtnClient.logger.debug { "[Translation.getString]component: #{component}, key: #{key}, locale: #{locale}" }
        translate(key, component, locale) { nil }
      end

      # <b>DEPRECATED:</b> Please use <tt>Sgtn:translate</tt> instead.
      def getString_p(component, key, plural_args, locale)
        SgtnClient.logger.debug { "[Translation][getString_p]component=#{component}, key=#{key}, locale=#{locale}" }
        translate(key, component, locale, **plural_args) { nil }
      end

      # <b>DEPRECATED:</b> Please use <tt>Sgtn:translate</tt> instead.
      def getString_f(component, key, args, locale, *_optionals)
        SgtnClient.logger.debug { "[Translation][getString_f]component=#{component}, key=#{key}, locale=#{locale}" }
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

      def get_translation!(key, component, locale)
        [get_bundle!(component, locale)[key], locale]
      end

      def translate(key, component, locale = nil, **kwargs, &block)
        translate!(key, component, locale, **kwargs, &block)
      rescue StandardError => e
        SgtnClient.logger.debug { "[#{method(__callee__).owner}.#{__callee__}] {#{key}, #{component}, #{locale}}. #{e}" }
        key
      end
      alias t translate

      # raise error when translation is not found
      def translate!(key, component, locale = nil, **kwargs, &block)
        SgtnClient.logger.debug { "[#{method(__callee__).owner}.#{__callee__}] key: #{key}, component: #{component}, locale: #{locale}, args: #{kwargs}" }

        begin
          best_match_locale = LocaleUtil.get_best_locale(locale || SgtnClient.locale, component)
          result, actual_locale = get_translation!(key, component, best_match_locale)
        rescue StandardError => e
          raise e if block.nil?
        end
        if result.nil?
          raise SingletonError, 'translation is missing.' if block.nil?

          result = block.call
          return if result.nil?
        end

        kwargs.empty? ? result : result.localize(actual_locale) % kwargs
      end
      alias t! translate!

      def get_translations(component, locale = nil)
        get_translations!(component, locale)
      rescue StandardError => e
        SgtnClient.logger.error "[#{method(__callee__).owner}.#{__callee__}] {#{component}, #{locale}}. #{e}"
        nil
      end

      def get_translations!(component, locale = nil)
        SgtnClient.logger.debug { "[#{method(__callee__).owner}.#{__callee__}] component: #{component}, locale: #{locale}" }

        best_match_locale = LocaleUtil.get_best_locale(locale || SgtnClient.locale, component)
        messages = get_bundle!(component, best_match_locale)

        { 'component' => component, 'locale' => best_match_locale, 'messages' => messages } if messages
      end

      private

      def get_bundle!(component, locale)
        SgtnClient.config.loader.get_bundle(component, locale)
      rescue StandardError
        # delete the locale from the available_bundles of component to avoid repeated calls to server
        SgtnClient.config.available_bundles.delete(Common::BundleID.new(component, locale))
        SgtnClient.config.available_locales(component)&.delete(locale)
        SgtnClient.config.changed
        SgtnClient.config.notify_observers(:available_locales, component)
        raise
      end
    end

    extend Implementation
    extend Fallbacks
  end
end
