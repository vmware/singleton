require 'singleton-cldr'

d = DateTime.new(2007,11,19,8,37,48,"-06:00")

@Result = d.to_full_s(:es)

puts @Result

str = 'there %<{ "cat_count": { "0": "no cat", "one": "is one cat", "other": "are %{cat_count} cats" } }> in the room'
p_str = str.to_plural_s(:es, { :cat_count => 3 })
puts p_str

p_str0 = str.to_plural_s(:es, { :cat_count => 0 })
puts p_str0
