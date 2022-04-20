# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'active_support'

module Helpers
  mattr_accessor :singleton_server, :server_url, :component, :locale, :en_locale, :source_locale, :components_url,
                 :locales_url, :component_only_on_server, :component_local_source_only,
                 :component_local_translation_only, :component_nonexistent, :locale_nonexistent,
                 :message_only_on_server_key, :message_only_in_local_source_key,
                 :message_only_in_local_translation_key, :source_changed_key, :key, :formatting_key,
                 :key_nonexistent, :value, :defaut_value, :en_value, :product_name, :version

  def config
    {
      'mode' => 'sandbox',
      'product_name' => product_name,
      'version' => version,
      # 'vip_server' => singleton_server,
      'translation_bundle' => './spec/fixtures/bundles',
      'source_bundle' => './spec/fixtures/sources',
      'cache_expiry_period' => 10,
      'default_language' => 'en'
    }
  end

  self.product_name = 'test'
  self.version = '4.8.1'
  self.singleton_server = 'https://localhost:8090'
  def translation_path
    config['translation_bundle']
  end

  def source_path
    config['source_bundle']
  end

  self.server_url = File.join(singleton_server, '/i18n/api/v2/translation/products', product_name, 'versions', version)
  self.components_url = File.join(server_url, 'componentlist')
  self.locales_url = File.join(server_url, 'localelist')

  self.component = 'JAVA'
  self.locale = 'zh-Hans'
  self.en_locale = 'en'
  self.source_locale = 'en'

  self.component_only_on_server = 'component_only_on_server'
  self.component_local_source_only = 'NEW'
  self.component_local_translation_only = 'local_only'
  self.component_nonexistent = 'nonexistent_component'

  self.locale_nonexistent = 'nonexistent_locale'

  self.message_only_on_server_key = 'message_only_on_server'
  self.message_only_in_local_source_key = 'new_helloworld'
  self.message_only_in_local_translation_key = 'local_only_key'
  self.source_changed_key = 'old_helloworld'
  self.key = 'helloworld'
  self.formatting_key = 'type_error'
  self.key_nonexistent = 'nonexistent_key'

  self.value = '你好世界'
  self.en_value = 'Hello world'
  self.defaut_value = 'defaut value'

  def components_stub
    stub_request(:get, components_url).to_return(body: File.new('spec/fixtures/mock_responses/componentlist'))
  end

  def locales_stub
    stub_request(:get, locales_url).to_return(body: File.new('spec/fixtures/mock_responses/localelist'))
  end

  def bundle_stub(component, locale, response)
    stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale }).to_return(body: response)
  end

  def stub_response(file_name)
    File.new("spec/fixtures/mock_responses/#{file_name}")
  end

  def nonexistent_response
    File.new('spec/fixtures/mock_responses/nonexistent').read
  end

  def stubs
    []
  end
end
