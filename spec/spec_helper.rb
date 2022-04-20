#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'bundler/setup'

require 'simplecov'
SimpleCov.start do
  add_filter '/spec/'
end

Bundler.require :default, :test

require 'singleton-client'
require 'webmock/rspec'
Dir[File.expand_path('support/**/*.rb', __dir__)].sort.each { |f| require f }

RSpec.configure do |config|
  config.include Helpers, :include_helpers
  config.extend  Helpers, :extend_helpers

  config.filter_run_excluding integration: true
  config.filter_run_excluding disabled: true
end

Singleton.load_config('./spec/config/sgtnclient.yml', 'test')
log_file = File.open('./unit_test.log', 'a')
SgtnClient.logger = Logger.new MultiIO.new(STDOUT, log_file)

WebMock.allow_net_connect!
