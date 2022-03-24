# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient::Core
  autoload :Request, 'sgtn-client/core/request'
end

class SgtnClient::TranslationLoader::ServerBundle
  def load_bundle(component, locale)
    env = SgtnClient::Config.default_environment
    product_name = SgtnClient::Config.configurations[env]['product_name']
    vip_server = SgtnClient::Config.configurations[env]['vip_server']
    version = SgtnClient::Config.configurations[env]['version'].to_s
    url = vip_server + '/i18n/api/v2/translation/products/' + product_name + '/versions/' + version + '/locales/' + locale + '/components/' + component + '?checkTranslationStatus=false&machineTranslation=false&pseudo=false'
    begin
      obj = SgtnClient::Core::Request.get(url)
    rescue StandardError => e
      SgtnClient.logger.error e.message
    end
    obj && obj['data']
  end
end
