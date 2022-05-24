# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    autoload :Source, 'sgtn-client/loader/source'
    autoload :SgtnServer, 'sgtn-client/loader/server'
    autoload :LocalTranslation, 'sgtn-client/loader/local_translation'
    autoload :Chain, 'sgtn-client/loader/chain_loader'
    autoload :SourceComparer, 'sgtn-client/loader/source_comparer'
    autoload :SingleLoader, 'sgtn-client/loader/single_loader'
    autoload :Cache, 'sgtn-client/loader/cache'
    autoload :CacheFiller, 'sgtn-client/loader/cache'

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
        chain_loader.include CacheFiller
        chain_loader.include SingleLoader
        chain_loader.include Cache

        chain_loader.new(*loaders)
      end
    end
  end
end
