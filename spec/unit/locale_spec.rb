# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe SgtnClient do
  describe "Locale" do

    it "get_best_locale_exact_match" do
      expect(SgtnClient::LocaleUtil.get_best_locale('de', 'JAVA')).to eq 'de'
      expect(SgtnClient::LocaleUtil.get_best_locale('en', 'JAVA')).to eq 'en'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hans', 'JAVA')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hant', 'JAVA')).to eq 'zh-Hant'
    end

    it "get_best_locale_special_mapping" do
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-CN', 'JAVA')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-TW', 'JAVA')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-TW-subtag', 'JAVA')).to eq 'zh-Hant'
    end

    it "get_best_locale_best_match" do
      expect(SgtnClient::LocaleUtil.get_best_locale('de-DE', 'JAVA')).to eq 'de'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hant-HK', 'JAVA')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('de-CH-1901', 'JAVA')).to eq 'de'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hant-HK', 'JAVA')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hans-CN', 'JAVA')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hant-TW', 'JAVA')).to eq 'zh-Hant'

      expect(SgtnClient::LocaleUtil.get_best_locale('zh_Hant_TW', 'JAVA')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh-Hant_TW', 'JAVA')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh_Hant-TW', 'JAVA')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.get_best_locale('zh_cn', 'JAVA')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('ZH-Cn', 'JAVA')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.get_best_locale('DE', 'JAVA')).to eq 'de'
      expect(SgtnClient::LocaleUtil.get_best_locale('ZH-HANS-CN', 'JAVA')).to eq 'zh-Hans'
    end
    
    it "get_best_locale_target_locale_is_nil" do
      expect(SgtnClient::LocaleUtil.get_best_locale(nil, 'JAVA')).to eq SgtnClient::LocaleUtil.get_default_locale
    end

    it "get_best_locale_invalid_or_unsupported_target" do
      expect(SgtnClient::LocaleUtil.get_best_locale('invalid', 'JAVA')).to eq SgtnClient::LocaleUtil.get_default_locale
      expect(SgtnClient::LocaleUtil.get_best_locale('fil_PH', 'JAVA')).to eq SgtnClient::LocaleUtil.get_default_locale
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
