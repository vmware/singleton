#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

RSpec.shared_context 'reset client' do
  prepend_before :all do
    @config_bak = SgtnClient.config.dup

    Helpers::CONFIG_HASH.each do |key, value|
      SgtnClient.config.send("#{key}=", value)
    end

    SgtnClient.config.instance_variable_set(:@loader, nil)
    SgtnClient::CacheUtil.clear_cache
  end
  after :all do
    @config_bak.instance_variables.each do |var|
      SgtnClient.config.instance_variable_set(var, @config_bak.instance_variable_get(var))
    end
    SgtnClient.config.instance_variable_set(:@loader, nil)
    SgtnClient::CacheUtil.clear_cache
  end
end
