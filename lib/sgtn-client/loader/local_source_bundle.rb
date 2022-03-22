# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  class Source
    def self.load_bundle(component)
      SgtnClient.logger.debug "[Source][getBundle]component=#{component}"
      env = SgtnClient::Config.default_environment
      source_bundle = SgtnClient::Config.configurations[env]['source_bundle']
      component_path = "#{source_bundle}/#{component}"
      total_messages = nil
      Dir.glob('**/*.{yml, yaml}', base: component_path) do |f|
        bundle = SgtnClient::FileUtil.read_yml("#{component_path}/#{f}")
        if total_messages.nil?
          total_messages = bundle&.first&.last
        else
          total_messages.merge!(bundle&.first&.last)
        end
      end

      if total_messages
        return { 'component' => component, 'locale' => SgtnClient::LocaleUtil.get_source_locale, 'messages' => total_messages }
      end
    rescue StandardError => e
      SgtnClient.logger.error e.message
      nil
    end
  end
end
