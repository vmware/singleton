# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require_relative 'singleton-ruby'

module Sgtn # :nodoc:
  # load configuration from a file
  def self.load_config(config_file, env)
    SgtnClient.load(config_file, env)
  end

  extend SgtnClient::Translation::Implementation

  private_class_method :getString, :getString_p, :getString_f, :getStrings
end
