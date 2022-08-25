# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module Fallbacks # :nodoc:
    def translate!(key, component, locale = nil, **kwargs, &block)
      error = nil
      locales = get_locales(locale)
      locales.each_with_index do |l, i|
        result = if i == locales.length - 1
                   super(key, component, l, **kwargs, &block)
                 else
                   super(key, component, l, **kwargs)
                 end
        return result if result
      rescue StandardError => e
        error = e
      end
      raise error if error
    end

    def get_translations!(component, locale = nil)
      error = nil
      locales = get_locales(locale)
      locales.each do |l|
        result = super(component, l)
        return result if result
      rescue StandardError => e
        error = e
      end
      raise error if error
    end

    private

    def get_locales(locale)
      fallbacks = LocaleUtil.locale_fallbacks.dup
      (index = fallbacks.index(locale)) && fallbacks.delete_at(index)
      [locale] + fallbacks
    end
  end
end
