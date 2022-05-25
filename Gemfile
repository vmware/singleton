#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

source 'https://rubygems.org'

if !!File::ALT_SEPARATOR
  gemspec name: 'singleton-ruby.windows'
else
  gemspec name: 'singleton-ruby'
end

gem 'rake', require: false

group :test do
  gem 'pry-byebug', require: true
  gem 'pry-doc'
  gem 'pry-inline', require: true
  gem 'rspec'
  gem 'simplecov-json', require: false
  gem 'webmock'
end
