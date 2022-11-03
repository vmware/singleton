# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module Sgtn
  module Pseudo # :nodoc:
    def match_locale(*)
      PSEUDO_LOCALE
    end

    def interpolate(translation, _locale, **kwargs)
      translation.localize(SgtnClient::LocaleUtil.get_source_locale) % kwargs
    end
  end
end
