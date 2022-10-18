require 'rest-client'
require 'rspec'
require 'singleton-client'

#include Singleton
describe "Translation test" do
    before :each do
        SgtnClient.config.instance_variable_set('@loader', nil)
        Sgtn.load_config("./config/sgtnclient.yml", "onlinemode")
        #SgtnClient::Source.loadBundles("default")
    end

    context "getString_f translation" do

        it "Get the string's translation and argument is more than 4" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"},"de","de")).to eq("xiaoming, welcome login singleton de!")
        end

        it "Get the string's translation and argument is less than 4" do
            expect{SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"})}.to raise_error(ArgumentError)
        end

        
        it "Get the string's translation and argument is more than empty" do
            expect{SgtnClient::Translation.getString_f()}.to raise_error(ArgumentError)
        end


        it "Get the string's translation and placehoder is more than argument" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton", "add": "singleton23"},"de")).to eq("xiaoming, welcome login singleton de!")
        end

        it "Get the string's translation and placehoder is less than argument" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"place": "singleton" },"de")).to eq("{name}, welcome login singleton de!")
        end

        it "Get the string's translation and placehoder is empty" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome",{} ,"de")).to eq("{name}, welcome login {place} de!")
        end

        it "Get the string's translation and placehoder is nil" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome",nil ,"de")).to eq("{name}, welcome login {place} de!")
        end





        # it "Get the string's translation and format it with %s placeholders and Parameter is empty" do
        #     expect(SgtnClient::Translation.getString_f("about", "about.welcome", {},"de")).to eq("xiaoming, welcome login singleton de!")
        # end


    end



end
