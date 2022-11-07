#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe Sgtn::Pseudo, :include_helpers, :extend_helpers do
  include_context 'reset client'

  let(:stubs) { [] }

  before :all do
    Sgtn.pseudo_mode = true
    WebMock.enable!
    WebMock.disable_net_connect!
    Sgtn.vip_server = singleton_server
  end
  after :all do
    WebMock.disable!
  end

  before :each do
    WebMock.reset!
  end

  it 'should be able to get pseudo translation' do
    stubs << bundle_stub(component, latest_locale, stub_response("#{component}-#{latest_locale}"))
    stubs << bundle_stub(component, Sgtn::PSEUDO_LOCALE, pseudo_response).with(query: { 'pseudo' => true })

    expect(Sgtn.translate(key, component, en_locale)).to eq "#@#{en_value}#@"
    expect(Sgtn.t(source_changed_key, component, locale)).to eq '@@Source Hello world@@'

    expect(Sgtn.get_translations(component, locale)).to include({ key => "#@#{en_value}#@" })

    stubs.each { |stub| expect(stub).to have_been_requested }
  end
end
