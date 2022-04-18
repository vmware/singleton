#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe Singleton, :include_helpers, :extend_helpers do
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

    it 'translate a nil key' do
      expect(Singleton.translate(nil, component, locale: locale)).to be_nil
    end
    it 'translate a nil key with default value in block' do
      expect(Singleton.translate(nil, component, locale: locale) { defaut_value }).to eq defaut_value
    end
  end

  describe '#translate a key with exception enabled' do
    it 'translate a key with exception enabled' do
      expect(Singleton.translate!(key, component, locale: locale)).to eq value
    end

    it 'translate a nonexistent key with exception enabled, should raise exception' do
      expect { Singleton.translate!(key_nonexistent, component, locale: locale) }.to raise_error(SgtnClient::SingletonError)
    end

    it 'translate a nonexistent key with default value in block and with exception enabled, should return defaut_value' do
      expect(Singleton.translate!(key_nonexistent, component, locale: locale) { defaut_value }).to eq defaut_value
    end

    it 'translate a nil key with exception enabled, should raise exception' do
      expect { Singleton.translate!(nil, component, locale: locale) }.to raise_error(SgtnClient::SingletonError)
    end
    it 'translate a nil key  with default value in block and with exception enabled, should raise exception' do
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
    it 'get translations of a nil locale and nil component' do
      expect(Singleton.get_translations(nil, locale: locale).dig('messages')).to eq({})
    end
  end

  describe '#get messages of a bundle with exception enabled' do
    it 'should be able to get translations of a bundle with exception enabled' do
      expect(Singleton.get_translations!(component, locale: locale).dig('messages')).to include({ key => value })
    end

    it 'get translations of a nonexistent component with exception enabled' do
      expect { Singleton.get_translations!(component_nonexistent, locale: locale) }.to raise_error(SgtnClient::SingletonError)
    end
    it 'get translations of a nil component with exception enabled' do
      expect { Singleton.get_translations!(nil, locale: locale) }.to raise_error(SgtnClient::SingletonError)
    end

    it 'get translations of a nonexistent locale with exception enabled' do
      expect(Singleton.get_translations!(component, locale: locale_nonexistent).dig('messages')).to include({ key => en_value })
    end
    it 'get translations of a nil locale. should fallback to en' do
      expect(Singleton.get_translations!(component, locale: nil).dig('messages')).to include({ key => en_value })
    end
    it 'get translations of a nil locale and nil component with exception enabled' do
      expect { Singleton.get_translations!(nil, locale: locale) }.to raise_error(SgtnClient::SingletonError)
    end
  end
end
