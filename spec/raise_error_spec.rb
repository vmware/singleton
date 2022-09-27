#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe 'raise_error', :include_helpers, :extend_helpers do
  include_context 'reset client'

  backend = Class.new { include SgtnClient::Translation::Implementation }.new
  err_msg = 'temporary error'

  before  { clear_cache }

  it '#translate a string' do
    expect(SgtnClient.config.loader).to receive(:get_bundle).with(component, locale).twice.and_raise(SgtnClient::SingletonError.new(err_msg))
    expect { backend.translate!(key, component, locale) }.to raise_error(err_msg)
    clear_cache
    expect { backend.t!(key, component, locale) }.to raise_error(err_msg)
  end

  it '#get a bundle\'s translation' do
    expect(SgtnClient.config.loader).to receive(:get_bundle).with(component, locale).once.and_raise(SgtnClient::SingletonError.new(err_msg))
    expect { backend.get_translations!(component, locale) }.to raise_error(err_msg)
  end
end
