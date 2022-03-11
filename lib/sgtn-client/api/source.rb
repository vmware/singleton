# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :CacheUtil,       'sgtn-client/util/cache-util'

  class Source
    def self.loadBundles(locale)
      SgtnClient.logger.debug "[Source][loadBundles]locale=#{locale}"
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations.default = locale
      source_bundle = SgtnClient::Config.configurations[env]['source_bundle']
      Dir.foreach(source_bundle) do |component|
        next if component == '.' || component == '..'

        getBundle(component)
      end
    end

    def self.getBundle(component)
      locale = SgtnClient::Config.configurations.default
      SgtnClient.logger.debug "[Source][getBundle]component=#{component}, locale=#{locale}"
      env = SgtnClient::Config.default_environment
      source_bundle = SgtnClient::Config.configurations[env]['source_bundle']
      bundlepath = source_bundle + '/' + component + '/' + locale + '.yml'
      begin
        bundle = SgtnClient::FileUtil.read_yml(bundlepath)
        return { 'component' => component, 'locale' => locale, 'messages' => bundle&.first&.last }
      rescue StandardError => e
        SgtnClient.logger.error e.message
      end
      nil
    end
  end
end
