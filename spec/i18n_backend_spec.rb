#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe Sgtn::I18nBackend, :include_helpers, :extend_helpers do
  include_context 'reset client'

  let(:backend) { Sgtn::I18nBackend.new(component) }

  before(:each) do
    I18n.backend = I18n::Backend::Simple.new
  end

  it '#itself' do
    expect(backend.initialized?).to eq true

    expect(backend.load_translations).to be_nil
    expect(backend.store_translations).to be_nil
    expect(backend.available_locales).to eq ["en", "de", "zh-Hans", "zh-Hant", "latest"]
    expect(backend.reload!).to be_nil
    expect(backend.eager_load!).to be_nil
    expect(backend.translations).to be_nil
    expect(backend.exists?(locale, key, I18n::EMPTY_HASH)).to eq true

    expect(backend.translate(locale, key, {})).to eq value
    expect(backend.translate(en_locale, key, {})).to eq en_value
  end

  it '#as the only one I18n backend' do
    I18n.backend = backend

    I18n.locale = en_locale
    expect(I18n.t(key)).to eq en_value

    I18n.locale = locale
    expect(I18n.t(key)).to eq value

    expect(I18n.reload!).to be_nil
    expect(I18n.eager_load!).to be_nil
    expect(I18n.translate!(key)).to eq value
    expect(I18n.exists?(key, locale)).to eq true
    expect(I18n.localize(Time.now)).to be_nil
    expect(I18n.locale_available?(locale)).to eq true
  end

  it '#as an I18n backend in chain' do
    I18n.backend = I18n::Backend::Chain.new(backend)
    I18n.locale = locale
    expect(I18n.t(key)).to eq value
    I18n.locale = en_locale
    expect(I18n.t(key)).to eq en_value
  end

  it '#as an I18n backend in the first position of chain' do
    I18n.backend = I18n::Backend::Chain.new(backend, I18n.backend)
    I18n.locale = locale
    expect(I18n.t(key)).to eq value
    I18n.locale = en_locale
    expect(I18n.t(key)).to eq en_value
  end

  it '#as an I18n backend in the second position of chain' do
    I18n.backend = I18n::Backend::Chain.new(I18n.backend, backend)
    I18n.locale = locale
    expect(I18n.t(key)).to eq value
    I18n.locale = en_locale
    expect(I18n.t(key)).to eq en_value
  end
end
