# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'spec_helper'
require 'request_store'

describe SgtnClient do
  describe "Locale" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Source.loadBundles("default")
      RequestStore.store[:locale] = 'zh-Hans'
      RequestStore.store[:component] = 'JAVA'
    end

    it "GET" do
      expect(SgtnClient::T.s("helloworld")).to eq '你好世界'
      expect(SgtnClient::T.s_f("welcome", ["虚拟世界", "机器人"])).to eq '机器人，欢迎登录虚拟世界！'
      expect(SgtnClient::T.c()["component"]).to eq 'JAVA'
    end

    it "compare_component_sources" do
      RequestStore.store[:locale] = 'zh-Hant'
      RequestStore.store[:component] = 'JAVA'
      expect(SgtnClient::T.c(true)["messages"]["old_helloworld"]).to eq 'Source Hello world'
    end
  end

end
