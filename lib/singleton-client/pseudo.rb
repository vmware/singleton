# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module Sgtn
  module Pseudo # :nodoc:
    protected

    def pickup_locale(locale, _component)
      locale == PSEUDO_LOCALE ? PSEUDO_LOCALE : super
    end
  end
end
