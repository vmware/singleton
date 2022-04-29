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

  describe '#availale bundles/locales - Config', :include_helpers, :extend_helpers do
    subject { SgtnClient::Config }
    include_examples 'Available Bundles' do
      include_context 'reset client'

      before :all do
        SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = @config
      end
      before do
        SgtnClient::Config.instance_variable_set(:@loader, nil)
      end
    end

    before :each, locales: true do
      @config['vip_server'] = singleton_server
      @config['translation_bundle'] = translation_path
      @config['source_bundle'] = source_path
    end

    it '#should be able to get available locales', locales: true do
      stubs << components_stub << locales_stub
      result = subject.available_locales
      expect(result).to be_a_kind_of(Set)
      expect(result).to include(locale, en_locale, source_locale)
      stubs.each { |stub| expect(stub).to have_been_requested }
    end
  end
end
