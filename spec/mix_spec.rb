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
  component_only_on_server = 'component_only_on_server'
  locale = 'zh-Hans'
  en_locale = 'en'

  before :each do
    config['vip_server'] = nil
    config['translation_bundle'] = nil
    config['source_bundle'] = nil
    SgtnClient::Config.instance_variable_set('@loader', nil)
    SgtnClient::CacheUtil.clear_cache
  end

  after :all do
    SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = back_config
  end

  describe '#only local source is available' do
  end

  describe '#only local translation is available' do
  end

  describe '#only Singleton server is available' do
    before :each do
      config['vip_server'] = back_config['vip_server']
    end

    it "get #{locale} translation" do
      stubs = []

      zh_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_only_on_server, 'locales' => locale }).to_return(body: zh_response)
      result = SgtnClient::Translation.send(:get_cs, component_only_on_server, locale)
      expect(result).to_not be_nil
      expect(result.size).to be >  0

      stubs.each { |stub| expect(stub).to have_been_requested }
    end
    it "get '#{en_locale}' translation" do
      stubs = []

      en_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{en_locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_only_on_server, 'locales' => en_locale }).to_return(body: en_response)
      result = SgtnClient::Translation.send(:get_cs, component_only_on_server, en_locale)
      expect(result).to_not be_nil
      expect(result.size).to be >  0

      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should have only 1 loader' do
      expect(SgtnClient::Config.loader.loaders.size).to eq(1)
    end
  end

  describe '#both local source and local translation are available' do
  end

  describe '#both local source and Singleton server are available' do
  end

  it '#both Singleton server and Local Source are available' do
    config['vip_server'] = back_config['vip_server']
    config['source_bundle'] = back_config['source_bundle']
    stubs = []

    zh_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{locale}")
    stubs << stub_request(:get, server_url).with(query: { 'components' => component_only_on_server, 'locales' => locale }).to_return(body: zh_response)
    en_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{en_locale}")
    stubs << stub_request(:get, server_url).with(query: { 'components' => component_only_on_server, 'locales' => en_locale }).to_return(body: en_response)

    result = SgtnClient::Translation.send(:get_cs, component_only_on_server, locale)

    expect(SgtnClient::Config.loader.loaders.size).to eq(2)
    expect(result).to_not be_nil
    expect(result.size).to be >  0
    stubs.each { |stub| expect(stub).to have_been_requested }
  end

  describe '#Singleton server, local translation and local source are ALL available' do
  end

  describe '#verify configuration' do
    it 'source configuration item is nil' do
      config['source_bundle'] = nil

      expect { SgtnClient::Translation.send(:get_cs, '', '') }.to raise_error
    end
  end
end
