# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'spec_helper'

describe SgtnClient do
  describe "Base" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Source.loadBundles("default")
    end

    it "compareSource_same" do
      env = SgtnClient::Config.default_environment
      default_language = SgtnClient::Config.configurations[env]["default_language"]
      allow(SgtnClient::Base).to receive(:compareSource).exactly(5).times.and_return('你好世界')
      expect(SgtnClient::Base.compareSource("JAVA", "helloworld", default_language, 'Hello world', '你好世界')).to eq '你好世界'
    end

    it "compareSource_old" do
      env = SgtnClient::Config.default_environment
      default_language = SgtnClient::Config.configurations[env]["default_language"]
      allow(SgtnClient::Base).to receive(:compareSource).exactly(5).times.and_return('Source Hello world')
      expect(SgtnClient::Base.compareSource("JAVA", "old_helloworld", default_language, 'Source Hello world', 'Source Hello world')).to eq 'Source Hello world'
    end

    it "fallback_locale" do
      env = SgtnClient::Config.default_environment
      default_language = SgtnClient::Config.configurations[env]["default_language"]
      
      SgtnClient::Config.configurations[env]["default_language"] = nil
      expect(SgtnClient::Base.fallback_locale('en')).to eq 'default'
      expect(SgtnClient::Base.fallback_locale('zh-Hans')).to eq 'zh-Hans'

      SgtnClient::Config.configurations[env]["default_language"] = 'en'
      expect(SgtnClient::Base.fallback_locale('en')).to eq 'default'
      expect(SgtnClient::Base.fallback_locale('zh-Hans')).to eq 'zh-Hans'

      SgtnClient::Config.configurations[env]["default_language"] = 'zh-Hans'
      expect(SgtnClient::Base.fallback_locale('en')).to eq 'en'
      expect(SgtnClient::Base.fallback_locale('zh-Hans')).to eq 'default'
      
      SgtnClient::Config.configurations[env]["default_language"] = default_language
    end
    
  end

end
