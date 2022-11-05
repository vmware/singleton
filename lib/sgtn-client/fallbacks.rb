# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module Fallbacks # :nodoc:
    protected

    def get_bundle!(component, locale)
      error = nil
      localechain(locale) do |l|
        bundle = super(component, l)
        return bundle if bundle
      rescue StandardError => e
        error = e
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
