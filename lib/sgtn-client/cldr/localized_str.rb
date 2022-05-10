# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

String.class_eval <<-LOCALIZE, __FILE__, __LINE__ + 1
          # <b>DEPRECATED:</b> Please use <tt>Sgtn:translate</tt> instead.
          def to_plural_s(locale, arg)
            num_str = SgtnClient::Formatters::PluralFormatter.new(locale).num_s(self, arg)
            if num_str.nil? || num_str.empty?
              self.localize(locale) % arg
            else
              num_str
            end
          end
        LOCALIZE
