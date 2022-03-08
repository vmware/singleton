# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'spec_helper'

describe SgtnClient do
  describe "Locale" do

    before :each do
      SgtnClient::Config.configurations.default = 'default'
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
      config_lang = SgtnClient::Config.configurations[env]["default_language"]

      SgtnClient::Config.configurations[env]["default_language"] = nil
      expect(SgtnClient::LocaleUtil.get_source_locale()).to eq 'en'

      SgtnClient::Config.configurations[env]["default_language"] = 'en'
      expect(SgtnClient::LocaleUtil.get_source_locale()).to eq 'en'
      
      SgtnClient::Config.configurations[env]["default_language"] = 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_source_locale()).to eq 'zh-Hans'

      SgtnClient::Config.configurations[env]["default_language"] = config_lang
    end

    it "fallback_locale" do
      env = SgtnClient::Config.default_environment
      default_language = SgtnClient::Config.configurations[env]["default_language"]
      
      SgtnClient::Config.configurations[env]["default_language"] = nil
      expect(SgtnClient::LocaleUtil.get_best_locale('en')).to eq 'en'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hans')).to eq 'zh-Hans'

      SgtnClient::Config.configurations[env]["default_language"] = 'en'
      expect(SgtnClient::LocaleUtil.get_best_locale('en')).to eq 'en'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hans')).to eq 'zh-Hans'

      SgtnClient::Config.configurations[env]["default_language"] = 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('en')).to eq 'en'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hans')).to eq 'zh-Hans'
      
      SgtnClient::Config.configurations[env]["default_language"] = default_language
    end
  end

end
