# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'faraday'
require 'faraday_middleware'

module SgtnClient::Core
  autoload :Request, 'sgtn-client/core/request'
end

class SgtnClient::TranslationLoader::SgtnServer
  PPRODUCT_ROOT = '/i18n/api/v2/translation/products/%s/versions/%s'

  PRODUCT_TRANSLATION = PPRODUCT_ROOT
  PRODUCT_LOCALE_LIST = "#{PPRODUCT_ROOT}/localelist"
  PRODUCT_COMPONENT_LIST = "#{PPRODUCT_ROOT}/componentlist"

  ERROR_ILLEGAL_DATA = 'server returned illegal data.'
  ERROR_BUSINESS_ERROR = 'server returned business error.'

  @@request_arguments = { timeout: 10 }

  def initialize
    env = SgtnClient::Config.default_environment
    @config = SgtnClient::Config.configurations[env]

    @server_url = @config['vip_server']

    product_name = @config['product_name']
    version = @config['version']

    # TODO: none is defined, throw error

    @bundle_url = format(PRODUCT_TRANSLATION, product_name, version)
    @locales_url = format(PRODUCT_LOCALE_LIST, product_name, version)
    @components_url = format(PRODUCT_COMPONENT_LIST, product_name, version)
  end

  def load_bundle(component, locale)
    messages = query_server(
      @bundle_url,
      ['bundles', 0],
      { locales: locale, components: component }
    )
     messages
  end

  private

  def query_server(url, path_to_data = [], queries = nil, headers = nil)
    conn = Faraday.new(@server_url, request: @@request_arguments) do |f|
      f.response :json # decode response bodies as JSON
      f.use :gzip
      f.response :raise_error
      f.response :logger
    end
    resp = conn.get(url, queries, headers)

    return nil, extract_cacheinfo(resp.headers) if resp.status == 304

    process_business_error(resp.body)
    extract_data(resp.body, path_to_data)
  end

  def extract_data(parsedbody, path_to_data)
    data = parsedbody.dig('data', *path_to_data)
    raise SingletonError, "no expected data in response. Body is: #{parsedbody}" unless data

    data
  end

  def process_business_error(parsedbody)
    b_code = parsedbody.dig('response', 'code')
    unless b_code >= 200 && b_code < 300 || b_code >= 600 && b_code < 700
      raise SingletonError, "ERROR_BUSINESS_ERROR #{parsedbody['response']}"
    end

    Common.logger.warn "ERROR_BUSINESS_ERROR #{parsedbody['response']}" if b_code > 600
  rescue TypeError, ArgumentError, NoMethodError => e
    raise SingletonError, "#{ERROR_ILLEGAL_DATA} #{e}. Body is: #{parsedbody}"
  end
end
