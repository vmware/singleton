require 'rest-client'
require 'rspec'
require 'singleton-ruby'

include SgtnClient
describe SgtnClient do

    context "string the cache expiry period is 1 minute" do
        before :each do
            SgtnClient.load("./config/sgtnclient.yml", "production")
            SgtnClient::Source.loadBundles("default")
        end

        it "update value of key" do
            system("E:\\E2\\ruby_client\\bat_script\\RevertString_de.bat")
            #expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test__de_value")
            puts SgtnClient::Translation.getStrings("about", "de")
            #system("E:\\E2\\ruby_client\\bat_script\\ModifyString_de.bat")
            sleep 55
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test__de_value")
            puts SgtnClient::Translation.getStrings("about", "de")
            sleep 10
            #expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test__de_value_change")
            puts SgtnClient::Translation.getStrings("about", "de")
        end

        ### Because keys cannot be removed through the API, they need to be changed manually if repeated testing is required
        it "add new key" do
            expect(SgtnClient::Translation.getString("about", "about.addkey", "de")).to eq(nil)
            expect(SgtnClient::Translation.getString("about", "about.addkey", "en")).to eq(nil)
            system("E:\\E2\\ruby_client\\bat_script\\addkey_en.bat")
            system("E:\\E2\\ruby_client\\bat_script\\addkey_de.bat")
            sleep 55
            expect(SgtnClient::Translation.getString("about", "about.addkey", "de")).to eq(nil)
            expect(SgtnClient::Translation.getString("about", "about.addkey", "en")).to eq(nil)
            sleep 10
            expect(SgtnClient::Translation.getString("about", "about.addkey", "de")).to eq("add de value")
            expect(SgtnClient::Translation.getString("about", "about.addkey", "en")).to eq("add en value")
        end

        ### Because locale file cannot be removed through the API, they need to be changed manually if repeated testing is required
        it "add new locale" do
            expect(SgtnClient::Translation.getString("about", "about.title", "pl")).to eq(nil)
            expect(SgtnClient::Translation.getString("about", "about.title", "en")).to eq("About")
            system("E:\\E2\\ruby_client\\bat_script\\ModifyString_pl.bat")
            sleep 5
            #puts SgtnClient::Translation.getString("about", "about.title", "pl")
            expect(SgtnClient::Translation.getString("about", "about.title", "pl")).to eq("test_pl_value_change")
            expect(SgtnClient::Translation.getString("about", "about.title", "en")).to eq("About")
            sleep 10
            #puts SgtnClient::Translation.getString("about", "about.title", "pl")
            expect(SgtnClient::Translation.getString("about", "about.title", "pl")).to eq("test_pl_value_change")
            expect(SgtnClient::Translation.getString("about", "about.title", "en")).to eq("About")
        end
        
        ### Because component cannot be removed through the API, they need to be changed manually if repeated testing is required
        it "add new component" do
            # puts SgtnClient::Translation.getString("addcomponent", "addabout.message", "en")
            # puts SgtnClient::Translation.getString("addcomponent", "addabout.message", "fr")
            expect(SgtnClient::Translation.getString("addcomponent", "addabout.message", "en")).to eq("addabout.message")
            expect(SgtnClient::Translation.getString("addcomponent", "addabout.message", "fr")).to eq("addabout.message")
            system("E:\\E2\\ruby_client\\bat_script\\Addcomponenten.bat")
            system("E:\\E2\\ruby_client\\bat_script\\Addcomponent.bat")
            sleep 5
            expect(SgtnClient::Translation.getString("addcomponent", "addabout.message", "en")).to eq("test_en_change123")
            expect(SgtnClient::Translation.getString("addcomponent", "addabout.message", "fr")).to eq("test_value_change123")
        end
    end

    context "string_f the cache expiry period is 1 minute" do
        before :each do
            SgtnClient.load("./config/sgtnclient.yml", "production")
            SgtnClient::Source.loadBundles("default")
        end

        it "string_f: update value of key" do
            system("E:\\E2\\ruby_client\\bat_script\\RevertString_f_de.bat")
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", ["a1","a2"],"de")).to eq("a2, welcome login a1!")
            #puts SgtnClient::Translation.getString_f("about", "de")
            system("E:\\E2\\ruby_client\\bat_script\\ModifyString_f_de.bat")
            sleep 55
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", ["a1","a2"],"de")).to eq("a2, welcome login a1!")
            #puts SgtnClient::Translation.getStrings("about", "de")
            sleep 10
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", ["a1","a2"],"de")).to eq("a2, welcome change login a1!")
            #puts SgtnClient::Translation.getStrings("about", "de")
        end

        ### Because keys cannot be removed through the API, they need to be changed manually if repeated testing is required
        it "add new key" do
            expect(SgtnClient::Translation.getString("about", "about.addkey1", "de")).to eq(nil)
            expect(SgtnClient::Translation.getString("about", "about.addkey1", "en")).to eq(nil)
            # expect(SgtnClient::Translation.getString_f("about", "about.addkey1", ["a1","a2"],"de")).to eq(nil)
            # expect(SgtnClient::Translation.getString_f("about", "about.addkey1",["a1","a2"], "en")).to eq(nil)
            system("E:\\E2\\ruby_client\\bat_script\\addkey_f_en.bat")
            system("E:\\E2\\ruby_client\\bat_script\\addkey_f_de.bat")
            sleep 55
            # expect(SgtnClient::Translation.getString_f("about", "about.addkey1", ["a1","a2"],"de")).to eq(nil)
            # expect(SgtnClient::Translation.getString_f("about", "about.addkey1", ["a1","a2"],"en")).to eq(nil)
            sleep 10
            expect(SgtnClient::Translation.getString_f("about", "about.addkey1", ["a1","a2"],"de")).to eq("add de value a2")
            expect(SgtnClient::Translation.getString_f("about", "about.addkey1", ["a1","a2"],"en")).to eq("add en value a2")
        end

        ### Because locale file cannot be removed through the API, they need to be changed manually if repeated testing is required
        it "add new locale" do
            expect(SgtnClient::Translation.getString("about", "about.description", "pl")).to eq(nil)
            #expect(SgtnClient::Translation.getString_f("about", "about.description", ["a1","a2"],"pl")).to eq(nil)
            expect(SgtnClient::Translation.getString_f("about", "about.description", ["a1","a2"],"en")).to eq("Use this area to provide additional information")
            system("E:\\E2\\ruby_client\\bat_script\\ModifyString_f_pl.bat")
            sleep 5
            #puts SgtnClient::Translation.getString("about", "about.title", "pl")
            expect(SgtnClient::Translation.getString_f("about", "about.description",["a1","a2"], "pl")).to eq("a2, welcome change pl login a1!")
            expect(SgtnClient::Translation.getString_f("about", "about.description", ["a1","a2"],"en")).to eq("Use this area to provide additional information")
        end
        
        ### Because component cannot be removed through the API, they need to be changed manually if repeated testing is required
        it "add new component" do
            # puts SgtnClient::Translation.getString("addcomponent", "addabout.message", "en")
            # puts SgtnClient::Translation.getString("addcomponent", "addabout.message", "fr")
            expect(SgtnClient::Translation.getString_f("addcomponent1", "addabout.message", ["a1","a2"],"en")).to eq("addabout.message")
            expect(SgtnClient::Translation.getString_f("addcomponent1", "addabout.message", ["a1","a2"],"fr")).to eq("addabout.message")
            system("E:\\E2\\ruby_client\\bat_script\\Addcomponenten1.bat")
            system("E:\\E2\\ruby_client\\bat_script\\Addcomponent1.bat")
            sleep 5
            #expect(SgtnClient::Translation.getString_f("addcomponent1", "addabout.message", ["a1","a2"],"en")).to eq("test_en_change123")
            expect(SgtnClient::Translation.getString_f("addcomponent1", "addabout.message", ["a1","a2"],"fr")).to eq("test_value_change123 a2")
        end
    end

    context "strings the cache expiry period is 1 minute" do
        before :each do
            SgtnClient.load("./config/sgtnclient.yml", "production")
            SgtnClient::Source.loadBundles("default")
        end

        it "strings: update value of key" do
            system("E:\\E2\\ruby_client\\bat_script\\RevertString_de.bat")
            #expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test__de_value")
            expect(SgtnClient::Translation.getStrings("about", "de")["messages"]["about.message"]).to eq("test__de_value")
            system("E:\\E2\\ruby_client\\bat_script\\ModifyString_de.bat")
            sleep 55
            expect(SgtnClient::Translation.getStrings("about", "de")["messages"]["about.message"]).to eq("test__de_value")
            #expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test__de_value")
            puts SgtnClient::Translation.getStrings("about", "de")
            sleep 10
            expect(SgtnClient::Translation.getStrings("about", "de")["messages"]["about.message"]).to eq("test__de_value_change")
            #expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test__de_value_change")
            #puts SgtnClient::Translation.getStrings("about", "de")
        end

        ### Because keys cannot be removed through the API, they need to be changed manually if repeated testing is required
        it "strings: add new key" do
            expect(SgtnClient::Translation.getStrings("about", "de")["messages"]["about.addkey"]).to eq(nil)
            expect(SgtnClient::Translation.getStrings("about", "en")["messages"]["about.addkey"]).to eq(nil)
            # expect(SgtnClient::Translation.getString("about", "about.addkey", "de")).to eq(nil)
            # expect(SgtnClient::Translation.getString("about", "about.addkey", "en")).to eq(nil)
            system("E:\\E2\\ruby_client\\bat_script\\addkey_en.bat")
            system("E:\\E2\\ruby_client\\bat_script\\addkey_de.bat")
            sleep 55
            expect(SgtnClient::Translation.getStrings("about", "de")["messages"]["about.addkey"]).to eq(nil)
            expect(SgtnClient::Translation.getStrings("about", "en")["messages"]["about.addkey"]).to eq(nil)
            # expect(SgtnClient::Translation.getString("about", "about.addkey", "de")).to eq(nil)
            # expect(SgtnClient::Translation.getString("about", "about.addkey", "en")).to eq(nil)
            sleep 10
            expect(SgtnClient::Translation.getStrings("about", "de")["messages"]["about.addkey"]).to eq("add de value")
            expect(SgtnClient::Translation.getStrings("about", "en")["messages"]["about.addkey"]).to eq("add en value")
            # expect(SgtnClient::Translation.getString("about", "about.addkey", "de")).to eq("add de value")
            # expect(SgtnClient::Translation.getString("about", "about.addkey", "en")).to eq("add en value")
        end

        ### Because locale file cannot be removed through the API, they need to be changed manually if repeated testing is required
        it "strings: add new locale" do
            # expect(SgtnClient::Translation.getString("about", "about.title", "pl")).to eq(nil)
            # expect(SgtnClient::Translation.getString("about", "about.title", "en")).to eq("About")
            expect(SgtnClient::Translation.getStrings("about", "pl")["messages"]["about.title"]).to eq(nil)
            expect(SgtnClient::Translation.getStrings("about", "en")["messages"]["about.title"]).to eq("About")
            system("E:\\E2\\ruby_client\\bat_script\\ModifyString_pl.bat")
            sleep 5
            expect(SgtnClient::Translation.getStrings("about", "pl")["messages"]["about.title"]).to eq("test_pl_value_change")
            expect(SgtnClient::Translation.getStrings("about", "en")["messages"]["about.title"]).to eq("About")
            #puts SgtnClient::Translation.getString("about", "about.title", "pl")
        end
        
        ### Because component cannot be removed through the API, they need to be changed manually if repeated testing is required
        it "strings: add new component" do
            puts SgtnClient::Translation.getString("addcomponent", "addabout.message", "en")
            puts SgtnClient::Translation.getString("addcomponent", "addabout.message", "fr")
            # expect(SgtnClient::Translation.getStrings("addcomponent", "en")["messages"]["addabout.message"]).to eq("addabout.message")
            # expect(SgtnClient::Translation.getStrings("addcomponent", "fr")["messages"]["addabout.message"]).to eq("addabout.message")
            #SgtnClient::Translation.getStrings("addcomponent", "en")
            #SgtnClient::Translation.getStrings("addcomponent", "fr")
            # expect(SgtnClient::Translation.getStrings("addcomponent", "fr")["messages"]["addabout.message"]).to eq("addabout.message")
            # expect(SgtnClient::Translation.getString("addcomponent", "addabout.message", "en")).to eq("addabout.message")
            # expect(SgtnClient::Translation.getString("addcomponent", "addabout.message", "fr")).to eq("addabout.message")
            system("E:\\E2\\ruby_client\\bat_script\\Addcomponenten.bat")
            system("E:\\E2\\ruby_client\\bat_script\\Addcomponent.bat")
            sleep 5
            expect(SgtnClient::Translation.getStrings("addcomponent", "en")["messages"]["addabout.message"]).to eq("test_en_change123")
            expect(SgtnClient::Translation.getStrings("addcomponent", "fr")["messages"]["addabout.message"]).to eq("test_value_change123")
            # expect(SgtnClient::Translation.getString("addcomponent", "addabout.message", "en")).to eq("test_en_change123")
            # expect(SgtnClient::Translation.getString("addcomponent", "addabout.message", "fr")).to eq("test_value_change123")
        end
    end

    it "config testcache_expiry_periodmode" do
        SgtnClient.load("./config/sgtnclient.yml", "testcache_expiry_periodmode")
        SgtnClient::Source.loadBundles("default")
        xxx = SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a3","a2","a3"], "de")
        puts xxx
        #expect(SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a1","a2","a3"], "de")).to eq("主机")
    end

    
    it "config testdisable_cache" do
        SgtnClient.load("./config/sgtnclient.yml", "testdisable_cache")
        SgtnClient::Source.loadBundles("default")
        xxx = SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a3","a2","a3"], "de")
        puts xxx
        #expect(SgtnClient::Translation.getString_f("JAVA", "com.vmware.loginsight.web.utilities.EmailUtil.upgrade.body48",["a1","a2","a3"], "de")).to eq("主机")
    end

end
