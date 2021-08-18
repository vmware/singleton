require 'bundler/setup'
require_relative '../lib/sgtn-client/sgtn-client.rb'
require 'twitter_cldr'

if ENV['COVERAGE']
  require 'simplecov'
  require 'coveralls'
  Coveralls.wear!
  SimpleCov.start do
    add_filter "/spec/"
  end
end

Bundler.require :default, :test
#SgtnClient.load("./spec/config/sgtnclient.yml", "test")

#require 'SgtnClient'

include SgtnClient
include SgtnClient::Logging
include SgtnClient::Exceptions

require 'logger'

SgtnClient.load("./spec/config/sgtnclient.yml", "test", './sgtnclient.log')

Dir[File.expand_path("../support/**/*.rb", __FILE__)].each {|f| require f }

# Set logger for http
http_log = File.open(File.expand_path('../log/http.log', __FILE__), "w")
#Payment.api.http.set_debug_output(http_log)

RSpec.configure do |config|
  config.filter_run_excluding :integration => true
  config.filter_run_excluding :disabled => true
  config.include SampleData
  # config.include PayPal::SDK::REST::DataTypes
end

WebMock.allow_net_connect!
