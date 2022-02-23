require 'rest-client'
require 'rspec'
require 'singleton-ruby'

include SgtnClient
describe "Translation test" do

    context "test default_language config" do
        it "no default_labguage and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "test")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message11", "de")).to eq(nil)
            expect(SgtnClient::Translation.getString("about", "about.key3", "de")).to eq("fall back key31")          
        end

        it "default_language is en and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale1")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("abou34324t", "about.message", "de")).to eq(nil)
            expect(SgtnClient::Translation.getString("about43242", "about.key3", "de")).to eq(nil)          
        end

        it "default_language is zh-Hans and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale2")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString_f("about32", "about.test123",["a1","a2","a3"], "de")).to eq(nil)
        end

        
        it "default_language is da and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale3")
            SgtnClient::Source.loadBundles("default")
            puts SgtnClient::Translation.getStrings("about12", "en-US")
        end

        
        it "default_language is abcd and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale4")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("Your application description page.")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test de key") 
        end

        it "default_language is en and loadbundle is en.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale1")
            SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("fall back about")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test de key") 

        end

        it "default_language is fr and loadbundle is en.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale5")
            SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("fall back about")
            expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("fall back about") 

        end

        

        it "string test disablecache is true" do
            SgtnClient.load("./config/sgtnclient.yml", "testdisablecachetrue")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("test fr key")
            expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("test fr key")
            #expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "de-DE")).to eq("host__de_online")
        end

        it "strings test disablecache is true" do
            SgtnClient.load("./config/sgtnclient.yml", "testdisablecachetrue")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getStrings("about", "zh-CN")["messages"]["about.description"]).to eq("使用此区域可提供其他信息")
            expect(SgtnClient::Translation.getStrings("about", "zh-CN")["messages"]["about.description"]).to eq("使用此区域可提供其他信息")
            #expect(SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "de-DE")).to eq("host__de_online")
        end

    end


end
