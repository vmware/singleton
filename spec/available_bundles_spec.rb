# frozen_string_literal: true

#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'webmock/rspec'

describe 'Available Bundles', :include_helpers, :extend_helpers do
  new_config = config.dup
  let(:loader) { SgtnClient::TranslationLoader::LoaderFactory.create(new_config) }

  before :all do
    WebMock.enable!
    WebMock.disable_net_connect!
  end
  after :all do
    WebMock.disable!
  end

  before :each do
    new_config['vip_server'] = nil
    new_config['translation_bundle'] = nil
    new_config['source_bundle'] = nil
    SgtnClient::CacheUtil.clear_cache
    WebMock.reset!
  end

  describe '#Only Singleton Server is available' do
    before :each do
      new_config['vip_server'] = singleton_server
    end

    it '#should be able to get available bundles' do
      stubs << components_stub << locales_stub
      result = loader.available_bundles
      expect(result).to_not be_nil
      expect(result).to include(SgtnClient::Common::BundleID.new(component_only_on_server, locale))
      stubs.each { |stub| expect(stub).to have_been_requested }
    end
  end

  describe '#Only local translation is available' do
    before :each do
      new_config['translation_bundle'] = translation_path
    end

    it '#should be able to get available bundles' do
      result = loader.available_bundles
      expect(result).to_not be_nil
      expect(result).to include(SgtnClient::Common::BundleID.new(component_local_translation_only, en_locale))
    end
  end

  describe '#Only local source is available' do
    before :each do
      new_config['source_bundle'] = source_path
    end

    it '#should be able to get available bundles' do
      result = loader.available_bundles
      expect(result).to_not be_nil
      expect(result).to include(SgtnClient::Common::BundleID.new(component_local_source_only, source_locale))
    end
  end
end
