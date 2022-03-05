# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'spec_helper'

describe SgtnClient do
  describe "source comparison" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Config.configurations[env]["cache_expiry_period"] = 1
      SgtnClient::Config.configurations.default = "default"
      SgtnClient::Source.loadBundles(SgtnClient::Config.configurations.default)
      @component = 'H5C'
      @translations = SgtnClient::Translation.load(@component, "es")
      @sources, @old_sources = SgtnClient::Translation.get_bundles_for_source_comparison(@component)
      @key_updated_source = 'AdminUi.ExportSystemLogsWizard.deselectAll'
    end

    it "compare_each_source_key_large_data_set" do 
      expect(@translations["messages"].length).to eq 29992 # H5C component has 29,992 key-value pairs
      SgtnClient::Translation.compare_each_source_key(@translations, @sources, @old_sources)
      expect {
        updated_source = @sources[SgtnClient::Config.configurations.default][@key_updated_source]
        # need PR 1551 for the following test to work
        #expect(@translations["messages"][@key_updated_source]).to eq updated_source 
      }.to perform_under(1).ms
    end
  end
end
