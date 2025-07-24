require File.expand_path('lib/version', __dir__)

Gem::Specification.new do |s|
  s.name = 'singleton-client'
  s.version = VERSION
  s.authors = ['VMware G11n Team']
  s.description = 'Singleton Ruby client'
  s.license = 'EPL 2.0'
  s.email = 'g11n-vip-project@vmware.com'
  s.extra_rdoc_files = ['README.md']
  s.homepage = 'https://github.com/vmware/singleton'
  s.summary = 'Singleton Ruby client'

  require 'rake'
  s.files         = FileList['lib/**/*.rb', 'bin/*'].to_a
  s.executables   = s.files.grep(%r{^bin/}).map { |f| File.basename(f) }
  s.require_paths = ['lib']

  s.add_dependency('concurrent-ruby')
  s.add_dependency 'faraday', '~> 2.7'
  s.add_dependency 'faraday-gzip', '~> 3'
  s.add_dependency('i18n')
  s.add_dependency('logging')
  s.add_dependency('multi_json') # TODO
  s.add_dependency 'observer'
  s.add_dependency('request_store')
  s.add_dependency('twitter_cldr')

  s.required_ruby_version = '>= 2.3.0'
  s.metadata['rubygems_mfa_required'] = 'true'
end
