require 'spec_helper'

describe SgtnClient do
  describe "OnlineAPI" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::CacheUtil.clear_cache()
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'online'
      SgtnClient::Source.loadBundles("default")
    end

    it "GET_EN" do
      expect(SgtnClient::Translation.getString("JAVA", "hello", "en")).to eq 'Hello world'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello", "en")).to eq 'Hello world'
    end

    it "GET" do
      expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "zh-Hans")).to eq '主机'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "zh-Hans")).to eq '主机'
    end

    it "NonExistingKey" do
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello world'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello world'
    end
  end

end
