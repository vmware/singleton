require 'rest-client'
require 'rspec'
require 'singleton-client'

describe "tmplete 1 test" do
    before :each do
        SgtnClient.config.instance_variable_set('@loader', nil)
        Sgtn.load_config("./config/sgtnclient.yml", "mixed")
        #SgtnClient::Source.loadBundles("default")
    end

    context "getString_f translation" do

        it "Get a string's translation and arguments is more than 3" do
            expect{Sgtn.translate('about.message', 'allin', 'en-US',"bbb")}.to raise_error(ArgumentError)
            #expect(SgtnClient::Translation.getString("allin", "about.message", "en-US")).to eq("Your application description page. offline")
        end

        it "Get a string's translation and arguments is less than 2" do
            expect{Sgtn.translate('about.message')}.to raise_error(ArgumentError)
            #expect(SgtnClient::Translation.getString("allin", "about.message", "en-US")).to eq("Your application description page. offline")
        end

        # it "Get a string's translation and arguments is less than 2" do
        #     expect{Sgtn.translate('about.message' , name:"xsdsad")}.to raise_error(ArgumentError)
        #     #expect(SgtnClient::Translation.getString("allin", "about.message", "en-US")).to eq("Your application description page. offline")
        # end

        it "Get a string's translation and arguments is empty" do
            expect{Sgtn.translate()}.to raise_error(ArgumentError)
            #expect(SgtnClient::Translation.getString("allin", "about.message", "en-US")).to eq("Your application description page. offline")
        end



        it "Get a string's translation and locale is en-US" do
            expect(Sgtn.translate('about.message', 'allin', 'en-US')).to eq("Your application description page. offline")
            #expect(SgtnClient::Translation.getString("allin", "about.message", "en-US")).to eq("Your application description page. offline")
        end


        
        it "Get a string's translation and component is int___new discuss" do
            expect(Sgtn.translate('about.message', 123, 'en-US')).to eq("about.message")
        end

        it "Get a string's translation and component is nil" do
            expect(Sgtn.translate('about.message', nil, 'en-US')).to eq("about.message")
        end

        it "Get a string's translation and key is nil" do
            expect(Sgtn.translate(nil, 'allin', 'en-US')).to eq(nil)
        end



        it "Get a string's translation and component is int___new discuss" do
            expect(Sgtn.translate('about.message', "notexistcomponent", 'en-US')).to eq("about.message")
        end

        
        it "Get a string's translation and componet in online" do
            expect(Sgtn.translate('contact.welcome3', "onlyonline", 'en-US')).to eq("contact.welcome3")
        end

        it "Get a string's translation and component in online and locale is de____new dleng bug" do
            expect(Sgtn.translate('contact.welcome3', "onlyonline", 'de')).to eq("contact.welcome3")
        end

        # it "Get a string's translation and component in bundle and locale is ttttttttttttttt de____new dleng bug" do
        #     expect(Sgtn.translations("contact", 'de')["messages"]).to eq("contact.welcome3")
        #     puts(Sgtn.get_translations("allin", 'en')["messages"])
        # end

        it "Get a string's translation and component in source" do
            expect(Sgtn.translate('about.welcome4', "onlysource", 'de')).to eq("%{name}, welcome about2 source login %{place}!")
        end

        it "Get a string's translation and componet in offline" do
            expect(Sgtn.translate('onlyoffline.key2', "onlyoffline", 'en-US')).to eq("test value 2 source")
        end

        it "Get a string's translation and component in offline and locale is de" do
            expect(Sgtn.translate('onlyoffline.key1', "onlyoffline", 'de')).to eq("test value de1")
        end

        it "Get a string's translation and componet in online and source" do
            expect(Sgtn.translate('onlineandsource.key2', "onlineandsource", 'en-US')).to eq("online and source value 2")
        end

        it "Get a string's translation and component in online and source" do
            expect(Sgtn.translate('onlineandsource.key2', "onlineandsource", 'de')).to eq("online and source de value 2")
        end

        it "Get a string's translation and componet in online and offline" do
            expect(Sgtn.translate('intest.home', "onlineandoffline", 'en-US')).to eq("Home")
        end

        it "Get a string's translation and component in online and offline" do
            expect(Sgtn.translate('intest.home', "onlineandoffline", 'de')).to eq("Home de")
        end

        it "Get a string's translation and componet in online and offline" do
            expect(Sgtn.translate('source.key2', "offlineandsource", 'en-US')).to eq("test value 2")
        end

        it "Get a string's translation and component in online and offline" do
            expect(Sgtn.translate('source.key2', "offlineandsource", 'de')).to eq("test value de 2")
        end



        it "Get a string's translation and key is int___new discuss" do
            expect(Sgtn.translate(1234, 'about', 'en-US')).to eq(1234)
            #expect(SgtnClient::Translation.getString("allin", "about.message", "en-US")).to eq("Your application description page. offline")
        end

        it "Get a string's translation and key is notexist" do
            expect(Sgtn.translate("keynotexist", 'about', 'en-US')).to eq("keynotexist")
            #expect(SgtnClient::Translation.getString("allin", "about.message", "en-US")).to eq("Your application description page. offline")
        end



        it "Get a string's translation and key is onlyonline test" do
            expect(Sgtn.translate("about.onlyonline", 'allin', 'de')).to eq("online de 1")
        end

        it "Get a string's translation and key is onlysource" do
            expect(Sgtn.translate("about.onlysource", 'allin', 'de')).to eq("source 1")
        end

        it "Get a string's translation and key is onlyoffline" do
            expect(Sgtn.translate("about.onlyoffline", 'allin', 'de')).to eq("about.onlyoffline")
        end

        it "Get a string's translation and key is onlysourceandoffline" do
            expect(Sgtn.translate("about.offlineandource", 'allin', 'de')).to eq("test translation offline")
        end

        it "Get a string's translation and value not equal with online and offline" do
            expect(Sgtn.translate("about.title", 'allin', 'de')).to eq("About de")
        end

        it "Get a string's translation and value not equal with online and offline cpmpare" do
            expect(Sgtn.translate("about.welcome", 'allin', 'de')).to eq("{name}, welcome login {place} de online!")
        end

        it "Get a string's translation and value not equal with online and offline cpmpare about.sourceandoffline2" do
            expect(Sgtn.translate("about.sourceandoffline2", 'allin', 'de')).to eq("test add offline 2")
        end






        # it "Get a string's translation and source is equal" do
        #     expect(Sgtn.translate('about.testw', "allin", 'en', name: "haha", place: "single")).to eq("haha, welcome single!")
        # end

        # it "Get a string's translation and source is not equal" do
        #     expect(Sgtn.translate('about.notequal2', "allin", 'en', name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        # end

        # it "Get a string's translation and source is equal and locle is de" do
        #     expect(Sgtn.translate('about.testw', "allin", 'de', name: "haha", place: "single")).to eq("haha, welcome de single!")
        # end

        # it "Get a string's translation and source is equal and locle is DE___new bug 1691" do
        #     expect(Sgtn.translate('about.testw', "allin", 'DE', name: "haha", place: "single")).to eq("haha, welcome de single!")
        # end

        
        # it "Get a string's translation and source is not equal and locale is de" do
        #     expect(Sgtn.translate('about.notequal2', "allin", 'de', name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        # end

        # it "Get a string's translation and source is not equal and locale is da" do
        #     expect(Sgtn.translate('about.notequal2', "allin", 'da', name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        # end

        
        # it "Get a string's translation and source is not equal and locale is xxxxx" do
        #     expect(Sgtn.translate('about.notequal2', "allin", 'xxxxxx', name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        # end


        # it "Get a string's translation and source is not equal and locale is xxxxx" do
        #     expect(Sgtn.translate('about.notequal2', "allin", nil, name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        # end

        # it "Get a string's translation and source is not equal and locale is xxxxx" do
        #     expect(Sgtn.translate('about.notequal2', "allin", 123, name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        # end

        # it "Get a string's translation and source is not equal and locale is EN-uk" do
        #     expect(Sgtn.translate('about.notequal2', "allin", "en-UK", name: "haha", place: "single")).to eq("haha, welcome login not equal source single!")
        # end
        
        # it "Get a string's translation and source is zh-Hans-CN" do
        #     expect(Sgtn.translate('about.testw', "allin", 'zh-Hans-CN', name: "haha", place: "single")).to eq("haha, welcome zh-Hans single!")
        # end

        # it "Get a string's translation and placehorder is more than argument" do
        #     expect(Sgtn.translate('about.testw', "allin", 'zh-Hans', name: "haha", place: "single",aaaa: "xxxxx")).to eq("haha, welcome zh-Hans single!")
        # end

        # it "Get a string's translation and placehorder is less than argument____new discuss" do
        #     expect(Sgtn.translate('about.testw', "allin", 'zh-Hans', name: "haha")).to eq("haha, welcome zh-Hans single!")
        # end

        # it "Get a string's translation and placehorder is nil" do
        #     expect(Sgtn.translate('about.testw', "allin", 'zh-Hans', name: "haha", place: nil)).to eq("haha, welcome zh-Hans !")
        # end

        it "Get a string's translation and placehorder is int" do
            expect(Sgtn.translate('about.testw', "allin", 'zh-Hans', name: "haha", place: 123)).to eq("haha, welcome zh-Hans 123!")
        end

        # it "Get a string's translation and placehorder is empty" do
        #     expect(Sgtn.translate('about.testw', "allin", 'zh-Hans', name: "haha", place: "")).to eq("haha, welcome zh-Hans !")
        # end


        
        # it "Get a string's translation and locale is empty" do
        #     expect(Sgtn.translate('about.message', 'about')).to eq("Your application description page. offline")
        #     #expect(SgtnClient::Translation.getString("allin", "about.message", "en-US")).to eq("Your application description page. offline")
        # end

        # it "Get a string's translation and locale is singleton locale" do
        #     Sgtn.locale = "de"
        #     expect(Sgtn.translate('about.testw', 'about',name:"hhh" , place:"test")).to eq("hhh, welcome de test!")
        #     expect(Sgtn.translate('about.message', 'about')).to eq("test de key offline")
        #     #expect(SgtnClient::Translation.getString("allin", "about.message", "en-US")).to eq("Your application description page. offline")
        # end

        it "Get a string's translation and locale is singleton locale and default value" do
            Sgtn.locale = "de"
            expect(Sgtn.translate('about.addkey', 'allin',name:"hhh") {"default value"}) .to eq("test value hhh")
            expect(Sgtn.translate('about.notexist', 'allin',name:"hhhh") {"default value %{name}"}).to eq("default value hhhh")
            expect(Sgtn.translate('about.notequal2', 'allin',name:"hhhh" , place: "change") {"default value %{name} %{place}"}).to eq("hhhh, welcome login not equal en change!")
            #expect(SgtnClient::Translation.getString("allin", "about.message", "en-US")).to eq("Your application description page. offline")
        end






    end



end
