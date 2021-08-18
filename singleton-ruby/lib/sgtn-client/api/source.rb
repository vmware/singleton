require 'erb'
require 'yaml'

module SgtnClient

  autoload :CacheUtil,       "sgtn-client/util/cache-util"

  class Source

      def self.getSource(component, key, locale)
        cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
        items = SgtnClient::CacheUtil.get_cache(cache_key)
        if items.nil?
          items = getBundle(component, locale)
          SgtnClient.logger.debug "Putting sources items into cache with key: " + cache_key
          SgtnClient::CacheUtil.write_cache(cache_key, items)
        else
          SgtnClient.logger.debug "Getting sources from cache with key: " + cache_key
        end
        if items.nil?
          return key
        end
        str = items[locale][key]
        return str
      end

      def self.loadBundles(locale)
        env = SgtnClient::Config.default_environment
        source_bundle = SgtnClient::Config.configurations[env]["source_bundle"]
        SgtnClient.logger.debug "Loading [" + locale + "] bundles from path: " + source_bundle
        Dir.children(source_bundle).each do |component|
          yamlfile = File.join(source_bundle, component + "/" + locale + ".yml")
          bundle = read_yml(yamlfile)
          cachekey = SgtnClient::CacheUtil.get_cachekey(component, locale)
          SgtnClient::CacheUtil.write_cache(cachekey,bundle)
        end

      end

      private

      def self.getBundle(component, locale)
        env = SgtnClient::Config.default_environment
        source_bundle = SgtnClient::Config.configurations[env]["source_bundle"]
        bundlepath = source_bundle  + "/" + component + "/" + locale + ".yml"
        SgtnClient.logger.debug "Getting source from  bundle: " + bundlepath
        begin
          bundle = read_yml(bundlepath)
        rescue => exception
          SgtnClient.logger.error exception.message
        end
        return bundle
      end

      def self.read_yml(file_name)
        erb = ERB.new(File.read(file_name))
        erb.filename = file_name
        YAML.load(erb.result)
      end

  end

end