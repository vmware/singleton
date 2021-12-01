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
        str = getTranslation(component, key, locale)
        if str.nil?
          str = SgtnClient::Source.getSource(component, key, SgtnClient::Config.configurations.default)
        end
        str
      end

      def self.getString_p(component, key, plural_args, locale)
        str = getTranslation(component, key, locale)
        if str.nil?
          str = SgtnClient::Source.getSource(component, key, SgtnClient::Config.configurations.default)
          str.to_plural_s(:en, plural_args)
        else
          str.to_plural_s(locale, plural_args)
        end
      end

      def self.getString_f(component, key, args, locale, *optionals)
         s = getString(component, key, locale, *optionals)
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
        flocale = SgtnClient::LocaleUtil.fallback(locale)
        cache_key = SgtnClient::CacheUtil.get_cachekey(component, flocale)
        items = SgtnClient::CacheUtil.get_cache(cache_key)
        if items.nil?
          items = getTranslations(component, flocale)
          SgtnClient::CacheUtil.write_cache(cache_key, items)
        else
          SgtnClient.logger.debug "Getting translations from cache with key: " + cache_key
        end

        default = SgtnClient::Config.configurations.default
        if items.nil? || items["messages"] == nil
          items = {}
          s = SgtnClient::Source.getSources(component, default)
          default_component, value = s.first
          items["component"] = component
          items["messages"] = value
          items["locale"] = 'source'
        end
        return items
       end


      private

      def self.getTranslation(component, key, locale)
        flocale = SgtnClient::LocaleUtil.fallback(locale)
        cache_key = SgtnClient::CacheUtil.get_cachekey(component, flocale)
        items = SgtnClient::CacheUtil.get_cache(cache_key)
        if items.nil?
          items = getTranslations(component, flocale)
          SgtnClient::CacheUtil.write_cache(cache_key, items)
        else
          SgtnClient.logger.debug "Getting translations from cache with key: " + cache_key
        end
        if items.nil? || items["messages"] == nil
          nil
        else
          items["messages"][key]
        end
      end
      
      def self.getTranslations(component, locale)
        env = SgtnClient::Config.default_environment
        mode = SgtnClient::Config.configurations[env]["bundle_mode"]
        if mode == 'offline'
          return get_offbundle(component, locale)
        else
          return get_server(component, locale)
        end
      end

      def self.get_offbundle(component, locale)
        env = SgtnClient::Config.default_environment
        product_name = SgtnClient::Config.configurations[env]["product_name"]
        version = SgtnClient::Config.configurations[env]["version"].to_s
        translation_bundle = SgtnClient::Config.configurations[env]["translation_bundle"]
        bundlepath = translation_bundle + "/" + product_name + "/" + version + "/" + component + "/messages_" + locale + ".json"
        SgtnClient.logger.debug "Getting translations from offline bundle: " + bundlepath
        begin
          file = File.read(bundlepath)
          data_hash = MultiJson.load(file)
        rescue => exception
          SgtnClient.logger.error exception.message
        end
        return data_hash
      end

      def self.get_server(component, locale)
        env = SgtnClient::Config.default_environment
        product_name = SgtnClient::Config.configurations[env]["product_name"]
        vip_server = SgtnClient::Config.configurations[env]["vip_server"]
        SgtnClient.logger.debug "Getting translations from server: " + vip_server
        version = SgtnClient::Config.configurations[env]["version"].to_s
        url = vip_server + "/i18n/api/v2/translation/products/" + product_name + "/versions/" + version + "/locales/" + locale + "/components/" + component+ "?checkTranslationStatus=false&machineTranslation=false&pseudo=false"
        SgtnClient.logger.debug url
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

  end

end