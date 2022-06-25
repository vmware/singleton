# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe SgtnClient do
  describe "Locale" do

    before :all do
      SgtnClient.load("./spec/config/sgtnclient.yml", 'test')
    end

    it "get_best_locale_exact_match" do
      expect(SgtnClient::LocaleUtil.get_best_locale('de')).to eq 'de'
      expect(SgtnClient::LocaleUtil.get_best_locale('en')).to eq 'en'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hans')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hant')).to eq 'zh-Hant'
    end

    it "get_best_locale_special_mapping" do
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-CN')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-TW')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-TW-subtag')).to eq 'zh-Hant'
    end

    it "get_best_locale_best_match" do
      expect(SgtnClient::LocaleUtil.get_best_locale('de-DE')).to eq 'de'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hant-HK')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('de-CH-1901')).to eq 'de'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hant-HK')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hans-CN')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hant-TW')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh_Hant_TW')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hant_TW')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh_Hant-TW')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh_cn')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('ZH-Cn')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('DE')).to eq 'de'
      expect(SgtnClient::LocaleUtil.get_best_locale('ZH-HANS-CN')).to eq 'zh-Hans'
    end
    
    it "get_best_locale_target_locale_is_nil" do
      expect(SgtnClient::LocaleUtil.get_best_locale(nil)).to eq SgtnClient::LocaleUtil.get_default_locale
    end

    it "get_best_locale_invalid_or_unsupported_target" do
      expect(SgtnClient::LocaleUtil.get_best_locale('invalid')).to eq SgtnClient::LocaleUtil.get_default_locale
      expect(SgtnClient::LocaleUtil.get_best_locale('fil_PH')).to eq SgtnClient::LocaleUtil.get_default_locale
    end

    # it "get_source_locale" do
    #   env = SgtnClient::Config.default_environment
    #   config_lang = SgtnClient::Config.configurations[env]["default_language"]

    #   SgtnClient::Config.configurations[env]["default_language"] = nil
    #   expect(SgtnClient::LocaleUtil.get_source_locale()).to eq 'en'

    #   SgtnClient::Config.configurations[env]["default_language"] = 'en'
    #   expect(SgtnClient::LocaleUtil.get_source_locale()).to eq 'en'
      
    #   # SgtnClient::Config.configurations[env]["default_language"] = 'zh-Hans'
    #   # expect(SgtnClient::LocaleUtil.get_source_locale()).to eq 'zh-Hans'

    # ensure
    #   SgtnClient::Config.configurations[env]["default_language"] = config_lang
    # end
  end

end
