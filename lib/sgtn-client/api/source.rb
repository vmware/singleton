
module SgtnClient
  
  autoload :CacheUtil,       "sgtn-client/util/cache-util"

  class Source

      def self.getSource(component, key, locale=SgtnClient::Config.configurations.default)
        SgtnClient.logger.debug "[Source][getSource]component=#{component}, key=#{key}, locale=#{locale}"
        cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
        expired, items = SgtnClient::CacheUtil.get_cache(cache_key)
        if items.nil?
          items = getBundle(component, locale)    
          SgtnClient::CacheUtil.write_cache(cache_key, items)
        else
          SgtnClient.logger.debug "[Source][getSource]getting sources from cache with key: " + cache_key
        end
        s = items.nil?? nil : items[locale][key]
        if items.nil? || s.nil?
          SgtnClient.logger.debug "[Source][getSource]source not found, return key: " + key
          #return key
          return nil
        else
          return s
        end
      end

      def self.getSources(component, locale=SgtnClient::Config.configurations.default)
        SgtnClient.logger.debug "[Source][getSources]component=#{component}, locale=#{locale}"
        cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
        expired, items = SgtnClient::CacheUtil.get_cache(cache_key)
        if items.nil? || expired
          items = getBundle(component, locale)
          SgtnClient::CacheUtil.write_cache(cache_key, items)
        else
          SgtnClient.logger.debug "[Source][getSources]getting sources from cache with key: " + cache_key
        end
        return items
      end

      def self.loadBundles(locale)
        SgtnClient.logger.debug "[Source][loadBundles]locale=#{locale}"
        if locale.nil?
          SgtnClient.logger.error "[Source][loadBundles]the input is nil!"
          return
        end
        env = SgtnClient::Config.default_environment
        SgtnClient::Config.configurations.default = locale
        source_bundle = SgtnClient::Config.configurations[env]["source_bundle"]
        Dir.foreach(source_bundle) do |component|
          next if component == '.' || component == '..'
          yamlfile = File.join(source_bundle, component + "/" + locale + ".yml")
          bundle = SgtnClient::FileUtil.read_yml(yamlfile)
          cachekey = SgtnClient::CacheUtil.get_cachekey(component, locale)
          SgtnClient::CacheUtil.write_cache(cachekey,bundle)
        end
      end

      private
      def self.getBundle(component, locale)
        SgtnClient.logger.debug "[Source][getBundle]component=#{component}, locale=#{locale}"
        env = SgtnClient::Config.default_environment
        source_bundle = SgtnClient::Config.configurations[env]["source_bundle"]
        bundlepath = source_bundle  + "/" + component + "/" + locale + ".yml"
        begin
          bundle = SgtnClient::FileUtil.read_yml(bundlepath)
        rescue => exception
          SgtnClient.logger.error exception.message
        end
        return bundle
      end

  end

end