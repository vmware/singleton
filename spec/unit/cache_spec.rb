require 'spec_helper'

describe SgtnClient do
  describe "Cache" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Config.configurations[env]["cache_expiry_period"] = 1
      SgtnClient::Source.loadBundles("default")
    end

    it "GETTranslation" do
      # get translation from server
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      # get translation from cache
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      # get from server again after data is expired
      #sleep 70
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      #sleep 10
    end


    it "default" do
      env = SgtnClient::Config.default_environment
      component = "JAVA"
      default_language = "default"
      default_cache_key = "test_4.8.1_JAVA_default"
      SgtnClient::Source.loadBundles(default_language)
      config_lang = SgtnClient::Config.configurations[env]["default_language"]
      if config_lang == nil || config_lang == 'en'
        expect(SgtnClient::CacheUtil.get_cachekey(component, SgtnClient::LocaleUtil.fallback('en-US'))).to eq default_cache_key
        expect(SgtnClient::CacheUtil.get_cachekey(component, SgtnClient::LocaleUtil.fallback('de-DE'))).to eq 'test_4.8.1_JAVA_de'
      end
      # reset the default language to 'zh-Hans'
      SgtnClient::Config.configurations[env]["default_language"] = 'zh-Hans'
      expect(SgtnClient::CacheUtil.get_cachekey(component, SgtnClient::LocaleUtil.fallback('zh-Hans'))).to eq default_cache_key
      expect(SgtnClient::CacheUtil.get_cachekey(component, SgtnClient::LocaleUtil.fallback('zh-CN'))).to eq default_cache_key
      expect(SgtnClient::CacheUtil.get_cachekey(component, SgtnClient::LocaleUtil.fallback('en-US'))).to eq 'test_4.8.1_JAVA_en'
    end

  end

end
