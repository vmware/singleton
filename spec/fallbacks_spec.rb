#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe SgtnClient::Fallbacks, :include_helpers, :extend_helpers do
  include_context 'reset client'

  err_msg = 'temporary error'

  before  { reset_client }

  describe '#zh-Hans' do
    it '#translate a string' do
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, en_locale).twice.and_call_original.ordered

      # request zh-Hans once then request en once
      expect(Sgtn.translate(key, component, locale)).to eq en_value

      # test 't' method
      # request en directly because zh-Hans isn't a valid locale any more
      expect(Sgtn.t(key, component, locale)).to eq en_value
    end

    it "#get a bundle's translation" do
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, en_locale).once.and_call_original.ordered

      bundle = Sgtn.get_translations(component, locale)
      expect(bundle['helloworld']).to eq en_value
    end

    it '#translate a string to raise error' do
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, en_locale).twice.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered

      expect { Sgtn.translate!(key, component, locale) }.to raise_error(err_msg)

      # test 't!' method
      # request en directly because zh-Hans isn't a valid locale any more
      expect { Sgtn.t!(key, component, locale) }.to raise_error(err_msg)
    end

    it "#get a bundle's translation to raise error" do
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, en_locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered

      expect { Sgtn.get_translations!(component, locale) }.to raise_error(err_msg)
    end
  end

  describe '#English' do
    it '#translate a string' do
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, en_locale).once.and_call_original.ordered

      expect(Sgtn.translate(key, component, en_locale)).to eq en_value
    end

    it "#get a bundle's translation" do
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, en_locale).once.and_call_original.ordered

      bundle = Sgtn.get_translations(component, en_locale)
      expect(bundle['helloworld']).to eq en_value
    end

    it '#translate a string to raise error' do
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, en_locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered

      expect { Sgtn.t!(key, component, en_locale) }.to raise_error(err_msg)
    end

    it "#get a bundle's translation to raise error" do
      expect(Sgtn.config.loader).to receive(:get_bundle).with(component, en_locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered

      expect { Sgtn.get_translations!(component, en_locale) }.to raise_error(err_msg)
    end
  end
end
