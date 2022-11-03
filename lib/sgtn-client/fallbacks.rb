# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module Fallbacks # :nodoc:
    def get_bundle!(component, locale)
      error = nil
      localechain(locale) do |l|
        result, actual_locale = super(component, l)
        return [result, actual_locale] if result
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
