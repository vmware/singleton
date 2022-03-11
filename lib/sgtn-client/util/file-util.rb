require 'erb'
require 'yaml'

module SgtnClient

  class FileUtil
      def self.read_json(bundlepath)
        SgtnClient.logger.debug "[FileUtil]read json file from: " + bundlepath
        @mutex.synchronize do
          data_hash = nil
          begin
            file = File.read(bundlepath)
            data_hash = MultiJson.load(file)
          rescue => exception
            SgtnClient.logger.error exception.message
          end
          return data_hash
        end
      end

      def self.read_yml(file_name)
        File.open(file_name) do |file|
          sources = YAML::load(file)
        end
      end
  end
end