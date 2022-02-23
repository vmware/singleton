require 'rest-client'
require 'rspec'
require 'singleton-ruby'
require 'request_store'

include SgtnClient
describe "Translation test" do
    before :each do
        SgtnClient.load("./config/sgtnclient.yml", "test")
        SgtnClient::Source.loadBundles("default")
    end

    context "Get string's translation" do

        it "Get a string's translation and locale is en-US" do
            RequestStore.store[:locale] = 'en-US'
            RequestStore.store[:component] = 'about'
            #SgtnClient::Source.loadBundles("de")
            expect(SgtnClient::T.s("about.message")).to eq("fall back about")
        end

        it "Get a string's translation and locale is en-UK" do
            RequestStore.store[:locale] = 'en-Uk'
            RequestStore.store[:component] = 'about'
            #SgtnClient::Source.loadBundles("de")
            expect(SgtnClient::T.s("about.message")).to eq("fall back about")
        end

        it "Get a string's translation and locale is fr-FR" do
            RequestStore.store[:locale] = 'fr-FR'
            RequestStore.store[:component] = 'about'
            #SgtnClient::Source.loadBundles("de")
            expect(SgtnClient::T.s("about.message")).to eq("test fr key")
        end

        it "Get a string's translation and locale is zh-Hans" do
            RequestStore.store[:locale] = 'zh-Hans'
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.s("about.message")).to eq("应用程序说明页。")
        end

        it "Get a string's translation and locale is zh-Hans-CN" do
            RequestStore.store[:locale] = 'zh-Hans-CN'
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.s("about.message")).to eq("应用程序说明页。")
        end

        it "Get a string's translation and locale not exist in server" do
            RequestStore.store[:locale] = "da"
            RequestStore.store[:component] = 'about'
            #SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::T.s("about.message")).to eq("fall back about")
        end

        it "Get a string's translation and key not exist in server but exist in source" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 'about'
            #SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::T.s("about.key1")).to eq("fall back key1")
        end

        # it "Get a string's translation and key not exist in server and not exist in source--bug" do
        #     RequestStore.store[:locale] = 'de'
        #     RequestStore.store[:component] = 'about'
        #     #SgtnClient::Source.loadBundles("en")
        #     expect(SgtnClient::T.s("about.key134")).to eq("about.key134")
        # end

        it "Get a string's translation and component not exist in server but exist in source" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 'about2'
            #SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::T.s("about2.key1")).to eq("test")
        end

        # it "Get a string's translation and component not exist in server and not exist in source" do
        #     RequestStore.store[:locale] = 'de'
        #     RequestStore.store[:component] = 'aboutnotexist'
        #     #SgtnClient::Source.loadBundles("en")
        #     expect(SgtnClient::T.s("about.key")).to eq("about.key")
        # end


        it "Get a string's translation and locale argument type is incorrect" do
            RequestStore.store[:locale] = 123
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.s("about.message")).to eq("fall back about")
            #expect{SgtnClient::T.s("about.message")}.to raise_error(NoMethodError)
        end

        it "Get a string's translation and locale argument type is nil" do
            RequestStore.store[:locale] = nil
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.s("about.message")).to eq("fall back about")
            #expect{SgtnClient::T.s("about.message")}.to raise_error(NoMethodError)
        end

        # it "Get a string's translation and key type is incorrect" do
        #     RequestStore.store[:locale] = 'de'
        #     RequestStore.store[:component] = 'about'
        #     expect(SgtnClient::T.s(123)).to eq(nil)

        # end

        it "Get a string's translation and component type is incorrect" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 123
            expect{SgtnClient::T.s("about.message")}.to raise_error(TypeError)
        end


        it "Get a string's translation and input parameters more than interface parameters" do
            RequestStore.store[:locale] = 'zh-CN'
            RequestStore.store[:component] = 'about'
            expect{SgtnClient::T.s("about.message","de")}.to raise_error(ArgumentError)
        end


        it "Get a string's translation and input parameters less than interface parameters" do
            RequestStore.store[:locale] = 'zh-CN'
            RequestStore.store[:component] = 'about'
            expect{SgtnClient::T.s()}.to raise_error(ArgumentError)
        end

        
        it "loadBundles(language) translation and fallback " do
            RequestStore.store[:locale] = 'en'
            RequestStore.store[:component] = 'about'
            SgtnClient::Source.loadBundles("en")
            expect(SgtnClient::T.s("about.message")).to eq("fall back about")
        end

    end

    context "getString_f translation" do

        it "Get the string's translation and format it with %s placeholders and Parameter is empty" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 'about'
            expect{SgtnClient::T.s_f("about.test", [])}.to raise_error(ArgumentError)
        end

        it "Get the string's translation and format it with %s placeholders and Parameter is more than placeholders" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.s_f( "about.test",["a1","a2","a3"])).to eq("test de the a1 to a2")
        end

        it "Get the string's translation and format it with %s placeholders and Parameter is less than placeholders" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 'about'
            expect{SgtnClient::T.s_f("about.test",["a1"])}.to raise_error(ArgumentError)
        end
        
        it "Get the string's translation and format it with %s placeholders and Parameter is equals placeholders" do
            RequestStore.store[:locale] = 'de_DE'
            RequestStore.store[:component] = 'about'
            ##xxx= SgtnClient::T.s("about", "about.messagexxxxxxx", ["a1","a2",5], "de"))
            expect(SgtnClient::T.s_f("about.test",["a1","a2"])).to eq("test de the a1 to a2")
        end

        it "Get the string's translation and format it with placeholders and cotains 5 is Positive value" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 'about'
            ##xxx= SgtnClient::T.s("about", "about.messagexxxxxxx", ["a1","a2",5], "de"))
            expect(SgtnClient::T.s_f("about.welcome",["a1","a2",5])).to eq("   a2, welcome login de a1 5!")
        end

        it "Get the string's translation and format it with placeholders and cotains -5 is negative value" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.s_f("about.welcome",["a1","a2",-5])).to eq("a2   , welcome login de a1 -5!")
        end

        it "Get the string's translation from source bundle and format it with %s placeholders" do
            RequestStore.store[:locale] = 'zh-Hans'
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.s_f("about.test1",["a1","a2"])).to eq("test source the a1 to a2")
        end

    end

    context "getStrings component" do

        it "Get a component's translations en-US" do
            RequestStore.store[:locale] = "en-US"
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.c()["messages"]["about.description"]).to eq(nil)
        end

        it "Get a component's translations zh-CN" do
            RequestStore.store[:locale] = 'zh-CN'
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.c()["messages"]["about.description"]).to eq("使用此区域可提供其他信息")
        end

        it "Get a component's translations zh-Hans-CN" do
            RequestStore.store[:locale] = 'zh-Hans-CN'
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.c()["messages"]["about.description"]).to eq("使用此区域可提供其他信息")
        end

        it "Get a component's translations da" do
            RequestStore.store[:locale] = 'da'
            RequestStore.store[:component] = 'about'
            #puts SgtnClient::T.c("about", "da")
            expect(SgtnClient::T.c()["messages"]["about.message"]).to eq("fall back about")
        end

        it "Get a component's translations not exist in server but exist in source" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 'about2'
            #puts SgtnClient::T.c("about2", "de")
            expect(SgtnClient::T.c()["messages"]["about.key2"]).to eq("fall back key12")
            ##puts SgtnClient::T.c("about", "zh-CN")
        end

        # it "Get a component's translations not exist in server and not exist in source" do
        #     RequestStore.store[:locale] = 'de'
        #     RequestStore.store[:component] = 'aboutxxxxx'
        #     expect(SgtnClient::T.c()).to eq(nil)
        #     puts xxx
        #     ##puts SgtnClient::T.c("about", "zh-CN")
        # end

        it "Get a component's translations and component type is incorrect" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 123
            expect{SgtnClient::T.c()}.to raise_error(TypeError)
            ##puts SgtnClient::T.c("about", "zh-CN")
        end

        it "Get a component's translations and locale type is incorrect" do
            RequestStore.store[:locale] = 123
            RequestStore.store[:component] = 'about'
            expect(SgtnClient::T.c()["messages"]["about.key3"]).to eq("fall back key31")
            #expect{SgtnClient::T.c()}.to raise_error(NoMethodError)
            ##puts SgtnClient::T.c("about", "zh-CN")
        end

        it "Get a component's translations and input parameters more than interface parameters" do
            RequestStore.store[:locale] = 'de'
            RequestStore.store[:component] = 'about'
            expect{SgtnClient::T.c("abc")}.to raise_error(ArgumentError)
            ##puts SgtnClient::T.c("about", "zh-CN")
        end
    end


end
