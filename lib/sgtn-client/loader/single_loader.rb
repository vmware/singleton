# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    module SingleLoader
      def load_bundle(component, locale)
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}"

        @single_bundle_loader ||= single_loader { |c, l| super(c, l) }
        id = CacheUtil.get_cachekey(component, locale)
        @single_bundle_loader.operate(id, component, locale)&.value
      end

      def available_bundles
        SgtnClient.logger.debug "[#{__FILE__}][#{__callee__}]"

        @single_available_bundles_loader ||= single_loader { super }
        @single_available_bundles_loader.operate(CONSTS::AVAILABLE_BUNDLES_KEY)&.value
      end

      private

      def single_loader(&block)
        loader = nil
        none_alive = proc { |_, thread| thread.nil? }
        creator = proc do |id, _, *args|
          Thread.new do
            SgtnClient.logger.debug "start single loading #{id}"
            begin
              block.call(*args)
            ensure
              # delete thread from hash after finish
              loader.remove_object(id)
            end
          end
        end

        loader = SgtnClient::SingleOperation.new(none_alive, &creator)
      end
    end
  end
end
