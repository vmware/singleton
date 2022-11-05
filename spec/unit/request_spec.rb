# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe SgtnClient do
  describe "Locale" do

    before :each do
      Sgtn.vip_server = nil
      RequestStore.store[:locale] = 'zh-Hans'
      RequestStore.store[:component] = 'JAVA'
    end

    it "GET" do
      expect(SgtnClient::T.s("helloworld")).to eq '你好世界'
      expect(SgtnClient::T.s_f("welcome", ["虚拟世界", "机器人"])).to eq '机器人，欢迎登录虚拟世界！'
      expect(SgtnClient::T.c().component).to eq 'JAVA'
    end
  end

end
