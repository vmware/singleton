# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    module SingleLoader
      def load_bundle(component, locale, sync: true)
        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}" }

        do_single_load(Common::BundleID.new(component, locale), sync) { super(component, locale) }
      end

      def available_bundles(sync: true)
        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}]" }

        do_single_load(CONSTS::AVAILABLE_BUNDLES_KEY, sync) { super() }
      end

      def initialize(*args)
        none_alive = proc { |_, thread| thread.nil? || thread.alive? == false }
        creator = proc do |id, &block|
          Thread.new do
            SgtnClient.logger.debug { "start single loading #{id}" }
            begin
              block.call
            ensure
              # delete thread from hash after finish
              @single_loader.remove_object(id)
            end
          end
        end

        @single_loader = SingleOperation.new(none_alive, &creator)

        super
      end

      private

      def do_single_load(id, sync, &block)
        thread = @single_loader.operate(id, &block)
        thread&.value if sync
      end
    end
  end
end
