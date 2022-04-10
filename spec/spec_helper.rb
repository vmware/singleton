#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'bundler/setup'
require 'sgtn-client/sgtn-client.rb'

require 'simplecov'
SimpleCov.start do
  add_filter '/spec/'
end

Bundler.require :default, :test

require 'singleton-ruby'
require 'webmock/rspec'

module SgtnClient
  autoload :CacheUtil, 'sgtn-client/util/cache-util'
end

SgtnClient.load('./spec/config/sgtnclient.yml', 'test', './sgtnclient_d.log')

Dir[File.expand_path('support/**/*.rb', __dir__)].sort.each { |f| require f }

RSpec.configure do |config|
  config.filter_run_excluding integration: true
  config.filter_run_excluding disabled: true
  config.include SampleData
end

RSpec.configure do |config|
  config.include Helpers, :include_helpers
  config.extend  Helpers, :extend_helpers
end
