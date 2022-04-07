# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'active_support'
require 'webmock/rspec'

module Helpers
  def config
    SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
  end

  mattr_accessor :singleton_server, :server_url, :component, :locale, :en_locale, :components_url, :locales_url # , :components_stub, :locales_stub

  self.singleton_server = 'https://localhost:8090'
  self.server_url = File.join(singleton_server, '/i18n/api/v2/translation/products',
                              'test', 'versions', '4.8.1')
  self.components_url = File.join(server_url, 'componentlist')
  self.locales_url = File.join(server_url, 'localelist')

  self.component = 'JAVA'
  self.locale = 'zh-Hans'
  self.en_locale = 'en'

  def components_stub
    stub_request(:get, components_url).to_return(body: File.new('spec/fixtures/mock_responses/componentlist'))
  end

  def locales_stub
    stub_request(:get, locales_url).to_return(body: File.new('spec/fixtures/mock_responses/localelist'))
  end

  def nonexistent_response
    File.new('spec/fixtures/mock_responses/nonexistent').read
  end

  def stubs
    []
  end
end
