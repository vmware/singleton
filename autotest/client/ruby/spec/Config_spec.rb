require 'rest-client'
require 'rspec'
require 'singleton-ruby'

include SgtnClient
describe SgtnClient do
    # before :each do
    #     SgtnClient.load("./config/sgtnclient.yml", "development")
    # end

    context "Normal Config" do
        it "Config online" do
            SgtnClient.load("./config/sgtnclient.yml", "test")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test de key")
        end

        it "config offline" do
            SgtnClient.load("./config/sgtnclient.yml", "development")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test de key offline")
        end

        it "config mode" do
            SgtnClient.load("./config/sgtnclient.yml", "testmode")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test de key")
           #expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test de key offline")
            #xxx = SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a3","a2","a3"], "de")
            # puts xxx
            #expect(SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a1","a2","a3"], "de")).to eq("主机")
        end

        
        it "config testcache_expiry_periodmode" do
            SgtnClient.load("./config/sgtnclient.yml", "testcache_expiry_periodmode")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test__de_value_change")
            # xxx = SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a3","a2","a3"], "de")
            # puts xxx
            #expect(SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a1","a2","a3"], "de")).to eq("主机")
        end

        
        it "config testdisable_cache" do
            SgtnClient.load("./config/sgtnclient.yml", "testdisable_cache")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test__de_value_change")
            # xxx = SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a3","a2","a3"], "de")
            # puts xxx
            #expect(SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a1","a2","a3"], "de")).to eq("主机")
        end

        it "config testversion_fallback" do
            SgtnClient.load("./config/sgtnclient.yml", "testversion_fallback")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("test fr key")
            #expect(SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a1","a2","a3"], "de")).to eq("主机")
        end

    end

    

    context "Abnormal config" do
        it "config testproductname" do
            SgtnClient.load("./config/sgtnclient.yml", "testproductname")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("fall back about")
            # xxx = SgtnClient::Translation.getStrings("JAVA", "zh-CN")
            # expect(xxx["messages"]["com.vmware.loginsight.web.settings.stats.StatsTableType.eventsIngestionRatePerSecond"]).to eq("事件载入速率 (每秒)")
        end

        it "config testversion1" do
            SgtnClient.load("./config/sgtnclient.yml", "testversion")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("fall back about")
            # expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "de-DE")).to eq("host__de_online")
        end

        it "config testvipserver" do
            SgtnClient.load("./config/sgtnclient.yml", "testvipserver")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("fall back about")
            # expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "de-DE")).to eq("host__de_online")
        end

        it "config testbundlemode" do
            SgtnClient.load("./config/sgtnclient.yml", "testbundlemode")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("test fr key")
            #expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "de-DE")).to eq("host__de_online")
        end

        it "config testtranslationbundle" do
            SgtnClient.load("./config/sgtnclient.yml", "testtranslationbundle")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("fall back about")
            #expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "de-DE")).to eq("host__de_online")
        end

        # it "test testsourcebundle" do
        #     SgtnClient.load("./config/sgtnclient.yml", "testsourcebundle")
        #     SgtnClient::Source.loadBundles("default")
        #     expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("test fr key")
        #     #expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "de-DE")).to eq("host__de_online")
        # end

        # it "test testcacheexpiryperiod" do
        #     SgtnClient.load("./config/sgtnclient.yml", "testcacheexpiryperiod")
        #     SgtnClient::Source.loadBundles("default")
        #     expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("test fr key")
        #     #expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "de-DE")).to eq("host__de_online")
        # end

        it "test testdisablecache" do
            SgtnClient.load("./config/sgtnclient.yml", "testdisablecache")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("test fr key")
            #expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "de-DE")).to eq("host__de_online")
        end
    end
end
