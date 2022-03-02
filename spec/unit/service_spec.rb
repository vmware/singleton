# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'spec_helper'

describe SgtnClient do
  describe "ServiceUtil" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Source.loadBundles("default")
    end

    it "compareSource_same" do
      expect(SgtnClient::ServiceUtil.compare_source("JAVA", "helloworld", 'Hello world', '你好世界')).to eq '你好世界'
    end

    it "compareSource_old" do
      expect(SgtnClient::ServiceUtil.compare_source("JAVA", "old_helloworld", 'Source Hello world', 'Source Hello world')).to eq 'Source Hello world'
    end
    
  end

end
