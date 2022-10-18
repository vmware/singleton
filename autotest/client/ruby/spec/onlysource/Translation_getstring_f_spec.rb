require 'rest-client'
require 'rspec'
require 'singleton-client'

describe "Translation test" do
    before :each do
        SgtnClient.config.instance_variable_set('@loader', nil)
        Sgtn.load_config("./config/sgtnclient.yml", "onlysource")
    end

    context "getString_f translation" do

        it "Get the string's translation and argument is less than 4" do
            expect{SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"})}.to raise_error(ArgumentError)
        end

        
        it "Get the string's translation and argument is more than empty" do
            expect{SgtnClient::Translation.getString_f()}.to raise_error(ArgumentError)
        end

        it "Get the string's translation and placehoder is equal argument" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"},"de")).to eq("xiaoming, welcome login singleton!")
        end

        it "Get the string's translation and placehoder is component name is int" do
            expect(SgtnClient::Translation.getString_f(123, "about.welcome", {"name": "xiaoming", "place": "singleton"},"de")).to eq(nil)
        end

        it "Get the string's translation and placehoder is component name not exist" do
            expect(SgtnClient::Translation.getString_f("aboutnotexist", "about.welcome", {"name": "xiaoming", "place": "singleton"},"de")).to eq(nil)
        end

        it "Get the string's translation and key is int" do
            expect(SgtnClient::Translation.getString_f("about2", 234, {"name": "xiaoming", "place": "singleton"},"de")).to eq(nil)
        end

        it "Get the string's translation and key is notexist" do
            expect(SgtnClient::Translation.getString_f("about2", "aboutnotesixtkey", {"name": "xiaoming", "place": "singleton"},"de")).to eq(nil)
        end
      
        it "Get the string's translation and en source equal" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"},"en")).to eq("xiaoming, welcome login singleton!")
        end

    end

end
