# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'erb'
require 'yaml'

module SgtnClient

  class FileUtil
      def self.read_json(bundlepath)
        SgtnClient.logger.debug "[FileUtil]read json file from: " + bundlepath
        begin
          return MultiJson.load(File.read(bundlepath))
        rescue => exception
          SgtnClient.logger.error exception.message
        end
        nil
      end

      def self.read_yml(file_name)
        YAML::load(File.read(file_name))
      end
  end
end

