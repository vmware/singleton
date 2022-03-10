# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

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
    end

    it "get_cachekey" do
      env = SgtnClient::Config.default_environment
      product_name = SgtnClient::Config.configurations[env]["product_name"].to_s
      version = SgtnClient::Config.configurations[env]["version"].to_s
      expect(SgtnClient::CacheUtil.get_cachekey("java", "zh-Hans")).to eq "#{product_name}_#{version}_java_zh-Hans"
    end

    it "get_cachekey_sourceLocale" do
      env = SgtnClient::Config.default_environment
      product_name = SgtnClient::Config.configurations[env]["product_name"].to_s
      version = SgtnClient::Config.configurations[env]["version"].to_s
      locale = SgtnClient::Config.configurations.default
      expect(SgtnClient::CacheUtil.get_cachekey("java", locale)).to eq "#{product_name}_#{version}_java_#{locale}"
    end
    
  end

end
