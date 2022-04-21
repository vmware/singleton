#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe Singleton, :include_helpers, :extend_helpers do
  include_context 'reset client' do
    before(:all) do
      SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = config.dup
    end
  end

  describe '#translate a key' do
    it 'translate a key' do
      expect(Singleton.translate(key, component, locale: locale)).to eq value
    end

    it 'translate a nonexistent key, should return key' do
      expect(Singleton.translate(key_nonexistent, component, locale: locale)).to eq key_nonexistent
    end

    it 'translate a nonexistent key with default value in block, should return defaut_value' do
      expect(Singleton.translate(key_nonexistent, component, locale: locale) { defaut_value }).to eq defaut_value
    end

    it 'translate a nil key, should return nil' do
      expect(Singleton.translate(nil, component, locale: locale)).to be_nil
    end
    it 'translate a nil key with default value in block, should return defaut_value' do
      expect(Singleton.translate(nil, component, locale: locale) { defaut_value }).to eq defaut_value
    end
  end

  describe '#translate a key with exception enabled' do
    it 'translate a key' do
      expect(Singleton.translate!(key, component, locale: locale)).to eq value
    end

    it 'translate a nonexistent key, should raise exception' do
      expect { Singleton.translate!(key_nonexistent, component, locale: locale) }.to raise_error(SgtnClient::SingletonError)
    end

    it 'translate a nonexistent key with default value in block, should return defaut_value' do
      expect(Singleton.translate!(key_nonexistent, component, locale: locale) { defaut_value }).to eq defaut_value
    end

    it 'translate a nil key, should raise exception' do
      expect { Singleton.translate!(nil, component, locale: locale) }.to raise_error(SgtnClient::SingletonError)
    end
    it 'translate a nil key with default value in block, should return defaut_value' do
      expect(Singleton.translate!(nil, component, locale: locale) { defaut_value }).to eq defaut_value
    end
  end

  describe '#get messages of a bundle' do
    it 'should be able to get translations of a bundle' do
      expect(Singleton.get_translations(component, locale: locale).dig('messages')).to include({ key => value })
    end

    it 'get translations of a nonexistent component, should return empty messages' do
      expect(Singleton.get_translations(component_nonexistent, locale: locale).dig('messages')).to eq({})
    end
    it 'get translations of a nil component, should return empty messages' do
      expect(Singleton.get_translations(nil, locale: locale).dig('messages')).to eq({})
    end

    it 'get translations of a nonexistent locale. should fallback to en' do
      expect(Singleton.get_translations(component, locale: locale_nonexistent).dig('messages')).to include({ key => en_value })
    end

    it 'get translations of a nil locale. should fallback to en' do
      expect(Singleton.get_translations(component, locale: nil).dig('messages')).to include({ key => en_value })
    end
    it 'get translations of a nil locale and nil component, should return empty messages' do
      expect(Singleton.get_translations(nil, locale: locale).dig('messages')).to eq({})
    end
  end

  describe '#get messages of a bundle with exception enabled' do
    it 'should be able to get translations of a bundle' do
      expect(Singleton.get_translations!(component, locale: locale).dig('messages')).to include({ key => value })
    end

    it 'get translations of a nonexistent component, should raise exception' do
      expect { Singleton.get_translations!(component_nonexistent, locale: locale) }.to raise_error(SgtnClient::SingletonError)
    end
    it 'get translations of a nil component, should raise exception' do
      expect { Singleton.get_translations!(nil, locale: locale) }.to raise_error(SgtnClient::SingletonError)
    end

    it 'get translations of a nonexistent locale, should fallback to en' do
      expect(Singleton.get_translations!(component, locale: locale_nonexistent).dig('messages')).to include({ key => en_value })
    end
    it 'get translations of a nil locale, should fallback to en' do
      expect(Singleton.get_translations!(component, locale: nil).dig('messages')).to include({ key => en_value })
    end
    it 'get translations of a nil locale and nil component, should raise exception' do
      expect { Singleton.get_translations!(nil, locale: locale) }.to raise_error(SgtnClient::SingletonError)
    end
  end

  describe '#format messages' do
    it '#format english messages' do
      expect(Singleton.translate(formatting_key, component, locale: en_locale, error: 'syntax error', correct: 'correct words')).to eq 'syntax error detected, please enter correct words!'
    end
    it "#format #{locale} messages" do
      expect(Singleton.translate(formatting_key, component, locale: locale, error: '语法error', correct: 'correct单词')).to eq '检测到语法error，请输入correct单词!'
    end

    it '#format messages with insufficient arguments, should return key' do
      expect(Singleton.translate(formatting_key, component, locale: en_locale, error: 'syntax error')) .to eq formatting_key
    end
    it '#format messages with empty arguments' do
      expect(Singleton.translate(formatting_key, component, locale: locale)).to eq '检测到%{error}，请输入%{correct}!'
    end
    it '#format messages with additional arguments' do
      expect(Singleton.translate(formatting_key, component, locale: locale, error: '语法error', correct: 'correct单词', additional: 'additional')).to eq '检测到语法error，请输入correct单词!'
    end
  end

  describe '#format messages with exception enabled' do
    it '#format english messages' do
      expect(Singleton.translate!(formatting_key, component, locale: en_locale, error: 'syntax error', correct: 'correct words')).to eq 'syntax error detected, please enter correct words!'
    end
    it "#format #{locale} messages" do
      expect(Singleton.translate!(formatting_key, component, locale: locale, error: '语法error', correct: 'correct单词')).to eq '检测到语法error，请输入correct单词!'
    end

    it '#format messages with insufficient arguments, should raise exception' do
      expect { Singleton.translate!(formatting_key, component, locale: en_locale, error: 'syntax error') } .to raise_error(KeyError)
    end
    it '#format messages with empty arguments' do
      expect(Singleton.translate!(formatting_key, component, locale: locale)).to  eq '检测到%{error}，请输入%{correct}!'
    end
    it '#format messages with additional arguments' do
      expect(Singleton.translate!(formatting_key, component, locale: locale, error: '语法error', correct: 'correct单词', additional: 'additional')).to eq '检测到语法error，请输入correct单词!'
    end
  end

  describe '#set locale and get translation' do
    it "should be able to set #{en_locale}" do
      Singleton.locale = en_locale
      expect(Singleton.locale).to eq en_locale
      expect(Singleton.translate(key, component)).to eq en_value
    end
    it "should be able to set #{locale}" do
      Singleton.locale = locale
      expect(Singleton.locale).to eq locale
      expect(Singleton.translate(key, component)).to eq value
    end
    it 'should be able to set locale with nil' do
      Singleton.locale = nil
      expect(Singleton.locale).to eq en_locale
      expect(Singleton.translate(key, component)).to eq en_value
    end
    it 'should be able to set locale with empty string' do
      Singleton.locale = ''
      expect(Singleton.locale).to eq en_locale
      expect(Singleton.translate(key, component)).to eq en_value
    end
    it 'should be able to set locale with invalid locale' do
      Singleton.locale = 'invalid'
      expect(Singleton.locale).to eq 'en'
      expect(Singleton.translate(key, component)).to eq en_value
    end
  end
end
