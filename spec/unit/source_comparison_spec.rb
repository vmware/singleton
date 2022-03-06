# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'spec_helper'
require './spec/support/source_comparison_helper.rb'

RSpec.configure do |c|
  c.include SourceComparisonHelpers
end

describe SgtnClient do
  describe "source comparison" do

    before :each do
      configs = SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
      SgtnClient::Config.configurations.default = "default"
      component = 'H5C'
      @translations = load_bundle("#{configs["translation_bundle"]}/#{configs["product_name"]}/#{configs["version"]}/#{component}/messages_es.json")
      @sources = load_bundle("#{configs["source_bundle"]}/#{component}/#{SgtnClient::Config.configurations.default}.yml")
      @old_sources =load_bundle("#{configs["translation_bundle"]}/#{configs["product_name"]}/#{configs["version"]}/#{component}/messages_en.json")
      @key_updated_source = 'AdminUi.ExportSystemLogsWizard.deselectAll'
    end


    it "compare_each_source_key_large_data_set" do 
      expect(@translations["messages"].length).to eq 29992 # H5C component has 29,992 key-value pairs
      SgtnClient::Translation.compare_each_source_key(@translations, @sources, @old_sources)
      expect {
        updated_source = @sources[SgtnClient::Config.configurations.default][@key_updated_source]
        expect(@translations["messages"][@key_updated_source]).to eq updated_source 
      }.to perform_under(1).ms
    end
  end
end
