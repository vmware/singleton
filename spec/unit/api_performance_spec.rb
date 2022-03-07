# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'spec_helper'

describe SgtnClient do
  describe "OfflineAPI" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Source.loadBundles("default")
    end

    it "get_string_performance" do
      t1 = DateTime.now.strftime("%Q")
      SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")
      t2 = DateTime.now.strftime("%Q")
      expect(t2.to_i-t1.to_i).to be < 100
    end

    it "get_strings_performance" do
      RequestStore.store[:locale] = 'zh-Hant'
      RequestStore.store[:component] = 'JAVA'
      t1 = DateTime.now.strftime("%Q")
      SgtnClient::T.c(true)["messages"]["old_helloworld"]
      t2 = DateTime.now.strftime("%Q")
      expect(t2.to_i-t1.to_i).to be < 100
    end

  end

end
