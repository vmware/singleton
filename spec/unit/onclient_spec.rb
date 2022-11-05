# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe SgtnClient do
  describe "OnlineAPI" do

    before :each do
      clear_cache
      Sgtn.vip_server = nil
    end

    it "GET_EN" do
      allow(SgtnClient::LocaleUtil).to receive(:get_best_locale).exactly(3).times.and_return('en')
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "en")).to eq 'Hello world'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "en")).to eq 'Hello world'
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", :en)).to eq 'Hello world'
    end

    it "GET_NIL_LOCALE" do
      allow(SgtnClient::LocaleUtil).to receive(:get_best_locale).and_return('en')
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", nil)).to eq 'Hello world'
    end 

    it "GET" do
      allow(SgtnClient::LocaleUtil).to receive(:get_best_locale).exactly(5).times.and_return('zh-Hans')
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", :"zh-Hans")).to eq '你好世界'

      expect(SgtnClient::Translation.getString("JAVA", "old_helloworld", "zh-Hans")).to eq 'Source Hello world'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "old_helloworld", "zh-Hans")).to eq 'Source Hello world'
    end

    it "NewComponent" do
      expect(SgtnClient::Translation.getString("NEW", "new_hello", "zh-Hans")).to eq 'New Hello'
      # if SgtnClient::Config.configurations[env]["disable_cache"] == false
        expect(get_cache(SgtnClient::Common::BundleID.new("NEW", "en"))["new_hello"]).to eq 'New Hello'
      # end
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("NEW", "new_hello", "zh-Hans")).to eq 'New Hello'
      jsonObj =  SgtnClient::Translation.getStrings("NEW", "zh-Hans")
      expect(jsonObj.component).to eq 'NEW'
      expect(jsonObj.locale).to eq SgtnClient::LocaleUtil.get_source_locale
    end

    it "NonExistingComponent" do
      expect(SgtnClient::Translation.getString("NonExisting", "new_hello", "zh-Hans")).to eq nil
      expect(SgtnClient::Translation.getStrings("NonExisting", "zh-Hans")).to eq nil
    end

    it "NonExistingLanuage" do
      expect(SgtnClient::Translation.getString("JAVA", "hello", "kk_NonExistingLanuage")).to eq 'Hello'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello", "kk_NonExistingLanuage")).to eq 'Hello'

      # enable to 'online' mode and observe the log file to see if there are more requests to server
      jsonObj = SgtnClient::Translation.getStrings("JAVA", "kk_NonExistingLanuage");
      jsonObj = SgtnClient::Translation.getStrings("JAVA", "kk_NonExistingLanuage");
      jsonObj = SgtnClient::Translation.getStrings("JAVA", "kk_NonExistingLanuage");
      expect(jsonObj.component).to eq 'JAVA'
      expect(jsonObj.locale).to eq SgtnClient::LocaleUtil.get_source_locale

    end

    it "NonExistingKey" do
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello'
      # get from cache in 2nd time
      expect(SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")).to eq 'Hello'
    end
  end

end
