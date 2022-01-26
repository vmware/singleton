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
      SgtnClient.logger.debug "----------Start to get translation from server---------"
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      # get translation from cache
      SgtnClient.logger.debug "----------Start to get translation from cache---------"
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'

      # get from server again after data is expired
      SgtnClient.logger.debug "----------Sleep 70s---------"
      #sleep 70
      SgtnClient.logger.debug "----------Start to get translation from expired cache---------"
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'

      SgtnClient.logger.debug "----------End to get translation from server---------"
    end


    it "default" do
      env = SgtnClient::Config.default_environment
      default_language = "default"
      component = "JAVA"
      SgtnClient::Source.loadBundles(default_language)
      config_lang = SgtnClient::Config.configurations[env]["default_language"]
      if config_lang == nil || config_lang == 'en'
        expect(SgtnClient::CacheUtil.get_cachekey(component, SgtnClient::LocaleUtil.fallback('en-US'))).to eq 'test_4.8.1_JAVA_default'
        expect(SgtnClient::CacheUtil.get_cachekey(component, SgtnClient::LocaleUtil.fallback('de-DE'))).to eq 'test_4.8.1_JAVA_de'
      end
      SgtnClient::Config.configurations[env]["default_language"] = 'zh-Hans'
      expect(SgtnClient::CacheUtil.get_cachekey(component, SgtnClient::LocaleUtil.fallback('en-US'))).to eq 'test_4.8.1_JAVA_en'
      expect(SgtnClient::CacheUtil.get_cachekey(component, SgtnClient::LocaleUtil.fallback('zh-Hans'))).to eq 'test_4.8.1_JAVA_default'
      expect(SgtnClient::CacheUtil.get_cachekey(component, SgtnClient::LocaleUtil.fallback('zh-CN'))).to eq 'test_4.8.1_JAVA_default'
    end

  end

end
