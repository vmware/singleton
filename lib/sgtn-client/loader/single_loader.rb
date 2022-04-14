# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :SingleOperation, 'sgtn-client/common/single_operation'
  autoload :CacheUtil, 'sgtn-client/util/cache-util'

  module TranslationLoader
    autoload :CONSTS, 'sgtn-client/loader/consts'

    module SingleLoader
      def load_bundle(component, locale)
        SgtnClient.logger.debug "[#{__FILE__}.#{__callee__}] component=#{component}, locale=#{locale}"
        @single_bundle_loader ||= single_loader { |c,l| super(c,l) }
        @single_bundle_loader.operate(SgtnClient::CacheUtil.get_cachekey(component, locale), component, locale)&.value
      end

      def available_bundles
        SgtnClient.logger.debug "[#{__FILE__}.#{__callee__}]"
        @single_bundles_loader ||= single_loader { super }
        @single_bundles_loader.operate(CONSTS::AVAILABLE_BUNDLES_KEY)&.value
      end

      private
      def single_loader(&block)
        none_alive = proc { |_, thread| thread.nil? || thread.alive? == false }
        creator = proc do |id, _, *args|
          Thread.new do
            SgtnClient.logger.debug "start single loading #{id}"
            item = block.call(*args)

            # delete thread from hash after finish
            Thread.new { @single_loader.remove_object(id) }
            item
          end
        end

        SgtnClient::SingleOperation.new(none_alive, &creator)
      end
    end
  end
end
