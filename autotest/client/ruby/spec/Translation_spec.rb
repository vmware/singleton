require 'rest-client'
require 'rspec'
require 'singleton-ruby'

include SgtnClient
describe "Translation test" do
    before :each do
        SgtnClient.load("./config/sgtnclient.yml", "test")
        SgtnClient::Source.loadBundles("default")
    end

    context "Get string's translation" do

        it "Get a string's translation and locale is en-US" do
            #SgtnClient::Source.loadBundles("de")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("Your application description page.")
        end

        it "Get a string's translation and locale is en-UK" do
            #SgtnClient::Source.loadBundles("de")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("Your application description page.")
        end

        it "Get a string's translation and locale is fr" do
            #SgtnClient::Source.loadBundles("de")
            expect(SgtnClient::Translation.getString("about", "about.message", :fr)).to eq("test fr key")
        end

        it "Get a string's translation and locale is zh-Hans" do
            expect(SgtnClient::Translation.getString("about", "about.message", "zh-Hans")).to eq("应用程序说明页。")
        end

        it "Get a string's translation and locale is zh-Hans-CN" do
            expect(SgtnClient::Translation.getString("about", "about.message", "zh-Hans-CN")).to eq("应用程序说明页。")
        end

        it "Get a string's translation and locale not exist in server" do
            #SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::Translation.getString("about", "about.message", "da")).to eq("Your application description page.")
        end

        it "Get a string's translation and key not exist in server but exist in source" do
            #SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::Translation.getString("about", "about.key1", "de")).to eq("fall back key1")
        end

        # it "Get a string's translation and key not exist in server and not exist in source--bug" do
        #     #SgtnClient::Source.loadBundles("en")
        #     expect(SgtnClient::Translation.getString("about", "about.key134", "de")).to eq("about.key134")
        # end

        it "Get a string's translation and component not exist in server but exist in source" do
            #SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::Translation.getString("about2", "about2.key1", "de")).to eq("test")
        end

        # it "Get a string's translation and component not exist in server and not exist in source" do
        #     #SgtnClient::Source.loadBundles("en")
        #     expect(SgtnClient::Translation.getString("aboutnotexist", "about.key", "de")).to eq("about.key")
        # end


        it "Get a string's translation and locale argument type is incorrect" do
            #expect{SgtnClient::Translation.getString("about", "about.message", 123)}.to raise_error(NoMethodError)
            expect(SgtnClient::Translation.getString("about", "about.message", 123)).to eq("Your application description page.")
        end

        it "Get a string's translation and locale argument type is nil" do
            #expect{SgtnClient::Translation.getString("about", "about.message", 123)}.to raise_error(NoMethodError)
            expect(SgtnClient::Translation.getString("about", "about.message", nil)).to eq("Your application description page.")
        end

        # it "Get a string's translation and key type is incorrect" do
        #     expect(SgtnClient::Translation.getString("about", 123, "de")).to eq(nil)

        # end

        it "Get a string's translation and component type is incorrect" do
            expect{SgtnClient::Translation.getString(123, "about.message", "de")}.to raise_error(TypeError)
        end


        it "Get a string's translation and input parameters more than interface parameters" do
            expect{SgtnClient::Translation.getString("about", "about.message", "zh-CN","de")}.to raise_error(ArgumentError)
        end


        it "Get a string's translation and input parameters less than interface parameters" do
            expect{SgtnClient::Translation.getString("about", "about.message")}.to raise_error(ArgumentError)
        end

        
        it "loadBundles(language) translation and fallback " do
            SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("fall back about")
        end

        

    end

    context "getString_f translation" do

        it "Get the string's translation and format it with %s placeholders and Parameter is empty" do
            expect{SgtnClient::Translation.getString_f("about", "about.test", [],"de")}.to raise_error(ArgumentError)
        end

        it "Get the string's translation and format it with %s placeholders and Parameter is more than placeholders" do
            expect(SgtnClient::Translation.getString_f("about", "about.test",["a1","a2","a3"], "de")).to eq("test de the a1 to a2")
        end

        it "Get the string's translation and format it with %s placeholders and Parameter is less than placeholders" do
            expect{SgtnClient::Translation.getString_f("about", "about.test",["a1"], "de")}.to raise_error(ArgumentError)
        end
        
        it "Get the string's translation and format it with %s placeholders and Parameter is equals placeholders" do
            ##xxx= SgtnClient::Translation.getString("about", "about.messagexxxxxxx", ["a1","a2",5], "de"))
            expect(SgtnClient::Translation.getString_f("about", "about.test",["a1","a2"], "de_DE")).to eq("test de the a1 to a2")
        end

        it "Get the string's translation and format it with placeholders and cotains 5 is Positive value" do
            ##xxx= SgtnClient::Translation.getString("about", "about.messagexxxxxxx", ["a1","a2",5], "de"))
            expect(SgtnClient::Translation.getString_f("about", "about.welcome",["a1","a2",5], "de")).to eq("   a2, welcome login de a1 5!")
        end

        it "Get the string's translation and format it with placeholders and cotains -5 is negative value" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome",["a1","a2",-5], "de")).to eq("a2   , welcome login de a1 -5!")
        end

        it "Get the string's translation from source bundle and format it with %s placeholders" do
            expect(SgtnClient::Translation.getString_f("about", "about.test1",["a1","a2"], "zh_Hans")).to eq("test source the a1 to a2")
        end

    end

    context "getStrings component" do

        it "Get a component's translations en-US" do
            expect(SgtnClient::Translation.getStrings("about", "en-US")["messages"]["about.description"]).to eq(nil)
        end

        it "Get a component's translations zh-CN" do
            expect(SgtnClient::Translation.getStrings("about", "zh-CN")["messages"]["about.description"]).to eq("使用此区域可提供其他信息")
        end

        it "Get a component's translations zh-Hans-CN" do
            expect(SgtnClient::Translation.getStrings("about", "zh-Hans-CN")["messages"]["about.description"]).to eq("使用此区域可提供其他信息")
        end

        it "Get a component's translations da" do
            #puts SgtnClient::Translation.getStrings("about", "da")
            expect(SgtnClient::Translation.getStrings("about", "da")["messages"]["about.message"]).to eq("Your application description page.")
        end

        it "Get a component's translations not exist in server but exist in source" do
            #puts SgtnClient::Translation.getStrings("about2", "de")
            expect(SgtnClient::Translation.getStrings("about2", "de")["messages"]["about.key2"]).to eq("fall back key12")
            ##puts SgtnClient::Translation.getStrings("about", "zh-CN")
        end

        # it "Get a component's translations not exist in server and not exist in source" do
        #     SgtnClient::Translation.getStrings("notexistcomponent", "de")
        #     # expect(SgtnClient::Translation.getStrings("aboutxxxxx", "de")).to eq(nil)
        #     # puts xxx
        #     ##puts SgtnClient::Translation.getStrings("about", "zh-CN")
        # end

        it "Get a component's translations and component type is incorrect" do
            expect{SgtnClient::Translation.getStrings(123, "de")}.to raise_error(TypeError)
            ##puts SgtnClient::Translation.getStrings("about", "zh-CN")
        end

        it "Get a component's translations and locale type is incorrect" do
            #expect{SgtnClient::Translation.getStrings("about", 123)}.to raise_error(NoMethodError)
            expect(SgtnClient::Translation.getStrings("about", 123)["messages"]["about.message"]).to eq("Your application description page.")
            ##puts SgtnClient::Translation.getStrings("about", "zh-CN")
        end

        it "Get a component's translations and locale type is nil" do
            expect(SgtnClient::Translation.getStrings("about", nil)["messages"]["about.message"]).to eq("Your application description page.")
            #expect{SgtnClient::Translation.getStrings("about", 123)}.to raise_error(NoMethodError)
            ##puts SgtnClient::Translation.getStrings("about", "zh-CN")
        end

        it "Get a component's translations and input parameters more than interface parameters" do
            expect{SgtnClient::Translation.getStrings("about", "de","abc")}.to raise_error(ArgumentError)
            ##puts SgtnClient::Translation.getStrings("about", "zh-CN")
        end

        it "Get a component's translations and input parameters less than interface parameters" do
            expect{SgtnClient::Translation.getStrings("about2312")}.to raise_error(ArgumentError)
            ##puts SgtnClient::Translation.getStrings("about", "zh-CN")
        end
    end

    context "test default_language config" do
        it "no default_labguage and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "test")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.description", "en")).to eq(nil)     
            expect(SgtnClient::Translation.getString("about", "about.message", "en-US")).to eq("Your application description page.")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test de key")
            expect(SgtnClient::Translation.getString("about", "about.message", "da")).to eq("Your application description page.")          
        end

        it "default_language is en and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale1")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "en-US")).to eq("Your application description page.")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test de key")
            expect(SgtnClient::Translation.getString("about", "about.message", "da")).to eq("Your application description page.")       
        end

        it "default_language is zh-Hans and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale2")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("Your application description page.")
            expect(SgtnClient::Translation.getString("about", "about.message", "zh-Hans")).to eq("Your application description page.")
            expect(SgtnClient::Translation.getString("about", "about.message", "da")).to eq("Your application description page.")   
        end

        
        it "default_language is da and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale3")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("Your application description page.")
            expect(SgtnClient::Translation.getString("about", "about.message", "da")).to eq("Your application description page.")
            expect(SgtnClient::Translation.getString("about", "about.message", "es")).to eq("Your application description page.")   
        end

        
        it "default_language is abcd and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale4")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("Your application description page.")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test de key") 
            expect(SgtnClient::Translation.getString("about", "about.message", "abcd")).to eq("Your application description page.")
            expect(SgtnClient::Translation.getString("about", "about.message", "da")).to eq("Your application description page.")  
        end

        it "default_language is en and loadbundle is en.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale1")
            SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("fall back about")
            expect(SgtnClient::Translation.getString("about", "about.message", "de")).to eq("test de key")
            expect(SgtnClient::Translation.getString("about", "about.message", "da")).to eq("fall back about")  

        end

        it "getstring default_language is fr and loadbundle is en.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale5")
            SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::Translation.getString("about", "about.message", "en")).to eq("fall back about")
            # expect(SgtnClient::Translation.getString("about", "about.message", "fr")).to eq("fall back about") 
            # expect(SgtnClient::Translation.getString("about", "about.message", "da")).to eq("fall back about") 

        end


    end

    context "test getstrings default_language config" do
        it "getstrings no default_labguage and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "test")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getStrings("about", "en-US")["messages"]["about.description"]).to eq(nil)  
            expect(SgtnClient::Translation.getStrings("about", "en-US")["messages"]["about.message"]).to eq("Your application description page.")
            expect(SgtnClient::Translation.getStrings("about", "de")["messages"]["about.message"]).to eq("test de key")
            expect(SgtnClient::Translation.getStrings("about", "da")["messages"]["about.message"]).to eq("Your application description page.")   
        end

        it "getstrings default_language is en and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale1")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getStrings("about", "en-US")["messages"]["about.message"]).to eq("Your application description page.")
            expect(SgtnClient::Translation.getStrings("about", "de")["messages"]["about.message"]).to eq("test de key")
            expect(SgtnClient::Translation.getStrings("about", "da")["messages"]["about.message"]).to eq("Your application description page.")  
    
        end

        it "getstrings default_language is zh-Hans and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale2")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getStrings("about", "en")["messages"]["about.message"]).to eq("Your application description page.")
            expect(SgtnClient::Translation.getStrings("about", "zh-Hans")["messages"]["about.message"]).to eq("Your application description page.")
            expect(SgtnClient::Translation.getStrings("about", "da")["messages"]["about.message"]).to eq("Your application description page.")   
        end

        
        it "getstrings default_language is da and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale3")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getStrings("about", "en")["messages"]["about.message"]).to eq("Your application description page.")
            expect(SgtnClient::Translation.getStrings("about", "da")["messages"]["about.message"]).to eq("Your application description page.")
            expect(SgtnClient::Translation.getStrings("about", "es")["messages"]["about.message"]).to eq("Your application description page.")   

        end

        
        it "getstrings default_language is abcd and loadbundle is default.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale4")
            SgtnClient::Source.loadBundles("default")
            expect(SgtnClient::Translation.getStrings("about", "en")["messages"]["about.message"]).to eq("Your application description page.")
            expect(SgtnClient::Translation.getStrings("about", "de")["messages"]["about.message"]).to eq("test de key")
            expect(SgtnClient::Translation.getStrings("about", "da")["messages"]["about.message"]).to eq("Your application description page.")   
        end

        it "getstrings default_language is en and loadbundle is en.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale1")
            SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::Translation.getStrings("about", "en")["messages"]["about.message"]).to eq("fall back about")
            expect(SgtnClient::Translation.getStrings("about", "de")["messages"]["about.message"]).to eq("test de key")
            expect(SgtnClient::Translation.getStrings("about", "da")["messages"]["about.message"]).to eq("fall back about") 


        end

        it "getstrings default_language is fr and loadbundle is en.yml" do
            SgtnClient.load("./config/sgtnclient.yml", "testsourcelocale5")
            SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::Translation.getStrings("about", "en")["messages"]["about.message"]).to eq("fall back about")
            expect(SgtnClient::Translation.getStrings("about", "fr")["messages"]["about.message"]).to eq("fall back about")
            expect(SgtnClient::Translation.getStrings("about", "da")["messages"]["about.message"]).to eq("fall back about") 
        end


    end

end
