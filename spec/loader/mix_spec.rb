#  Copyright 2025 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe 'Mix', :include_helpers, :extend_helpers do
  include_context 'reset client'

  let(:loader) { SgtnClient::TranslationLoader::LoaderFactory.create(Sgtn.config) }
  let(:stubs) { [] }

  before :all do
    WebMock.enable!
    WebMock.disable_net_connect!
  end
  after :all do
    WebMock.disable!
  end

  before :each do
    Sgtn.vip_server = nil
    Sgtn.translation_bundle = nil
    Sgtn.source_bundle = nil
    WebMock.reset!
  end

  describe '#only local source is available' do
    before :each do
      Sgtn.source_bundle = Helpers::CONFIG_HASH['source_bundle']
    end

    it 'should be able to return En' do
      result = loader.get_bundle(component_local_source_only, en_locale)
      expect(result).to_not be_nil
      expect(result[message_only_in_local_source_key]).to eq 'New Hello world'
    end

    it "should return nil for #{locale}" do
      expect { loader.get_bundle(component_local_source_only, locale) }.to raise_error(SgtnClient::SingletonError)
      wait_threads_finish
    end

    it "should return nil for #{component_nonexistent}" do
      expect { loader.get_bundle(component_nonexistent, locale) }.to raise_error(SgtnClient::SingletonError)
      wait_threads_finish
    end

    it "should return nil for #{locale_nonexistent}" do
      expect { loader.get_bundle(component, locale_nonexistent) }.to raise_error(SgtnClient::SingletonError)
      wait_threads_finish
    end

    it 'should have only 1 loader' do
      expect(loader.loaders.size).to eq(1)
    end
  end

  describe '#only local translation is available' do
    before :each do
      Sgtn.translation_bundle = Helpers::CONFIG_HASH['translation_bundle']
    end

    it 'query source locale - English' do
      result = loader.get_bundle(component, en_locale)
      expect(result).to_not be_nil
      expect(result[source_changed_key]).to eq 'latest Hello world'
    end

    it "should return #{locale}" do
      result = loader.get_bundle(component, locale)
      expect(result).to_not be_nil
      expect(result[key]).to eq value
    end

    it "should return nil for #{component_nonexistent}" do
      expect { loader.get_bundle(component_nonexistent, locale) }.to raise_error(Errno::ENOENT)
      wait_threads_finish
    end

    it "should return nil for #{locale_nonexistent}" do
      expect { loader.get_bundle(component, locale_nonexistent) }.to raise_error(Errno::ENOENT)
      wait_threads_finish
    end

    it 'should have only 1 loader' do
      expect(loader.loaders.size).to eq(1)
    end
  end

  describe '#only Singleton server is available' do
    before :each do
      Sgtn.vip_server = singleton_server
    end

    it "get '#{en_locale}' translation - (get #{latest_locale} actually)" do
      latest_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{latest_locale}").read
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_only_on_server)).to_return_json(body: latest_response)

      result = loader.get_bundle(component_only_on_server, en_locale)

      expect(result).to_not be_nil
      expect(result['message_new_added']).to eq 'new message'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should be able to get #{locale} translation" do
      latest_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{latest_locale}").read
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_only_on_server)).to_return_json(body: latest_response)
      en_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{en_locale}").read
      stubs << stub_request(:get, format(bundle_url, en_locale, component_only_on_server)).to_return_json(body: en_response)
      zh_response = File.new("spec/fixtures/mock_responses/#{component_only_on_server}-#{locale}").read
      stubs << stub_request(:get, format(bundle_url, locale, component_only_on_server)).to_return_json(body: zh_response)

      result = loader.get_bundle(component_only_on_server, locale)

      expect(result).to_not be_nil
      expect(result[message_only_on_server_key]).to eq '仅在服务器上的消息'

      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{component_nonexistent}" do
      # stubs << stub_request(:get, format(bundle_url, en_locale, component_nonexistent)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_nonexistent)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale, component_nonexistent)).to_return_json(body: nonexistent_response)

      expect { loader.get_bundle(component_nonexistent, locale) }.to raise_error(SgtnClient::SingletonError)

      wait_threads_finish
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{locale_nonexistent}" do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component)).to_return_json(body: nonexistent_response)
      # stubs << stub_request(:get, format(bundle_url, en_locale, component)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale_nonexistent, component)).to_return_json(body: nonexistent_response)

      expect { loader.get_bundle(component, locale_nonexistent) }.to raise_error(SgtnClient::SingletonError)

      wait_threads_finish
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should have only 1 loader' do
      expect(loader.loaders.size).to eq(1)
    end
  end

  describe '#both Singleton server and Local Source are available' do
    server_local_source_key = 'server_local_source_key'

    before :each do
      Sgtn.vip_server = singleton_server
      Sgtn.source_bundle = Helpers::CONFIG_HASH['source_bundle']
    end

    it 'should be able to get a bundle in local source' do
      result = loader.get_bundle(component_local_source_only, en_locale)

      expect(result).to_not be_nil
      expect(result[message_only_in_local_source_key]).to eq 'New Hello world'
    end

    it 'should return local source for En' do
      result = loader.get_bundle(component, en_locale)

      expect(result).to_not be_nil
      expect(result[source_changed_key]).to eq 'Source Hello world'
    end

    it "should be able to get #{locale}" do
      zh_response = File.new("spec/fixtures/mock_responses/#{component}-#{locale}").read
      stubs << stub_request(:get, format(bundle_url, locale, component)).to_return_json(body: zh_response)
      en_response = File.new("spec/fixtures/mock_responses/#{component}-#{en_locale}").read
      stubs << stub_request(:get, format(bundle_url, en_locale, component)).to_return_json(body: en_response)

      result = loader.get_bundle(component, locale)

      expect(result).to_not be_nil
      expect(result[server_local_source_key]).to eq 'server_local_source - 从服务器来的消息'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'fallback to server when a component is unavailable in local source' do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_only_on_server)).to_return_json(body: stub_response("#{component_only_on_server}-#{latest_locale}"))

      result = loader.get_bundle(component_only_on_server, en_locale)
      expect(result[message_only_on_server_key]).to eq 'Message only on server'

      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{component_nonexistent}" do
      # stubs << stub_request(:get, format(bundle_url, en_locale, component_nonexistent)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_nonexistent)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale, component_nonexistent)).to_return_json(body: nonexistent_response)

      expect { loader.get_bundle(component_nonexistent, locale) }.to raise_error(SgtnClient::SingletonError)

      wait_threads_finish
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{locale_nonexistent}" do
      # stubs << stub_request(:get, format(bundle_url, en_locale, component)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale_nonexistent, component)).to_return_json(body: nonexistent_response)

      expect { loader.get_bundle(component, locale_nonexistent) }.to raise_error(SgtnClient::SingletonError)

      wait_threads_finish
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should have 2 loaders' do
      expect(loader.loaders.size).to eq(2)
    end
  end

  describe '#both Singleton server and local translation are available' do
    server_local_translation_key = 'server_local_translation_key'

    before :each do
      Sgtn.vip_server = singleton_server
      Sgtn.translation_bundle = Helpers::CONFIG_HASH['translation_bundle']
    end

    it 'should be able to get En' do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component)).to_return_json(body: stub_response("#{component}-#{latest_locale}"))

      result = loader.get_bundle(component, en_locale)

      expect(result).to_not be_nil
      expect(result[server_local_translation_key]).to eq 'Message from server'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should be able to get #{locale}" do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component)).to_return_json(body: stub_response("#{component}-#{latest_locale}"))
      stubs << stub_request(:get, format(bundle_url, en_locale, component)).to_return_json(body: stub_response("#{component}-#{en_locale}"))
      stubs << stub_request(:get, format(bundle_url, locale, component)).to_return_json(body: stub_response("#{component}-#{locale}"))

      result = loader.get_bundle(component, locale)

      expect(result).to_not be_nil
      expect(result[server_local_translation_key]).to eq '从服务器来的消息'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should NOT fallback old SOURCE from remote to local when doing source comparison' do
      local_translation = Sgtn.config.loader.loaders.filter { |v| v.is_a?(SgtnClient::TranslationLoader::LocalTranslation) }
      expect(local_translation).to_not receive(:load_bundle)
      stubs << stub_request(:get, format(bundle_url, latest_locale, component)).to_return_json(body: stub_response("#{component}-#{latest_locale}"))
      stubs << stub_request(:get, format(bundle_url, en_locale, component)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale, component)).to_return_json(body: stub_response("#{component}-#{locale}"))

      result = loader.get_bundle(component, locale)

      expect(result).to_not be_nil
      expect(result[source_changed_key]).to eq '旧 Hello world'

      wait_threads_finish
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it '#raise exception when querying En and there is no latest_locale bundle on both server and local translations' do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_local_source_only)).to_return_json(body: nonexistent_response)

      expect { loader.get_bundle(component_local_source_only, en_locale) }.to raise_error(Errno::ENOENT)

      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should be able to fallback to local latest for SOURCE locale - #{en_locale}" do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_local_translation_only)).to_return_json(body: nonexistent_response)

      result = loader.get_bundle(component_local_translation_only, en_locale)

      expect(result).to_not be_nil
      expect(result[message_only_in_local_translation_key]).to eq 'local only message'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should be able to fallback to local translation for #{locale}" do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_local_translation_only)).to_return_json(body: nonexistent_response)
      # stubs << stub_request(:get, format(bundle_url, en_locale, component_local_translation_only)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale, component_local_translation_only)).to_return_json(body: nonexistent_response)

      result = loader.get_bundle(component_local_translation_only, locale)

      expect(result).to_not be_nil
      expect(result[message_only_in_local_translation_key]).to eq '仅在本地的消息'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{component_nonexistent}" do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_nonexistent)).to_return_json(body: nonexistent_response)
      # stubs << stub_request(:get, format(bundle_url, en_locale, component_nonexistent)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale, component_nonexistent)).to_return_json(body: nonexistent_response)

      expect { loader.get_bundle(component_nonexistent, locale) }.to raise_error(Errno::ENOENT)

      wait_threads_finish
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{locale_nonexistent}" do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component)).to_return_json(body: nonexistent_response)
      # stubs << stub_request(:get, format(bundle_url, en_locale, component)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale_nonexistent, component)).to_return_json(body: nonexistent_response)

      expect { loader.get_bundle(component, locale_nonexistent) }.to raise_error(Errno::ENOENT)

      wait_threads_finish
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should have 2 loaders' do
      expect(loader.loaders.size).to eq(2)
    end
  end

  describe '#both local translation and local source are available' do
    before :each do
      Sgtn.translation_bundle = Helpers::CONFIG_HASH['translation_bundle']
      Sgtn.source_bundle = Helpers::CONFIG_HASH['source_bundle']
    end

    it 'En should use local source bundle' do
      result = loader.get_bundle(component, en_locale)
      expect(result).to_not be_nil
      expect(result[source_changed_key]).to eq 'Source Hello world'
    end

    it "#{locale} should use local translation bundle" do
      result = loader.get_bundle(component, locale)
      expect(result).to_not be_nil
      expect(result[key]).to eq '你好世界'
    end

    it "#{locale} should use local source bundle if source is changed" do
      result = loader.get_bundle(component, locale)
      expect(result).to_not be_nil
      expect(result[source_changed_key]).to eq 'Source Hello world'
    end

    it "fallback SOURCE from local translation to local source when querying source locale - #{en_locale}" do
      result = loader.get_bundle(component_local_source_only, en_locale)

      expect(result).to_not be_nil
      expect(result[message_only_in_local_source_key]).to eq 'New Hello world'
    end

    it "should return nil for #{component_nonexistent}" do
      expect { loader.get_bundle(component_nonexistent, locale) }.to raise_error(Errno::ENOENT)
      wait_threads_finish
    end

    it "should return nil for #{locale_nonexistent}" do
      expect { loader.get_bundle(component, locale_nonexistent) }.to raise_error(Errno::ENOENT)
      wait_threads_finish
    end

    it 'should have 2 loaders' do
      expect(loader.loaders.size).to eq(2)
    end
  end

  describe '#Singleton server, local translation and local source are ALL available' do
    server_local_translation_source_key = 'server_local_translation_source_key'

    before :each do
      Sgtn.vip_server = singleton_server
      Sgtn.source_bundle = Helpers::CONFIG_HASH['source_bundle']
      Sgtn.translation_bundle = Helpers::CONFIG_HASH['translation_bundle']
    end

    it 'En should use local source bundle' do
      result = loader.get_bundle(component, en_locale)
      expect(result).to_not be_nil
      expect(result[source_changed_key]).to eq 'Source Hello world'
    end

    it "#{locale} should use translation from server" do
      en_response = File.new("spec/fixtures/mock_responses/#{component}-#{en_locale}").read
      stubs << stub_request(:get, format(bundle_url, en_locale, component)).to_return_json(body: en_response)
      zh_response = File.new("spec/fixtures/mock_responses/#{component}-#{locale}").read
      stubs << stub_request(:get, format(bundle_url, locale, component)).to_return_json(body: zh_response)

      result = loader.get_bundle(component, locale)

      expect(result).to_not be_nil
      expect(result[server_local_translation_source_key]).to eq '从服务器来的消息 server-local-translation-source'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should be able to fallback to local translation for #{locale}" do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_local_translation_only)).to_return_json(body: nonexistent_response)
      # stubs << stub_request(:get, format(bundle_url, en_locale, component_local_translation_only)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale, component_local_translation_only)).to_return_json(body: nonexistent_response)

      result = loader.get_bundle(component_local_translation_only, locale)

      expect(result).to_not be_nil
      expect(result[message_only_in_local_translation_key]).to eq '仅在本地的消息'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'source bundle fallback to server when a component is unavailable in local source' do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_only_on_server)).to_return_json(body: stub_response("#{component_only_on_server}-#{latest_locale}"))

      result = loader.get_bundle(component_only_on_server, en_locale)
      expect(result[message_only_on_server_key]).to eq 'Message only on server'

      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "fallback latest from remote to local when querying #{locale}" do
      # stubs << stub_request(:get, format(bundle_url, latest_locale, component)).to_return_json(body: stub_response("#{component}-#{latest_locale}"))
      stubs << stub_request(:get, format(bundle_url, en_locale, component)).to_return_json(body: stub_response("#{component}-#{en_locale}"))
      stubs << stub_request(:get, format(bundle_url, locale, component)).to_return_json(body: stub_response("#{component}-#{locale}"))

      result = loader.get_bundle(component, locale)

      expect(result).to_not be_nil
      expect(result[key]).to eq '你好世界-server'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "fallback translation from remote to local when querying #{locale}" do
      # stubs << stub_request(:get, format(bundle_url, en_locale, component)).to_return_json(body: stub_response("#{component}-#{en_locale}"))
      stubs << stub_request(:get, format(bundle_url, locale, component)).to_return_json(body: nonexistent_response)

      result = loader.get_bundle(component, locale)

      expect(result).to_not be_nil
      expect(result[key]).to eq '你好世界'
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should return nil for #{component_nonexistent}" do
      stubs << stub_request(:get, format(bundle_url, latest_locale, component_nonexistent)).to_return_json(body: nonexistent_response)
      # stubs << stub_request(:get, format(bundle_url, en_locale, component_nonexistent)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale, component_nonexistent)).to_return_json(body: nonexistent_response)

      expect { loader.get_bundle(component_nonexistent, locale) }.to raise_error(Errno::ENOENT)

      wait_threads_finish
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it "should raise exception for #{locale_nonexistent}" do
      # stubs << stub_request(:get, format(bundle_url, en_locale, component)).to_return_json(body: nonexistent_response)
      stubs << stub_request(:get, format(bundle_url, locale_nonexistent, component)).to_return_json(body: nonexistent_response)

      expect { loader.get_bundle(component, locale_nonexistent) }.to raise_error(Errno::ENOENT)

      wait_threads_finish
      stubs.each { |stub| expect(stub).to have_been_requested }
    end

    it 'should have 3 loaders' do
      expect(loader.loaders.size).to eq(3)
    end
  end

  describe '#verify configuration' do
    it 'source configuration item is nil' do
      expect { loader.get_bundle('', '') }.to raise_error(SgtnClient::SingletonError)
    end
  end
end
