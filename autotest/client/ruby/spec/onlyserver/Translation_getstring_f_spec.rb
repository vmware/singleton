require 'rest-client'
require 'rspec'
require 'singleton-client'

describe "Translation test" do
    before :each do
        #SgtnClient.config.instance_variable_set('@loader', nil)
        SgtnClient.config.instance_variable_set('@loader', nil)
        Sgtn.load_config("./config/sgtnclient.yml", "onlyonlinemode")
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

        it "Get the string's translation and placehoder is equal argument" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"},"de")).to eq("xiaoming, welcome login singleton de!")
        end

        it "Get the string's translation and placehoder is component name is int" do
            expect(SgtnClient::Translation.getString_f(123, "about.welcome", {"name": "xiaoming", "place": "singleton"},"de")).to eq(nil)
        end

        it "Get the string's translation and placehoder is component name not exist" do
            expect(SgtnClient::Translation.getString_f("aboutnotexist", "about.welcome", {"name": "xiaoming", "place": "singleton"},"de")).to eq(nil)
        end

        it "Get the string's translation and placehoder is component not in source and locale is en" do
            expect(SgtnClient::Translation.getString_f("contact", "contact.welcome2", {"name": "xiaoming", "place": "singleton"},"en")).to eq("xiaoming, welcome contact login singleton!")
        end

        # it "Get the string's translation and placehoder is component not in source and locale is de___new dleng bug" do
        #     expect(SgtnClient::Translation.getString_f("contact", "contact.welcome2", {"name": "xiaoming", "place": "singleton"},"de")).to eq(nil)
        # end

        it "Get the string's translation and placehoder is component not in bundle and in source" do
            expect(SgtnClient::Translation.getString_f("about2", "about.welcome2", {"name": "xiaoming", "place": "singleton"},"de")).to eq("xiaoming, welcome about2 login singleton!")
        end

        it "Get the string's translation and key is int" do
            expect(SgtnClient::Translation.getString_f("about2", 234, {"name": "xiaoming", "place": "singleton"},"de")).to eq(nil)
        end

        it "Get the string's translation and key is notexist" do
            expect(SgtnClient::Translation.getString_f("about2", "aboutnotesixtkey", {"name": "xiaoming", "place": "singleton"},"de")).to eq(nil)
        end

        
        it "Get the string's translation and key in source and not in bunlde" do
            expect(SgtnClient::Translation.getString_f("about", "about.test1", {"1": "xiaoming", "2": "singleton"},"de")).to eq("test source the xiaoming to singleton")
        end

        it "Get the string's translation and key not in source and in bunlde locale is en" do
            expect(SgtnClient::Translation.getString_f("about", "about.change", {"name": "xiaoming", "place": "singleton"},"en")).to eq(nil)
        end

        # it "Get the string's translation and key not in source and in bunlde and locale is de____new dleng bug" do
        #     expect(SgtnClient::Translation.getString_f("about", "about.change", {"name": "xiaoming", "place": "singleton"},"de")).to eq(nil)
        # end

        
        it "Get the string's translation and en source equal" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"},"en")).to eq("xiaoming, welcome login singleton!")
        end

        it "Get the string's translation and en source not equal" do
            expect(SgtnClient::Translation.getString_f("about", "about.notequal", {"name": "xiaoming", "place": "singleton"},"en")).to eq("xiaoming, welcome login not equal singleton!")
        end

        it "Get the string's translation and source not equal and locale is de" do
            expect(SgtnClient::Translation.getString_f("about", "about.notequal", {"name": "xiaoming", "place": "singleton"},"de")).to eq("xiaoming, welcome login not equal singleton!")
        end

        it "Get the string's translation and locale is da and fallback" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"},"da")).to eq("xiaoming, welcome login singleton!")
        end

        it "Get the string's translation and locale is xxxx" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"},"xxxx")).to eq("xiaoming, welcome login singleton!")
        end
        
        it "Get the string's translation and locale is nil" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"},nil)).to eq("xiaoming, welcome login singleton!")
        end

        it "Get the string's translation and locale is int" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"}, 234)).to eq("xiaoming, welcome login singleton!")
        end

        it "Get the string's translation and locale is en -uk" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"}, "en_UK")).to eq("xiaoming, welcome login singleton!")
        end

        it "Get the string's translation and locale is zh-hans" do
            expect(SgtnClient::Translation.getString_f("about", "about.welcome", {"name": "xiaoming", "place": "singleton"}, "zh-Hans-CN")).to eq("xiaoming, welcome login singleton zh-Hans offline!")
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
        #     expect(SgtnClient::Translation.getString_f("about", "about.welcome", {},"de")).to eq("xiaoming, welcome login singleton de offline!")
        # end


    end



end
