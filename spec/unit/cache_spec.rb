# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'spec_helper'

describe 'Cache' do
  before :all do
    SgtnClient::Config.instance_variable_set('@loader', nil)
    SgtnClient::CacheUtil.clear_cache
  end

  before :each do
    env = SgtnClient::Config.default_environment
    SgtnClient::Config.configurations[env]['vip_server'] = nil
    SgtnClient::Config.configurations[env]['cache_expiry_period'] = 1
  end

  it 'GETTranslation' do
    # get translation from server
    expect(SgtnClient::Translation.getString('JAVA', 'helloworld', 'zh-Hans')).to eq '你好世界'
    # get translation from cache
    expect(SgtnClient::Translation.getString('JAVA', 'helloworld', 'zh-Hans')).to eq '你好世界'
    # get from server again after data is expired
    # sleep 70
    expect(SgtnClient::Translation.getString('JAVA', 'helloworld', 'zh-Hans')).to eq '你好世界'
  end

  describe '#cache expiration', :include_helpers, :extend_helpers do
    include_context 'reset client'
    before(:all) do
      SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = config.dup
    end
    # before(:each) do
    #   SgtnClient::CacheUtil.clear_cache
    # end

    it '#populate cache at the beginning' do
      SgtnClient::Config.loader.loaders.each do |loader|
        if loader.is_a?(SgtnClient::TranslationLoader::Source)
          expect(loader).to receive(:load_bundle).once.with(component, latest_locale).and_call_original
          expect(loader).to receive(:load_bundle).once.with(component, en_locale).and_call_original
          expect(loader).to receive(:load_bundle).once.with(component, locale).and_call_original
        else
          expect(loader).to receive(:load_bundle).once.with(component, locale).and_call_original
          expect(loader).to receive(:load_bundle).once.with(component, source_locale).and_call_original
        end
      end

      cache_item = SgtnClient::CacheUtil.get_cache(bundle_id)
      expect(cache_item).to be_nil

      translations = Singleton.get_translations(component, locale: locale)
      expect(translations['messages']).to include({ key => value })
    end

    it '#get translations from cache for the first time' do
      SgtnClient::Config.loader.loaders.each do |loader|
        expect(loader).to_not receive(:load_bundle)
      end
      translations = Singleton.get_translations(component, locale: locale)
      expect(translations['messages']).to include({ key => value })
      cache_item = SgtnClient::CacheUtil.get_cache(bundle_id)
      expect(SgtnClient::CacheUtil.is_expired(cache_item)).to be false
    end

    it '#get translations from cache for the second time' do
      SgtnClient::Config.loader.loaders.each do |loader|
        expect(loader).to_not receive(:load_bundle)
      end
      translations = Singleton.get_translations(component, locale: locale)
      expect(translations['messages']).to include({ key => value })
      cache_item = SgtnClient::CacheUtil.get_cache(bundle_id)
      expect(SgtnClient::CacheUtil.is_expired(cache_item)).to be false
    end

    new_value = value + 'new'
    it '#expire cache' do
      expire_cache(SgtnClient::Common::BundleID.new(component, locale))
      cache_item = SgtnClient::CacheUtil.get_cache(bundle_id)
      expect(SgtnClient::CacheUtil.is_expired(cache_item)).to be true

      SgtnClient::Config.loader.loaders.each do |loader|
        if loader.is_a?(SgtnClient::TranslationLoader::Source)
          expect(loader).to receive(:load_bundle).once.with(component, latest_locale).and_call_original
          expect(loader).to receive(:load_bundle).once.with(component, en_locale).and_call_original
          expect(loader).to receive(:load_bundle).once.with(component, locale).and_call_original
        else
          expect(loader).to receive(:load_bundle).once.with(component, locale).and_return({ key => new_value })
          expect(loader).to receive(:load_bundle).once.with(component, source_locale).and_call_original
        end
      end

      # still return expired value
      translations = Singleton.get_translations(component, locale: locale)
      expect(translations['messages']).to include({ key => value })
      wait_threads_finish
    end

    it '#get translations from cache for the second time after expiration' do
      SgtnClient::Config.loader.loaders.each do |loader|
        expect(loader).to_not receive(:load_bundle)
      end

      # return new value
      translations = Singleton.get_translations(component, locale: locale)
      expect(translations['messages']).to include({ key => new_value })
      cache_item = SgtnClient::CacheUtil.get_cache(bundle_id)
      expect(SgtnClient::CacheUtil.is_expired(cache_item)).to be false
    end
  end

  # english bundles exipred
  # availabel bundles exipred
end
