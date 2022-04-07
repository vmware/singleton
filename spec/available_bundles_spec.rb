# frozen_string_literal: true

#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'webmock/rspec'

describe 'Available Bundles', :include_helpers, :extend_helpers do
  new_config = config.dup
  let(:loader) { SgtnClient::TranslationLoader::LoaderFactory.create(new_config) }

  before :all do
    WebMock.enable!
    WebMock.disable_net_connect!
  end
  after :all do
    WebMock.disable!
  end

  before :each do
    SgtnClient::CacheUtil.clear_cache
    WebMock.reset!
    new_config['vip_server'] = singleton_server
  end

  it '#should be able to get available bundles' do
    stubs << components_stub << locales_stub
    result = loader.available_bundles
    expect(result).to_not be_nil
    stubs.each { |stub| expect(stub).to have_been_requested }
  end
end
