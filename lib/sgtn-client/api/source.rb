# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

module SgtnClient
  
  autoload :CacheUtil,       "sgtn-client/util/cache-util"

  class Source

      def self.loadBundles(locale)
        SgtnClient.logger.debug "[Source][loadBundles]locale=#{locale}"
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
