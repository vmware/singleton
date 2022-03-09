# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'json'

module SgtnClient
    module Formatters
        class PluralFormatter

            attr_reader :locale

            def initialize(locale = TwitterCldr.locale)
              @locale = TwitterCldr.convert_locale(locale)
            end

            def num_s(string, replacements)
                reg = Regexp.union(
                  /%<(\{.*?\})>/
                )
                string.gsub(reg) do
                  count_placeholder, patterns = if $1
                    pluralization_hash = JSON.parse($1)
                    if pluralization_hash.is_a?(Hash) && pluralization_hash.size == 1
                      pluralization_hash.first
                    else
                      raise ArgumentError.new('expected a Hash with a single key')
                    end
                  else
                    raise ArgumentError.new('invalide format')
                  end
                  count  = replacements[count_placeholder.to_sym].to_s
                  if patterns.is_a?(Hash)
                    return TwitterCldr::Utils.deep_symbolize_keys(patterns)[count.to_sym]
                  else
                    raise ArgumentError.new('expected patterns to be a Hash')
                  end
                end
              end
        end
    end
end
