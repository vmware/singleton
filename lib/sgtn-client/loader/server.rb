# frozen_string_literal: true

# Copyright 2025 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'faraday'
require 'faraday/gzip'
require 'set'

module SgtnClient
  module TranslationLoader
    class SgtnServer # :nodoc:
      ERROR_ILLEGAL_DATA = 'server returned illegal data.'
      ERROR_BUSINESS_ERROR = 'server returned business error.'
      ERROR_PARTIAL_SUCCESS = 'the request to server was partially successful.'
      ERROR_NO_DATA = 'no expected data in response from server. path: %s. Body is: %s'

      REQUEST_ARGUMENTS = { timeout: 10 }.freeze

      def initialize(config)
        product_root = format('i18n/api/v2/translation/products/%<name>s/versions/%<version>s',
                              name: config.product_name, version: config.version)

        @bundle_url = "#{product_root}/locales/%s/components/%s"
        @locales_url = "#{product_root}/localelist"
        @components_url = "#{product_root}/componentlist"

        @conn = Faraday.new(config.vip_server, request: REQUEST_ARGUMENTS) do |f|
          f.response :json
          f.response :raise_error
          f.response :logger, config.logger, { log_level: :debug, headers: false, bodies: true }
          f.request :gzip
        end
      end

      def load_bundle(component, locale)
        SgtnClient.logger.debug { "[#{method(__callee__).owner}.#{__callee__}] component=#{component}, locale=#{locale}" }

        data = query_server(format(@bundle_url, locale, component), ['messages'])
        Common::BundleData.new(data, origin: self, component: component,
                                     locale: locale == CONSTS::REAL_SOURCE_LOCALE ? LocaleUtil.get_source_locale : locale)
      end

      def available_bundles
        SgtnClient.logger.debug { "[#{method(__callee__).owner}.#{__callee__}]" }

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
        resp = @conn.get(url, queries, headers)

        process_business_error(resp.body)

        resp.body&.dig('data', *path_to_data) ||
          (raise SingletonError, format(ERROR_NO_DATA, path_to_data, resp.body))
      end

      def process_business_error(parsedbody)
        b_code = parsedbody.dig('response', 'code')
        unless b_code >= 200 && b_code < 300 || b_code >= 600 && b_code < 700
          raise SingletonError, "#{ERROR_BUSINESS_ERROR} #{parsedbody['response']}"
        end

        # 600/200 means a successful response, 6xx/2xx means partial successful.
        SgtnClient.logger.warn "#{ERROR_PARTIAL_SUCCESS} #{parsedbody['response']}" if b_code != 600 && b_code != 200
      rescue TypeError, ArgumentError, NoMethodError => e
        raise SingletonError, "#{ERROR_ILLEGAL_DATA} #{e}. Body is: #{parsedbody}"
      end
    end
  end
end
