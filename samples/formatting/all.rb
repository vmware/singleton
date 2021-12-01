require 'singleton-ruby'

include SgtnClient

d = DateTime.new(2007,11,19,8,37,48,"-06:00")

@Result = d.l_full_s(:es)

puts @Result

