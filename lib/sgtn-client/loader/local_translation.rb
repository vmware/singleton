# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'multi_json'

class SgtnClient::TranslationLoader::LocalTranslation
  BUNDLE_PREFIX = 'messages_'.freeze
  BUNDLE_SUFFIX = '.json'.freeze

  def initialize
    env = SgtnClient::Config.default_environment
    @config = SgtnClient::Config.configurations[env]

    #  @config['translation_bundle'] isn't defined, throw error
    @base_path = Pathname.new(@config['translation_bundle']) + @config['product_name'] + @config['version']
  end

  def load_bundle(component, locale)
    return if locale == SgtnClient::LocaleUtil::REAL_SOURCE_LOCALE # only return when NOT querying source

    file_name = BUNDLE_PREFIX + locale + BUNDLE_SUFFIX
    file_path = @base_path + component + file_name

    json_data = JSON.parse(File.read(file_path))
    messages = json_data['messages']

    raise SingletonError, 'no messages in bundle.' unless messages

    messages
  end

  def available_locales
    locales = Set.new
    @base_path.glob('*/*.json') do |f|
      locale = f.basename.to_s.sub!(BUNDLE_PREFIX, '').sub!(BUNDLE_SUFFIX, '')
      locales.add locale
    end
    locales
  end

  def available_components
    components = Set.new
    @base_path.glob('*/') do |f| # TODO: folder shouldn't be empty?
      components << f.basename.to_s
    end
    components
  end

  def available_bundles
    bundles = Set.new
    @base_path.glob('*/*.json') do |f|
      locale = f.basename.to_s.sub!(BUNDLE_PREFIX, '').sub!(BUNDLE_SUFFIX, '')
      bundles.add [f.parent.basename.to_s, locale]
    end
    bundles
  end
end
