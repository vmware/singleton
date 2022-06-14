require 'rest-client'
require 'rspec'
require 'singleton-client'

describe "Translation test" do
    before :each do
      Sgtn.load_config("./config/sgtnclient.yml", "mixed")
    end

      it "DateTime1" do
        d = DateTime.new(2007,11,19,8,37,48,"-06:00")
        expect(d.l_full_s('es')).to eq 'lunes, 19 de noviembre de 2007, 14:37:48 (tiempo universal coordinado)'
        expect(d.l_long_s(:es)).to eq '19 de noviembre de 2007, 14:37:48 UTC'
        expect(d.l_medium_s(:es)).to eq '19 nov 2007, 14:37:48'
        expect(d.l_short_s(:es)).to eq '19/11/07, 14:37'
        # puts d.l_full_s(:xxxx)
        # xxx = d.l_full_s(:zh-Hans)
      end
  
      it "Date1" do
        d = Date.new(2001,2,3)
        expect(d.l_full_s(:es)).to eq 'sábado, 3 de febrero de 2001'
        expect(d.l_long_s(:es)).to eq '3 de febrero de 2001'
        expect(d.l_medium_s(:es)).to eq '3 feb 2001'
        expect(d.l_short_s(:es)).to eq '3/2/01'
        # puts d.l_full_s(:xxxx)
        # xxx = d.l_full_s(:zh-Hans)
      end
  
      it "DateOfDateTime" do
        d = DateTime.new(2007,11,19,8,37,48,"-06:00")
        expect(d.to_date.l_full_s(:es)).to eq 'lunes, 19 de noviembre de 2007'
        expect(d.to_date.l_long_s(:es)).to eq '19 de noviembre de 2007'
        expect(d.to_date.l_medium_s(:es)).to eq '19 nov 2007'
        expect(d.to_date.l_short_s(:es)).to eq '19/11/07'
        # puts d.to_date.l_full_s(:xxxx)
        # xxx = d.to_date.l_full_s(:zh-Hans)
      end
      
  
      it "time1" do
        d = Time.new(2007,11,1,15,25,0, "+09:00")
        expect(d.l_full_s(:'es')).to eq '6:25:00 (tiempo universal coordinado)'
        expect(d.l_long_s(:es)).to eq '6:25:00 UTC'
        expect(d.l_medium_s(:es)).to eq '6:25:00'
        expect(d.l_short_s(:es)).to eq '6:25'
        # puts d.l_full_s(:xxxx)
        # xxx = d.l_full_s(:zh-Hans)
      end
    

      it "errorTime" do
        d = DateTime.new(2007,11,19,8,37,48,"-06:00")
        expect(d.l_full_s(:es)).to eq 'lunes, 19 de noviembre de 2007, 14:37:48 (tiempo universal coordinado)'
        expect(d.l_long_s(:es)).to eq '19 de noviembre de 2007, 14:37:48 UTC'
        expect(d.l_medium_s(:es)).to eq '19 nov 2007, 14:37:48'
        expect(d.l_short_s(:es)).to eq '19/11/07, 14:37'
      end

      it "testtimezone" do
        d = DateTime.new(2007,11,19,8,37,48,"-06:00")
        expect(d.l_full_s(:es, 'America/Los_Angeles')).to eq 'lunes, 19 de noviembre de 2007, 6:37:48 (hora estándar del Pacífico)'
        #expect(d.l_full_s(:es, 'Europe/xxxx')).to raise_error
        expect(d.l_long_s(:es, 'America/Los_Angeles')).to eq '19 de noviembre de 2007, 6:37:48 GMT-8'
        expect(d.l_medium_s(:es, 'America/Los_Angeles')).to eq '19 nov 2007, 6:37:48'
        expect(d.l_short_s(:es, 'America/Los_Angeles')).to eq '19/11/07, 6:37'
      end

      # it "Datetestzone" do
      #   d = Date.new(2001,2,3)
      #   expect(d.l_full_s(:es,'America/Los_Angxxx')).to eq 'sábado, 3 de febrero de 2001'
      #   expect(d.l_long_s(:es)).to eq '3 de febrero de 2001'
      #   expect(d.l_medium_s(:es)).to eq '3 feb 2001'
      #   expect(d.l_short_s(:es)).to eq '3/2/01'
      #   # puts d.l_full_s(:xxxx)
      #   # xxx = d.l_full_s(:zh-Hans)
      # end

      # it "timetestzone" do
      #   d = Time.new(2007,11,1,15,25,0, "+09:00")
      #   expect(d.l_full_s(:es, 'America/Los_Angeles')).to eq '6:25:00 (tiempo universal coordinado)'
      #   # expect(d.l_long_s(:es)).to eq '6:25:00 UTC'
      #   # expect(d.l_medium_s(:es)).to eq '6:25:00'
      #   # expect(d.l_short_s(:es)).to eq '6:25'
      #   # puts d.l_full_s(:xxxx)
      #   # xxx = d.l_full_s(:zh-Hans)
      # end

end
