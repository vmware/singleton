# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe SgtnClient::Config do
  describe 'load_config' do
    it 'not define configuration' do
      begin
        Sgtn.load_config('./spec/config/sgtnclient.yml', 'aa')
      rescue StandardError => e
        expect(e.message).to eq 'Configuration[aa] NotFound'
      end
    end
  end

  describe '#availale bundles/locales - Config', :include_helpers, :extend_helpers do
    subject { Sgtn.config }
    it_behaves_like 'Available Bundles' do
      include_context 'reset client'

      before do
        Sgtn.config.instance_variable_set(:@loader, nil)
      end
    end

    describe '#available locales' do
      include_context 'reset client'
      include_context 'webmock'
      let(:stubs) { [] }
      before :all do
        Sgtn.vip_server = singleton_server
      end

      before :each do
        clear_cache
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
        expect(result).to contain_exactly(en_locale, locale, latest_locale)
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

    describe '#available_locales expired' do
      include_context 'reset client' do
        before(:all) do
          @config = Sgtn.config
        end
      end

      it '#available bundles is expired' do
        # @config.translation_bundle = translation_path
        # @config.source_bundle = source_path

        # populate cache
        locales = subject.available_locales(component)
        expect(locales).to_not be_empty

        expire_cache(SgtnClient::TranslationLoader::CONSTS::AVAILABLE_BUNDLES_KEY)

        # return expired data
        second_locales = subject.available_locales(component)
        expect(second_locales).to be locales
        wait_threads_finish

        # return updated data but it's the same object because data isn't changed.
        new_locales = subject.available_locales(component)
        expect(new_locales).to be locales

        ### data is changed
        expire_cache(SgtnClient::TranslationLoader::CONSTS::AVAILABLE_BUNDLES_KEY)
        expect(Sgtn.config.loader.loaders[1]).to(receive(:available_bundles).with(no_args).once { Set[SgtnClient::Common::BundleID.new(component, 'fr')] })

        ### return expired data
        locales1 = subject.available_locales(component)
        expect(locales1).to be locales
        wait_threads_finish

        ### return updated data
        expect(subject).to receive(:notify_observers).once.with(:available_locales).and_call_original
        expect(SgtnClient::LocaleUtil).to receive(:reset_locale_data).once.with(:available_locales).and_call_original
        locales2 = subject.available_locales(component)
        expect(locales2).to_not be locales
        expect(locales2).to include 'fr'
      end
    end
  end
end
