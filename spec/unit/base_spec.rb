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
      expect(SgtnClient::Base.compare_source("JAVA", "helloworld", default_language, 'Hello world', '你好世界')).to eq '你好世界'
    end

    it "compareSource_old" do
      env = SgtnClient::Config.default_environment
      default_language = SgtnClient::Config.configurations[env]["default_language"]
      expect(SgtnClient::Base.compare_source("JAVA", "old_helloworld", default_language, 'Source Hello world', 'Source Hello world')).to eq 'Source Hello world'
    end
    
  end

end
