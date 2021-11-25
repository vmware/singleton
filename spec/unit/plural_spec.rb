require 'spec_helper'

describe SgtnClient do
  describe "Plural" do

    before :each do
      env = SgtnClient::Config.default_environment
      SgtnClient::Config.configurations[env]["bundle_mode"] = 'offline'
      SgtnClient::Source.loadBundles("default")
    end

    it "plural_fallback" do
      expect(SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 1 }, "zh-Hans")).to eq 'there is one cat in the room'
      expect(SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 0 }, "zh-Hans")).to eq 'there are 0 cats in the room'
      expect(SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 5 }, "zh-Hans")).to eq 'there are 5 cats in the room'
      expect(SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 3 }, "zh-Hans")).to eq 'there are 3 cats in the room'
      expect(SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 2 }, "zh-Hans")).to eq 'there are 2 cats in the room'
    end

    it "plural_normal" do
      expect(SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 1 }, "en")).to eq 'there is one cat in the room'
      expect(SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 0 }, "en")).to eq 'there are 0 cats in the room'
      expect(SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 5 }, "en")).to eq 'there are 5 cats in the room'
      expect(SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 3 }, "en")).to eq 'there are 3 cats in the room'
      expect(SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 2 }, "en")).to eq 'there are 2 cats in the room'
    end
    
    it "plural_to" do
      str = 'there %<{ "cat_count": { "one": "is one cat", "zero":"is no cat", "two": "are two cats", "few": "are some cats", "many": "are many cats", "other": "are %{cat_count} cats" } }> in the room'
      expect(str.to_plural_s(:cy, { :cat_count => 1 })).to eq 'there is one cat in the room'
      expect(str.to_plural_s(:cy, { :cat_count => 0 })).to eq 'there is no cat in the room'
      expect(str.to_plural_s(:cy, { :cat_count => 5 })).to eq 'there are 5 cats in the room'
      expect(str.to_plural_s(:cy, { :cat_count => 3 })).to eq 'there are some cats in the room'
      expect(str.to_plural_s(:cy, { :cat_count => 2 })).to eq 'there are two cats in the room'
      s = '%<{"count": {"0": "no horse", "one": "one horse", "other": "%{count} horses"}}>'
      expect(s.to_plural_s(:cy, { :count => 0 })).to eq 'no horse'
      
    end
  end
end
