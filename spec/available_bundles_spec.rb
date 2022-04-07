# frozen_string_literal: true

#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'webmock/rspec'

describe 'Available Bundles' do
  orig_config = SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
  config = orig_config.dup

  vip_server = 'https://localhost:8090'
  server_url = File.join(vip_server, '/i18n/api/v2/translation/products', config['product_name'], 'versions', config['version'])
  components_url = File.join(server_url, 'componentlist')
  locales_url =  File.join(server_url, 'localelist')

  let(:stubs) { [] }
  let(:components_stub) { stub_request(:get, components_url).to_return(body: File.new('spec/fixtures/mock_responses/componentlist')) }
  let(:locales_stub) { stub_request(:get, locales_url).to_return(body: File.new('spec/fixtures/mock_responses/localelist')) }

  before :all do
    WebMock.enable!
    WebMock.disable_net_connect!
  end
  after :all do
    SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = orig_config
    WebMock.disable!
  end

  let(:loader) { SgtnClient::TranslationLoader::LoaderFactory.create(config) }

  before :each do
    SgtnClient::CacheUtil.clear_cache
    WebMock.reset!
    config['vip_server'] = vip_server
  end

  it '#should be able to get available bundles' do
    stubs << components_stub << locales_stub
    result = loader.available_bundles
    expect(result).to_not be_nil
    stubs.each { |stub| expect(stub).to have_been_requested }
  end
end
