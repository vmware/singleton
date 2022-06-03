# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe SgtnClient::Config do
  describe 'load_config' do
    # before :each do
    #   SgtnClient.load('./spec/config/sgtnclient.yml', 'test', './sgtnclient_config.log')
    # end

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

    it '#available bundles is expired' do
      @config['translation_bundle'] = translation_path
      @config['source_bundle'] = source_path

      # populate cache
      locales = subject.available_locales
      expect(locales).to_not be_empty

      expire_cache(SgtnClient::TranslationLoader::CONSTS::AVAILABLE_BUNDLES_KEY)

      # return expired data
      second_locales = subject.available_locales
      expect(second_locales).to be locales

      wait_threads_finish

      expect(subject).to receive(:notify_observers).once.with(:available_locales).and_call_original
      expect(SgtnClient::LocaleUtil).to receive(:reset_available_locales).once.with(:available_locales).and_call_original

      # return updated data
      new_locales = subject.available_locales
      expect(new_locales).to_not be locales
    end
  end
end
