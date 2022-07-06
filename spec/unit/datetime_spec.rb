# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe SgtnClient do
  describe "DateTime" do

    before :each do

    end

    it "DateTime" do
      d = DateTime.new(2007,11,19,8,37,48,"-06:00")
      expect(d.l_full_s(:es)).to eq 'lunes, 19 de noviembre de 2007, 14:37:48 (tiempo universal coordinado)'
      expect(d.l_long_s(:es)).to eq '19 de noviembre de 2007, 14:37:48 UTC'
      expect(d.l_medium_s(:es)).to eq '19 nov 2007, 14:37:48'
      expect(d.l_short_s(:es)).to eq '19/11/07, 14:37'
    end

    it "Date" do
      d = Date.new(2001,2,3)
      expect(d.l_full_s(:es)).to eq 'sábado, 3 de febrero de 2001'
      expect(d.l_long_s(:es)).to eq '3 de febrero de 2001'
      expect(d.l_medium_s(:es)).to eq '3 feb 2001'
      expect(d.l_short_s(:es)).to eq '3/2/01'
    end

    it "DateOfDateTime" do
      d = DateTime.new(2007,11,19,8,37,48,"-06:00")
      expect(d.to_date.l_full_s(:es)).to eq 'lunes, 19 de noviembre de 2007'
      expect(d.to_date.l_long_s(:es)).to eq '19 de noviembre de 2007'
      expect(d.to_date.l_medium_s(:es)).to eq '19 nov 2007'
      expect(d.to_date.l_short_s(:es)).to eq '19/11/07'
    end

    it "time" do
      d = Time.new(2007,11,1,15,25,0, "+09:00")
      expect(d.l_full_s(:es)).to eq '6:25:00 (tiempo universal coordinado)'
      expect(d.l_long_s(:es)).to eq '6:25:00 UTC'
      expect(d.l_medium_s(:es)).to eq '6:25:00'
      expect(d.l_short_s(:es)).to eq '6:25'
    end

    it "timezone" do
      d = DateTime.new(2007,11,19,8,37,48,"-06:00")

      expect(d.l_full_s(:es, 'America/Los_Angeles')).to eq 'lunes, 19 de noviembre de 2007, 6:37:48 (hora estándar del Pacífico)'
      expect(d.l_long_s(:es, 'America/Los_Angeles')).to eq '19 de noviembre de 2007, 6:37:48 GMT-8'
      expect(d.l_medium_s(:es, 'America/Los_Angeles')).to eq '19 nov 2007, 6:37:48'
      expect(d.l_short_s(:es, 'America/Los_Angeles')).to eq '19/11/07, 6:37'
    
      expect(d.l_full_s(:es, 'America/Los_Angeles', :long_gmt)).to eq 'GMT-08:00'

    end 
  end

end
