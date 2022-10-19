# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'forwardable'
require_relative 'singleton-ruby'

module Sgtn # :nodoc:
  autoload :Pseudo, 'sgtn/pseudo'
  autoload :I18nBackend, 'sgtn/i18n_backend'

  class << self
    extend Forwardable

    # load configurations from a file
    def load_config(config_file, env)
      SgtnClient.load(config_file, env)
    end

    def with_locale(tmp_locale = nil)
      if tmp_locale.nil?
        yield
      else
        current_locale = locale
        self.locale = tmp_locale
        begin
          yield
        ensure
          self.locale = current_locale
        end
      end
    end

    private

    def translation
      @translation ||= Class.new do
        include SgtnClient::Translation::Implementation
        include SgtnClient::Fallbacks
        include Sgtn::Pseudo
      end.new
    end

    def_delegator SgtnClient::Config, :instance, :config
    delegate %i[translate! t! translate t get_translations! get_translations] => :translation,
             %i[locale locale=] => SgtnClient,
             %i[logger product_name version vip_server translation_bundle
                source_bundle cache_expiry_period log_file log_level
                pseudo_mode pseudo_prefix pseudo_suffix ].flat_map { |m|
               [m, "#{m}=".to_sym]
             } => :config
  end
end
