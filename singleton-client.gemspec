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
  s.test_files    = s.files.grep(%r{^(test|spec|features)/})
  s.require_paths = ['lib']

  s.add_development_dependency('rdoc', '>= 2.4.2', '< 6.0')
  s.add_development_dependency('rspec', '~> 3.0')
  s.add_development_dependency('rubocop')
  s.add_development_dependency('webmock', '~> 2.0')

  s.add_dependency('faraday')
  s.add_dependency('faraday_middleware')
  s.add_dependency('multi_json', '~> 1.0')
  s.add_dependency('request_store')
  s.add_dependency('twitter_cldr', '~> 6.6')
  s.add_dependency('i18n')
  s.add_dependency('lumberjack')

  s.required_ruby_version = '>= 2.0.0'
end
