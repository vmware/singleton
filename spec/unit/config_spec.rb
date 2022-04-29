# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe SgtnClient::Config do
  describe 'load_config' do
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
    it_behaves_like 'Available Bundles' do
      include_context 'reset client'

      before :all do
        SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = @config
      end
      before do
        SgtnClient::Config.instance_variable_set(:@loader, nil)
      end
    end

    describe '#available locales' do
      include_context 'reset client'
      include_context 'webmock'
      let(:stubs) { [] }
      before :all do
        config = SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
        config['vip_server'] = singleton_server
      end

      before :each do
        SgtnClient::CacheUtil.clear_cache
      end

      # it '#should be able to get available locales of all' do
      #   stubs << components_stub << locales_stub
      #   result = subject.available_locales
      #   expect(result).to be_a_kind_of(Set)
      #   expect(result).to include(locale, en_locale, source_locale)
      #   stubs.each { |stub| expect(stub).to have_been_requested }
      # end

      it '#should be able to get available locales of a component' do
        stubs << components_stub << locales_stub
        result = subject.available_locales(component)
        expect(result).to be_a_kind_of(Set)
        expect(result).to include(locale, en_locale, source_locale)
        stubs.each { |stub| expect(stub).to have_been_requested }
      end

      it '#should be able to get available locales of a component only in source' do
        stubs << components_stub << locales_stub
        result = subject.available_locales(component_local_source_only)
        expect(result).to be_a_kind_of(Set)
        expect(result).to contain_exactly(source_locale)
        stubs.each { |stub| expect(stub).to have_been_requested }
      end

      it '#should be able to get available locales of a component only in local' do
        stubs << components_stub << locales_stub
        result = subject.available_locales(component_local_translation_only)
        expect(result).to be_a_kind_of(Set)
        expect(result).to contain_exactly(en_locale, locale)
        stubs.each { |stub| expect(stub).to have_been_requested }
      end

      it '#should be able to get available locales of a component only on server' do
        stubs << components_stub << locales_stub
        result = subject.available_locales(component_only_on_server)
        expect(result).to be_a_kind_of(Set)
        expect(result).to contain_exactly(en_locale, locale)
        stubs.each { |stub| expect(stub).to have_been_requested }
      end
    end
  end
end
