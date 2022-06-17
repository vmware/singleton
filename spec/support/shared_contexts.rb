#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

RSpec.shared_context 'reset client' do
  prepend_before :all do
    @config_bak = SgtnClient.config.dup
    SgtnClient.config.instance_variable_set(:@loader, nil)
    SgtnClient::CacheUtil.clear_cache
  end
  after :all do
    SgtnClient.config = @config_bak
    SgtnClient.config.instance_variable_set(:@loader, nil)
    SgtnClient::CacheUtil.clear_cache
  end
end
