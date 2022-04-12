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

module SgtnClient
  autoload :CacheUtil, 'sgtn-client/util/cache-util'
end

SgtnClient.load('./spec/config/sgtnclient.yml', 'test', './sgtnclient_d.log')
SgtnClient.logger = Logger.new(STDOUT)

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

require 'sgtn-client/loader/single_loader'
# require 'pry-byebug'
# require 'pry-inline'
# binding.pry
TracePoint.new(:call) do |tp|
SgtnClient.logger.debug "calling #{tp.defined_class}.#{tp.method_id}"
SgtnClient.logger.debug tp.binding.local_variables 
SgtnClient.logger.debug tp.binding.receiver 
SgtnClient.logger.debug tp.binding.source_location 
SgtnClient.logger.debug tp.inspect
SgtnClient.logger.debug tp.method_id
SgtnClient.logger.debug tp.callee_id
SgtnClient.logger.debug tp.parameters()

end.enable(target: SgtnClient::TranslationLoader::SingleLoader.instance_method(:load_bundle))
# TracePoint.new(:return) do |tp|
# require 'pry-byebug'
# require 'pry-inline'
# binding.pry
#     p [tp.lineno, tp.event, tp.raised_exception]
# end.enable
# TracePoint.new(:thread_begin) do |tp|
# require 'pry-byebug'
# require 'pry-inline'
# binding.pry
#     p [tp.lineno, tp.event, tp.raised_exception]
# end.enable
# TracePoint.new(:thread_end) do |tp|
# require 'pry-byebug'
# require 'pry-inline'
# binding.pry
#     p [tp.lineno, tp.event, tp.raised_exception]
# end.enable
