#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'sgtn-client/loader/server'

describe SgtnClient::TranslationLoader::SgtnServer, :include_helpers, :extend_helpers do
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

  new_config = config.dup
  new_config['vip_server'] = singleton_server
  server = SgtnClient::TranslationLoader::SgtnServer.new(new_config)

  it 'server returns HTTP error' do
    stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale }).to_return(status: 404)

    expect { server.load_bundle(component, locale) }.to raise_error(Faraday::ResourceNotFound)

    stubs.each { |stub| expect(stub).to have_been_requested }
  end

  it 'server returns business error 404' do
    stubs << stub_request(:get, server_url).with(query: { 'components' => component_nonexistent, 'locales' => locale }).to_return(body: nonexistent_response)

    expect { server.load_bundle(component_nonexistent, locale) }.to raise_error(SgtnClient::SingletonError)

    stubs.each { |stub| expect(stub).to have_been_requested }
  end

  it 'server returns another business error 701' do
    stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale }).to_return(body: bs_701_response)

    expect { server.load_bundle(component, locale) }.to raise_error(SgtnClient::SingletonError)

    stubs.each { |stub| expect(stub).to have_been_requested }
  end

  it 'server returns business error 60x' do
    stubs << stub_request(:get, server_url).with(query: { 'components' => component, 'locales' => locale }).to_return(body: bs_601_response)

    expect(server.load_bundle(component, locale)).not_to be_nil

    stubs.each { |stub| expect(stub).to have_been_requested }
  end
end
