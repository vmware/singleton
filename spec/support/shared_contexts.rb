#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

RSpec.shared_context 'reset client' do
  prepend_before :all do
    reset_client
  end
  after :all do
    # wait_threads_finish
    reset_client
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
