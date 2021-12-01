require 'spec_helper'

describe SgtnClient do
  describe "Cache" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Config.configurations[env]["cache_expiry_period"] = 1
      SgtnClient::Source.loadBundles("default")
    end

    it "GETTranslation" do

      # get translation from server
      SgtnClient.logger.debug "----------Start to get translation from server---------"
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'
      # get translation from cache
      SgtnClient.logger.debug "----------Start to get translation from cache---------"
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'

      # get from server again after data is expired
      SgtnClient.logger.debug "----------Sleep 70s---------"
      #sleep 70
      SgtnClient.logger.debug "----------Start to get translation from expired cache---------"
      expect(SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")).to eq '你好世界'

      SgtnClient.logger.debug "----------End to get translation from server---------"
    end
  end

end
