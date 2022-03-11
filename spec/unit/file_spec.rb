#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
require 'spec_helper'

describe SgtnClient do
  describe "File" do

    before :each do
      @configurations = SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
      @component = 'JAVA'
    end

    it "read_json" do
      jsonfile = @configurations["translation_bundle"] + "/" + @configurations["product_name"] + "/" + @configurations["version"].to_s + "/" + @component + "/messages_en.json"
      expect(SgtnClient::FileUtil.read_json(jsonfile)["messages"]["helloworld"]).to eq "Hello world"
    end

    it "read_yml" do
      SgtnClient::Config.configurations.default = 'default'
      yamlfile = File.join(@configurations["source_bundle"], @component + "/" + SgtnClient::Config.configurations.default + ".yml")
      expect(SgtnClient::FileUtil.read_yml(yamlfile)[SgtnClient::Config.configurations.default]["hello"]).to eq "Hello"
    end

  end
end
