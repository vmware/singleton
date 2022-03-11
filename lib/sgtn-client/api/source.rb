# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  
  autoload :CacheUtil,       "sgtn-client/util/cache-util"
  autoload :Concurrent,      'current'

  class Source

      def self.loadBundles(locale)
        SgtnClient.logger.debug "[Source][loadBundles]locale=#{locale}"
        env = SgtnClient::Config.default_environment
        SgtnClient::Config.configurations.default = locale
        source_bundle = SgtnClient::Config.configurations[env]["source_bundle"]
        Dir.foreach(source_bundle) do |component|
          next if component == '.' || component == '..'
          getBundle(component)
        end
      end

      @source_bundles = Concurrent::Hash.new
      def self.getBundle(component)
        bundle = @source_bundles[component]
        return bundle if bundle

        locale = SgtnClient::Config.configurations.default
        SgtnClient.logger.debug "[Source][getBundle]component=#{component}, locale=#{locale}"
        env = SgtnClient::Config.default_environment
        source_bundle = SgtnClient::Config.configurations[env]["source_bundle"]
        bundlepath = source_bundle  + "/" + component + "/" + locale + ".yml"
        begin
          bundle = SgtnClient::FileUtil.read_yml(bundlepath)
          @source_bundles[component] = { 'component' => component, 'locale' => locale, 'messages' => bundle&.first&.last }
        rescue => exception
          SgtnClient.logger.error exception.message
        end
        return bundle
      end
      
      private_class_method :getBundle
  end
end
