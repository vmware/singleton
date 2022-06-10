# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'faraday'
require 'faraday_middleware'

module SgtnClient
  module Common
    autoload :BundleID, 'sgtn-client/common/data'
  end

  module TranslationLoader
    autoload :CONSTS, 'sgtn-client/loader/consts'

    class SgtnServer
      ERROR_ILLEGAL_DATA = 'server returned illegal data.'
      ERROR_BUSINESS_ERROR = 'server returned business error.'

      REQUEST_ARGUMENTS = { timeout: 10 }.freeze

      def initialize(config)
        @server_url = config['vip_server']

        product_root = format('i18n/api/v2/translation/products/%s/versions/%s', config['product_name'], config['version'])

        @bundle_url = "#{product_root}/locales/%s/components/%s"
        @locales_url = "#{product_root}/localelist"
        @components_url = "#{product_root}/componentlist"
      end

      def load_bundle(component, locale)
        SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}] component=#{component}, locale=#{locale}"

        messages = query_server(format(@bundle_url, locale, component), ['messages'])
        messages
      end

      def available_bundles
        SgtnClient.logger.debug "[#{method(__callee__).owner}.#{__callee__}]"

        components_thread = Thread.new { available_components }
        available_locales.reduce(Set.new) do |bundles, locale|
          components_thread.value.reduce(bundles) do |inner_bundles, component|
            inner_bundles << Common::BundleID.new(component, locale)
          end
        end
      end

      private

      def available_locales
        query_server(@locales_url, ['locales'])
      end

      def available_components
        query_server(@components_url, ['components'])
      end

      def query_server(url, path_to_data = [], queries = nil, headers = nil)
        conn = Faraday.new(@server_url, request: REQUEST_ARGUMENTS) do |f|
          f.response :json # decode response bodies as JSON
          f.use :gzip
          f.response :raise_error
          f.response :logger, SgtnClient.logger, { log_level: :debug }
        end
        resp = conn.get(url, queries, headers)

        process_business_error(resp.body)
        extract_data(resp.body, path_to_data)
      end

      def extract_data(parsedbody, path_to_data)
        data = parsedbody.dig('data', *path_to_data)
        raise SgtnClient::SingletonError, "no expected data in response. Body is: #{parsedbody}" unless data

        data
      end

      def process_business_error(parsedbody)
        b_code = parsedbody.dig('response', 'code')
        unless b_code >= 200 && b_code < 300 || b_code >= 600 && b_code < 700
          raise SgtnClient::SingletonError, "#{ERROR_BUSINESS_ERROR} #{parsedbody['response']}"
        end

        # 600 means a successful response, 6xx means partial successful.
        SgtnClient.logger.warn "#{ERROR_BUSINESS_ERROR} #{parsedbody['response']}" if b_code > 600
      rescue TypeError, ArgumentError, NoMethodError => e
        raise SgtnClient::SingletonError, "#{ERROR_ILLEGAL_DATA} #{e}. Body is: #{parsedbody}"
      end
    end
  end
end
