#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe Sgtn::I18nBackend, :include_helpers, :extend_helpers do
  include_context 'reset client'

  let(:backend) { Sgtn::I18nBackend.new(component) }

  it '#translate a string' do
    expect(backend.translate(locale, key, {})).to eq value
  end

  it '#translate an English string' do
    expect(backend.translate(en_locale, key, {})).to eq en_value
  end

  it '#as an I18n backend' do
    I18n.backend = Sgtn::I18nBackend.new(component)
    I18n.locale = locale
    expect(I18n.t(key)).to eq value
    I18n.locale = en_locale
    expect(I18n.t(key)).to eq en_value
  end

  it '#as an I18n backend in chain' do
    I18n.backend = I18n::Backend::Chain.new(Sgtn::I18nBackend.new(component))
    I18n.locale = locale
    expect(I18n.t(key)).to eq value
    I18n.locale = en_locale
    expect(I18n.t(key)).to eq en_value
  end

  it '#as an I18n backend in the first position of chain' do
    I18n.backend = I18n::Backend::Chain.new(Sgtn::I18nBackend.new(component), I18n.backend)
    I18n.locale = locale
    expect(I18n.t(key)).to eq value
    I18n.locale = en_locale
    expect(I18n.t(key)).to eq en_value
  end

  it '#as an I18n backend in the second position of chain' do
    I18n.backend = I18n::Backend::Chain.new(I18n.backend, Sgtn::I18nBackend.new(component))
    I18n.locale = locale
    expect(I18n.t(key)).to eq value
    I18n.locale = en_locale
    expect(I18n.t(key)).to eq en_value
  end

  # it '#pseudo mode' do
  #   Sgtn.pseudo_mode = true
  #   old_enforce = I18n.enforce_available_locales
  #   I18n.enforce_available_locales = false

  #   stubs << components_stub << locales_stub
  #   stubs << bundle_stub(component, en_locale, stub_response("#{component}-#{en_locale}"))
  #   stubs << bundle_stub(component, Sgtn::PSEUDO_LOCALE, pseudo_response).with(query: { 'pseudo' => true })

  #   I18n.backend = Sgtn::I18nBackend.new(component_local_source_only)

  #   expect(I18n.exists?(message_only_in_local_source_key, locale)).to eq true

  #   expect(I18n.translate(message_only_in_local_source_key, locale: locale)).to eq '@@New Hello world@@'

  #   stubs.each { |stub| expect(stub).to have_been_requested }
  # ensure
  #   Sgtn.pseudo_mode = false
  #   I18n.enforce_available_locales = old_enforce
  # end
end
