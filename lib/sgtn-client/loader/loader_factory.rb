# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    module LoaderFactory
      def self.create(config)
        SgtnClient.logger.info "[#{method(__callee__).owner}.#{__callee__}] config=#{config}"

        loaders = []
        loaders << Source.new(config) if config['source_bundle']
        loaders << SgtnServer.new(config) if config['vip_server']
        loaders << LocalTranslation.new(config) if config['translation_bundle']
        raise SgtnClient::SingletonError, 'no translation is available!' if loaders.empty?

        chain_loader = Class.new(Chain)
        chain_loader.include SourceComparer if config['source_bundle'] || config['vip_server']
        chain_loader.include SingleLoader
        chain_loader.include Cache

        chain_loader.new(*loaders)
      end
    end
  end
end
