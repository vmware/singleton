#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

RSpec.shared_context 'reset client' do
  prepend_before :all do
    SgtnClient.config.update(Helpers::CONFIG_HASH)

    SgtnClient.config.instance_variable_set(:@loader, nil)
    clear_cache
  end
  after :all do
    # wait_threads_finish
    SgtnClient.config.instance_variable_set(:@loader, nil)
    clear_cache
  end
end
