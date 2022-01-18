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

    it "GET" do
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
    end

    it "NewComponent" do
      expect(SgtnClient::Translation.getString("NEW", "new_hello", "zh-Hans")).to eq 'New Hello'
      expect(SgtnClient::CacheUtil.get_cache("test_4.8.1_NEW_zh-Hans")["default"]["new_hello"]).to eq 'New Hello'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("NEW", "new_hello", "zh-Hans")).to eq 'New Hello'
    end

    it "NonExistingKey" do
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello'
    end
  end

end
