require 'rest-client'
require 'rspec'
require 'singleton-client'

describe "Translation test" do
    before :each do
        SgtnClient.config.instance_variable_set('@loader', nil)
        Sgtn.load_config("./config/sgtnclient.yml", "onlysource")
    end

    context "Get string's translation" do
        
        
        it "Get a component's translations en-US" do
            expect(SgtnClient::Translation.getStrings("about", "en-US")["messages"]["about.description"]).to eq("Use this area to provide additional information source")
        end

        it "Get a component's translations component notexist" do
            expect(SgtnClient::Translation.getStrings("aboutnotexist", "zh-CN")).to eq(nil)
        end
        
        it "Get a component's translations and component type is incorrect" do
            expect(SgtnClient::Translation.getStrings(123, "de")).to eq(nil)
        end

        it "Get a component's translations and locale type is incorrect" do
            expect(SgtnClient::Translation.getStrings("about", 123)["messages"]["about.message"]).to eq("Your application description page. offline")
        end

        it "Get a component's translations and locale type is nil" do
            expect(SgtnClient::Translation.getStrings("about", nil)["messages"]["about.message"]).to eq("Your application description page. offline")
        end

        it "Get a component's translations and input parameters more than interface parameters" do
            expect{SgtnClient::Translation.getStrings("about", "de","abc")}.to raise_error(ArgumentError)
        end

        it "Get a component's translations and input parameters less than interface parameters" do
            expect{SgtnClient::Translation.getStrings("about2312")}.to raise_error(ArgumentError)
        end

    end

end
