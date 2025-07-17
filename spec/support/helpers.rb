# frozen_string_literal: true

# Copyright 2025 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'active_support'

module Helpers
  mattr_accessor :singleton_server, :server_url, :component, :locale, :en_locale, :source_locale, :components_url,
                 :locales_url, :component_only_on_server, :component_local_source_only,
                 :component_local_translation_only, :component_nonexistent, :locale_nonexistent,
                 :message_only_on_server_key, :message_only_in_local_source_key,
                 :message_only_in_local_translation_key, :source_changed_key, :key, :formatting_key,
                 :key_nonexistent, :value, :defaut_value, :en_value, :product_name, :version,
                 :latest_locale, :bundle_url, :bundle_id

  CONFIG_HASH =
    {
      'product_name' => 'test',
      'version' => '4.8.1',
      'vip_server' => nil,
      'translation_bundle' => './spec/fixtures/bundles',
      'source_bundle' => './spec/fixtures/sources',
      'cache_expiry_period' => 10,
      'log_file' => './unit_test.log',
      # 'default_language' => 'en'
    }.freeze

  def product_name
    CONFIG_HASH['product_name']
  end

  def version
    CONFIG_HASH['version']
  end
  self.singleton_server = 'http://localhost:8091/vipserver'
  def translation_path
    CONFIG_HASH['translation_bundle']
  end

  def source_path
    CONFIG_HASH['source_bundle']
  end

  self.server_url = File.join(singleton_server, '/i18n/api/v2/translation/products', CONFIG_HASH['product_name'], 'versions', CONFIG_HASH['version'])
  self.bundle_url = "#{server_url}/locales/%s/components/%s"
  self.components_url = File.join(server_url, 'componentlist')
  self.locales_url = File.join(server_url, 'localelist')

  self.component = 'JAVA'
  self.locale = 'zh-Hans'
  self.en_locale = 'en'
  self.source_locale = 'en'
  self.latest_locale = 'latest'

  self.component_only_on_server = 'component_only_on_server'
  self.component_local_source_only = 'NEW'
  self.component_local_translation_only = 'local_only'
  self.component_nonexistent = 'nonexistent_component'

  self.locale_nonexistent = 'nonexistent_locale'

  self.bundle_id = SgtnClient::Common::BundleID.new(component, locale)

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
    stub_request(:get, components_url).to_return_json(body: File.new('spec/fixtures/mock_responses/componentlist').read)
  end

  def locales_stub
    stub_request(:get, locales_url).to_return_json(body: File.new('spec/fixtures/mock_responses/localelist').read)
  end

  def bundle_stub(component, locale, response)
    stub_request(:get, format(bundle_url, locale, component)).to_return_json(body: response)
  end

  def stub_response(file_name)
    File.new("spec/fixtures/mock_responses/#{file_name}").read
  end

  def nonexistent_response
    File.new('spec/fixtures/mock_responses/nonexistent').read
  end

  def pseudo_response
    File.new('spec/fixtures/mock_responses/JAVA-pseudo').read
  end

  def reset_client
    Sgtn.config.update(Helpers::CONFIG_HASH)
    Sgtn.instance_variable_set(:@translation, nil)
    Sgtn.config.instance_variable_set(:@loader, nil)
    SgtnClient::LocaleUtil.send(:reset_locale_data, :available_locales)
  end
end
