#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

shared_examples 'Available Bundles' do
  let(:stubs) { [] }
  prepend_before :all do
    @config = Sgtn.config
    WebMock.enable!
    WebMock.disable_net_connect!
  end
  after :all do
    WebMock.disable!
  end

  before :each do
    @config.vip_server = nil
    @config.translation_bundle = nil
    @config.source_bundle = nil
    WebMock.reset!
  end

  describe '#Only Singleton server is available' do
    before :each do
      @config.vip_server = singleton_server
    end

    it '#should be able to get available bundles' do
      stubs << components_stub << locales_stub
      result = subject.available_bundles
      expect(result).to be_a_kind_of(Set)
      expect(result).to include(SgtnClient::Common::BundleID.new(component_only_on_server, locale))
      stubs.each { |stub| expect(stub).to have_been_requested }
    end
  end

  describe '#Only local translation is available' do
    before :each do
      @config.translation_bundle = translation_path
    end

    it '#should be able to get available bundles' do
      result = subject.available_bundles
      expect(result).to be_a_kind_of(Set)
      expect(result).to include(SgtnClient::Common::BundleID.new(component_local_translation_only, en_locale))
    end
  end

  describe '#Only local source is available' do
    before :each do
      @config.source_bundle = source_path
    end

    it '#should be able to get available bundles' do
      result = subject.available_bundles
      expect(result).to be_a_kind_of(Set)
      expect(result).to include(SgtnClient::Common::BundleID.new(component_local_source_only, source_locale))
    end
  end

  describe '#Singleton server and local source are available' do
    before :each do
      @config.vip_server = singleton_server
      @config.source_bundle = source_path
    end

    it '#should be able to get available bundles' do
      stubs << components_stub << locales_stub
      result = subject.available_bundles
      expect(result).to be_a_kind_of(Set)
      expect(result).to include(SgtnClient::Common::BundleID.new(component_only_on_server, locale), SgtnClient::Common::BundleID.new(component_local_source_only, source_locale))
      stubs.each { |stub| expect(stub).to have_been_requested }
    end
  end

  describe '#Singleton server and local translation are available' do
    before :each do
      @config.vip_server = singleton_server
      @config.translation_bundle = translation_path
    end

    it '#should be able to get available bundles' do
      stubs << components_stub << locales_stub
      result = subject.available_bundles
      expect(result).to be_a_kind_of(Set)
      expect(result).to include(SgtnClient::Common::BundleID.new(component_only_on_server, locale), SgtnClient::Common::BundleID.new(component_local_translation_only, en_locale))
      stubs.each { |stub| expect(stub).to have_been_requested }
    end
  end

  describe '#local translation and local source are available' do
    before :each do
      @config.translation_bundle = translation_path
      @config.source_bundle = source_path
    end

    it '#should be able to get available bundles' do
      result = subject.available_bundles
      expect(result).to be_a_kind_of(Set)
      expect(result).to include(SgtnClient::Common::BundleID.new(component_local_translation_only, en_locale), SgtnClient::Common::BundleID.new(component_local_source_only, source_locale))
    end
  end

  describe '#Singleton server, local translation and local source are available' do
    before :each do
      @config.vip_server = singleton_server
      @config.translation_bundle = translation_path
      @config.source_bundle = source_path
    end

    it '#should be able to get available bundles' do
      stubs << components_stub << locales_stub
      result = subject.available_bundles
      expect(result).to be_a_kind_of(Set)
      expect(result).to include(SgtnClient::Common::BundleID.new(component_only_on_server, locale),
                                SgtnClient::Common::BundleID.new(component_local_translation_only, en_locale),
                                SgtnClient::Common::BundleID.new(component_local_source_only, source_locale))
      stubs.each { |stub| expect(stub).to have_been_requested }
    end
  end
end
