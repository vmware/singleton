#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'bundler/setup'
require_relative '../lib/sgtn-client/sgtn-client.rb'
require 'logger'

if ENV['COVERAGE']
  require 'simplecov-json'
  SimpleCov.formatter = SimpleCov::Formatter::JSONFormatter
  # require 'coveralls'
  # Coveralls.wear!
  SimpleCov.start do
    add_filter "/spec/"
  end
end

Bundler.require :default, :test

require 'singleton-ruby'
require 'sgtn-client/api/source'

include SgtnClient
include SgtnClient::Logging
include SgtnClient::Exceptions

SgtnClient.load("./spec/config/sgtnclient.yml", "test", './sgtnclient.log')

Dir[File.expand_path("../support/**/*.rb", __FILE__)].each {|f| require f }

RSpec.configure do |config|
  config.filter_run_excluding :integration => true
  config.filter_run_excluding :disabled => true
  config.include SampleData
end

WebMock.allow_net_connect!
