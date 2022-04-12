# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe SgtnClient::Config do
  describe 'load_config' do
    before :each do
      SgtnClient.load('./spec/config/sgtnclient.yml', 'test', './sgtnclient_config.log')
    end

    it 'define configuration' do
      env = SgtnClient::Config.default_environment
      mode = SgtnClient::Config.configurations[env]['mode']
      expect(mode).to eq 'sandbox'
    end

    it 'not define configuration' do
      begin
        SgtnClient::Config.config('aa', { app_id: 'XYZ' })
      rescue StandardError => e
        expect(e.message).to eq 'Configuration[aa] NotFound'
      end
    end
  end

  describe '#availale bundles', :include_helpers, :extend_helpers do
    new_config = config.dup

    before :all do
      WebMock.enable!
      WebMock.disable_net_connect!
      SgtnClient::Config.instance_variable_set(:@loader, nil)
      SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = new_config
    end
    after :all do
      WebMock.disable!
      SgtnClient::Config.instance_variable_set(:@loader, nil)
      SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = config.dup
    end

    before :each do
      new_config['vip_server'] = nil
      new_config['translation_bundle'] = nil
      new_config['source_bundle'] = nil
      SgtnClient::CacheUtil.clear_cache
      WebMock.reset!
    end
    describe '#Singleton server, local translation and local source are available' do
      before :each do
        new_config['vip_server'] = singleton_server
        new_config['translation_bundle'] = translation_path
        new_config['source_bundle'] = source_path
      end

      it '#should be able to get available bundles' do
        stubs << components_stub << locales_stub
        result = SgtnClient::Config.available_bundles
        expect(result).to be_a_kind_of(Set)
        expect(result).to include(SgtnClient::Common::BundleID.new(component_only_on_server, locale),
                                  SgtnClient::Common::BundleID.new(component_local_translation_only, en_locale),
                                  SgtnClient::Common::BundleID.new(component_local_source_only, source_locale))
        stubs.each { |stub| expect(stub).to have_been_requested }
      end

      it '#should be able to get available locales' do
        stubs << components_stub << locales_stub
        result = SgtnClient::Config.available_locales
        expect(result).to be_a_kind_of(Set)
        expect(result).to include(locale, en_locale, source_locale)
        stubs.each { |stub| expect(stub).to have_been_requested }
      end
    end
  end
end
