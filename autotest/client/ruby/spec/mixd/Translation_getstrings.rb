require 'rest-client'
require 'rspec'
require 'singleton-client'

describe "Translation test" do
    before :each do
        SgtnClient.config.instance_variable_set('@loader', nil)
        Sgtn.load_config("./config/sgtnclient.yml", "mixed")
        #SgtnClient::Source.loadBundles("default")
    end

    context "Get string's translation" do
        
        
        it "Get a component's translations en-US" do
            expect(SgtnClient::Translation.getStrings("allin", "en-US")["messages"]["about.description"]).to eq("Use this area to provide additional information source")
        end

        it "Get a component's translations zh-CN" do
            expect(SgtnClient::Translation.getStrings("allin", "zh-CN")["messages"]["about.message"]).to eq("应用程序说明页。")
        end

        it "Get a component's translations component notexist" do
            expect(SgtnClient::Translation.getStrings("aboutnotexist", "zh-CN")).to eq(nil)
        end


        it "Get a component's translations zh-Hans-CN" do
            expect(SgtnClient::Translation.getStrings("allin", "zh-Hans-CN")["messages"]["about.message"]).to eq("应用程序说明页。")
        end 
        
        it "Get a component's translations and component type is incorrect" do
            expect(SgtnClient::Translation.getStrings(123, "de")).to eq(nil)
            ##puts SgtnClient::Translation.getStrings("allin", "zh-CN")
        end

        it "Get a component's translations and locale type is incorrect" do
            #expect{SgtnClient::Translation.getStrings("allin", 123)}.to raise_error(NoMethodError)
            expect(SgtnClient::Translation.getStrings("allin", 123)["messages"]["about.message"]).to eq("Your application description page. offline")
            ##puts SgtnClient::Translation.getStrings("allin", "zh-CN")
        end

        it "Get a component's translations and locale type is nil" do
            expect(SgtnClient::Translation.getStrings("allin", nil)["messages"]["about.message"]).to eq("Your application description page. offline")
            #expect{SgtnClient::Translation.getStrings("allin", 123)}.to raise_error(NoMethodError)
            ##puts SgtnClient::Translation.getStrings("allin", "zh-CN")
        end

        it "Get a component's translations and input parameters more than interface parameters" do
            expect{SgtnClient::Translation.getStrings("allin", "de","abc")}.to raise_error(ArgumentError)
            ##puts SgtnClient::Translation.getStrings("allin", "zh-CN")
        end

        it "Get a component's translations and input parameters less than interface parameters" do
            expect{SgtnClient::Translation.getStrings("about2312")}.to raise_error(ArgumentError)
            ##puts SgtnClient::Translation.getStrings("allin", "zh-CN")
        end

    end



end
