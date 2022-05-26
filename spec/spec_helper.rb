#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'bundler/setup'

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

require 'singleton-client'

require 'webmock/rspec'
Dir[File.expand_path('support/**/*.rb', __dir__)].sort.each { |f| require f }

RSpec.configure do |config|
  config.include Helpers, :include_helpers
  config.extend  Helpers, :extend_helpers

  config.filter_run_excluding integration: true
  config.filter_run_excluding disabled: true
end

Sgtn.load_config('./spec/config/sgtnclient.yml', 'test')
log_file = File.open('./unit_test.log', 'a')
SgtnClient.logger = Logger.new(MultiIO.new(STDOUT, log_file),
                               formatter: proc { |severity, datetime, progname, msg|
                                 "[#{datetime.strftime('%Y-%m-%d %H:%M:%S')} #{Thread.current.object_id}] #{severity[0]} - #{progname}: #{msg}\n"
                               })

WebMock.allow_net_connect!
