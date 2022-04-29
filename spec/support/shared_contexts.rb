#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

RSpec.shared_context 'reset client' do
  prepend_before :all do
    @config_bak = SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
    SgtnClient::Config.instance_variable_set(:@loader, nil)
    SgtnClient::CacheUtil.clear_cache
  end
  after :all do
    SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = @config_bak
    SgtnClient::Config.instance_variable_set(:@loader, nil)
    SgtnClient::CacheUtil.clear_cache
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
