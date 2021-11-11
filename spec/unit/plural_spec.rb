require 'spec_helper'
require 'twitter_cldr'

describe SgtnClient do
  describe "Plural" do

    before :each do

    end
    
    it "plural" do
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
