# frozen_string_literal: true

#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'webmock/rspec'

describe 'Available Bundles' do
  config = SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
  back_config = config.dup

  server_url = File.join(config['vip_server'], '/i18n/api/v2/translation/products', config['product_name'], 'versions', config['version'])
  components_url = File.join(server_url, 'componentlist')
  locales_url =  File.join(server_url, 'localelist')

  component_only_on_server = 'component_only_on_server'
  component_local_source_only = 'NEW'
  component_local_translation_only = 'local_only'
  component_nonexistent = 'nonexistent_component'
  component = 'JAVA'

  locale_nonexistent = 'nonexistent_locale'
  locale = 'zh-Hans'
  en_locale = 'en'

  message_only_on_server_key = 'message_only_on_server'
  message_only_in_local_source_key = 'new_helloworld'
  message_only_in_local_translation_key = 'local_only_key'
  source_changed_key = 'old_helloworld'
  key = 'helloworld'

  let(:nonexistent_response) { File.new('spec/fixtures/mock_responses/nonexistent').read }

  # let (:en_stub) { stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => en_locale }).to_return(body: File.new("spec/fixtures/mock_responses/#{component}-#{en_locale}")) }
  # let (:zh_stub) { stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale }).to_return(body: File.new("spec/fixtures/mock_responses/#{component}-#{locale}")) }

  let(:stubs) { [] }
  let(:components_stub) { stub_request(:get, components_url).to_return(body: File.new('spec/fixtures/mock_responses/componentlist')) }
  let(:locales_stub) { stub_request(:get, locales_url).to_return(body: File.new('spec/fixtures/mock_responses/localelist')) }

  before :all do
    WebMock.enable!
    WebMock.disable_net_connect!
  end
  after :all do
    SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = back_config
    WebMock.disable!
  end

  before :each do
    # config['vip_server'] = nil
    # config['translation_bundle'] = nil
    # config['source_bundle'] = nil
    SgtnClient::Config.instance_variable_set('@loader', nil)
    SgtnClient::CacheUtil.clear_cache
    WebMock.reset!
  end

  it '#should be able to get available bundles' do
    stubs << components_stub << locales_stub
    result = SgtnClient::Config.available_bundles
    expect(result).to_not be_nil
    stubs.each { |stub| expect(stub).to have_been_requested }
  end
  it '#should be able to get available locales' do
    stubs << components_stub << locales_stub
    result = SgtnClient::Config.available_locales
    expect(result).to_not be_nil
    stubs.each { |stub| expect(stub).to have_been_requested }
  end
  it '#should be able to get available components' do
    stubs << components_stub << locales_stub
    result = SgtnClient::Config.available_components
    expect(result).to_not be_nil
    stubs.each { |stub| expect(stub).to have_been_requested }
  end
end
