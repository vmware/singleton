require 'rest-client'
require 'rspec'
require 'singleton-client'

describe "tmplete 1 test" do
    before :each do
        SgtnClient.config.instance_variable_set('@loader', nil)
        Sgtn.load_config("./config/sgtnclient.yml", "onlysource")

    end

    context "getString_f translation" do

        it "Get a string's translation and arguments is more than 3" do
            expect{Sgtn.translate('about.message', 'about', 'en-US',"bbb")}.to raise_error(ArgumentError)
        end

        it "Get a string's translation and arguments is less than 2" do
            expect{Sgtn.translate('about.message')}.to raise_error(ArgumentError)
        end

        # it "Get a string's translation and arguments is less than 2____new dleng bug" do
        #     expect{Sgtn.translate('about.message' , name:"xsdsad")}.to raise_error(ArgumentError)
        # end

        it "Get a string's translation and arguments is empty" do
            expect{Sgtn.translate()}.to raise_error(ArgumentError)
        end



        it "Get a string's translation and locale is en-US" do
            expect(Sgtn.translate('about.message', 'about', 'en-US')).to eq("Your application description page. offline")
        end


        
        it "Get a string's translation and component is int___new discuss" do
            expect(Sgtn.translate('about.message', 123, 'en-US')).to eq("about.message")
        end

        it "Get a string's translation and component is int___new discuss" do
            expect(Sgtn.translate('about.message', "notexistcomponent", 'en-US')).to eq("about.message")
        end

        
        it "Get a string's translation and componet in bundle" do
            expect(Sgtn.translate('contact.welcome3', "contact", 'en')).to eq("contact.welcome3")
        end



        it "Get a string's translation and component in source" do
            expect(Sgtn.translate('about.welcome4', "about2", 'de')).to eq("%{name}, welcome about2 source login %{place}!")
        end


        it "Get a string's translation and key is ints" do
            expect(Sgtn.translate(1234, 'about', 'en-US')).to eq(1234)
        end

        it "Get a string's translation and key is notexist" do
            expect(Sgtn.translate("keynotexist", 'about', 'en-US')).to eq("keynotexist")
        end

        it "Get a string's translation and key is in source" do
            expect(Sgtn.translate("about.insource", 'about', 'de')).to eq("%{name}, only in source %{place}!")
        end


        it "Get a string's translation and key not in source and locale is en" do
            expect(Sgtn.translate("about.change", 'about', 'en')).to eq("about.change")
        end


        
        it "Get a string's translation and source is zh-Hans-CN" do
            expect(Sgtn.translate('about.testw', "about", 'zh-Hans-CN', name: "haha", place: "single")).to eq("haha, welcome single!")
        end


        it "Get a string's translation and locale is singleton locale and default value" do
            Sgtn.locale = "de"
            expect(Sgtn.translate('about.addkey', 'about',name:"hhh") {"default value"}) .to eq("test value hhh")
            expect(Sgtn.translate('about.notexist', 'about',name:"hhhh") {"default value %{name}"}).to eq("default value hhhh")
            expect(Sgtn.translate('about.notequal2', 'about',name:"hhhh" , place: "change") {"default value %{name} %{place}"}).to eq("hhhh, welcome login not equal source change!")
        end

    end

end
