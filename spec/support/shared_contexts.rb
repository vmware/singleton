#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

RSpec.shared_context 'reset client' do
  prepend_before :all do
    SgtnClient.config.update(Helpers::CONFIG_HASH)

    SgtnClient.config.instance_variable_set(:@loader, nil)
    SgtnClient::LocaleUtil.send(:reset_locale_data, :available_locales)
  end
  after :all do
    SgtnClient.config.update(Helpers::CONFIG_HASH)
    # wait_threads_finish
    SgtnClient.config.instance_variable_set(:@loader, nil)
    SgtnClient::LocaleUtil.send(:reset_locale_data, :available_locales)
  end
end

RSpec.shared_context 'webmock' do
  before :all do
    WebMock.enable!
    WebMock.disable_net_connect!
  end
  after :all do
    WebMock.disable!
  end

  before :each do
    WebMock.reset!
  end
end
