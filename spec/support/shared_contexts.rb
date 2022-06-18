#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

RSpec.shared_context 'reset client' do
  prepend_before :all do
    Helpers::CONFIG_HASH.each do |key, value|
      SgtnClient.config.send("#{key}=", value)
    end

    SgtnClient.config.instance_variable_set(:@loader, nil)
    SgtnClient::CacheUtil.clear_cache
  end
  after :all do
    SgtnClient.config.instance_variable_set(:@loader, nil)
    SgtnClient::CacheUtil.clear_cache
  end
end
