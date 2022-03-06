# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'multi_json'

module SgtnClient

  module Core
    autoload :Request,     "sgtn-client/core/request"
    autoload :Cache,       "sgtn-client/core/cache"
    autoload :CacheUtil,   "sgtn-client/util/cache-util"
    autoload :LocaleUtil,   "sgtn-client/util/locale-util"
  end
  
  class Translation

      def self.getString(component, key, locale)
        SgtnClient.logger.debug "[Translation.getString]component: #{component}, key: #{key}, locale: #{locale}"
        str = getTranslation(component, key, locale)
        if str.nil?
          str = SgtnClient::Source.getSource(component, key, SgtnClient::Config.configurations.default)
          if str.nil?
            SgtnClient.logger.debug "[Translation][getString] Missing source string with key: #{key}, component: #{component}, locale: #{locale}"
          end
        end
        str
      end

      def self.getString_p(component, key, plural_args, locale)
        SgtnClient.logger.debug "[Translation][getString_p]component=#{component}, key=#{key}, locale=#{locale}"
        str = getTranslation(component, key, locale)
        if str.nil?
          str = SgtnClient::Source.getSource(component, key, SgtnClient::Config.configurations.default)
          if str.nil?
            SgtnClient.logger.debug "[Translation][getString_p] Missing source string with key: #{key}, component: #{component}, locale: #{locale}"
            return nil
          end
          str.to_plural_s(:en, plural_args)
        else
          str.to_plural_s(locale, plural_args)
        end
      end

      def self.getString_f(component, key, args, locale, *optionals)
         SgtnClient.logger.debug "[Translation][getString_f]component=#{component}, key=#{key}, locale=#{locale}"
         s = getString(component, key, locale, *optionals)
         if s.nil?
          return nil
         end
         if args.is_a?(Hash)
          args.each do |source, arg|
            s.gsub! "{#{source}}", arg
          end
         elsif args.is_a?(Array)
          s = sprintf s % args
         end
         return s
      end

      def self.getStrings(component, locale)
        SgtnClient.logger.debug "[Translation][getStrings]component=#{component}, locale=#{locale}"
        items = get_cs(component, locale)
        default = SgtnClient::Config.configurations.default
        if items.nil? || items["messages"] == nil
          items = {}
          s = SgtnClient::Source.getSources(component, default)
          if s.nil?
            SgtnClient.logger.error "[Translation][getStrings] Missing component: #{component}, locale: #{locale}"
          else
            default_component, value = s.first
            items["component"] = component
            items["messages"] = value
            items["locale"] = 'source'
          end
        end
        return items
       end


      private

      def self.getTranslation(component, key, locale)
        items = get_cs(component, locale)
        if items.nil? || items["messages"] == nil
          nil
        else
          items["messages"][key]
        end
      end

      def self.get_cs(component, locale)
        locale = SgtnClient::LocaleUtil.process_locale(locale)
        flocale = SgtnClient::LocaleUtil.fallback(locale)
        cache_key = SgtnClient::CacheUtil.get_cachekey(component, flocale)
        SgtnClient.logger.debug "[Translation][get_cs]cache_key=#{cache_key}"
        expired, items = SgtnClient::CacheUtil.get_cache(cache_key)
        if items.nil? || expired
          items = load(component, flocale)
          if items.nil?
            items = SgtnClient::Source.getSources(component, SgtnClient::Config.configurations.default)
            SgtnClient::Core::Cache.put(cache_key, items, 60)
          else
            if locale != SgtnClient::LocaleUtil.get_source_locale && locale != SgtnClient::Config.configurations.default
              self.compare_source(component, items) 
            end
            SgtnClient::CacheUtil.write_cache(cache_key, items)
          end
        else
          SgtnClient.logger.debug "[Translation]get translations from cache with key: " + cache_key
        end

        return items
       end

      def self.load(component, locale)
        env = SgtnClient::Config.default_environment
        mode = SgtnClient::Config.configurations[env]["bundle_mode"]
        SgtnClient.logger.debug "[Translation][load]mode=#{mode}"
        if mode == 'offline'
          return load_o(component, locale)
        else
          return load_s(component, locale)
        end
      end

      def self.load_o(component, locale)
        env = SgtnClient::Config.default_environment
        product_name = SgtnClient::Config.configurations[env]["product_name"]
        version = SgtnClient::Config.configurations[env]["version"].to_s
        translation_bundle = SgtnClient::Config.configurations[env]["translation_bundle"]
        bundlepath = translation_bundle + "/" + product_name + "/" + version + "/" + component + "/messages_" + locale + ".json"
        SgtnClient::FileUtil.read_json(bundlepath)
      end

      def self.load_s(component, locale)
        env = SgtnClient::Config.default_environment
        product_name = SgtnClient::Config.configurations[env]["product_name"]
        vip_server = SgtnClient::Config.configurations[env]["vip_server"]
        version = SgtnClient::Config.configurations[env]["version"].to_s
        url = vip_server + "/i18n/api/v2/translation/products/" + product_name + "/versions/" + version + "/locales/" + locale + "/components/" + component+ "?checkTranslationStatus=false&machineTranslation=false&pseudo=false"
        begin
          obj = SgtnClient::Core::Request.get(url)
        rescue => exception
          SgtnClient.logger.error exception.message
        end
        if obj != nil
          obj = obj["data"]
        end
        return obj
      end
      
      def self.compare_source(component, translations)        
        sources, old_sources = self.get_bundles_for_source_comparison(component)
        self.compare_each_source_key(translations, sources, old_sources)
      end

      def self.get_bundles_for_source_comparison(component)
        old_sources = get_cs(component, SgtnClient::LocaleUtil.get_source_locale)
        sources = SgtnClient::Source.getSources(component, SgtnClient::Config.configurations.default)
        return sources, old_sources
      end

      def self.compare_each_source_key(translations, sources, old_sources)
        source_bundle_key = SgtnClient::Config.configurations.default
        translations["messages"].each do |message| 
          key = message[0]
          source = sources[source_bundle_key][key] if sources[source_bundle_key] != nil
          old_source = old_sources["messages"][key] if old_sources["messages"] != nil
          if source != nil and old_source != nil and source != old_source
            SgtnClient.logger.debug "[#{self.class}][#{__method__}] Source is used instead of translation for key=#{key} source=#{source}, translation=#{translations["messages"][key]}"
            translations["messages"][key] = source 
          end
        end
      end

  end

end