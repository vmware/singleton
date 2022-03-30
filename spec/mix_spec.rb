#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'webmock/rspec'

describe 'Singleton Server' do
  WebMock.disable_net_connect!

  config = SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
  back_config = config.dup
  server_url = File.join(config['vip_server'], '/i18n/api/v2/translation/products', config['product_name'], 'versions', config['version'])
  components_url = File.join(server_url, 'componentlist')
  locales_url =  File.join(server_url, 'localelist')
  component = 'JAVA'
  locale = 'zh-Hans'
  en_locale = 'en'

  before :each do
    config['vip_server'] = nil
    config['translation_bundle'] = nil
    SgtnClient::Config.instance_variable_set('@loader', nil)
  end

  after :all do
    SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = back_config
  end

  it 'only Singleton servier is available' do
    config['vip_server'] = back_config['vip_server']
    stubs = []

    zh_response = File.new("spec/fixtures/mock_responses/#{component}_#{locale}")
    stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale }).to_return(body: zh_response)
    en_response = File.new("spec/fixtures/mock_responses/#{component}_#{en_locale}")
    stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => en_locale }).to_return(body: en_response)

    result = SgtnClient::Translation.send(:get_cs, component, locale)

    expect(result).to_not be_nil
    expect(result.size).to be >  0
    stubs.each { |stub| expect(stub).to have_been_requested }
  end

  it 'only local translation is available' do
  end

  it 'source configuration item is nil' do
    config['source_bundle'] = nil

    expect { SgtnClient::Translation.send(:get_cs, '', '') }.to raise_error
  end
end
