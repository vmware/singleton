# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  class Source
    def self.load_bundle(component)
      locale = SgtnClient::Config.configurations.default
      SgtnClient.logger.debug "[Source][getBundle]component=#{component}, locale=#{locale}"
      env = SgtnClient::Config.default_environment
      source_bundle = SgtnClient::Config.configurations[env]['source_bundle']
      bundlepath = source_bundle + '/' + component + '/' + locale + '.yml'
      begin
        bundle = SgtnClient::FileUtil.read_yml(bundlepath)
        return { 'component' => component, 'locale' => LocaleUtil.get_source_locale, 'messages' => bundle&.first&.last }
      rescue StandardError => e
        SgtnClient.logger.error e.message
      end
      nil
    end
  end
end
