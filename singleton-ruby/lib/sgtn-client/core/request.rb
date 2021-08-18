require 'rest-client'
require 'multi_json'

module SgtnClient::Core
    class Request
        def self.get(url)
          res = RestClient.get(url)
          begin
            obj = MultiJson.load(res)
          rescue MultiJson::ParseError => exception
            exception.data
            exception.cause
          end
          return obj
        end
    end
end