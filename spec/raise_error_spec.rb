#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe 'raise_error', :include_helpers, :extend_helpers do
  include_context 'reset client'

  backend = Class.new { include SgtnClient::Translation::Implementation }.new
  err_msg = 'temporary error'

  before  { reset_client }

  it '#translate a string with errors' do
    expect(Sgtn.config.loader).to receive(:get_bundle).with(component, locale).twice.and_raise(SgtnClient::SingletonError.new(err_msg))

    expect { backend.translate!(key, component, locale) }.to raise_error(err_msg)

    clear_cache
    expect { backend.t!(key, component, locale) }.to raise_error(err_msg)
  end

  it '#translate a string without errors' do
    expect(Sgtn.config.loader).to receive(:get_bundle).with(component, locale).twice.and_call_original

    expect(backend.translate(key, component, locale)).to eq value

    clear_cache
    expect(backend.t(key, component, locale)).to eq value
  end

  it "#get a bundle's translation with errors" do
    expect(Sgtn.config.loader).to receive(:get_bundle).with(component, locale).once.and_raise(SgtnClient::SingletonError.new(err_msg))

    expect { backend.get_translations!(component, locale) }.to raise_error(err_msg)
  end

  it "#get a bundle's translation without errors" do
    expect(Sgtn.config.loader).to receive(:get_bundle).with(component, locale).once.and_call_original

    expect(backend.get_translations(component, locale)['helloworld']).to eq value
  end

  it 'nonexistent key' do
    expect { backend.translate!(key_nonexistent, component, locale) }.to raise_error(KeyError)
  end
end
