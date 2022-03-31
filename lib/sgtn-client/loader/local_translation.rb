# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'multi_json'

class SgtnClient::TranslationLoader::LocalTranslation
  BUNDLE_PREFIX = 'messages_'
  BUNDLE_SUFFIX = '.json'

  def initialize
    env = SgtnClient::Config.default_environment
    @config = SgtnClient::Config.configurations[env]

    #  @config['translation_bundle'] isn't defined, throw error
    @base_path = Pathname.new(@config['translation_bundle']) + @config['product_name'] + @config['version']
  end

  def load_bundle(component, locale)
    return if locale == LocaleUtil::REAL_SOURCE_LOCALE # only return when NOT querying source

    file_name = BUNDLE_PREFIX + locale + BUNDLE_SUFFIX
    file_path = @base_path + component + file_name

    json_data = JSON.parse(File.read(file_path))
    messages = json_data['messages']

    raise Error::SingletonError, 'no messages in bundle.' unless messages

    messages
  end
end
