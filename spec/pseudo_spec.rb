describe SgtnClient::Pseudo, :include_helpers, :extend_helpers do
  include_context 'reset client' do
    Sgtn.config.pseudo_mode = true
  end

  it 'should be able to get pseudo translation' do
    expect(Sgtn.translate(key, component, locale)).to eq "@@#{en_value}@@"
    expect(Sgtn.t(key, component, locale)).to eq "@@#{en_value}@@"
  end

  it 'should be able to get pseudo translations of a bundle' do
    expect(Sgtn.get_translations(component, locale)['messages']).to include({ key => "@@#{en_value}@@" })
  end
end
