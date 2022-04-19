#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'bundler/setup'

require 'simplecov'
SimpleCov.start do
  add_filter '/spec/'
end

Bundler.require :default, :test

require 'singleton-ruby'
require 'webmock/rspec'
Dir[File.expand_path("../support/**/*.rb", __FILE__)].each {|f| require f }

RSpec.configure do |c|
  c.include Helpers, :include_helpers
  c.extend  Helpers, :extend_helpers
end

SgtnClient.load("./spec/config/sgtnclient.yml", "test", './sgtnclient_d.log')
log_file = File.open('./unit_test.log', "a")
SgtnClient.logger = Logger.new MultiIO.new(STDOUT, log_file)

RSpec.configure do |config|
  config.filter_run_excluding integration: true
  config.filter_run_excluding disabled: true
  config.include SampleData
end
