# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient::TranslationLoader
  class Source
    def initialize
      env = SgtnClient::Config.default_environment
      @config = SgtnClient::Config.configurations[env]

      # raise error if @config['source_bundle']
      @source_bundle_path = Pathname.new(@config['source_bundle'])
    end

    def load_bundle(component, locale = nil)
      return if locale && locale != SgtnClient::LocaleUtil::REAL_SOURCE_LOCALE # only return when querying source

      SgtnClient.logger.debug "[Source][getBundle]component=#{component}"

      total_messages = {}

      (@source_bundle_path + component).glob('**/*.{yml, yaml}') do |f|
        bundle = YAML.load(File.read(f))
        total_messages.merge!(bundle&.first&.last) # TODO: Warning about inconsistent source locale
      end

      total_messages.empty? ? nil : total_messages
    end
  end
end
