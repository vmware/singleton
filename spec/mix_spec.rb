# frozen_string_literal: true

#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'webmock/rspec'
require 'support/helpers'

RSpec.configure do |c|
  c.include Helpers, :include_helpers
  c.extend  Helpers, :extend_helpers
end

describe 'Mix', :include_helpers, :extend_helpers do
  orig_config = SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
  config = orig_config.dup

  component_only_on_server = 'component_only_on_server'
  component_local_source_only = 'NEW'
  component_local_translation_only = 'local_only'
  component_nonexistent = 'nonexistent_component'

  locale_nonexistent = 'nonexistent_locale'

  message_only_on_server_key = 'message_only_on_server'
  message_only_in_local_source_key = 'new_helloworld'
  message_only_in_local_translation_key = 'local_only_key'
  source_changed_key = 'old_helloworld'
  key = 'helloworld'

  let(:loader) { SgtnClient::TranslationLoader::LoaderFactory.create(config) }

  before :all do
    WebMock.enable!
    WebMock.disable_net_connect!
  end
  after :all do
    WebMock.disable!
  end

  before :each do
    config['vip_server'] = nil
    config['translation_bundle'] = nil
    config['source_bundle'] = nil
    SgtnClient::CacheUtil.clear_cache
    WebMock.reset!
  end

  describe '#only local source is available' do
    before :each do
      config['source_bundle'] = orig_config['source_bundle']
    end

    it 'should be able to return En' do
      result = loader.get_bundle(component_local_source_only, en_locale)
      expect(result).to_not be_nil
      expect(result.dig(message_only_in_local_source_key)).to eq 'New Hello world'
    end

    it "should return nil for #{locale}" do
      result = loader.get_bundle(component_local_source_only, locale)
      expect(result).to be_nil
    end

    it "should return nil for #{component_nonexistent}" do
      result = loader.get_bundle(component_nonexistent, locale)
      expect(result).to be_nil
    end

    it "should return nil for #{locale_nonexistent}" do
      result = loader.get_bundle(component, locale_nonexistent)
      expect(result).to be_nil
    end

    it 'should have only 1 loader' do
      expect(loader.loaders.size).to eq(1)
    end
  end

  describe '#only local translation is available' do
    before :each do
      config['translation_bundle'] = orig_config['translation_bundle']
    end

    it 'query source locale - English' do
      result = loader.get_bundle(component, en_locale)
      expect(result).to_not be_nil
      expect(result.dig(source_changed_key)).to eq 'old Hello world'
    end

    it "should return #{locale}" do
      result = loader.get_bundle(component, locale)
      expect(result).to_not be_nil
      expect(result.dig(source_changed_key)).to eq '旧 Hello world'
    end

    it "should return nil for #{component_nonexistent}" do
      result = loader.get_bundle(component_nonexistent, locale)
      expect(result).to be_nil
    end

    it "should return nil for #{locale_nonexistent}" do
      result = loader.get_bundle(component, locale_nonexistent)
      expect(result).to be_nil
    end

    it 'should have only 1 loader' do
      expect(loader.loaders.size).to eq(1)
    end
  end

  describe '#only Singleton server is available' do
    before :each do
      config['vip_server'] = singleton_server
    end

    it "get '#{en_locale}' translation" do
      en_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{en_locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_only_on_server, 'locales' => en_locale }).to_return(body: en_response)
      result = loader.get_bundle(component_only_on_server, en_locale)
      expect(result).to_not be_nil
      expect(result.dig(message_only_on_server_key)).to eq 'Message only on server'

      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should be able to get #{locale} translation" do
      zh_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_only_on_server, 'locales' => locale }).to_return(body: zh_response)
      result = loader.get_bundle(component_only_on_server, locale)
      expect(result).to_not be_nil
      expect(result.dig(message_only_on_server_key)).to eq '仅在服务器上的消息'

      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{component_nonexistent}" do
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_nonexistent, 'locales' => locale }).to_return(body: nonexistent_response)

      result = loader.get_bundle(component_nonexistent, locale)

      expect(result).to be_nil
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{locale_nonexistent}" do
      stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale_nonexistent }).to_return(body: nonexistent_response)

      result = loader.get_bundle(component, locale_nonexistent)

      expect(result).to be_nil
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should have only 1 loader' do
      expect(loader.loaders.size).to eq(1)
    end
  end

  describe '#both Singleton server and Local Source are available' do
    server_local_source_key = 'server_local_source_key'

    before :each do
      config['vip_server'] = singleton_server
      config['source_bundle'] = orig_config['source_bundle']
    end

    it 'should be able to get component in local source' do
      result = loader.get_bundle(component_local_source_only, en_locale)

      expect(result).to_not be_nil
      expect(result.dig(message_only_in_local_source_key)).to eq 'New Hello world'
    end

    it 'should return local source for En' do
      result = loader.get_bundle(component, en_locale)

      expect(result).to_not be_nil
      expect(result.dig(source_changed_key)).to eq 'Source Hello world'
    end

    it "should be able to get #{locale}" do
      zh_response = File.new("spec/fixtures/mock_responses/#{component}-#{locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale }).to_return(body: zh_response)
      en_response = File.new("spec/fixtures/mock_responses/#{component}-#{en_locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => en_locale }).to_return(body: en_response)

      result = loader.get_bundle(component, locale)

      expect(result).to_not be_nil
      expect(result.dig(server_local_source_key)).to eq 'server_local_source - 从服务器来的消息'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should NOT be able to get a component(on server only) for En because En will only use local source' do
      result = loader.get_bundle(component_only_on_server, en_locale)
      expect(result).to be_nil
    end

    it "should return nil for #{component_nonexistent}" do
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_nonexistent, 'locales' => en_locale }).to_return(body: nonexistent_response)
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_nonexistent, 'locales' => locale }).to_return(body: nonexistent_response)

      result = loader.get_bundle(component_nonexistent, locale)

      expect(result).to be_nil
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{locale_nonexistent}" do
      stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => en_locale }).to_return(body: nonexistent_response)
      stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale_nonexistent }).to_return(body: nonexistent_response)

      result = loader.get_bundle(component, locale_nonexistent)

      expect(result).to be_nil
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should have 2 loaders' do
      expect(loader.loaders.size).to eq(2)
    end
  end

  describe '#both Singleton server and local translation are available' do
    server_local_translation_key = 'server_local_translation_key'

    before :each do
      config['vip_server'] = singleton_server
      config['translation_bundle'] = orig_config['translation_bundle']
    end

    it 'should be able to get En' do
      en_response = File.new("spec/fixtures/mock_responses/#{component}-#{en_locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => en_locale }).to_return(body: en_response)

      result = loader.get_bundle(component, en_locale)

      expect(result).to_not be_nil
      expect(result.dig(server_local_translation_key)).to eq 'Message from server'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should be able to get #{locale}" do
      zh_response = File.new("spec/fixtures/mock_responses/#{component}-#{locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale }).to_return(body: zh_response)

      result = loader.get_bundle(component, locale)

      expect(result).to_not be_nil
      expect(result.dig(server_local_translation_key)).to eq '从服务器来的消息'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should be able to fallback to local translation for En' do
      en_response = File.new("spec/fixtures/mock_responses/#{component_local_translation_only}-#{en_locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_local_translation_only, 'locales' => en_locale }).to_return(body: en_response)

      result = loader.get_bundle(component_local_translation_only, en_locale)

      expect(result).to_not be_nil
      expect(result.dig(message_only_in_local_translation_key)).to eq 'local only message'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should be able to fallback to local translation for #{locale}" do
      response = File.new("spec/fixtures/mock_responses/#{component_local_translation_only}-#{locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_local_translation_only, 'locales' => locale }).to_return(body: response)

      result = loader.get_bundle(component_local_translation_only, locale)

      expect(result).to_not be_nil
      expect(result.dig(message_only_in_local_translation_key)).to eq '仅在本地的消息'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{component_nonexistent}" do
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_nonexistent, 'locales' => locale }).to_return(body: nonexistent_response)

      result = loader.get_bundle(component_nonexistent, locale)

      expect(result).to be_nil
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{locale_nonexistent}" do
      stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale_nonexistent }).to_return(body: nonexistent_response)

      result = loader.get_bundle(component, locale_nonexistent)

      expect(result).to be_nil
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should have 2 loaders' do
      expect(loader.loaders.size).to eq(2)
    end
  end

  describe '#both local translation and local source are available' do
    before :each do
      config['translation_bundle'] = orig_config['translation_bundle']
      config['source_bundle'] = orig_config['source_bundle']
    end

    it 'En should use local source bundle' do
      result = loader.get_bundle(component, en_locale)
      expect(result).to_not be_nil
      expect(result.dig(source_changed_key)).to eq 'Source Hello world'
    end

    it "#{locale} should use local translation bundle" do
      result = loader.get_bundle(component, locale)
      expect(result).to_not be_nil
      expect(result.dig(key)).to eq '你好世界'
    end

    it "#{locale} should use local source bundle if source is changed" do
      result = loader.get_bundle(component, locale)
      expect(result).to_not be_nil
      expect(result.dig(source_changed_key)).to eq 'Source Hello world'
    end

    it "should return nil for #{component_nonexistent}" do
      result = loader.get_bundle(component_nonexistent, locale)
      expect(result).to be_nil
    end

    it "should return nil for #{locale_nonexistent}" do
      result = loader.get_bundle(component, locale_nonexistent)
      expect(result).to be_nil
    end

    it 'should have 2 loaders' do
      expect(loader.loaders.size).to eq(2)
    end
  end

  describe '#Singleton server, local translation and local source are ALL available' do
    server_local_translation_source_key = 'server_local_translation_source_key'

    before :each do
      config['vip_server'] = singleton_server
      config['source_bundle'] = orig_config['source_bundle']
      config['translation_bundle'] = orig_config['translation_bundle']
    end

    it 'En should use local source bundle' do
      result = loader.get_bundle(component, en_locale)
      expect(result).to_not be_nil
      expect(result.dig(source_changed_key)).to eq 'Source Hello world'
    end

    it "#{locale} should use translation from server" do
      en_response = File.new("spec/fixtures/mock_responses/#{component}-#{en_locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => en_locale }).to_return(body: en_response)
      zh_response = File.new("spec/fixtures/mock_responses/#{component}-#{locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale }).to_return(body: zh_response)

      result = loader.get_bundle(component, locale)

      expect(result).to_not be_nil
      expect(result.dig(server_local_translation_source_key)).to eq '从服务器来的消息 server-local-translation-source'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should be able to fallback to local translation for #{locale}" do
      en_response = File.new("spec/fixtures/mock_responses/#{component_local_translation_only}-#{en_locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_local_translation_only, 'locales' => en_locale }).to_return(body: en_response)
      response = File.new("spec/fixtures/mock_responses/#{component_local_translation_only}-#{locale}")
      stubs << stub_request(:get, server_url).with(query: { 'components' => component_local_translation_only, 'locales' => locale }).to_return(body: response)

      result = loader.get_bundle(component_local_translation_only, locale)

      expect(result).to_not be_nil
      expect(result.dig(message_only_in_local_translation_key)).to eq '仅在本地的消息'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should have 3 loaders' do
      expect(loader.loaders.size).to eq(3)
    end
  end

  describe '#verify configuration' do
    it 'source configuration item is nil' do
      expect { loader.get_bundle('', '') }.to raise_error
    end
  end
end
