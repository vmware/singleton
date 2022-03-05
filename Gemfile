# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#  
source "https://rubygems.org"

if !!File::ALT_SEPARATOR
  gemspec :name => 'singleton-ruby.windows'
else
  gemspec :name => 'singleton-ruby'
end

gem 'rake', :require => false
gem 'rspec-benchmark'

group :test do
  gem 'simplecov-json', :require => false
  gem 'rspec'
  gem 'webmock'
end
