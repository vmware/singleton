# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'spec_helper'

describe 'Cache' do
  before :all do
    SgtnClient::Config.instance_variable_set('@loader', nil)
    SgtnClient::CacheUtil.clear_cache
  end

  before :each do
    env = SgtnClient::Config.default_environment
    SgtnClient::Config.configurations[env]["vip_server"] = nil
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

  it "get_cachekey_source_locale" do
    env = SgtnClient::Config.default_environment
    product_name = SgtnClient::Config.configurations[env]["product_name"].to_s
    version = SgtnClient::Config.configurations[env]["version"].to_s
    locale = SgtnClient::Config.configurations.default
    expect(locale).to eq 'default'
    expect(SgtnClient::CacheUtil.get_cachekey("java", locale)).to eq "#{product_name}_#{version}_java_#{locale}"
  end

  it "get_cachekey_en_locale" do
    env = SgtnClient::Config.default_environment
    product_name = SgtnClient::Config.configurations[env]["product_name"].to_s
    version = SgtnClient::Config.configurations[env]["version"].to_s
    locale = SgtnClient::LocaleUtil.get_source_locale()
    expect(locale).to eq 'en'
    expect(SgtnClient::CacheUtil.get_cachekey("java", locale)).to eq "#{product_name}_#{version}_java_#{locale}"
  end
end
