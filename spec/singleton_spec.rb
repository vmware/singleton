#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

describe Singleton do
  it 'should be able to get translation of a string' do
    expect(Singleton.translate('helloworld', component: 'JAVA', locale: 'zh-Hans')).to eq '你好世界'
  end
end
