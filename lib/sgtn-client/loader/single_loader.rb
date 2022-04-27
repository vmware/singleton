# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :SingleOperation, 'sgtn-client/common/single_operation'
  autoload :CacheUtil, 'sgtn-client/util/cache-util'

  module TranslationLoader
    autoload :CONSTS, 'sgtn-client/loader/consts'

    module SingleLoader
      def load_bundle(component, locale, sync = true)
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}"

        @single_bundle_loader ||= single_loader { |c, l| super(c, l) }
        id = Common::BundleID.new(component, locale)
        thread = @single_bundle_loader.operate(id, component, locale)
        thread&.value if sync
      ensure
        # delete thread from hash after finish
        @single_bundle_loader.remove_object(id)
      end

      def available_bundles(sync = true)
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}]"

        @single_available_bundles_loader ||= single_loader { super() }
        thread = @single_available_bundles_loader.operate(CONSTS::AVAILABLE_BUNDLES_KEY)
        thread&.value if sync
      ensure
        @single_available_bundles_loader.remove_object(CONSTS::AVAILABLE_BUNDLES_KEY)
      end

      private

      def single_loader(&block)
        none_alive = proc { |_, thread| thread.nil? || thread.alive? == false }
        creator = proc do |id, _, *args|
          Thread.new do
            SgtnClient.logger.debug "start single loading #{id}"
            block.call(*args)
          end
        end

        SgtnClient::SingleOperation.new(none_alive, &creator)
      end
    end
  end
end
