require 'spec_helper'

describe SgtnClient do
  describe "OnlineAPI" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::CacheUtil.clear_cache()
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Source.loadBundles("default")
    end

    it "GET_EN" do
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "en")).to eq 'Hello world'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "en")).to eq 'Hello world'
    end

    it "GET_NIL_LOCALE" do
      allow(SgtnClient::LocaleUtil).to receive(:process_locale).and_return(SgtnClient::Config.configurations.default)
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", nil)).to eq 'Hello world'
    end 

    it "GET_zh-Hans" do
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'

      expect(SgtnClient::Translation.getString("JAVA", "old_helloworld", "zh-Hans")).to eq 'Source Hello world'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "old_helloworld", "zh-Hans")).to eq 'Source Hello world'
    end

    it "NewComponent" do
      expect(SgtnClient::Translation.getString("NEW", "new_hello", "zh-Hans")).to eq 'New Hello'
      env = SgtnClient::Config.default_environment
      if SgtnClient::Config.configurations[env]["disable_cache"] == false
        expect(SgtnClient::CacheUtil.get_cache("test_4.8.1_NEW_zh-Hans")[1]["default"]["new_hello"]).to eq 'New Hello'
      end
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("NEW", "new_hello", "zh-Hans")).to eq 'New Hello'
      jsonObj =  SgtnClient::Translation.getStrings("NEW", "zh-Hans")
      expect(jsonObj["component"]).to eq 'NEW'
      expect(jsonObj["locale"]).to eq 'source'
    end

    it "NonExistingComponent" do
      expect(SgtnClient::Translation.getString("NonExisting", "new_hello", "zh-Hans")).to eq nil
      emptyObj = {}
      expect(SgtnClient::Translation.getStrings("NonExisting", "zh-Hans")).to eq emptyObj
    end

    it "NonExistingLanuage" do
      expect(SgtnClient::Translation.getString("JAVA", "hello", "kk_NonExistingLanuage")).to eq 'Hello'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello", "kk_NonExistingLanuage")).to eq 'Hello'

      # enable to 'online' mode and observe the log file to see if there are more requests to server
      jsonObj = SgtnClient::Translation.getStrings("JAVA", "kk_NonExistingLanuage");
      jsonObj = SgtnClient::Translation.getStrings("JAVA", "kk_NonExistingLanuage");
      jsonObj = SgtnClient::Translation.getStrings("JAVA", "kk_NonExistingLanuage");
      expect(jsonObj["component"]).to eq 'JAVA'
      expect(jsonObj["locale"]).to eq 'source'

    end

    it "NonExistingKey" do
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello'
    end
  end

end
