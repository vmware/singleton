# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

#require 'spec_helper'
require 'stringio'
require_relative '../../lib/sgtn-client/sgtn-client.rb'

describe SgtnClient::Logging do
  #Logging = SgtnClient::Logging

  class TestLogging
    #include Logging
  end

  # before :each do
  #   @logger_file = StringIO.new
  #   Logging.logger = Logger.new(@logger_file)
  #   file = File.open('./spec/unit/foo.log', File::WRONLY | File::APPEND)
  #   Logging.logger = Logger.new(file)
  #   SgtnClient.logger = Logger.new(file)
  #   SgtnClient.load("./spec/config/sgtnclient.yml", "test", './sgtnclient_d.log')
  # end

  it "get logger object" do
    #expect(@test_logging.logger).to be_a Logger
    expect(SgtnClient.logger).to be_a Logger
  end

  it "write message to logger" do
    test_message = "Example log message!!!"
    SgtnClient.logger.debug(test_message)
   # @logger_file.rewind
   # expect(@logger_file.read).to match test_message
  end

end
