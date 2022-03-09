#Copyright 2019-2022 VMware, Inc.
#SPDX-License-Identifier: EPL-2.0
require 'spec_helper'

describe SgtnClient do
  describe "Locale" do

    before :each do
      SgtnClient.load("./config/sgtnclients.yml", 'test')
      SgtnClient::Source.loadBundles("default")
    end

    it "get_best_locale" do
      expect(SgtnClient::LocaleUtil.get_best_locale('ja')).to eq 'ja'
    end
    
    it "get_best_locale_target_locale_is_nil" do
      expect(SgtnClient::LocaleUtil.get_best_locale(nil)).to eq SgtnClient::Config.configurations.default
    end
    
    it "fallback_target_locale_is_source" do
      expect(SgtnClient::LocaleUtil.fallback(SgtnClient::Config.configurations.default)).to eq SgtnClient::Config.configurations.default
    end 
    it "fallback" do
      expect(SgtnClient::LocaleUtil.fallback('ja-JP')).to eq 'ja'
      expect(SgtnClient::LocaleUtil.fallback('de-DE')).to eq 'de'
      expect(SgtnClient::LocaleUtil.fallback('zh')).to eq 'zh'
      expect(SgtnClient::LocaleUtil.fallback('zh-Hans')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.fallback('zh-Hant')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.fallback('zh-CN')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.fallback('zh-TW')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.fallback('zh-Hans-CN')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.fallback('zh-Hant-TW')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.fallback('kong-kong')).to eq 'kong-kong'
    end

    it "process_locale" do
      expect(SgtnClient::LocaleUtil.process_locale('en')).to eq 'en'
      expect(SgtnClient::LocaleUtil.process_locale('de-DE')).to eq 'de-DE'
      expect(SgtnClient::LocaleUtil.process_locale(:'de-DE')).to eq 'de-DE'
      expect(SgtnClient::LocaleUtil.process_locale(:ja)).to eq 'ja'
      expect(SgtnClient::LocaleUtil.process_locale(:'ja')).to eq 'ja'
    end

    it "process_locale_nil" do
      expect(SgtnClient::LocaleUtil.process_locale(nil)).to eq SgtnClient::Config.configurations.default
    end

    it "get_source_locale" do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["default_language"] = 'ja'
      expect(SgtnClient::LocaleUtil.get_source_locale()).to eq 'ja'
    end
    
    it "get_source_locale_source_is_nil" do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["default_language"] = nil
      expect(SgtnClient::LocaleUtil.get_source_locale()).to eq 'en'
    end
  end
end
