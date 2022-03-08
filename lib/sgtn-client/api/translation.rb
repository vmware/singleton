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
        translation = getTranslation(component, key, locale)
        if translation.nil? && !LocaleUtil.is_source_locale(locale)
          translation = getTranslation(component, key, LocaleUtil.get_source_locale)
        end
        translation
      end

      def self.getString_p(component, key, plural_args, locale)
        SgtnClient.logger.debug "[Translation][getString_p]component=#{component}, key=#{key}, locale=#{locale}"
        translation = getTranslation(component, key, locale)
        if translation.nil?
          if !LocaleUtil.is_source_locale(locale)
            translation = getTranslation(component, key, LocaleUtil.get_source_locale)
            translation.to_plural_s(LocaleUtil.get_source_locale.to_sym, plural_args) if translation
          end
        else
          translation.to_plural_s(locale, plural_args)
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
        locale = SgtnClient::LocaleUtil.get_best_locale(locale)
        items = get_cs(component, locale)
        if (items.nil? || items["messages"].nil?) && !LocaleUtil.is_source_locale(locale)
          items = get_cs(component, LocaleUtil.get_source_locale)
        end

        return items
       end


      private

      def self.getTranslation(component, key, locale)
        locale = SgtnClient::LocaleUtil.get_best_locale(locale)
        items = get_cs(component, locale)
        items&.dig("messages", key)
      end

      def self.get_cs(component, locale)
        cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
        SgtnClient.logger.debug "[Translation][get_cs]cache_key=#{cache_key}"
        expired, items = SgtnClient::CacheUtil.get_cache(cache_key)
        if items.nil? || expired
          if items.nil?
            items = single_refresh(component, locale).value
          else
            if locale != LocaleUtil.get_source_locale # local source never expires.
              single_refresh(component, locale)
            end
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
        obj && obj["data"]
      end

    def self.load_and_compare_source(component, locale)
      source_cache_key = SgtnClient::CacheUtil.get_cachekey(component, LocaleUtil.get_source_locale)
      _, source_bundle = SgtnClient::CacheUtil.get_cache(source_cache_key)
      if source_bundle.nil? || source_bundle.empty?
        source_bundle = single_load(component, SgtnClient::Config.configurations.default).value
        SgtnClient::CacheUtil.write_cache(source_cache_key, source_bundle)
      end
      return source_bundle if LocaleUtil.is_source_locale(locale)

      translation_bundle_thread = single_load(component, locale)
      old_source_bundle_thread = single_load(component, LocaleUtil.get_source_locale)
      translation_bundle = translation_bundle_thread.value
      old_source_bundle = old_source_bundle_thread.value

      if translation_bundle.nil? || source_bundle.nil? || old_source_bundle.nil? ||
         translation_bundle.empty? || source_bundle.empty? || old_source_bundle.empty?
        return translation_bundle
      end

      source_messages = source_bundle.first[1]
      old_source_messages = old_source_bundle['messages']
      translation_messages = translation_bundle['messages']
      new_translation_messages = {}
      source_messages.each do |key, value|
        new_translation_messages[key] = if old_source_messages[key] == value
                                          translation_messages[key] || value
                                        else
                                          value
                                        end
      end
      translation_bundle['messages'] = new_translation_messages
      translation_bundle
    end

    @@load_threads_lock = Mutex.new
    @@load_threads = {}
    def self.single_load(component, locale)
      cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
      @@load_threads_lock.synchronize do
        thread = @@load_threads[cache_key]
        if thread.nil? || thread.alive? == false
          thread = Thread.new do
            if locale == SgtnClient::Config.configurations.default
              Source.getBundle(component, SgtnClient::Config.configurations.default)
            else
              load(component, locale)
            end
          end
          @@load_threads[cache_key] = thread
        end
        return thread
      end
    end

    @@refresh_threads_lock = Mutex.new
    @@refresh_threads = {}
    def self.single_refresh(component, locale)
      cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
      @@refresh_threads_lock.synchronize do
        thread = @@refresh_threads[cache_key]
        if thread.nil? || thread.alive? == false
          expired, items = SgtnClient::CacheUtil.get_cache(cache_key)
          if expired || items.nil?
            thread = Thread.new do
              items = load_and_compare_source(component, locale)
              SgtnClient::CacheUtil.write_cache(cache_key, items) if items&.empty? == false
              items
            end
            @@refresh_threads[cache_key] = thread
          end
        end
        return thread
      end
    end

    private_class_method :load, :load_o, :load_s, :load_and_compare_source, :single_load, :single_refresh
  end
end
