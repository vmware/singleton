#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe SgtnClient::Fallbacks, :include_helpers, :extend_helpers do
  include_context 'reset client'

  err_msg = 'temporary error'

  before  { clear_cache }

  it '#translate a string' do
    expect(SgtnClient.config.loader).to receive(:get_bundle).with(component, locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered
    expect(SgtnClient.config.loader).to receive(:get_bundle).with(component, en_locale).once.and_call_original.ordered

    expect(Sgtn.translate(key, component, locale)).to eq en_value

    expect(Sgtn.t(key, component, locale)).to eq en_value
  end

  it '#translate a bundle\'s translation' do
    expect(SgtnClient.config.loader).to receive(:get_bundle).with(component, locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered
    expect(SgtnClient.config.loader).to receive(:get_bundle).with(component, en_locale).once.and_call_original.ordered

    bundle = Sgtn.get_translations(component, locale)
    expect(bundle['messages']['helloworld']).to eq en_value
  end

  it '#translate a string to raise error' do
    expect(SgtnClient.config.loader).to receive(:get_bundle).with(component, locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered
    expect(SgtnClient.config.loader).to receive(:get_bundle).with(component, en_locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered

    expect { backend.t!(key, component, locale) }.to raise_error(err_msg)
  end

  it '#translate a bundle\'s translation to raise error' do
    expect(SgtnClient.config.loader).to receive(:get_bundle).with(component, locale).once.and_raise(SgtnClient::SingletonError.new(err_msg)).ordered
    expect(SgtnClient.config.loader).to receive(:get_bundle).with(component, en_locale).once.and_call_original.ordered

    bundle = Sgtn.get_translations(component, locale)
    expect(bundle['messages']['helloworld']).to eq en_value
  end
end
