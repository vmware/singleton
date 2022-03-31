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
  component_local_source = 'NEW'
  component = 'JAVA'

  locale = 'zh-Hans'
  en_locale = 'en'

  message_only_on_server_key = 'message_only_on_server'
  message_only_in_local_source_key = 'new_helloworld'
  key = 'old_helloworld'

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
    before :each do
      config['source_bundle'] = back_config['source_bundle']
    end

    it 'should be able to return En' do
      result = SgtnClient::Translation.send(:get_cs, component_local_source, en_locale)
      expect(result).to_not be_nil
      expect(result.dig(message_only_in_local_source_key)).to eq "New Hello world"
    end

    it "should return nil for #{locale}" do
      result = SgtnClient::Translation.send(:get_cs, component_local_source, locale)
      expect(result).to be_nil
    end

    it 'should have only 1 loader' do
      expect(SgtnClient::Config.loader.loaders.size).to eq(1)
    end
  end

  describe '#only local translation is available' do
    before :each do
      config['translation_bundle'] = back_config['translation_bundle']
    end

    it 'query source locale - English' do
      result = SgtnClient::Translation.send(:get_cs, component, en_locale)
      expect(result).to_not be_nil
      expect(result.dig(key)).to eq "old Hello world"
    end

    it "should return #{locale}" do
      result = SgtnClient::Translation.send(:get_cs, component, locale)
      expect(result).to_not be_nil
      expect(result.dig(key)).to eq "旧 Hello world"
    end

    it 'should have only 1 loader' do
      expect(SgtnClient::Config.loader.loaders.size).to eq(1)
    end
  end

  describe '#only Singleton server is available' do
    before :each do
      config['vip_server'] = back_config['vip_server']
    end

    it "get '#{en_locale}' translation" do
      stubs = []

      en_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{en_locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_only_on_server, 'locales' => en_locale }).to_return(body: en_response)
      result = SgtnClient::Translation.send(:get_cs, component_only_on_server, en_locale)
      expect(result).to_not be_nil
      expect(result.dig(message_only_on_server_key)).to eq "Message only on server"

      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should be able to get #{locale} translation" do
      stubs = []

      zh_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_only_on_server, 'locales' => locale }).to_return(body: zh_response)
      result = SgtnClient::Translation.send(:get_cs, component_only_on_server, locale)
      expect(result).to_not be_nil
      expect(result.dig(message_only_on_server_key)).to eq "仅在服务器上的消息"

      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should have only 1 loader' do
      expect(SgtnClient::Config.loader.loaders.size).to eq(1)
    end
  end

  describe '#both Singleton server and Local Source are available' do
    before :each do
      config['vip_server'] = back_config['vip_server']
      config['source_bundle'] = back_config['source_bundle']
    end

    it "should be able to get component in local source" do
      stubs = []

      result = SgtnClient::Translation.send(:get_cs, component_local_source, en_locale)

      expect(result).to_not be_nil
      expect(result.dig(message_only_in_local_source_key)).to eq "New Hello world"
    end

    it "should return local source for En" do
      stubs = []

      result = SgtnClient::Translation.send(:get_cs, component, en_locale)

      expect(result).to_not be_nil
      expect(result.dig(key)).to eq "Source Hello world"
    end

    it "should be able to get #{locale}" do
      stubs = []

      zh_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_only_on_server, 'locales' => locale }).to_return(body: zh_response)
      en_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{en_locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_only_on_server, 'locales' => en_locale }).to_return(body: en_response)

      result = SgtnClient::Translation.send(:get_cs, component_only_on_server, locale)

      expect(result).to_not be_nil
      expect(result.size).to be >  0
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should NOT be able to get component on server only for En" do
      result = SgtnClient::Translation.send(:get_cs, component_only_on_server, en_locale)
      expect(result).to be_nil
    end

    it 'should have 2 loaders' do
      expect(SgtnClient::Config.loader.loaders.size).to eq(2)
    end
  end

  describe '#both Singleton server and local translation are available' do
    before :each do
      config['vip_server'] = back_config['vip_server']
      config['translation_bundle'] = back_config['translation_bundle']
    end

    it 'should have 2 loaders' do
      expect(SgtnClient::Config.loader.loaders.size).to eq(2)
    end
  end

  describe '#both local translation and local source are available' do
    before :each do
      config['translation_bundle'] = back_config['translation_bundle']
      config['source_bundle'] = back_config['source_bundle']
    end

    it 'should have 2 loaders' do
      expect(SgtnClient::Config.loader.loaders.size).to eq(2)
    end
  end

  describe '#Singleton server, local translation and local source are ALL available' do
    before :each do
      config['vip_server'] = back_config['vip_server']
      config['source_bundle'] = back_config['source_bundle']
      config['translation_bundle'] = back_config['translation_bundle']
    end

    it 'should have 3 loaders' do
      expect(SgtnClient::Config.loader.loaders.size).to eq(3)
    end
  end

  describe '#verify configuration' do
    it 'source configuration item is nil' do
      config['source_bundle'] = nil

      expect { SgtnClient::Translation.send(:get_cs, '', '') }.to raise_error
    end
  end
end
