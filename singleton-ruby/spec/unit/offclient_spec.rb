require 'spec_helper'

describe SgtnClient do
  describe "OfflineAPI" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Source.loadBundles("default")
    end

    it "GET" do
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'

      expect(SgtnClient::Translation.getString_f("JAVA", "welcome", ["机器人", "虚拟世界"], "zh-Hans")).to eq '机器人，欢迎登录虚拟世界！'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString_f("JAVA", "welcome", ["机器人", "虚拟世界"], "zh-Hans")).to eq '机器人，欢迎登录虚拟世界！'    
    end

    it "GET_EN" do
      expect(SgtnClient::Translation.getString("JAVA", "hello", "en")).to eq 'Hello'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello", "en")).to eq 'Hello'
    end

    it "GET_zh_CN" do
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-CN")).to eq '你好世界'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-CN")).to eq '你好世界'
    end

    it "NonExistingKey" do
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello'

      expect(SgtnClient::Translation.getString_f("JAVA", "login", ["Robot", "VM"], "zh-Hans")).to eq 'Robot login VM!'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString_f("JAVA", "login", ["Robot", "VM"], "zh-Hans")).to eq 'Robot login VM!'    
    end

    it "Component" do
      jsonObj = SgtnClient::Translation.getStrings("JAVA", "zh-Hans");
      expect(jsonObj["component"]).to eq 'JAVA'
      # get from cache in 2nd time
      jsonObj_c = SgtnClient::Translation.getStrings("JAVA", "zh-Hans");
      expect(jsonObj_c["component"]).to eq 'JAVA'
    end
  end

end
