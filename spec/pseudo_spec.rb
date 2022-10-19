#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe Sgtn::Pseudo, :include_helpers, :extend_helpers do
  include_context 'reset client'

  before :all do
    Sgtn.pseudo_mode = true
  end

  it 'should be able to get pseudo translation' do
    expect(Sgtn.translate(key, component, locale)).to eq "@@#{en_value}@@"
    expect(Sgtn.t(key, component, locale)).to eq "@@#{en_value}@@"
  end

  it 'should be able to get pseudo translations of a bundle' do
    expect(Sgtn.get_translations(component, locale)['messages']).to include({ key => "@@#{en_value}@@" })
  end
end
