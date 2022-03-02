# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'multi_json'

module SgtnClient

  # A service util class to serve for translation API
  class ServiceUtil
    class << self

      # Get key_translation
      def get_translation(component, key, locale)
        locale = SgtnClient::LocaleUtil.get_best_locale(locale)
        items = get_cs(component, locale)
        if items.nil? || items["messages"].nil?
          nil
        else
          items["messages"][key]
        end
      end

      # Compare local source with remote source
      def compare_source(component, key, source, translation)
        source_locale = SgtnClient::LocaleUtil.get_source_locale
        SgtnClient.logger.debug "[ServiceUtil][compare_source]component=#{component},key=#{key},default_locale=#{source_locale},source=#{source},translation=#{translation}"
        items = get_cs(component, source_locale)
        if items.nil? || items["messages"].nil?
          translation
        else
          target = items["messages"][key]
          source == target ? translation : source
        end
      end

      # Get component-translations
      def get_cs(component, locale)
        cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
        SgtnClient.logger.debug "[ServiceUtil][get_cs]cache_key=#{cache_key}"
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
          SgtnClient.logger.debug "[ServiceUtil]get translations from cache with key: " + cache_key
        end

        return items
       end

      # Load component-translation
      def load(component, locale)
        env = SgtnClient::Config.default_environment
        mode = SgtnClient::Config.configurations[env]["bundle_mode"]
        SgtnClient.logger.debug "[ServiceUtil][load]mode=#{mode}"
        if mode == 'offline'
          return load_o(component, locale)
        else
          return load_s(component, locale)
        end
      end

      # Load component-translation from offline
      def load_o(component, locale)
        env = SgtnClient::Config.default_environment
        product_name = SgtnClient::Config.configurations[env]["product_name"]
        version = SgtnClient::Config.configurations[env]["version"].to_s
        translation_bundle = SgtnClient::Config.configurations[env]["translation_bundle"]
        bundlepath = translation_bundle + "/" + product_name + "/" + version + "/" + component + "/messages_" + locale + ".json"
        SgtnClient::FileUtil.read_json(bundlepath)
      end

      # Load component-translation from online
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
        if obj
          obj = obj["data"]
        end
        return obj
      end

    end
  end
end