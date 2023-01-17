# Copyright 2022-2023 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module Fallbacks # :nodoc:
    protected

    def get_bundle!(component, locale)
      error = nil
      localechain(locale) do |l|
        begin
          return super(component, l)
        rescue StandardError => e
          SgtnClient.logger.debug { (["[#{method(__callee__).owner}.#{__callee__}] {#{component}, #{locale}} #{e.message}"] + e.backtrace).join($/) }
          error = e
        end
      end
      raise error if error
    end

    private

    def localechain(locale)
      yield locale
      LocaleUtil.locale_fallbacks.each do |l|
        next if l == locale

        yield l
      end
    end
  end
end
