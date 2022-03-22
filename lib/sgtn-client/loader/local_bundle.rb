# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient::TranslationLoader::LocalBundle
  def load_bundle(component, locale)
    env = SgtnClient::Config.default_environment
    product_name = SgtnClient::Config.configurations[env]['product_name']
    version = SgtnClient::Config.configurations[env]['version'].to_s
    translation_bundle = SgtnClient::Config.configurations[env]['translation_bundle']
    bundlepath = translation_bundle + '/' + product_name + '/' + version + '/' + component + '/messages_' + locale + '.json'
    SgtnClient::FileUtil.read_json(bundlepath)
  end
end
