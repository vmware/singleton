require 'spec_helper'

describe SgtnClient do
  describe "Base" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Source.loadBundles("default")
    end

    it "compareSource" do
      env = SgtnClient::Config.default_environment
      default_language = SgtnClient::Config.configurations[env]["default_language"]
      expect(SgtnClient::Base.compareSource("JAVA", "helloworld", default_language, 'Hello world', '你好世界')).to eq '你好世界'
      expect(SgtnClient::Base.compareSource("JAVA", "helloworld", default_language, 'Hello world', 'test')).to eq 'test'
    end

    it "fallback_locale" do
      env = SgtnClient::Config.default_environment
      default_language = SgtnClient::Config.configurations[env]["default_language"]
      if default_language.nil? || default_language == 'en'
        expect(SgtnClient::Base.fallback_locale('en')).to eq 'default'
        expect(SgtnClient::Base.fallback_locale('zh-Hans')).to eq 'zh-Hans'
      end
      SgtnClient::Config.configurations[env]["default_language"] = 'zh-Hans'
      expect(SgtnClient::Base.fallback_locale('en')).to eq 'en'
      expect(SgtnClient::Base.fallback_locale('zh-Hans')).to eq 'default'
      SgtnClient::Config.configurations[env]["default_language"] = default_language
    end
    
  end

end
