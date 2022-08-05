require 'rest-client'
require 'rspec'
require 'singleton-client'

describe "Translation test" do
    before :each do
        SgtnClient.config.instance_variable_set('@loader', nil)
        Sgtn.load_config("./config/sgtnclient.yml", "onlysource")
    end

    context "Get string's translation" do


        it "Get a string's translation and key exist in source" do
            expect(SgtnClient::Translation.getString("about", "about.key3", "en")).to eq("fall back key31")
        end

        
        it "Get a string's translation and locale is en_UK" do
            expect(SgtnClient::Translation.getString("about", "about.message", "en_UK")).to eq("Your application description page. offline")
        end


    end



end
