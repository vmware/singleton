require 'erb'
require 'yaml'

module SgtnClient

  class FileUtil

      @mutex = Mutex.new

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
        SgtnClient.logger.debug "[FileUtil]read yml file from: " + file_name
        @mutex.synchronize do
          erb = ERB.new(File.read(file_name))
          erb.filename = file_name
          YAML.load(erb.result)
        end
      end
  end

end