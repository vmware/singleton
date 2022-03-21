# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :StringUtil, 'sgtn-client/util/string-util'

  module TranslationData
    autoload :LocalBundle, 'sgtn-client/loader/local_bundle'
    autoload :ServerBundle, 'sgtn-client/loader/server_bundle'
    autoload :SourceComparer, 'sgtn-client/loader/source_comparer'
    autoload :SingleLoader, 'sgtn-client/loader/single_loader'
    autoload :Cache, 'sgtn-client/loader/cache'
  end

  module Translation
    def self.getString(component, key, locale)
      SgtnClient.logger.debug "[Translation.getString]component: #{component}, key: #{key}, locale: #{locale}"
      str = getTranslation(component, key, locale)
      if str.nil? && !LocaleUtil.is_source_locale(locale)
        str = getTranslation(component, key, LocaleUtil.get_source_locale)
      end
      str
    end

    def self.getString_p(component, key, plural_args, locale)
      SgtnClient.logger.debug "[Translation][getString_p]component=#{component}, key=#{key}, locale=#{locale}"
      str = getTranslation(component, key, locale)
      if str.nil?
        unless LocaleUtil.is_source_locale(locale)
          str = getTranslation(component, key, LocaleUtil.get_source_locale)
          str.to_plural_s(LocaleUtil.get_source_locale, plural_args) if str
        end
      else
        locale = str.locale if str.is_a?(SgtnClient::StringUtil)
        str.to_plural_s(locale, plural_args)
      end
    end

    def self.getString_f(component, key, args, locale, *optionals)
      SgtnClient.logger.debug "[Translation][getString_f]component=#{component}, key=#{key}, locale=#{locale}"
      s = getString(component, key, locale, *optionals)
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

    def self.getStrings(component, locale)
      SgtnClient.logger.debug "[Translation][getStrings]component=#{component}, locale=#{locale}"
      locale = SgtnClient::LocaleUtil.get_best_locale(locale)
      items = get_cs(component, locale)
      if (items.nil? || items['messages'].nil?) && !LocaleUtil.is_source_locale(locale)
        items = get_cs(component, LocaleUtil.get_source_locale)
      end

      items
    end

    def self.getTranslation(component, key, locale)
      locale = SgtnClient::LocaleUtil.get_best_locale(locale)
      items = get_cs(component, locale)
      items&.dig('messages', key)
    end

    def self.get_cs(component, locale)
      cache_item = load_bundle(component, locale)
      cache_item&.dig(:items)
    end

    def self.load_bundle(component, locale)
      init_translations unless initialized?
      super
    end

    class << self
      def initialized?
        @initialized ||= false
      end

      def init_translations
        # TODO: Lock to initialize?
        env = SgtnClient::Config.default_environment
        mode = SgtnClient::Config.configurations[env]['bundle_mode']
        SgtnClient.logger.debug "[Translation][init_translations]mode=#{mode}"

        if mode == 'offline'
          extend SgtnClient::TranslationData::LocalBundle
        else
          extend SgtnClient::TranslationData::ServerBundle
        end

        extend SgtnClient::TranslationData::SourceComparer
        extend SgtnClient::TranslationData::SingleLoader
        extend SgtnClient::TranslationData::Cache

        load_translations
        @initialized = true
      end

      def load_translations; end
    end

    private_class_method :getTranslation, :get_cs, :load_bundle, :initialized?, :load_translations
  end
end
