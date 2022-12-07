require 'rest-client'
require 'rspec'
require 'singleton-client'


describe "get_translations online test" do
    before :each do
        SgtnClient.config.instance_variable_set('@loader', nil)
        Sgtn.load_config("./config/sgtnclient.yml", "onlineandofflinetranslations")
    end

    context "get_translations methods" do


        it "get component by get_translations and arguments is more than 2" do
            expect{Sgtn.get_translations("about", 'en', "add")}.to raise_error(ArgumentError)
            
        end

        it "get component by get_translations and arguments is less than 2" do
            #expect{Sgtn.get_translations("about")}.to raise_error(ArgumentError)
            Sgtn.locale = "de"
            expect(Sgtn.get_translations("about")).to eq({"about.key1"=>"test value de 1", "about.key2"=>"test value de 2", "about.key3"=>"test value de 3", "about.key4"=>"test value de 4"})

        end

        it "get component by get_translations and arguments is empty" do
            #expect{Sgtn.get_translations("about")}.to raise_error(ArgumentError)
            Sgtn.locale = "de"
            expect{Sgtn.get_translations()}.to raise_error(ArgumentError)
        end

        it "get component by get_translations" do
            expect(Sgtn.get_translations("about", 'en')).to eq({"about.key1"=>"test value 1", "about.key2"=>"test value 2", "about.key3"=>"test value 3", "about.key4"=>"test value 4"})
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
        
        it "get component by get_translations and component only in online" do
            expect(Sgtn.get_translations("onlyonline", 'en')).to eq({"about2.key1"=>"test value 1", "about2.key2"=>"test value 2"})
        end

        it "get component by get_translations and component only in offline" do
            expect(Sgtn.get_translations("onlyoffline", 'de')).to eq({"onlyoffline.key1"=>"add value de 1", "onlyoffline.key2"=>"add value de 2", "onlyoffline.key3"=>"add value de 3", "onlyoffline.key4"=>"add value 4"})
        end

        it "get component by get_translations and source not equal" do
            expect(Sgtn.get_translations("intest", 'en')).to eq({"intest.key1"=>"add value 1", "intest.key2"=>"add value source 2", "intest.key4"=>"add value 4"})
        end

        it "get component by get_translations and source not equal and locale is de" do
            expect(Sgtn.get_translations("intest", 'de')).to eq({"intest.key1"=>"add value de 1", "intest.key2"=>"add value source 2", "intest.key3"=>"add value de 3", "intest.key4"=>"add value 4"})
        end

        it "get component by get_translations and source not equal" do
            expect(Sgtn.get_translations("intest", 'da')).to eq({"intest.key1"=>"add value 1", "intest.key2"=>"add value source 2", "intest.key4"=>"add value 4"})
        end

        it "get component by get_translations and source not equal" do
            expect(Sgtn.get_translations("intest", 'xxxx')).to eq({"intest.key1"=>"add value 1", "intest.key2"=>"add value source 2", "intest.key4"=>"add value 4"})
        end

        it "get component by get_translations and source not equal" do
            Sgtn.locale = nil
            expect(Sgtn.get_translations("intest", nil)).to eq({"intest.key1"=>"add value 1", "intest.key2"=>"add value source 2", "intest.key4"=>"add value 4"})
        end

        it "get component by get_translations and source not equal" do
            expect(Sgtn.get_translations("intest", 123)).to eq({"intest.key1"=>"add value 1", "intest.key2"=>"add value source 2", "intest.key4"=>"add value 4"})
        end

        
        it "get component by get_translations and source not equal" do
            expect(Sgtn.get_translations("intest", "en_UK")).to eq({"intest.key1"=>"add value 1", "intest.key2"=>"add value source 2", "intest.key4"=>"add value 4"})
        end

        it "get component by get_translations and source not equal" do
            expect(Sgtn.get_translations("intest", "zh-Hans-CN")).to eq({"intest.key1"=>"add value zh 1", "intest.key2"=>"add value source 2", "intest.key3"=>"add value zh 3", "intest.key4"=>"add value 4"})
        end

        it "get component by get_translations and source not equal___new bug 1691" do
            expect(Sgtn.get_translations("intest", "DE")).to eq({"intest.key1"=>"add value de 1", "intest.key2"=>"add value source 2", "intest.key3"=>"add value de 3", "intest.key4"=>"add value 4"})
        end 

        it "get component by get_translations and source not equal and locale is de" do
            expect(Sgtn.get_translations("intest", 'fr-CA')).to eq({"intest.key1"=>"add value fr-CA 1", "intest.key2"=>"add value source 2", "intest.key3"=>"add value fr-CA 3", "intest.key4"=>"add value 4"})
        end

        it "get component by get_translations and source not equal and locale is de" do
            expect(Sgtn.get_translations("intest", 'fr')).to eq({"intest.key1"=>"add value 1", "intest.key2"=>"add value source 2", "intest.key4"=>"add value 4"})
        end

    end



end
