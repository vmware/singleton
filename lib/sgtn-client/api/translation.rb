# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :StringUtil, 'sgtn-client/util/string-util'

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
      if items.nil? && !LocaleUtil.is_source_locale(locale)
        items = get_cs(component, LocaleUtil.get_source_locale)
        locale = LocaleUtil.get_source_locale
      end

      { 'component' => component, 'locale' => locale, 'messages' => items || {} } if items
    end

    def self.getTranslation(component, key, locale)
      locale = SgtnClient::LocaleUtil.get_best_locale(locale)
      items = get_cs(component, locale)
      items&.fetch(key, nil)
    end

    def self.get_cs(component, locale)
      SgtnClient::Config.loader.get_bundle(component, locale)
    end

    private_class_method :getTranslation, :get_cs
  end
end
