require 'erb'
require 'yaml'

module SgtnClient

  module Core
    autoload :Cache,       "sgtn-client/core/cache"
  end
  
  class CacheUtil

      def self.get_cache(cache_key)
        if cache_key.nil?
            return nil, nil
        end
        expired, items = SgtnClient::Core::Cache.get(cache_key)
        SgtnClient.logger.debug "[CacheUtil]get cache with key #{cache_key}, expired #{expired}"
        return expired, items
      end

      def self.clear_cache()
        SgtnClient::Core::Cache.clear()
        SgtnClient.logger.debug "[CacheUtil]clear cache"
      end

      def self.write_cache(cache_key, items)
        if items.nil?
          return nil
        end
        env = SgtnClient::Config.default_environment
        cache_expiry_period = SgtnClient::Config.configurations[env]["cache_expiry_period"]
        # expired after 24 hours
        if cache_expiry_period == nil
            cache_expiry_period = 24*60
        end
        SgtnClient.logger.debug "[CacheUtil]write cache with key #{cache_key}, cache_expiry_period #{cache_expiry_period}, itmes #{items}"
        SgtnClient::Core::Cache.put(cache_key, items, cache_expiry_period)
      end

      def self.get_cachekey(component, locale)
        if locale.nil?
            return nil
        end
        env = SgtnClient::Config.default_environment
        product_name = SgtnClient::Config.configurations[env]["product_name"]
        version = SgtnClient::Config.configurations[env]["version"].to_s
        default_l = SgtnClient::Config.configurations[env]["default_language"]
        if default_l == nil
          default_l = 'en'
        end
        lc = locale == default_l ? SgtnClient::Config.configurations.default: locale
        SgtnClient.logger.debug "[CacheUtil]get cache key: #{lc}"
        return product_name + "_" + version + "_" + component + "_" + lc
      end
  end

end