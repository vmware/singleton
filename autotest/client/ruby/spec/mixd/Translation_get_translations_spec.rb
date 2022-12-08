require 'rest-client'
require 'rspec'
require 'singleton-client'

describe "get_translations online test" do
    before :each do
        SgtnClient.config.instance_variable_set('@loader', nil)
        #Sgtn.load_config("./config/sgtnclient.yml", "mixedtranslations")
        Sgtn.load_config("./config/sgtnclient.yml", "mixedtranslations")
    end

    context "get_translations methods" do


        it "get component by get_translations and arguments is more than 2" do
            expect{Sgtn.get_translations("about", 'en', "add")}.to raise_error(ArgumentError)
            
        end

        it "get component by get_translations and arguments is less than 2" do
            #expect{Sgtn.get_translations("about")}.to raise_error(ArgumentError)
            Sgtn.locale = "de"
            expect(Sgtn.get_translations("allin")).to eq({"about.addkey"=>"test value %{name}", "about.change"=>"%{name}, welcome chanege de %{place}!", "about.description"=>"Use this area to provide additional information source", "about.insource"=>"%{name}, only in source %{place}!", "about.key1"=>"fall back key1", "about.key3"=>"fall back key31", "about.message"=>"test de key offline", "about.notequal"=>"{name}, welcome login not equal {place}!", "about.notequal2"=>"%{name}, welcome login not equal source %{place}!", "about.onlyonline"=>"online de 1", "about.sourceandoffline2"=>"test add offline 2", "about.test"=>"test de the %1$s to %2$s", "about.test1"=>"test source the {1} to {2}", "about.testw"=>"%{name}, welcome de %{place}!", "about.title"=>"About de", "about.welcome"=>"{name}, welcome login {place} latest!", "com.vmware.loginsight.web.settings.stats.StatsTable.host"=>"123"})

        end

        it "get component by get_translations and arguments is empty" do
            #expect{Sgtn.get_translations("about")}.to raise_error(ArgumentError)
            Sgtn.locale = "de"
            expect{Sgtn.get_translations()}.to raise_error(ArgumentError)
        end

        it "get component by get_translations" do
            expect(Sgtn.get_translations("allin", 'en')).to eq({"about.addkey"=>"test value %{name}", "about.description"=>"Use this area to provide additional information source", "about.insource"=>"%{name}, only in source %{place}!", "about.key1"=>"fall back key1", "about.key3"=>"fall back key31", "about.message"=>"Your application description page. offline", "about.notequal"=>"{name}, welcome login not equal {place}!", "about.notequal2"=>"%{name}, welcome login not equal source %{place}!", "about.onlyonline"=>"online 1", "about.sourceandoffline2"=>"test add offline 2", "about.test1"=>"test source the {1} to {2}", "about.testw"=>"%{name}, welcome %{place}!", "about.title"=>"About", "about.welcome"=>"{name}, welcome login {place} latest!", "com.vmware.loginsight.web.settings.stats.StatsTable.host"=>"123"})
        end

        it "get component by get_translations and component is int" do
            expect(Sgtn.get_translations(123, 'en')).to eq(nil)
        end

        it "get component by get_translations and component is nil" do
            expect(Sgtn.get_translations(nil, 'en')).to eq(nil)
        end

        
        it "get component by get_translations and component is not exist" do
            expect(Sgtn.get_translations("notexist", 'en')).to eq(nil)
        end
        
        it "get component by get_translations and component not in source" do
            expect(Sgtn.get_translations("onlyonline", 'en')).to eq({"contact.applicationname"=>"Singleton Sample Web Application", "contact.message"=>"Your contact page.", "contact.title"=>"Contact"})
        end

        it "get component by get_translations and component not in source and locale is de___new dleng bug" do
            expect(Sgtn.get_translations("onlyonline", 'de')).to eq({"contact.applicationname"=>"Singleton Sample Web Application", "contact.message"=>"Your contact de page.", "contact.title"=>"Contact"})
        end

        it "get component by get_translations and component not in source and locale is de" do
            expect(Sgtn.get_translations("onlyoffline", 'de')).to eq({"onlyoffline.key1"=>"test value de1", "onlyoffline.key2"=>"test value 2 source", "onlyoffline.key3"=>"test value  de 3"})
        end

        
        it "Get a string's translation and key is int___new discuss" do
            expect(Sgtn.get_translations(123, 'de')).to eq(nil)
            #expect(SgtnClient::Translation.getString("about", "about.message", "en-US")).to eq("Your application description page. offline")
        end

        it "Get a string's translation and key is notexist" do
            expect(Sgtn.get_translations("notexist", 'de')).to eq(nil)
            #expect(SgtnClient::Translation.getString("about", "about.message", "en-US")).to eq("Your application description page. offline")
        end






        

    end



end
