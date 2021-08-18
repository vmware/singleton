require 'singleton-cldr'

d = DateTime.new(2007,11,19,8,37,48,"-06:00")

@Result = d.to_full_s(:es)
