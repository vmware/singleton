# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'spec_helper'

describe SgtnClient do
  describe "SourceAPI" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      @locale="default"
      @component="NEW"
      SgtnClient::Source.loadBundles(@locale)
    end

    it "getSource" do
      key="new_welcome"
      str = SgtnClient::Source.getSource(@component, key, SgtnClient::Config.configurations.default)
      expect(str).to eq 'New %2$s, welcome login %1$s!'
    end

    it "getSources" do
      str = SgtnClient::Source.getSources(@component, SgtnClient::Config.configurations.default)
      expect(str).not_to eq be_nil
    end

    it "loadBundles" do
      cache_key = SgtnClient::CacheUtil.get_cachekey(@component, @locale)
      expect(SgtnClient::CacheUtil.get_cache(cache_key)).not_to eq be_nil
    end

  end

end
