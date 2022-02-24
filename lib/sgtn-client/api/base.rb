require 'multi_json'

module SgtnClient

  class Base
    class << self

      def getTranslation(component, key, locale)
        locale = fallback_locale(locale)
        items = get_cs(component, locale)
        if items.nil? || items["messages"].nil?
          nil
        else
          items["messages"][key]
        end
      end

      def compareSource(component, key, default_locale, source, translation)
        SgtnClient.logger.debug "[Base][compareSource]component=#{component},key=#{key},default_locale=#{default_locale},source=#{source},translation=#{translation}"
        items = get_cs(component, default_locale)
        if items.nil? || items["messages"] == nil
          translation
        else
          target = items["messages"][key]
          source == target ? translation : source
        end
      end

      def fallback_locale(locale)
        locale = SgtnClient::LocaleUtil.process_locale(locale)
        flocale = SgtnClient::LocaleUtil.fallback(locale)
        flocale = SgtnClient::Config.configurations.default if flocale == SgtnClient::LocaleUtil.look_default()
        return flocale
      end

      def get_cs(component, locale)
        cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
        SgtnClient.logger.debug "[Base][get_cs]cache_key=#{cache_key}"
        expired, items = SgtnClient::CacheUtil.get_cache(cache_key)
        if items.nil? || expired
          items = load(component, locale)
          if items.nil?
            items = SgtnClient::Source.getSources(component, SgtnClient::Config.configurations.default)
            SgtnClient::Core::Cache.put(cache_key, items, 60)
          else
            SgtnClient::CacheUtil.write_cache(cache_key, items)
          end
        else
          SgtnClient.logger.debug "[Base]get translations from cache with key: " + cache_key
        end

        return items
       end

      def load(component, locale)
        env = SgtnClient::Config.default_environment
        mode = SgtnClient::Config.configurations[env]["bundle_mode"]
        SgtnClient.logger.debug "[Base][load]mode=#{mode}"
        if mode == 'offline'
          return load_o(component, locale)
        else
          return load_s(component, locale)
        end
      end

      def load_o(component, locale)
        env = SgtnClient::Config.default_environment
        product_name = SgtnClient::Config.configurations[env]["product_name"]
        version = SgtnClient::Config.configurations[env]["version"].to_s
        translation_bundle = SgtnClient::Config.configurations[env]["translation_bundle"]
        bundlepath = translation_bundle + "/" + product_name + "/" + version + "/" + component + "/messages_" + locale + ".json"
        SgtnClient::FileUtil.read_json(bundlepath)
      end

      def load_s(component, locale)
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
        if !obj.nil?
          obj = obj["data"]
        end
        return obj
      end

    end
  end
end