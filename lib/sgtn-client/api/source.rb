# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'sgtn-client/loader/source'

module SgtnClient
  autoload :CacheUtil, 'sgtn-client/util/cache-util'

  class Source
    def self.loadBundles(locale)
      SgtnClient.logger.debug "[Source][loadBundles]locale=#{locale}"
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations.default = locale
      source_bundle = SgtnClient::Config.configurations[env]['source_bundle']
      Dir.foreach(source_bundle) do |component|
        next if %w[. ..].include? component

        bundle = SgtnClient::TranslationLoader::Source.load_bundle(component)
        cachekey = SgtnClient::CacheUtil.get_cachekey(component, LocaleUtil.get_source_locale)
        SgtnClient::CacheUtil.write_cache(cachekey, bundle)
      end
    end
  end
end
