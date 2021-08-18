require 'spec_helper'

describe SgtnClient do
  describe "Cache" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'online'
      SgtnClient::Config.configurations[env]["cache_expiry_period"] = 1
      SgtnClient::Source.loadBundles("default")
    end

    it "GETTranslation" do

      # get translation from server
      SgtnClient.logger.debug "----------Start to get translation from server---------"
      expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "zh-Hans")).to eq '主机'
      # get translation from cache
      SgtnClient.logger.debug "----------Start to get translation from cache---------"
      expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "zh-Hans")).to eq '主机'

      # get from server again after data is expired
      SgtnClient.logger.debug "----------Sleep 70s---------"
      puts Time.now
      #sleep 70
      puts Time.now
      SgtnClient.logger.debug "----------Start to get translation from expired cache---------"
      expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "zh-Hans")).to eq '主机'

      SgtnClient.logger.debug "----------End to get translation from server---------"
    end
  end

end
