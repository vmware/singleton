require 'spec_helper'

describe SgtnClient do
  describe "File" do

    before :each do
      @configurations = SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
      @source_bundle = @configurations["source_bundle"] 
      SgtnClient::Config.configurations.default = 'default'
      @component = 'JAVA'
    end

    it "read_json" do
      product_name = @configurations["product_name"]
      version = @configurations["version"].to_s
      jsonfile = @configurations["translation_bundle"] + "/" + product_name + "/" + version + "/" + @component + "/messages_en.json"
      puts jsonfile
      expect(SgtnClient::FileUtil.read_json(jsonfile)["messages"]["helloworld"]).to eq "Hello world"
    end

    it "read_yml" do
      yamlfile = File.join(@source_bundle, @component + "/" + SgtnClient::Config.configurations.default + ".yml")
      expect(SgtnClient::FileUtil.read_yml(yamlfile)[SgtnClient::Config.configurations.default]["hello"]).to eq "Hello"
    end

  end
end
