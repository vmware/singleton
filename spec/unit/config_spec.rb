# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'spec_helper'
require_relative '../../lib/sgtn-client/sgtn-client.rb'

describe SgtnClient do

  describe "loadconfig" do

    before :each do
      SgtnClient.load("./spec/config/sgtnclient.yml", "test", './sgtnclient_config.log')
    end

    it "define configuration" do
      env = SgtnClient::Config.default_environment
      mode = SgtnClient::Config.configurations[env]["mode"]
      expect(mode).to eq 'sandbox'
    end

    it "not define configuration" do
      begin
        SgtnClient::Config.config("aa", { :app_id => "XYZ" })
      rescue => exception
        expect(exception.message).to eq 'Configuration[aa] NotFound'
      end
    end

  end 
end
