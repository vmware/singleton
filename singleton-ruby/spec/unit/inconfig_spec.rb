require 'spec_helper'
require_relative '../../lib/sgtn-client/sgtn-client.rb'

describe SgtnClient do

  describe "loadinconfig" do

    before :each do
      #SgtnClient.load("./spec/config/sgtnclient-invalide.yml", "test", './sgtnclient_config.log')
    end

    it "validate_configuration" do
      SgtnClient.load("./spec/config/sgtnclient-invalidate.yml", "test", './sgtnclient_config.log')
    end

  end 
end
