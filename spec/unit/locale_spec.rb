require 'spec_helper'

describe SgtnClient do
  describe "Locale" do

    before :each do
    end

    it "fallback" do
      expect(SgtnClient::LocaleUtil.fallback('ja-JP')).to eq 'ja'
      expect(SgtnClient::LocaleUtil.fallback('de-DE')).to eq 'de'
      expect(SgtnClient::LocaleUtil.fallback('zh')).to eq 'zh'
      expect(SgtnClient::LocaleUtil.fallback('zh-Hans')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.fallback('zh-Hant')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.fallback('zh-CN')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.fallback('zh-TW')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.fallback('zh-Hans-CN')).to eq 'zh-Hans'
      expect(SgtnClient::LocaleUtil.fallback('zh-Hant-TW')).to eq 'zh-Hant'
      expect(SgtnClient::LocaleUtil.fallback('kong-kong')).to eq 'kong-kong'
    end

    it "process_locale" do
      expect(SgtnClient::LocaleUtil.process_locale(:'de-DE')).to eq 'de-DE'
      expect(SgtnClient::LocaleUtil.process_locale(:ja)).to eq 'ja'
      expect(SgtnClient::LocaleUtil.process_locale(:'ja')).to eq 'ja'
    end
  end

end
