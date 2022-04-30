# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require_relative 'singleton-ruby'

module Singleton # :nodoc:
  include SgtnClient

  # load configuration from a file
  def self.load_config(*args)
    SgtnClient.load(*args)
  end

  extend SgtnClient::Translation::Implementation

  private_class_method :getString, :getString_p, :getString_f, :getStrings
end
