# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe SgtnClient do
  describe "OfflineAPI" do

    before :all do
      Sgtn.vip_server = nil
    end

    it "GET" do
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'

      expect(SgtnClient::Translation.getString_f("JAVA", "welcome", ["虚拟世界", "机器人"], "zh-Hans")).to eq '机器人，欢迎登录虚拟世界！'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString_f("JAVA", "welcome", ["虚拟世界", "机器人"], "zh-Hans")).to eq '机器人，欢迎登录虚拟世界！' 
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

      expect(SgtnClient::Translation.getString("JAVA", "hello.nonexisting", "zh-Hans")).to eq nil
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello.nonexisting", "zh-Hans")).to eq nil

      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello'

      expect(SgtnClient::Translation.getString_f("JAVA", "login", ["VM", "Robot"], "zh-Hans")).to eq 'Robot login VM!'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString_f("JAVA", "login", ["VM", "Robot"], "zh-Hans")).to eq 'Robot login VM!'

      expect(SgtnClient::Translation.getString_f("JAVA", "type_error", {"error": "错误数字类型", "correct": "正确数字类型"}, "zh-Hans")).to eq '检测到%错误数字类型，请输入%正确数字类型!'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString_f("JAVA", "type_error", {"error": "错误数字类型", "correct": "正确数字类型"}, "zh-Hans")).to eq '检测到%错误数字类型，请输入%正确数字类型!'
    end

    it "Component" do
      jsonObj = SgtnClient::Translation.getStrings("JAVA", "zh-Hans");
      expect(jsonObj.component).to eq 'JAVA'
      # get from cache in 2nd time
      jsonObj_c = SgtnClient::Translation.getStrings("JAVA", "zh-Hans");
      expect(jsonObj_c.component).to eq 'JAVA'
      # get non-translation of a locale and fallback to source file
      default_sources = SgtnClient::Translation.getStrings("JAVA", "zh-ff");
      expect(default_sources.locale).to eq SgtnClient::LocaleUtil.get_source_locale
    end
  end

end
