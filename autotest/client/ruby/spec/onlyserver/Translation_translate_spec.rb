require 'rest-client'
require 'rspec'
require 'singleton-client'

describe "tmplete 1 test" do
    before :each do
        SgtnClient.config.instance_variable_set('@loader', nil)
        Sgtn.load_config("./config/sgtnclient.yml", "onlyonlinemode")
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
            expect(Sgtn.translate('about.message', 'about', 'en-US')).to eq("Your application description page.")
        end
        
        it "Get a string's translation and component is int___new discuss" do
            expect(Sgtn.translate('about.message', 123, 'en-US')).to eq("about.message")
        end

        it "Get a string's translation and component is int___new discuss" do
            expect(Sgtn.translate('about.message', "notexistcomponent", 'en-US')).to eq("about.message")
        end

        
        it "Get a string's translation and componet in bundle" do
            expect(Sgtn.translate('contact.welcome3', "contact", 'en')).to eq("%{name}, welcome contact login %{place}!")
        end

        # it "Get a string's translation and component in bundle and locale is de____new dleng bug" do
        #     expect(Sgtn.translate('contact.welcome3', "contact", 'de')).to eq("contact.welcome3")
        # end

        it "Get a string's translation and component in source" do
            expect(Sgtn.translate('about.welcome4', "about2", 'de')).to eq("%{name}, welcome about2 source login %{place}!")
        end


        it "Get a string's translation and key is int___new discuss" do
            expect(Sgtn.translate(1234, 'about', 'en-US')).to eq(1234)
        end

        it "Get a string's translation and key is notexist" do
            expect(Sgtn.translate("keynotexist", 'about', 'en-US')).to eq("keynotexist")
        end

        it "Get a string's translation and key is in source and not in bundle" do
            expect(Sgtn.translate("about.insource", 'about', 'de')).to eq("%{name}, only in source %{place}!")
        end

        it "Get a string's translation and key is in bundle and ont in source and locale is de___new dleng bug" do
            expect(Sgtn.translate("about.change", 'about', 'de')).to eq("%{name}, welcome chanege de %{place}!")
        end

        it "Get a string's translation and key is in bundle and ont in source and locale is en" do
            expect(Sgtn.translate("about.change", 'about', 'en')).to eq("about.change")
        end


        it "Get a string's translation and source is equal" do
            expect(Sgtn.translate('about.testw', "about", 'en', name: "haha", place: "single")).to eq("haha, welcome single!")
        end

        it "Get a string's translation and source is not equal debug" do
            expect(Sgtn.translate('about.notequal2', "about", 'en', name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        end

        it "Get a string's translation and source is equal and locle is de" do
            expect(Sgtn.translate('about.testw', "about", 'de', name: "haha", place: "single")).to eq("haha, welcome de single!")
        end

        it "Get a string's translation and source is equal and locle is DE___new bug 1691" do
            expect(Sgtn.translate('about.testw', "about", 'DE', name: "haha", place: "single")).to eq("haha, welcome de single!")
        end

        
        it "Get a string's translation and source is not equal and locale is de" do
            expect(Sgtn.translate('about.notequal2', "about", 'de', name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        end

        it "Get a string's translation and source is not equal and locale is da" do
            expect(Sgtn.translate('about.notequal2', "about", 'da', name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        end

        
        it "Get a string's translation and source is not equal and locale is xxxxx" do
            expect(Sgtn.translate('about.notequal2', "about", 'xxxxxx', name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        end


        it "Get a string's translation and source is not equal and locale is xxxxx" do
            expect(Sgtn.translate('about.notequal2', "about", nil, name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        end

        it "Get a string's translation and source is not equal and locale is xxxxx" do
            expect(Sgtn.translate('about.notequal2', "about", 123, name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        end

        it "Get a string's translation and source is not equal and locale is EN-uk" do
            expect(Sgtn.translate('about.notequal2', "about", "en-UK", name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        end
        
        it "Get a string's translation and source is zh-Hans-CN" do
            expect(Sgtn.translate('about.testw', "about", 'zh-Hans-CN', name: "haha", place: "single")).to eq("haha, welcome zh-Hans single!")
        end

        it "Get a string's translation and placehorder is more than argument" do
            expect(Sgtn.translate('about.testw', "about", 'zh-Hans', name: "haha", place: "single",aaaa: "xxxxx")).to eq("haha, welcome zh-Hans single!")
        end

        # it "Get a string's translation and placehorder is less than argument____new discuss" do
        #     expect(Sgtn.translate('about.testw', "about", 'zh-Hans', name: "haha")).to eq("haha, welcome zh-Hans single!")
        # end

        it "Get a string's translation and placehorder is nil" do
            expect(Sgtn.translate('about.testw', "about", 'zh-Hans', name: "haha", place: nil)).to eq("haha, welcome zh-Hans !")
        end

        it "Get a string's translation and placehorder is int" do
            expect(Sgtn.translate('about.testw', "about", 'zh-Hans', name: "haha", place: 123)).to eq("haha, welcome zh-Hans 123!")
        end

        it "Get a string's translation and placehorder is empty" do
            expect(Sgtn.translate('about.testw', "about", 'zh-Hans', name: "haha", place: "")).to eq("haha, welcome zh-Hans !")
        end


        
        it "Get a string's translation and locale is empty" do
            expect(Sgtn.translate('about.message', 'about')).to eq("Your application description page.")
        end

        it "Get a string's translation and locale is singleton locale" do
            Sgtn.locale = "de"
            expect(Sgtn.translate('about.testw', 'about',name:"hhh" , place:"test")).to eq("hhh, welcome de test!")
            expect(Sgtn.translate('about.message', 'about')).to eq("test de key")
        end

        it "Get a string's translation and locale is singleton locale and default value" do
            Sgtn.locale = "de"
            expect(Sgtn.translate('about.addkey', 'about',name:"hhh") {"default value"}) .to eq("test value hhh")
            expect(Sgtn.translate('about.notexist', 'about',name:"hhhh") {"default value %{name}"}).to eq("default value hhhh")
            expect(Sgtn.translate('about.notequal2', 'about',name:"hhhh" , place: "change") {"default value %{name} %{place}"}).to eq("hhhh, welcome login not equal source change!")
        end






    end



end
